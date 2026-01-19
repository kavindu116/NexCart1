package controller;

import Hibernate.Address;
import Hibernate.Cart;
import Hibernate.City;
import Hibernate.DeliveryType;
import Hibernate.Order;
import Hibernate.OrderItem;
import Hibernate.OrderStatus;
import Hibernate.Product;
import Hibernate.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.PayHear;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "Checkout", urlPatterns = {"/Checkout"})
public class Checkout extends HttpServlet {

    private static final int SELECTER_DEFAULT_VALUE = 0;
    private static final int ORDER_PENDING = 1;
    private static final int WITHIN_GCOLOMBO = 1;
    private static final int OUT_OF_COLOMBO = 2;
    private static final int RATING_DEFAULT_VALUE = 0;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

        boolean isCurrentAddress = requestObject.get("isCurrentAddress").getAsBoolean();
        String fname = requestObject.get("fname").getAsString();
        String lname = requestObject.get("lname").getAsString();
        String cityselect = requestObject.get("city").getAsString();
        String line1 = requestObject.get("line1").getAsString();
        String line2 = requestObject.get("line2").getAsString();
        String postalCose = requestObject.get("postalCode").getAsString();
        String mobile = requestObject.get("mobile").getAsString();

        System.out.println(fname);

        SessionFactory sf = Hibernate.HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        Transaction tr = s.beginTransaction();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            responseObject.addProperty("message", "Session Expired Please Sign In");
        } else {
            if (isCurrentAddress) {
                Criteria c1 = s.createCriteria(Address.class);
                c1.add(Restrictions.eq("user", user));
                c1.addOrder(org.hibernate.criterion.Order.desc("id"));
                if (c1.list().isEmpty()) {
                    requestObject.addProperty("message", "Your current address is not found Please Add a new Address");
                } else {
                    Address address = (Address) c1.list().get(0);
                    processCheckout(s, tr, user, address, responseObject);
                }
            } else {
                if (fname.isEmpty()) {
                    responseObject.addProperty("message", "Frist Name is Requried");
                } else if (lname.isEmpty()) {
                    responseObject.addProperty("message", "Last Name is Requried");
                } else if (!Util.isInteger(cityselect)) {
                    responseObject.addProperty("message", "Invalid City");
                } else if (Integer.parseInt(cityselect) == SELECTER_DEFAULT_VALUE) {
                    responseObject.addProperty("message", "Invalid City");
                } else {
                    City city = (City) s.get(City.class, Integer.valueOf(cityselect));

                    if (city == null) {
                        responseObject.addProperty("message", "Invalid City Name");
                    } else {
                        if (line1.isEmpty()) {
                            responseObject.addProperty("message", "Address Line One is Requried");
                        } else if (line2.isEmpty()) {
                            responseObject.addProperty("message", "Address Line two is Requried");
                        } else if (postalCose.isEmpty()) {
                            responseObject.addProperty("message", "Postal Code is Requried");
                        } else if (!Util.isCodeValid(postalCose)) {
                            responseObject.addProperty("message", "Invalid Postal Code ");
                        } else if (mobile.isEmpty()) {
                            responseObject.addProperty("message", "Mobile Number is Requried");
                        } else if (!Util.isMobileValid(mobile)) {
                            responseObject.addProperty("message", "Invalid Mobile Number ");
                        } else {
                            Address address = new Address();
                            address.setFname(fname);
                            address.setLname(lname);
                            address.setLine1(line1);
                            address.setLine2(line2);
                            address.setCity(city);
                            address.setPostalCode(postalCose);
                            address.setMobile(mobile);
                            address.setUser(user);
                            s.save(address);
                            processCheckout(s, tr, user, address, responseObject);
                        }
                    }
                }
            }
        }

        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);

    }

    private void processCheckout(Session s, Transaction transaction, User user, Address address, JsonObject responseObject) {
        try {

            Order order = new Order();
            order.setAddress(address);
            order.setOrderAt(new Date());
            order.setUser(user);

            int orderid = (int) s.save(order);
            Criteria c1 = s.createCriteria(Cart.class);
            c1.add(Restrictions.eq("user", user));
            List<Cart> cartList = c1.list();

            OrderStatus orderStatus = (OrderStatus) s.get(OrderStatus.class, ORDER_PENDING);
            DeliveryType withinColombo = (DeliveryType) s.get(DeliveryType.class, WITHIN_GCOLOMBO);
            DeliveryType outOfColombo = (DeliveryType) s.get(DeliveryType.class, OUT_OF_COLOMBO);

            double ammount = 0;
            String Items = "";

            for (Cart cart : cartList) {
                ammount += cart.getQty() * cart.getProduct().getPrice();
                OrderItem orderItem = new OrderItem();

                if (address.getCity().getCity() == "Colombo") {

                    ammount +=  withinColombo.getPrice();
                    orderItem.setDeliveryType(withinColombo);

                } else { // out of colombo

                    ammount += outOfColombo.getPrice();
                    orderItem.setDeliveryType(outOfColombo);
                }

                Items += cart.getProduct().getTitle() + "X" + cart.getQty() + ",";

                Product product = cart.getProduct();
                orderItem.setOrderStatus(orderStatus);
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQty(cart.getQty());
                orderItem.setRating(RATING_DEFAULT_VALUE);

                s.save(orderItem);

                //Update Product
                product.setQty(product.getQty() - cart.getQty());
                s.update(product);

                //Delete cart item
                s.delete(cart);
            }
            transaction.commit();

            //pay hear process
            String merchantId = "1225859";
            String merchantSecret = "NTQzNTQ3ODE2MTQwNjkwOTE0NDE3NDY1MDMyMDkyOTI3OTg2NTgy";
            String orderId = "#000" + orderid;
            String currency = "LKR";
            String formattedAmount = new DecimalFormat("0.00").format(ammount);
            String merchantSecretMD5 = PayHear.generateMD5(merchantSecret);
            String hash = PayHear.generateMD5(merchantId + orderId + formattedAmount + currency + merchantSecretMD5);

            JsonObject PayHereJson = new JsonObject();
            PayHereJson.addProperty("sandbox", true);
            PayHereJson.addProperty("merchant_id", merchantId);

            PayHereJson.addProperty("return_url", "");
            PayHereJson.addProperty("cancel_url", "");
            PayHereJson.addProperty("notify_url", " https://c6e2a9629fea.ngrok-free.app/NexCart/VerifyPayment");

            PayHereJson.addProperty("order_id", orderId);
            PayHereJson.addProperty("items", Items);
            PayHereJson.addProperty("amount", ammount);
            PayHereJson.addProperty("currency", "LKR");
            PayHereJson.addProperty("hash", hash);

            PayHereJson.addProperty("first_name", user.getFristName());
            PayHereJson.addProperty("last_name", user.getLastName());
            PayHereJson.addProperty("email", user.getEmail());

            PayHereJson.addProperty("phone", address.getMobile());
            PayHereJson.addProperty("address", address.getLine1() + address.getLine2());
            PayHereJson.addProperty("city", address.getCity().getCity());
            PayHereJson.addProperty("country", "Sri Lanka");

            responseObject.addProperty("status", Boolean.TRUE);
            responseObject.addProperty("message", "Checkout completed");
            responseObject.add("payHereJson", new Gson().toJsonTree(PayHereJson));

        } catch (Exception e) {
            transaction.rollback();
        }
    }

}
