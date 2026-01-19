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
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Shaik Sameer
 */
@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject signIn = gson.fromJson(request.getReader(), JsonObject.class);

        String email = signIn.get("email").getAsString();
        String password = signIn.get("password").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email Can Not Be Empty");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please Enter Valid Email");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Password Can Not Be Empty");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(User.class);

            c.add(Restrictions.eq("email", email));
            c.add(Restrictions.eq("password", password));

            if (c.list().isEmpty()) {
                responseObject.addProperty("message", "Invalid Credentials");
            } else {
                responseObject.addProperty("status", true);
                if (email.equals("admin@nexcart.com") && password.equals("admin@4568")) {

                    responseObject.addProperty("message", "admin");
                } else {
                    User u = (User) c.list().get(0);

                    HttpSession ses = request.getSession();

                    if (!u.getVerificationCode().equals("Verified")) {

                        ses.setAttribute("email", email);

                        responseObject.addProperty("message", "101");

                    } else {
                        ses.setAttribute("user", u);

                        responseObject.addProperty("message", "102");
                    }
                }

            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

}
