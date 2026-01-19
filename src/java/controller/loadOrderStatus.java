package controller;

import Hibernate.Category;
import Hibernate.HibernateUtil;
import Hibernate.OrderStatus;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "loadOrderStatus", urlPatterns = {"/loadOrderStatus"})
public class loadOrderStatus extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria c = s.createCriteria(OrderStatus.class);
        List<OrderStatus> orderStatusList = c.list();

        Gson gson = new Gson();
        String toJson = gson.toJson(orderStatusList);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
        s.close();
    }

}
