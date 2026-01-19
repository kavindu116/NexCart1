package controller;

import Hibernate.HibernateUtil;
import Hibernate.OrderItem;
import Hibernate.OrderStatus;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@WebServlet(name = "LoadOrder", urlPatterns = {"/LoadOrder"})
public class LoadOrder extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         Gson gson = new Gson();
    JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

    String orderStatusId = requestObject.get("status").getAsString();
    String orderItemId = requestObject.get("itemID").getAsString();

    JsonObject responseObject = new JsonObject();
    responseObject.addProperty("status", false);

    Session session = null;
    Transaction transaction = null;
    try {
        // Open session and begin transaction
        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();

        // Retrieve OrderItem entity by ID (assuming ID is String, else convert)
        OrderItem orderItem = (OrderItem) session.get(OrderItem.class, Integer.parseInt(orderItemId));
        OrderStatus orderStatus = (OrderStatus) session.get(OrderStatus.class, Integer.parseInt(orderStatusId));

        if (orderItem != null) {
            // Set new status (you might need to convert orderStatusId to actual Status object or enum)
            orderItem.setOrderStatus(orderStatus);  // Assuming status is String; if foreign key object, fetch that first

            // Save the updated entity
            session.update(orderItem);

            transaction.commit();
            responseObject.addProperty("status", true);
        } else {
            responseObject.addProperty("message", "Order item not found.");
        }

    } catch (Exception e) {
        if (transaction != null) {
            transaction.rollback();
        }
        responseObject.addProperty("message", "Error: " + e.getMessage());
    } finally {
        if (session != null) {
            session.close();
        }
    }

    String responseText = gson.toJson(responseObject);
    response.setContentType("application/json");
    response.getWriter().write(responseText);

    }

}
