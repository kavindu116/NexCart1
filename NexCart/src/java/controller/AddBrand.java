
package controller;

import Hibernate.HibernateUtil;
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
import model.SubCategory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;


@WebServlet(name = "AddBrand", urlPatterns = {"/AddBrand"})
public class AddBrand extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

        String brand = requestObject.get("brandName").getAsString();
        System.out.println("ok");
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (brand.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Brand Name");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(Brand.class);
            c.add(Restrictions.eq("name", brand));

            if (!c.list().isEmpty()) {
                responseObject.addProperty("message", "This Brand Alredy Exsist");
            } else {
                Brand brand1 = new Brand();
                brand1.setName(brand);

                s.save(brand1);
                s.beginTransaction().commit();
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", " Brand Registerd");
            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

}
