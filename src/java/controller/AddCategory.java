package controller;

import Hibernate.Category;
import Hibernate.City;
import Hibernate.HibernateUtil;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddCategory", urlPatterns = {"/AddCategory"})
public class AddCategory extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

        String category = requestObject.get("categoryName").getAsString();
        System.out.println("ok");
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (category.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Category Name");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(Category.class);
            c.add(Restrictions.eq("name", category));

            if (!c.list().isEmpty()) {
                responseObject.addProperty("message", "This Category Alredy Exsist");
            } else {
                Category category1 = new Category();
                category1.setName(category);

                s.save(category1);
                s.beginTransaction().commit();
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", " Category Registerd");
            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        
        Criteria c = s.createCriteria(Category.class);
        List<Category> categoryList = c.list();
        
        Gson gson = new Gson();
        String toJson = gson.toJson(categoryList);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
        s.close();
    }

}
