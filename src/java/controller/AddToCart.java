package controller;

import Hibernate.Cart;
import Hibernate.HibernateUtil;
import Hibernate.Product;
import Hibernate.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String Pid = request.getParameter("prId");
        String qty = request.getParameter("qty");

        System.out.println(Pid);
        System.out.println(qty);

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (!Util.isInteger(Pid)) {
            responseObject.addProperty("message", "Invalid Product");
        } else if (!Util.isInteger(qty)) {
            responseObject.addProperty("message", "Invalid Product quantity!");
        } else {
//Add To Cart process
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tr = s.beginTransaction();

            Product product = (Product) s.get(Product.class, Integer.valueOf(Pid));

            if (product == null) {
                responseObject.addProperty("message", "Product is not found");
            } else { // product avalible 

                User user = (User) request.getSession().getAttribute("user");

                if (user != null) { //addproduct db m=user found in session
                    Criteria c1 = s.createCriteria(Cart.class);
                    c1.add(Restrictions.eq("user", user));

                    c1.add(Restrictions.eq("product", product));

                    if (c1.list().isEmpty()) {// no product in same pid

                        if (Integer.parseInt(qty) <= product.getQty()) {

                            Cart cart = new Cart();
                            cart.setQty(Integer.parseInt(qty));
                            cart.setProduct(product);
                            cart.setUser(user);

                            s.save(cart);
                            tr.commit();
                            responseObject.addProperty("status", Boolean.TRUE);
                            responseObject.addProperty("message", "Product has Been Added Into a Cart");
                        } else {
                            responseObject.addProperty("message", "OOPS! Insufficient Product quantity");
                        }
                    } else {//product availble
                        Cart cart = (Cart) c1.uniqueResult();
                        int newQty = cart.getQty() + Integer.parseInt(qty);

                        if (newQty <= Integer.parseInt(qty)) {
                            cart.setQty(newQty);
                            s.update(cart);
                            tr.commit();
                            responseObject.addProperty("status", Boolean.TRUE);
                            responseObject.addProperty("message", "Product has Been Added Into a Cart");
                        } else {
                            responseObject.addProperty("message", "OOPS! Insufficient Product quantity");
                        }

                    }

                } else { // add product session = user not found in session
                    HttpSession ses = request.getSession();
                    if (ses.getAttribute("sessionCart") == null) {
                        if (Integer.parseInt(qty) <= product.getQty()) {
                            List<Cart> sesCart = new ArrayList<>();
                            Cart cart = new Cart();
                            cart.setQty(Integer.parseInt(qty));
                            cart.setUser(null);
                            cart.setProduct(product);
                            sesCart.add(cart);
                            ses.setAttribute("sessionCart", sesCart);
                            responseObject.addProperty("status", true);
                            responseObject.addProperty("message", "Product has Been Added Into a Cart");
                        } else {
                            responseObject.addProperty("message", "OOPS! Insufficient Product quantity");
                        }

                    } else {

                        ArrayList<Cart> sessionList = (ArrayList<Cart>) ses.getAttribute("sessionCart");
                        Cart newCart = null;

                        for (Cart cart : sessionList) {
                            if (cart.getProduct().getId() == product.getId()) {
                                newCart = cart;
                                break;

                            }
                        }

                        if (newCart != null) {
                            int newQty = newCart.getQty() + Integer.parseInt(qty);

                            if (newQty <= product.getQty()) {
                                newCart.setQty(newQty);
                                responseObject.addProperty("status", true);
                                responseObject.addProperty("message", "Product has Been Added Into a Cart");
                            } else {
                                responseObject.addProperty("message", "OOPS! Insufficient Product quantity");
                            }

                        } else {

                            if (Integer.parseInt(qty) <= product.getQty()) {

                                newCart = new Cart();
                                newCart.setQty(Integer.parseInt(qty));
                                newCart.setUser(null);
                                newCart.setProduct(product);
                                sessionList.add(newCart);
                                responseObject.addProperty("status", true);
                                responseObject.addProperty("message", "Product has Been Added Into a Cart");
                            } else {
                                responseObject.addProperty("message", "OOPS! Insufficient Product quantity");
                            }
                        }

                    }
                }

            }

        }

        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);

    }

}
