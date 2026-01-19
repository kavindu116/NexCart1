package controller;

import Hibernate.Address;
import Hibernate.Cart;
import Hibernate.City;
import Hibernate.DeliveryType;
import Hibernate.HibernateUtil;
import Hibernate.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "loadCheckOutData", urlPatterns = {"/loadCheckOutData"})
public class loadCheckOutData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        User Sesuser = (User) request.getSession().getAttribute("user");

        if (Sesuser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c1 = s.createCriteria(Address.class);
            c1.add(Restrictions.eq("user", Sesuser));
            c1.addOrder(Order.desc("id"));

            //address ek register d baluwa ekak hari thiyeda kiyala
            if (c1.list().isEmpty()) {
                responseObject.addProperty("message", "Your Account deatails are incomplete.please Filling youer shipping address");
            } else {
                //anthimat update karapu address eka gaththa
                Address address = (Address) c1.list().get(0);
                address.getUser().setEmail(null);
                address.getUser().setPassword(null);
                address.getUser().setVerificationCode(null);
                address.getUser().setId(-1);
                address.getUser().setRegisterdAt(null);
                address.getUser().setOTP(null);
                responseObject.addProperty("status", Boolean.TRUE);
                responseObject.add("userAddress", gson.toJsonTree(address));

            }

            //getAll city data
            Criteria c2 = s.createCriteria(City.class);
            c2.addOrder(Order.asc("city"));
            List<City> cityList = c2.list();
            responseObject.add("cityList", gson.toJsonTree(cityList));

            //Cart data
            Criteria c3 = s.createCriteria(Cart.class);
            c3.add(Restrictions.eq("user", Sesuser));
            List<Cart> cartList = c3.list();

            if (cartList.isEmpty()) {
                responseObject.addProperty("message", "Youer Cart is Empty!");
            } else {
                for (Cart cart : cartList) {
                    cart.setUser(null);
                    cart.getProduct().setUser(null);
                    
                }

                Criteria c4 = s.createCriteria(DeliveryType.class);
                List<DeliveryType> deliveryTypeList = c4.list();

                responseObject.addProperty("status", Boolean.TRUE);
                responseObject.add("cartList", gson.toJsonTree(cartList));
                responseObject.add("deliveryTypeList", gson.toJsonTree(deliveryTypeList));
            }

        }

        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
    }

}
