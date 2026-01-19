package controller;

import Hibernate.Category;
import Hibernate.Color;
import Hibernate.Metirial;
import Hibernate.Product;
import Hibernate.Size;
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "Search_product", urlPatterns = {"/Search_product"})
public class Search_product extends HttpServlet {

    private static final int MAX_RESULT = 8;
    private static final int ACTIVE_ID = 1;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

        SessionFactory sf = Hibernate.HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        //getAll Product Details
        Criteria c = s.createCriteria(Product.class);

        if (requestObject.has("category")) {

            String cayegoryName = requestObject.get("category").getAsString();

            Criteria c2 = s.createCriteria(Category.class);
            c2.add(Restrictions.eq("name", cayegoryName));
            Category category = (Category) c2.uniqueResult();

            List<Category> categoryList = c2.list();

            Criteria c3 = s.createCriteria(SubCategory.class);
            c3.add(Restrictions.eq("category", category));

            c.add(Restrictions.in("category", categoryList));

        }
        
         if (requestObject.has("Subcategory")) {

            String subcayegoryName = requestObject.get("Subcategory").getAsString();

            Criteria c6 = s.createCriteria(SubCategory.class);
            c6.add(Restrictions.eq("name", subcayegoryName));
            SubCategory subcategory1 = (SubCategory) c6.uniqueResult();

      

             c.add(Restrictions.eq("subCategory", subcategory1));

        }
         
         if (requestObject.has("brand")) {

            String brandName = requestObject.get("brand").getAsString();

            Criteria c7 = s.createCriteria(Brand.class);
            c7.add(Restrictions.eq("name", brandName));
            Brand brand = (Brand) c7.uniqueResult();

      

             c.add(Restrictions.eq("brand", brand));

        }

        if (requestObject.has("metirial")) {

            String metirial = requestObject.get("metirial").getAsString();

            Criteria c4 = s.createCriteria(Metirial.class);
            c4.add(Restrictions.eq("name", metirial));
            Metirial metirial1 = (Metirial) c4.uniqueResult();

            c.add(Restrictions.eq("metirial", metirial1));

        }

        if (requestObject.has("color")) {
            //----------------Color------------------------------
            String colorValue = requestObject.get("color").getAsString();

            //get Color details
            Criteria c5 = s.createCriteria(Color.class);
            c5.add(Restrictions.eq("value", colorValue));
            Color color = (Color) c5.uniqueResult();

            //filter product using color
            c.add(Restrictions.eq("color", color));

            //----------------Color End--------------------------
        }

        if (requestObject.has("size")) {

            String size = requestObject.get("size").getAsString();

            Criteria c6 = s.createCriteria(Size.class);
            c6.add(Restrictions.eq("value", size));
            Size size1 = (Size) c6.uniqueResult();

            c.add(Restrictions.eq("size", size1));

        }

        if (requestObject.has("min_price") & requestObject.has("Max_price")) {

            double priceStart = requestObject.get("min_price").getAsDouble();
            double priceEnd = requestObject.get("Max_price").getAsDouble();

            c.add(Restrictions.ge("price", priceStart));
            c.add(Restrictions.le("price", priceEnd));
        }

        if (requestObject.has("sort_value")) {

            String sortValue = requestObject.get("sort_value").getAsString();
            if (sortValue.equals("Featured")) {
                c.addOrder(Order.desc("id"));
            } else if (sortValue.equals("Price: Low to High")) {
                c.addOrder(Order.asc("price"));
            } else if (sortValue.equals("Price: High to Low")) {
                c.addOrder(Order.desc("price"));
            } else if (sortValue.equals("Sort by Oldest")) {
                c.addOrder(Order.asc("id"));
            } else if (sortValue.equals("Sort by Name")) {
                c.addOrder(Order.asc("title"));
            }
        }

        responseObject.addProperty("allProductCount", c.list().size());

        if (requestObject.has("FristResult")) {
            int fristResult = requestObject.get("FristResult").getAsInt();
            c.setFirstResult(fristResult);
            c.setMaxResults(Search_product.MAX_RESULT);
        }

        Status status = (Status) s.get(Status.class, ACTIVE_ID);//get Active Product
        c.add(Restrictions.eq("status", status));
        List<Product> productList = c.list();

        for (Product product : productList) {
            product.setUser(null);
        }

        //hibernate close
        s.close();

        responseObject.add("productList", gson.toJsonTree(productList));
        responseObject.addProperty("status", true);
        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);

    }

}
