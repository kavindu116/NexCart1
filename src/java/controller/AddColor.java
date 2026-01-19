
package controller;

import Hibernate.Color;
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;


@WebServlet(name = "AddColor", urlPatterns = {"/AddColor"})
public class AddColor extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

        String color = requestObject.get("colorName").getAsString();
        System.out.println("ok");
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (color.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Color Name");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(Color.class);
            c.add(Restrictions.eq("value", color));

            if (!c.list().isEmpty()) {
                responseObject.addProperty("message", "This Color is Alredy Exsist");
            } else {
                Color color1 = new Color();
                color1.setValue(color);

                s.save(color1);
                s.beginTransaction().commit();
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", " Color Registerd");
            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

    
}
