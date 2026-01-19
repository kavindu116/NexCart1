package controller;

import Hibernate.Category;
import Hibernate.HibernateUtil;
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
import model.SubCategory;

import model.Util;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadSingleProduct", urlPatterns = {"/LoadSingleproduct"})
public class LoadSingleProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        String productId = request.getParameter("id");

        if (Util.isInteger(productId)) {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            try {
                Product product = (Product) s.get(Product.class, Integer.valueOf(productId));
                //load Similer Product
                Criteria c2 = s.createCriteria(Product.class);
                c2.add(Restrictions.ne("id", product.getId()));
                c2.createAlias("status", "s"); 


                c2.add(Restrictions.eq("s.value", "Active"));
                
               
                

                c2.setMaxResults(6);
                List<Product> productList = c2.list();
                
                
                if (product.getStatus().getValue().equals("Active")) {

                    product.getUser().setEmail(null);
                    product.getUser().setPassword(null);
                    product.getUser().setVerificationCode(null);
                    product.getUser().setId(-1);
                    product.getUser().setRegisterdAt(null);
                    product.getUser().setOTP(null);

                    responseObject.add("productList", gson.toJsonTree(productList));

                    responseObject.add("product", gson.toJsonTree(product));

                    responseObject.addProperty("status", true);

                } else {
                    responseObject.addProperty("message", "Product not Found");
                }

            } catch (NumberFormatException | HibernateException e) {
                responseObject.addProperty("message", "Product not Found");
            }

            String responseText = gson.toJson(responseObject);
            response.setContentType("application/json");
            response.getWriter().write(responseText);
        }
    }
}
