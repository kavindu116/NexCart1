
package controller;

import Hibernate.HibernateUtil;
import Hibernate.Metirial;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Brand;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;


@WebServlet(name = "AddMetirial", urlPatterns = {"/AddMetirial"})
public class AddMetirial extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       Gson gson = new Gson();
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

        String metirial = requestObject.get("MetirialName").getAsString();
        System.out.println("ok");
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (metirial.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Metirial Name");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(Metirial.class);
            c.add(Restrictions.eq("name", metirial));

            if (!c.list().isEmpty()) {
                responseObject.addProperty("message", "This Metirial is Alredy Exsist");
            } else {
                Metirial metirial1 = new Metirial();
                metirial1.setName(metirial);

                s.save(metirial1);
                s.beginTransaction().commit();
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", " Metirial Registerd");
            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

    

}
