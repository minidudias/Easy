package controller;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.PayHere_Hash;

@WebServlet(name = "VerifyPayments", urlPatterns = {"/VerifyPayments"})
public class VerifyPayments extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String merchant_id = request.getParameter("merchant_id");
        String order_id = request.getParameter("order_id");
        String payment_amount = request.getParameter("payhere_amount");
        String currency = request.getParameter("payhere_currency");
        String status_code = request.getParameter("status_code");
        String md5_hash_complete_from_PayHere = request.getParameter("md5sig");
        
        String merchant_secret = "OTg5MDU5NTI0MTMwOTA5NTY2MzE5OTg2ODQ2ODIyMTk1NDAxMw==";
        String merchant_secret_md5 = PayHere_Hash.generateMD5(merchant_secret);
        
        String md5_hash_complete_regenerated = PayHere_Hash.generateMD5(
                merchant_id
                + order_id
                + payment_amount
                + currency
                + status_code
                + merchant_secret_md5                
        );
                
        if(md5_hash_complete_regenerated.equals(md5_hash_complete_from_PayHere) && status_code.equals("2")) {
            System.out.println("Payment of " +order_id+ "has been completed");
            
            //payment verification and update order status to "Paid"
        }
    }
}
