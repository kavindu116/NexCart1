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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@WebServlet(name = "DeleteCategory", urlPatterns = {"/DeleteCategory"})
public class DeleteCategory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();

        String categoryId = request.getParameter("id");

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        int id = Integer.parseInt(categoryId);

        Transaction tx = null;

        try {
            tx = s.beginTransaction();

            // Load the Category object by ID
            Category category = (Category) s.get(Category.class, id);

            if (category != null) {
                s.delete(category);
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Category deleted successfully");
            } else {
                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Category not found");
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Error deleting category: " + e.getMessage());
        } finally {
            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().print(gson.toJson(responseObject));

    }

}
