package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Product;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

@WebServlet(name = "LoadIndexProducts", urlPatterns = {"/LoadIndexProducts"})
public class LoadIndexProducts extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            Criteria criteria1 = session.createCriteria(Product.class);
            criteria1.addOrder(Order.desc("id"));
            criteria1.setMaxResults(12);
            List<Product> productList = criteria1.list();
            for (Product p : productList) {
                p.getUser().setPassword(null);
                p.getUser().setVerification(null);
                p.getUser().setEmail(null);
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.add("productList", gson.toJsonTree(productList));

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(jsonObject));

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write(e.toString());
        }
    }
}
