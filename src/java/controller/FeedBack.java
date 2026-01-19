package controller;

import Hibernate.City;
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
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;

@WebServlet(name = "FeedBack", urlPatterns = {"/FeedBack"})
public class FeedBack extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject user = gson.fromJson(request.getReader(), JsonObject.class);

        String name = user.get("name").getAsString();
        String email = user.get("email").getAsString();
        String rangeValue = user.get("rangeValue").getAsString();
        String message = user.get("message").getAsString();

        System.out.println(name);
        System.out.println(email);
        System.out.println(rangeValue);
        System.out.println(message);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (name.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Your Name");
        } else if (email.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Your Email");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please Enter Valid Email Address");
        } else if (message.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Your FeedBack");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            User u = (User) request.getSession().getAttribute("user");
//            
            if (u == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                Hibernate.FeedBack feedBack = new Hibernate.FeedBack();
                feedBack.setName(name);
                feedBack.setEmail(email);
                feedBack.setMessage(message);
                feedBack.setRate(Integer.parseInt(rangeValue));
                feedBack.setUser(u);

                s.save(feedBack);
                s.beginTransaction().commit();

                responseObject.addProperty("message", "Thank you for Youer Valuable FeedBack.. ");
                responseObject.addProperty("status", true);

            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria c = s.createCriteria(Hibernate.FeedBack.class);
        List<Hibernate.FeedBack> feedBacks = c.list();
        c.addOrder(Order.desc("id"));
        c.setMaxResults(3);

        Gson gson = new Gson();
        responseObject.add("feedBacks", gson.toJsonTree(feedBacks));

//        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
