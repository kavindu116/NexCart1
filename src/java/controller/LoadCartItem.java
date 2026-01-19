
package controller;

import Hibernate.Cart;
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;


@WebServlet(name = "LoadCartItem", urlPatterns = {"/LoadCartItem"})
public class LoadCartItem extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        User user = (User) request.getSession().getAttribute("user");
        
         if (user != null) {//db cart
            SessionFactory sf = Hibernate.HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c1 = s.createCriteria(Cart.class);
            c1.add(Restrictions.eq("user", user));
            List<Cart> cartList = c1.list();
            if (cartList.isEmpty()) {
                responseObject.addProperty("message", "Your Cart is Empty");
            } else {
                for (Cart cart : cartList) {
                    cart.getProduct().setUser(null);
                    cart.setUser(null);
                }

                responseObject.addProperty("status", Boolean.TRUE);
                responseObject.addProperty("message", "Cart Items Load Success");
                responseObject.add("cartItems", gson.toJsonTree(cartList));
            }

        } else {//session cart

            ArrayList<Cart> sessioncart = (ArrayList<Cart>) request.getSession().getAttribute("sessionCart");
            if (sessioncart != null) {
                if (sessioncart.isEmpty()) {
                    responseObject.addProperty("message", "Your Cart is Empty");
                } else {
                    for (Cart cart : sessioncart) {
                        cart.getProduct().setUser(null);
                        cart.setUser(null);
                    }
                    responseObject.addProperty("status", Boolean.TRUE);
                    responseObject.addProperty("message", "Cart Items Load Success");
                    responseObject.add("cartItems", gson.toJsonTree(sessioncart));
                }
            }else{
               responseObject.addProperty("message", "Cart Is empty");  
            }

        }

        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);

    }
        
    }


