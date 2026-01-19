package controller;

import Hibernate.HibernateUtil;
import Hibernate.Order;
import Hibernate.OrderItem;
import Hibernate.Product;
import Hibernate.Size;
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
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AccountDetails", urlPatterns = {"/AccountDetails"})
public class AccountDetails extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseObject = new JsonObject();

        HttpSession ses = request.getSession(false);
        if (ses != null && ses.getAttribute("user") != null) {

            User u = (User) ses.getAttribute("user");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Gson gson = new Gson();

            Criteria c = s.createCriteria(Order.class);
            c.add(Restrictions.eq("user", u));

            if (!c.list().isEmpty()) {
                List<Order> orderList = c.list();

                for (Order order : orderList) {
                    order.setAddress(null);
                    //order.setUser(null);
                }

                responseObject.add("orderList", gson.toJsonTree(orderList));
            }

            Criteria c1 = s.createCriteria(OrderItem.class);
           
            c1.addOrder(org.hibernate.criterion.Order.desc("id"));
            List<OrderItem> orderItemList = c1.list();

            responseObject.add("orderItemList", gson.toJsonTree(orderItemList));

            Criteria c2 = s.createCriteria(Product.class);
            c2.add(Restrictions.eq("user", u));

            if (!c2.list().isEmpty()) {

               

                c2.setMaxResults(6);
                c2.addOrder(org.hibernate.criterion.Order.desc("id"));
                List<Product> productList = c2.list();

               

                responseObject.add("productList", gson.toJsonTree(productList));
            }

            responseObject.addProperty("allProductCount", c2.list().size());
            responseObject.addProperty("allOrderCount", c.list().size());

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseObject));

        }

    }

}
