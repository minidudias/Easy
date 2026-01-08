package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Cart_DTO;
import dto.User_DTO;
import entity.Address;
import entity.Cart;
import entity.City;
import entity.Order_Item;
import entity.Order_Status;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.PayHere_Hash;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "Checkout", urlPatterns = {"/Checkout"})
public class Checkout extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        HttpSession session_Http = request.getSession();

        Session session_Hibernate = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction_Hibernate = session_Hibernate.beginTransaction();

        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        boolean isLastAddressChecked = requestJsonObject.get("isLastAddressChecked").getAsBoolean();
        String first_name = requestJsonObject.get("first_name").getAsString();
        String last_name = requestJsonObject.get("last_name").getAsString();
        String city_id = requestJsonObject.get("city_id").getAsString();
        String address1 = requestJsonObject.get("address1").getAsString();
        String address2 = requestJsonObject.get("address2").getAsString();
        String postal_code = requestJsonObject.get("postal_code").getAsString();
        String mobile = requestJsonObject.get("mobile").getAsString();

        if (session_Http.getAttribute("user") != null) {
            //user signed in
            User_DTO user_DTO = (User_DTO) session_Http.getAttribute("user");
            Criteria criteria1 = session_Hibernate.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
            User user = (User) criteria1.uniqueResult();

            if (isLastAddressChecked) {
                //get lastly added address of the user
                Criteria criteria2 = session_Hibernate.createCriteria(Address.class);
                criteria2.add(Restrictions.eq("user", user));
                criteria2.addOrder(Order.desc("id"));
                criteria2.setMaxResults(1);

                if (criteria2.list().isEmpty()) {
                    //Addresses were not found for user. Please create a new address.
                    responseJsonObject.addProperty("message", "There are not any saved addresses. So, please create a new address!");

                } else {
                    //Get the last address
                    Address address = (Address) criteria2.list().get(0);

                    //*** Complete the checkout process ***
                    completeTheCheckoutProcess(session_Hibernate, transaction_Hibernate, user, address, responseJsonObject);
                }

                
            } else {
                //create new address
                if (first_name.isEmpty()) {
                    responseJsonObject.addProperty("message", "Please fill first name");
                } else if (last_name.isEmpty()) {
                    responseJsonObject.addProperty("message", "Please fill last name");
                } else if (!Validations.isInteger(city_id)) {
                    responseJsonObject.addProperty("message", "Invalid city selected");
                } else {
                    //check city from DB
                    Criteria criteria3 = session_Hibernate.createCriteria(City.class);
                    criteria3.add(Restrictions.eq("id", Integer.parseInt(city_id)));

                    if (criteria3.list().isEmpty()) {
                        responseJsonObject.addProperty("message", "Invalid city selected");
                    } else {
                        //city is found
                        City city = (City) criteria3.list().get(0);

                        if (address1.isEmpty()) {
                            responseJsonObject.addProperty("message", "Address line 1 is necessary to continue");
                        } else if (address2.isEmpty()) {
                            responseJsonObject.addProperty("message", "Address line 2 is necessary to continue");
                        } else if (postal_code.isEmpty()) {
                            responseJsonObject.addProperty("message", "Postal or zip code is necessary to continue");
                        } else if (postal_code.length() != 5) {
                            responseJsonObject.addProperty("message", "Valid Sri Lankan zip codes does not contain more than 5 digits");
                        } else if (!Validations.isInteger(postal_code)) {
                            responseJsonObject.addProperty("message", "Not a valid Sri Lankan zip code");
                        } else if (mobile.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please insert the mobile number");
                        } else if (!Validations.isMobileNumberValidSriLankan(mobile)) {
                            responseJsonObject.addProperty("message", "Not a valid Sri Lankan mobile number");
                        } else {
                            //create new address                            
                            Address address = new Address();
                            address.setCity(city);
                            address.setFirst_name(first_name);
                            address.setLast_name(last_name);
                            address.setLine1(address1);
                            address.setLine2(address2);
                            address.setMobile(mobile);
                            address.setPostal_code(postal_code);
                            address.setUser(user);
                            session_Hibernate.save(address);

                            //*** Complete the checkout process ***
                            completeTheCheckoutProcess(session_Hibernate, transaction_Hibernate, user, address, responseJsonObject);
                        }
                    }
                }
            }

        } else {
            //user not signed in
            responseJsonObject.addProperty("message", "You have to sign in first!");
        }
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJsonObject));
    }

    
    
    
    private void completeTheCheckoutProcess(Session session_Hibernate, Transaction transaction_Hibernate, User user, Address address, JsonObject responseJsonObject){
        //*** Complete the checkout process ***
        try {
            //Add the order to the DB
            entity.Orders order = new entity.Orders();
            order.setAddress(address);
            order.setDate_time(new Date());
            order.setUser(user);
            int order_id =(int) session_Hibernate.save(order);

            //Get cart items
            Criteria critertia4 = session_Hibernate.createCriteria(Cart.class);
            critertia4.add(Restrictions.eq("user", user));
            List<Cart> cartList = critertia4.list();

            //Get order status (5. Pending Payment) from DB
            Order_Status order_Status = (Order_Status) session_Hibernate.get(Order_Status.class, 5);

            //create order_item in db
            double payment_amount = 0;
            String paying_product_items = "";
            for (Cart cartItem : cartList) {
                //calculate amount
                payment_amount += cartItem.getQty() * cartItem.getProduct().getPrice();
                if(address.getCity().getId()==1){
                    payment_amount += 100;
                }else{
                    payment_amount += 300;
                }
                
                //get paying product item details
                paying_product_items += cartItem.getProduct().getTitle()+" x "+cartItem.getQty()+", ";
                
                //Get product
                Product specific_Product = cartItem.getProduct();

                Order_Item order_Item = new Order_Item();
                order_Item.setOrder(order);
                order_Item.setOrder_status(order_Status);
                order_Item.setProduct(specific_Product);
                order_Item.setQty(cartItem.getQty());
                session_Hibernate.save(order_Item);

                //update product quantity in DB
                specific_Product.setQuantity(specific_Product.getQuantity() - cartItem.getQty());
                session_Hibernate.update(specific_Product);

                //Delete cart item from DB
                session_Hibernate.delete(cartItem);
            }
            transaction_Hibernate.commit();
            
            
            //Payhere: set payment data
            String merchant_id = "1221043";
            String amount = new DecimalFormat("0.00").format(payment_amount);
            String currency = "LKR";
            String merchant_secret = "OTg5MDU5NTI0MTMwOTA5NTY2MzE5OTg2ODQ2ODIyMTk1NDAxMw==";
            String merchant_secret_md5 = PayHere_Hash.generateMD5(merchant_secret);
                        
            JsonObject payHere = new JsonObject();
            payHere.addProperty("sandbox", true);
            
            payHere.addProperty("merchant_id", merchant_id);
                        
            payHere.addProperty("return_url", "");
            payHere.addProperty("cancel_url", "");
            payHere.addProperty("notify_url", "");
                        
            payHere.addProperty("first_name", user.getFirst_name());
            payHere.addProperty("last_name", user.getLast_name());
            payHere.addProperty("email", user.getEmail());
            payHere.addProperty("phone", "");
            payHere.addProperty("address", "");
            payHere.addProperty("city", "");
            payHere.addProperty("country", "Sri Lanka");
            payHere.addProperty("order_id", String.valueOf(order_id));
            payHere.addProperty("items", paying_product_items);
            payHere.addProperty("currency", currency);
            payHere.addProperty("amount", amount);            
            //Generate MD5 Hash
            String md5_hash_complete = PayHere_Hash.generateMD5(merchant_id + order_id + amount + currency + merchant_secret_md5);
            payHere.addProperty("hash", md5_hash_complete);
            
            responseJsonObject.addProperty("success", true);
            responseJsonObject.addProperty("message", "PayHere checkout process has started!");
            
            Gson gson = new Gson();
            responseJsonObject.add("payHereJson", gson.toJsonTree(payHere));
            
        } catch (Exception e) {
            transaction_Hibernate.rollback();
            e.printStackTrace();
        }
    }
}
