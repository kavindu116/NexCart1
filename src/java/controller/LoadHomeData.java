package controller;

import Hibernate.Category;
import Hibernate.Product;
import Hibernate.Status;
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
import model.Brand;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadHomeData", urlPatterns = {"/LoadHomeData"})
public class LoadHomeData extends HttpServlet {
    
    private static final int ACTIVE_STATUS_ID = 1;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);
        
        SessionFactory sf = Hibernate.HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        //brand
//        Criteria c1 = s.createCriteria(Brand.class);
//        List<Brand> brandList = c1.list();
//        responseObject.add("brandList", gson.toJsonTree(brandList));
//Categorys
        Criteria c1 = s.createCriteria(Category.class);
        List<Category> categoryList = c1.list();
        responseObject.add("categoryList", gson.toJsonTree(categoryList));

        //product
        Criteria c2 = s.createCriteria(Product.class);
        c2.addOrder(Order.desc("title"));
        
        Status status = (Status) s.get(Status.class, ACTIVE_STATUS_ID);
        c2.add(Restrictions.eq("status", status));
        
        c2.setFirstResult(0);
        c2.setMaxResults(10);
        
        List<Product> productList = c2.list();
        for (Product product : productList) {
            product.setUser(null);
        }
        responseObject.add("productList", gson.toJsonTree(productList));
        
        responseObject.addProperty("status", Boolean.TRUE);
        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
    }
    
}
