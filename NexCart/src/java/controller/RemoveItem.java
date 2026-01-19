package controller;

import Hibernate.Cart;
import Hibernate.HibernateUtil;
import Hibernate.Product;
import Hibernate.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "RemoveItem", urlPatterns = {"/RemoveItem"})
public class RemoveItem extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false); // default

        String productIdStr = request.getParameter("id");

        HttpSession httpSession = request.getSession(false);
        User userObj = (User) httpSession.getAttribute("user");

        if (Util.isInteger(productIdStr) && userObj != null) {
            int productId = Integer.parseInt(productIdStr);
            int userId = userObj.getId();

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tx = null;

            try {
                tx = s.beginTransaction();

                // Get all cart items (if not using query or criteria)
                List<Cart> allCartItems = s.createCriteria(Cart.class).list();

                // Filter manually to find match
                Cart targetCartItem = null;
                for (Cart item : allCartItems) {
                    if (item.getUser().getId() == userId && item.getProduct().getId() == productId) {
                        targetCartItem = item;
                        break;
                    }
                }

                if (targetCartItem != null) {
                    s.delete(targetCartItem); // Hibernate method
                    tx.commit();
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Product removed from cart.");
                } else {
                    responseObject.addProperty("message", "Item not found in cart.");
                }

            } catch (Exception e) {
                if (tx != null) {
                    tx.rollback();
                }
                e.printStackTrace();
                responseObject.addProperty("message", "Error: " + e.getMessage());
            } finally {
                s.close();
            }

        } else {
            responseObject.addProperty("message", "Invalid product ID or user session.");
        }

        response.setContentType("application/json");
        response.getWriter().print(gson.toJson(responseObject));
    }

}
