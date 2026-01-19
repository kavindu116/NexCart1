
package controller;

import Hibernate.Cart;
import Hibernate.Product;
import Hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;


@WebServlet(name = "CheckSessionCart", urlPatterns = {"/CheckSessionCart"})
public class CheckSessionCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            ArrayList<Cart> sessionCart = (ArrayList<Cart>) request.getSession().getAttribute("sessionCart");
            if (sessionCart != null) {
                SessionFactory sf = Hibernate.HibernateUtil.getSessionFactory();
                Session s = sf.openSession();
                Transaction tr = s.beginTransaction();

                for (Cart sessioncart : sessionCart) {
                    
                   Product product = (Product) s.get(Product.class, sessioncart.getProduct().getId());
                    Criteria c1 = s.createCriteria(Cart.class);
                    c1.add(Restrictions.eq("user", user));
                    c1.add(Restrictions.eq("product", sessioncart.getProduct()));
                    if(c1.list().isEmpty()){
                        sessioncart.setUser(user);
                        s.save(sessioncart);
                        tr.commit();
                    }else{
                        
                        Cart dbCart = (Cart) c1.uniqueResult();
                        int newqty = sessioncart.getQty() + dbCart.getQty();
                        if(newqty <= product.getQty()){
                            dbCart.setQty(newqty);
                            dbCart.setUser(user);
                            
                            s.update(dbCart);
                            tr.commit();
                        }
                        
                    }
                    
                }
                request.getSession().setAttribute("sessionCart", null);
            }
        }
    }

  

}
