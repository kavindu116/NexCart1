
package controller;

import Hibernate.Category;
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
import model.SubCategory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;


@WebServlet(name = "AddSubCategory", urlPatterns = {"/AddSubCategory"})
public class AddSubCategory extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

        String subcategory = requestObject.get("SubcategoryName").getAsString();
        System.out.println("ok");
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (subcategory.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Sub Category Name");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(SubCategory.class);
            c.add(Restrictions.eq("name", subcategory));

            if (!c.list().isEmpty()) {
                responseObject.addProperty("message", "This Sub Category Alredy Exsist");
            } else {
                SubCategory subcategory1 = new SubCategory();
                subcategory1.setName(subcategory);

                s.save(subcategory1);
                s.beginTransaction().commit();
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", " Sub Category Registerd");
            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

}
