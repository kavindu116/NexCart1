package controller;

import Hibernate.Category;
import Hibernate.Color;
import Hibernate.HibernateUtil;
import Hibernate.Metirial;
import Hibernate.Product;
import Hibernate.Size;
import Hibernate.Status;
import Hibernate.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Brand;
import model.SubCategory;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Shaik Sameer
 */
@MultipartConfig
@WebServlet(name = "SaveProduct", urlPatterns = {"/SaveProduct"})
public class SaveProduct extends HttpServlet {

    private static final int PENDING_STATUS_ID = 1;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String Pname = request.getParameter("Pname");
        String discription = request.getParameter("discription");
        String category = request.getParameter("category");
        String subcategory = request.getParameter("subcategory");
        String brand = request.getParameter("brand");
        String metirial = request.getParameter("metirial");
        String price = request.getParameter("price");
        String qty = request.getParameter("qty");
        String size = request.getParameter("size");
        String color = request.getParameter("color");

        Part part1 = request.getPart("img1");
        Part part2 = request.getPart("img2");
        Part part3 = request.getPart("img3");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        //Validation
        if (Pname.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Product Name");
        } else if (discription.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Product Discription");
        } else if (request.getSession().getAttribute("user") == null) {
            responseObject.addProperty("message", "User Not Found! Please Singn Frist");
        } else if (!Util.isInteger(subcategory)) {
            responseObject.addProperty("message", "Invalid Sub Category");
        } else if (Integer.parseInt(category) == 0) {
            responseObject.addProperty("message", "Please Select Categoory");
        } else if (Integer.parseInt(subcategory) == 0) {
            responseObject.addProperty("message", "Please Select Sub Categoory");
        } else if (Integer.parseInt(brand) == 0) {
            responseObject.addProperty("message", "Please Select Brand");
        } else if (!Util.isInteger(brand)) {
            responseObject.addProperty("message", "Invalid Brand");
        } else if (Integer.parseInt(metirial) == 0) {
            responseObject.addProperty("message", "Please Select Metirial");
        } else if (!Util.isInteger(brand)) {
            responseObject.addProperty("message", "Invalid Metirial");
        } else if (!Util.isDouble(price)) {
            responseObject.addProperty("message", "Please Enter Valid price");
        } else if (Double.parseDouble(price) <= 0) {
            responseObject.addProperty("message", "Price must be greater than 0");
        } else if (!Util.isInteger(qty)) {
            responseObject.addProperty("message", "Please Enter Valid Quantity");
        } else if (Integer.parseInt(qty) <= 0) {
            responseObject.addProperty("message", "Quantity must be greater than 0");
        } else if (!Util.isInteger(size)) {
            responseObject.addProperty("message", "Invalid Size");
        } else if (Integer.parseInt(size) == 0) {
            responseObject.addProperty("message", "Please Select Size");
        } else if (!Util.isInteger(color)) {
            responseObject.addProperty("message", "Invalid Color");
        } else if (Integer.parseInt(color) == 0) {
            responseObject.addProperty("message", "Please Select Color");
        } else if (part1.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "product image 1 is requerd");
        } else if (part2.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "product image 2 is requerd");
        } else if (part3.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "product image 3 is requerd");
        } else {
            Category category1 = (Category) s.get(Category.class, Integer.parseInt(category));

            if (category1 == null) {
                responseObject.addProperty("message", "Invalid Category");
            } else {
                SubCategory subcategory1 = (SubCategory) s.get(SubCategory.class, Integer.parseInt(subcategory));

                if (subcategory1 == null) {
                    responseObject.addProperty("message", "Invalid Sub Category");
                } else {
                    Brand brand1 = (Brand) s.get(Brand.class, Integer.parseInt(brand));
                    if (brand1 == null) {
                        responseObject.addProperty("message", "Invalid Brand");
                    } else {
                        Metirial metirial1 = (Metirial) s.get(Metirial.class, Integer.parseInt(metirial));
                        if (metirial1 == null) {
                            responseObject.addProperty("message", "Invalid Metirial");
                        } else {
                            Size size1 = (Size) s.get(Size.class, Integer.parseInt(size));
                            if (size1 == null) {
                                responseObject.addProperty("message", "Invalid Size");
                            } else {
                                Color color1 = (Color) s.get(Color.class, Integer.parseInt(color));

                                if (color1 == null) {
                                    responseObject.addProperty("message", "Invalid Color");
                                } else {

                                    Status status = (Status) s.load(Status.class, SaveProduct.PENDING_STATUS_ID);
                                    User user = (User) request.getSession().getAttribute("user");
                                    
                                    Criteria c1 = s.createCriteria(User.class);
                                    c1.add(Restrictions.eq("email", user.getEmail()));
                                    User u1 = (User) c1.uniqueResult();
                                    
                                    Product p = new Product();
                                    p.setTitle(Pname);
                                    p.setDiscription(discription);
                                    p.setPrice(Double.parseDouble(price));
                                    p.setQty(Integer.parseInt(qty));
                                    p.setRegisterdAt(new Date());
                                    p.setUser(u1);
                                    p.setColor(color1);
                                    p.setStatus(status);
                                    p.setMetirial(metirial1);
                                    p.setCategory(category1);
                                    p.setSize(size1);
                                    p.setSubCategory(subcategory1);
                                    p.setBrand(brand1);

                                    int id = (int) s.save(p);
                                    s.beginTransaction().commit();
                                    s.close();

                                    //image uploding
                                    String appPath = getServletContext().getRealPath(""); //  = C:\Users\Shaik Sameer\OneDrive\Documents\NetBeansProjects\SmartTrade\build\web|#]

                                    String newPath = appPath.replace("build\\web", "web\\Product-Image"); // = C:\Users\Shaik Sameer\OneDrive\Documents\NetBeansProjects\NexCart\web\Product-Image

                                    File productFolder = new File(newPath, String.valueOf(id));
                                    productFolder.mkdir();

                                    File file1 = new File(productFolder, "image1.png");
                                    Files.copy(part1.getInputStream(), file1.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                    File file2 = new File(productFolder, "image2.png");
                                    Files.copy(part2.getInputStream(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                    File file3 = new File(productFolder, "image3.png");
                                    Files.copy(part3.getInputStream(), file3.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    //image uploding 
                                    
                                    responseObject.addProperty("status", true);
                                     responseObject.addProperty("message", "The Product Has Been Succesfully Registerd.");
                                }
                            }
                        }
                    }
                }
            }

        }
        //Validation


    
        //Send Response
        Gson gson = new Gson();
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }

}
