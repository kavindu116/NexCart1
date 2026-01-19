package controller;


import Hibernate.Category;
import Hibernate.Color;
import Hibernate.HibernateUtil;
import Hibernate.Metirial;
import Hibernate.Size;

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

@WebServlet(name = "LoadProductData", urlPatterns = {"/LoadProductData"})
public class LoadProductData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseObject = new JsonObject();

        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        
        Criteria c = s.createCriteria(Size.class);
        List<Size> sizeList = c.list();
        
        Criteria c1 = s.createCriteria(Category.class);
        List<Category>categoryList = c1.list();
        
        Criteria c2 =s.createCriteria(Metirial.class);
        List<Metirial> metirialList = c2.list();
        
        Criteria c3 = s.createCriteria(SubCategory.class);
        List<SubCategory> subCategoryList = c3.list();
        
       
//        
        Criteria c4 = s.createCriteria(Brand.class);
        List<Brand> brandList = c4.list();
        
        
        Criteria c5 = s.createCriteria(Color.class);
        List<Color> colorList = c5.list();
        
        for (Color color : colorList) {
            System.out.println(color.getValue());
        }
        
        s.close();

        Gson gson = new Gson();
        
        responseObject.addProperty("status", true);
        
        responseObject.add("brandList", gson.toJsonTree(brandList));
        responseObject.add("sizeList", gson.toJsonTree(sizeList));
        responseObject.add("categoryList", gson.toJsonTree(categoryList));
        responseObject.add("metirialList", gson.toJsonTree(metirialList));
        responseObject.add("subCategoryList", gson.toJsonTree(subCategoryList));
        responseObject.add("colorList", gson.toJsonTree(colorList));
//        
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
         
    }

}



