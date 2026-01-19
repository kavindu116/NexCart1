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
import javax.servlet.http.HttpSession;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "ForgetPassword", urlPatterns = {"/ForgetPassword"})
public class ForgetPassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject fp = gson.fromJson(request.getReader(), JsonObject.class);

        String otp = fp.get("otp").getAsString();
        String email = fp.get("email").getAsString();
       

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email Can Not Be Empty");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please Enter Valid Email");
        } else {
            String OTP = Util.generateOTP();

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(User.class);
            c.add(Restrictions.eq("email", email));
            

            if (c.list().isEmpty()) {
                responseObject.addProperty("message", "No User Found On This Email Address.");
            } else {
                User user = (User) c.list().get(0);
                user.setOTP(OTP);

                s.update(user);
                s.beginTransaction().commit();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mail.sendMail(email, "Smart Trade Verification ", "<!DOCTYPE html>\n"
                                + "<html>\n"
                                + "<head>\n"
                                + "    <meta charset=\"UTF-8\">\n"
                                + "    <title>NexCart OTP Verification</title>\n"
                                + "</head>\n"
                                + "<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;\">\n"
                                + "    <div style=\"max-width: 500px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">\n"
                                + "        <h2 style=\"color: #333333;\">NexCart - OTP Verification</h2>\n"
                                + "        <p>Hi,</p>\n"
                                + "        <p>Use the following One-Time Password (OTP) to complete your action on <strong>NexCart</strong>.</p>\n"
                                + "        \n"
                                + "        <div style=\"text-align: center; margin: 30px 0;\">\n"
                                + "            <p style=\"font-size: 24px; letter-spacing: 3px; font-weight: bold; color: #2c3e50;\">" + OTP + "</p>\n"
                                + "        </div>\n"
                                + "\n"
                                + "        <p>This OTP is valid for a limited time and should not be shared with anyone.</p>\n"
                                + "        <p>If you didn’t request this, please ignore this email.</p>\n"
                                + "\n"
                                + "        <p style=\"margin-top: 30px;\">Thank you,<br><strong>NexCart Team</strong></p>\n"
                                + "    </div>\n"
                                + "</body>\n"
                                + "</html>");
                    }
                }).start();

                
                HttpSession ses = request.getSession();
                ses.setAttribute("ForgrtPemail", email);
                
                responseObject.addProperty("message", "OTP Has Been Send.Please check youer Email for the OTP Code. ");
                responseObject.addProperty("status", true);
            }
            s.close();

        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject fp = gson.fromJson(request.getReader(), JsonObject.class);

        String otp = fp.get("otp").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (otp.isEmpty()) {
            responseObject.addProperty("message", "Please Enter OTP");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(User.class);
            c.add(Restrictions.eq("OTP", otp));

            if (c.list().isEmpty()) {
                responseObject.addProperty("message", "Invalid User");
            } else {
                responseObject.addProperty("status", true);
            }

            String responseText = gson.toJson(responseObject);
            response.setContentType("application/json");
            response.getWriter().write(responseText);
        }
    }

}
