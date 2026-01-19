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
import javax.validation.constraints.Email;
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
@WebServlet(name = "ResendVerifyCode", urlPatterns = {"/ResendVerifyCode"})
public class ResendVerifyCode extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String Email = request.getSession().getAttribute("email").toString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria c = s.createCriteria(User.class);

        c.add(Restrictions.eq("email", Email));

        if (c.list().isEmpty()) {

        } else {
            User u = (User) c.list().get(0);
            final String vCode = Util.genarateCode();

            u.setVerificationCode(vCode);

            s.update(u);
            s.beginTransaction().commit();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Mail.sendMail(Email, "NexCart Account Verification ", ""
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
                            + "        <p>Hi <strong> </strong>,</p>\n"
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

            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Verification Code Has been Send.Check Your Email.");

        }

        Gson gson = new Gson();
        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);

    }

}
