package controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import Hibernate.HibernateUtil;
import Hibernate.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Shaik Sameer
 */
@WebServlet(urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {
 @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject user = gson.fromJson(request.getReader(), JsonObject.class);

        String fname = user.get("fristName").getAsString();
        String lname = user.get("lastName").getAsString();
        final String email = user.get("email").getAsString();
        String password = user.get("password").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (fname.isEmpty()) {
            responseObject.addProperty("message", "Frist Name Can Not Be Empty");
        } else if (lname.isEmpty()) {
            responseObject.addProperty("message", "Last Name Can Not Be Empty");
        } else if (email.isEmpty()) {
            responseObject.addProperty("message", "Email Can Not Be Empty");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please Enter Valid Email");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Password Can Not Be Empty");
        } else if (!Util.isPasswordValid(password)) {
            responseObject.addProperty("message", "The password must contains at least Uppercase,Lowecase Number,"
                    + "Special Character and to be minimum 8 Characters ");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(User.class);
            c.add(Restrictions.eq("email", email));

            if (!c.list().isEmpty()) {
                responseObject.addProperty("message", "User With the Email Alredy Exsists");
            } else {

                User u = new User();
                u.setFristName(fname);
                u.setLastName(lname);
                u.setEmail(email);
                u.setPassword(password);

                final String vCode = Util.genarateCode();
                u.setVerificationCode(vCode);

                u.setRegisterdAt(new Date());

                s.save(u);
                s.beginTransaction().commit();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mail.sendMail(email, "NexCart Account Verification ", ""
                                + "<!DOCTYPE html>\n"
                                + "<html>\n"
                                + "<head>\n"
                                + "  <meta charset=\"UTF-8\">\n"
                                + "  <title>Verification Code</title>\n"
                                + "</head>\n"
                                + "<body style=\"font-family: Arial, sans-serif; background-color: #f6f6f6; padding: 20px;\">\n"
                                + "  <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);\">\n"
                                + "    <tr>\n"
                                + "      <td>\n"
                                + "        <h2 style=\"color: #333333;\">Email Verification</h2>\n"
                                + "        <p>Hi <strong>" + fname + "</strong>,</p>\n"
                                + "        <p>Your verification code is:</p>\n"
                                + "        <div style=\"font-size: 24px; font-weight: bold; background-color: #f0f0f0; padding: 15px; border-radius: 5px; text-align: center; letter-spacing: 4px; color: #222222;\">\n"
                                + "          " + vCode + "\n"
                                + "        </div>\n"
                                + "        <p style=\"margin-top: 20px;\">Please use this code to verify your email address. This code will expire in 10 minutes.</p>\n"
                                + "        <p>If you did not request this code, please ignore this email.</p>\n"
                                + "        <p style=\"margin-top: 30px;\">Thanks,<br>The <strong>NexCart</strong> Team</p>\n"
                                + "      </td>\n"
                                + "    </tr>\n"
                                + "  </table>\n"
                                + "</body>\n"
                                + "</html>");
                    }
                }).start();
                
                HttpSession ses = request.getSession();
                ses.setAttribute("email", email);
                
                responseObject.addProperty("message", "Registration Success.Please check youer Email for the Verification Code. ");
                responseObject.addProperty("status", true);

            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }

}
