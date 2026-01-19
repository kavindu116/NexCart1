/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import Hibernate.Address;
import Hibernate.City;
import Hibernate.HibernateUtil;
import Hibernate.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Shaik Sameer
 */
@WebServlet(name = "saveChanges", urlPatterns = {"/saveChanges"})
public class saveChanges extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(request.getReader(), JsonObject.class);

        String fname = userData.get("fristName").getAsString();
        String lname = userData.get("lastName").getAsString();
        String line1 = userData.get("line1").getAsString();
        String line2 = userData.get("line2").getAsString();
        String posCode = userData.get("posCode").getAsString();
        int cityId = userData.get("cityId").getAsInt();
        String currentPassword = userData.get("currentPassword").getAsString();
        String newPassword = userData.get("newPassword").getAsString();
        String confirmPassword = userData.get("confirmPassword").getAsString();

        System.out.println(fname);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (fname.isEmpty()) {
            responseObject.addProperty("message", "Frist Name Can Not Be Empty");
        } else if (lname.isEmpty()) {
            responseObject.addProperty("message", "Last Name Can Not Be Empty");
        } else if (line1.isEmpty()) {
            responseObject.addProperty("message", "Line 1 Can Not Be Empty");
        } else if (line2.isEmpty()) {
            responseObject.addProperty("message", "Line 2 cant be Empty");
        } else if (posCode.isEmpty()) {
            responseObject.addProperty("message", "PostalCode Can Not Be Empty");
        } else if (!Util.isCodeValid(posCode)) {
            responseObject.addProperty("message", "Enter Correct PostalCode");
        } else if (cityId == 0) {
            responseObject.addProperty("message", "Select City ");
        } else if (currentPassword.isEmpty()) {
            responseObject.addProperty("message", "please Enter Current Password");
        } else if (!Util.isPasswordValid(currentPassword)) {
            responseObject.addProperty("message", "The password must contains at least Uppercase,Lowecase Number,"
                    + "Special Character and to be minimum 8 Characters Long! ");
        } else if (!newPassword.isEmpty() && !Util.isPasswordValid(newPassword)) {
            responseObject.addProperty("message", "The password must contains at least Uppercase,Lowecase Number,"
                    + "Special Character and to be minimum 8 Characters Long! ");
        } else if (!confirmPassword.isEmpty() && !Util.isPasswordValid(confirmPassword)) {
            responseObject.addProperty("message", "The password must contains at least Uppercase,Lowecase Number,"
                    + "Special Character and to be minimum 8 Characters Long! ");
        } else if (!newPassword.isEmpty() && newPassword.equals(currentPassword)) {
            responseObject.addProperty("message", "The new password does not match current password ");
        } else if (!confirmPassword.equals(confirmPassword)) {
            responseObject.addProperty("message", "Confirmd Password does not matching enterd new password");
        } else {
            HttpSession ses = request.getSession();
            if (ses.getAttribute("user") != null) {
                User u = (User) ses.getAttribute("user");

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria c = s.createCriteria(User.class);
                c.add(Restrictions.eq("email", u.getEmail()));

                if (!c.list().isEmpty()) {
                    User u1 = (User) c.list().get(0);

                    u1.setFristName(fname);
                    u1.setLastName(lname);
                    if (!confirmPassword.isEmpty()) {
                        u1.setPassword(confirmPassword);
                    } else {
                        u1.setPassword(currentPassword);
                    }

                    City city = (City) s.load(City.class, cityId);
                    Address address = new Address();
                    address.setLine1(line1);
                    address.setLine2(line2);
                    address.setPostalCode(posCode);
                    address.setCity(city);
                    address.setUser(u1);

                    s.merge(u1);
                    s.merge(address);
                    s.beginTransaction().commit();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "User Profile details updated successful");
                }
            }

        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);

    }

}
