/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

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
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Shaik Sameer
 */
@WebServlet(name = "ChangePasssword", urlPatterns = {"/ChangePasssword"})
public class ChangePasssword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject fp = gson.fromJson(request.getReader(), JsonObject.class);

        String newPassword = fp.get("newPasword").getAsString();
        String ReEnter = fp.get("ReEnter").getAsString();

        String email = request.getSession().getAttribute("ForgrtPemail").toString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (newPassword.isEmpty()) {
            responseObject.addProperty("message", "please Enter New Password");
        } else if (ReEnter.isEmpty()) {
            responseObject.addProperty("message", "please Re Enter New Password");
        } else if (!Util.isPasswordValid(newPassword)) {
            responseObject.addProperty("message", "The password must contains at least Uppercase,Lowecase Number,"
                    + "Special Character and to be minimum 8 Characters Long! ");
        } else if (!ReEnter.equals(newPassword)) {
            responseObject.addProperty("message", "The new password does not match Re Entered password ");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(User.class);
            c.add(Restrictions.eq("email", email));

            if (c.list().isEmpty()) {
                responseObject.addProperty("message", "Cant Change Password.No User Found.");
            } else {
                User user = (User) c.list().get(0);
                user.setPassword(ReEnter);
                s.update(user);
                s.beginTransaction().commit();

                responseObject.addProperty("message", "Password Has Been Change");
                responseObject.addProperty("status", true);

            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }
}
