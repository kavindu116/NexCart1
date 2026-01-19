
package controller;

import Hibernate.City;
import Hibernate.HibernateUtil;
import Hibernate.OrderItem;
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


@WebServlet(name = "loadOrders", urlPatterns = {"/loadOrders"})
public class loadOrders extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        
        Criteria c = s.createCriteria(OrderItem.class);
        List<OrderItem> orderItemList = c.list();
        
        Gson gson = new Gson();
        String toJson = gson.toJson(orderItemList);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
        s.close();
        
    }

    
}
