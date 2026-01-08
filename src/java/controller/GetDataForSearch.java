package controller;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Category;
import entity.Color;
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
import org.hibernate.criterion.Order;

@WebServlet(name = "GetDataForSearch", urlPatterns = {"/GetDataForSearch"})
public class GetDataForSearch extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);
        
        Session session_Hibernate = HibernateUtil.getSessionFactory().openSession();
                
        
        //Get category list from DB
        Criteria criteria1 = session_Hibernate.createCriteria(Category.class);
        List<Category> categoryList = criteria1.list();
        jsonObject.add("categoryList", gson.toJsonTree(categoryList));
        
        //Get color list from DB
        Criteria criteria3 = session_Hibernate.createCriteria(Color.class);
        List<Color> colorList = criteria3.list();
        jsonObject.add("colorList", gson.toJsonTree(colorList));
        
        //Get product_condition list from DB
        Criteria criteria2 = session_Hibernate.createCriteria(Product_Condition.class);
        List<Product_Condition> conditionList = criteria2.list();
        jsonObject.add("conditionList", gson.toJsonTree(conditionList));
        
        //Get product list from DB
        Criteria criteria5 = session_Hibernate.createCriteria(Product.class);
        criteria5.addOrder(Order.desc("id")); //get latest products
        jsonObject.addProperty("countOfAllProducts", criteria5.list().size());
        criteria5.setFirstResult(0);
        criteria1.setMaxResults(6);
        List<Product> productList = criteria5.list();
        for(Product product : productList){ //remove users from products
            product.setUser(null);
        }
        jsonObject.add("productList", gson.toJsonTree(productList));
        
        
        jsonObject.addProperty("success", true);
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));
    }
}