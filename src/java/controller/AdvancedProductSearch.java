package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Category;
import entity.Color;
import entity.Product_Type;
import entity.Product;
import entity.Product_Condition;
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
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AdvancedProductSearch", urlPatterns = {"/AdvancedProductSearch"})
public class AdvancedProductSearch extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);
        Session session = HibernateUtil.getSessionFactory().openSession();

        //search all products
        Criteria criteria1 = session.createCriteria(Product.class);

        if (requestJsonObject.has("category_name")) {

            String category_name = requestJsonObject.get("category_name").getAsString();
            Criteria criteria2 = session.createCriteria(Category.class);
            criteria2.add(Restrictions.eq("name", category_name));
            Category category = (Category) criteria2.uniqueResult();

            Criteria criteria3 = session.createCriteria(Product_Type.class);
            criteria3.add(Restrictions.eq("category", category));
            List<Product_Type> modelList = criteria3.list();

            criteria1.add(Restrictions.in("model", modelList));
            
            System.out.println(category_name);
        }

        if (requestJsonObject.has("condition_name")) {

            String condition_name = requestJsonObject.get("condition_name").getAsString();
            Criteria criteria4 = session.createCriteria(Product_Condition.class);
            criteria4.add(Restrictions.eq("name", condition_name));
            Product_Condition product_condition = (Product_Condition) criteria4.uniqueResult();

            criteria1.add(Restrictions.eq("product_condition", product_condition));
            
            System.out.println(condition_name);
        }

        if (requestJsonObject.has("color_name")) {

            String color_name = requestJsonObject.get("color_name").getAsString();
            Criteria criteria5 = session.createCriteria(Color.class);
            criteria5.add(Restrictions.eq("name", color_name));
            Color color = (Color) criteria5.uniqueResult();
            
            criteria1.add(Restrictions.eq("color", color));
            
            System.out.println(color_name);
        }
        
        String search_text = requestJsonObject.get("search_text").getAsString();
        double price_start = requestJsonObject.get("price_start").getAsDouble();
        double price_end = requestJsonObject.get("price_end").getAsDouble();
                
        criteria1.add(Restrictions.like("title", search_text, MatchMode.ANYWHERE));
        criteria1.add(Restrictions.ge("price", price_start));
        criteria1.add(Restrictions.le("price", price_end));
        
        String sort_text = requestJsonObject.get("sort_text").getAsString();
                                                        
        if(sort_text.equals("Sort by Latest")){
            criteria1.addOrder(Order.desc("id"));
            
        }else if(sort_text.equals("Sort by Oldest")){
            criteria1.addOrder(Order.asc("id"));
            
        }else if(sort_text.equals("Sort by Name")){
            criteria1.addOrder(Order.asc("title"));
            
        }else if(sort_text.equals("Sort by Price")){
            criteria1.addOrder(Order.asc("price"));
        }
        
        responseJsonObject.addProperty("countOfAllProducts", criteria1.list().size());
        
        int firstResult = requestJsonObject.get("first_result").getAsInt();
        criteria1.setFirstResult(firstResult);
        criteria1.setMaxResults(6);
        
        List<Product> productList = criteria1.list();
        
        for(Product product : productList){
            product.setUser(null);
        }
        
        responseJsonObject.addProperty("success", true);
        responseJsonObject.add("productList", gson.toJsonTree(productList));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJsonObject));
    }
}