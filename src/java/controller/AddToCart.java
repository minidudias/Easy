package controller;

import com.google.gson.Gson;
import dto.Cart_DTO;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Cart;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        
        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new Gson();

        try {
            String productId = request.getParameter("id");
            String addingQty = request.getParameter("qty");

            if (!Validations.isInteger(productId)) {
                //product not found
                response_DTO.setContent("Product not found");
                        
            } else if (!Validations.isInteger(addingQty)) {
                //invalid adding qty
                response_DTO.setContent("Passed quantity is invalid");
                
            } else {
                int productIdInt = Integer.parseInt(productId);
                int addingQtyInt = Integer.parseInt(addingQty);

                if (addingQtyInt <= 0) {
                    //qty must not be 0 or less than that
                    response_DTO.setContent("Quantity must not be 0 or less than that");
                    
                } else {
                    Product product =(Product) session.get(Product.class, productIdInt);
                    if (product != null) {
                        //product found

                        if (request.getSession().getAttribute("user") != null){
                            //DB cart
                            User_DTO user_DTO =(User_DTO) request.getSession().getAttribute("user");
                            
                            //search user
                            Criteria criteria1 = session.createCriteria(User.class);
                            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
                            User user =(User) criteria1.uniqueResult();
                            
                            //check in DB cart
                            Criteria criteria2 = session.createCriteria(Cart.class);
                            criteria2.add(Restrictions.eq("user", user));
                            criteria2.add(Restrictions.eq("product", product));
                            
                            if(criteria2.list().isEmpty()){
                                //item was not found in cart
                                
                                if(addingQtyInt <= product.getQuantity()){
                                    //add product to DB cart
                                    
                                    Cart cartDb = new Cart();
                                    cartDb.setProduct(product);
                                    cartDb.setUser(user);
                                    cartDb.setQty(addingQtyInt);
                                    session.save(cartDb);
                                    transaction.commit();
                                    
                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Added to the cart successfully");
                                    
                                }else{
                                    //insufficient quantity                                    
                                    response_DTO.setContent("Quantity is insufficient");
                                    
                                }                                
                            }else{
                                //item is in the cart
                                Cart cartItem =(Cart) criteria2.uniqueResult();
                                
                                if((cartItem.getQty()  +  addingQtyInt)  <=  product.getQuantity()){
                                    cartItem.setQty(cartItem.getQty()  +  addingQtyInt);
                                    session.update(cartItem);
                                    transaction.commit();
                                    
                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Updated the cart successfully");
                                    
                                }else{
                                    //can't update the cart
                                    response_DTO.setContent("Can't update the cart");
                                    
                                }
                            }
                            
                        } else {
                            //session cart
                            
                            HttpSession httpSession = request.getSession();
                            
                            if(httpSession.getAttribute("sessionCart") != null){
                                //session cart found
                                ArrayList<Cart_DTO> sessionCart =(ArrayList<Cart_DTO>)  httpSession.getAttribute("sessionCart");
                                
                                Cart_DTO foundCart_DTO = null;
                                
                                for(Cart_DTO cart_DTO : sessionCart){
                                    if(cart_DTO.getProduct().getId() == product.getId()){
                                        
                                        foundCart_DTO = cart_DTO;
                                        break;                                        
                                    }
                                }
                                
                                if(foundCart_DTO  !=  null){
                                    //product found inside session cart
                                    
                                    if((foundCart_DTO.getQty()  +  addingQtyInt)  <=  product.getQuantity()){
                                        //update qty                                        
                                        foundCart_DTO.setQty(foundCart_DTO.getQty()  +  addingQtyInt);
                                        
                                        response_DTO.setSuccess(true);
                                        response_DTO.setContent("Updated the cart successfully");
                                    
                                    }else{
                                        //quantity not available
                                        response_DTO.setContent("Quantity not available");
                                        
                                    }
                                    
                                    
                                }else{
                                    //product not found inside session cart                                    
                                    if(addingQtyInt  <=  product.getQuantity()){
                                        //add to session cart
                                        
                                        Cart_DTO cart_DTO = new Cart_DTO();
                                        cart_DTO.setProduct(product);
                                        cart_DTO.setQty(addingQtyInt);
                                        sessionCart.add(cart_DTO);
                                        
                                        response_DTO.setSuccess(true);
                                        response_DTO.setContent("Updated the cart successfully");
                                    
                                    }else{
                                        //quantity not available
                                        response_DTO.setContent("Quantity not available");
                                        
                                    }
                                }
                            
                            }else{
                                //session cart not found                                
                                
                                if(addingQtyInt  <=  product.getQuantity()){
                                    //add to session cart                                    
                                    ArrayList<Cart_DTO> sessionCart = new ArrayList();
                                
                                    Cart_DTO cart_DTO = new Cart_DTO();
                                    cart_DTO.setProduct(product);
                                    cart_DTO.setQty(addingQtyInt);
                                    sessionCart.add(cart_DTO);
                                
                                    httpSession.setAttribute("sessionCart", sessionCart);
                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Updated the cart successfully");
                                    
                                }else{
                                    //quantity not available
                                    response_DTO.setContent("Quantity not available");
                                    
                                }
                            }
                        }
                    } else {
                        //product not found
                        response_DTO.setContent("Unable to process, requested product was not found");
                        
                    }
                }
            }
            
        } catch (Exception e) {
            response_DTO.setContent("Unable to process the request");
            e.printStackTrace();
        }
        
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO)); 
        session.close();  
    }
}
