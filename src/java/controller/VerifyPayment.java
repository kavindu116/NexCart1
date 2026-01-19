
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;


@WebServlet(name = "VerifyPayment", urlPatterns = {"/VerifyPayment"})
public class VerifyPayment extends HttpServlet {
@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Gson gson = new Gson();
        HttpSession ses = request.getSession();
        if (ses.getAttribute("email") == null) {
            responseObject.addProperty("message", "101");
        } else {
            String email = ses.getAttribute("email").toString();

            JsonObject Code = gson.fromJson(request.getReader(), JsonObject.class);

            String verifyCode = Code.get("verificationCode").getAsString();
            System.out.println(verifyCode);

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(User.class);
            c.add(Restrictions.eq("email", email));
            c.add(Restrictions.eq("verificationCode", verifyCode));

            if (!c.list().isEmpty()) {
                User user = (User) c.list().get(0);
                user.setVerificationCode("Verified");

                s.update(user);
                s.beginTransaction().commit();

                s.close();
                
                ses.setAttribute("user", user);
                
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Verification Success");
            } else {
                responseObject.addProperty("message", "Invalid Verification Code.");
            }

        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
}
}
