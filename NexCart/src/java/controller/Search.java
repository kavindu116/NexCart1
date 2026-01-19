package controller;

import Hibernate.HibernateUtil;
import Hibernate.Product;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "Search", urlPatterns = {"/Search"})
public class Search extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);
        String searchText = requestObject.get("searchText").getAsString();

        JsonObject responseObject = new JsonObject();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();

        try {
            Criteria criteria = session.createCriteria(Product.class);
            criteria.add(Restrictions.ilike("title", searchText,MatchMode.ANYWHERE)); // case-insensitive LIKE

            List<Product> productList = criteria.list();

            responseObject.addProperty("status", true);
             responseObject.addProperty("allProductCount", criteria.list().size());
            responseObject.add("productList", gson.toJsonTree(productList));

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("status", false);
        } finally {
            session.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(responseObject.toString());
    }

}
