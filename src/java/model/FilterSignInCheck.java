package model;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = {"/my-account.html", "/checkout.html"})
public class FilterSignInCheck implements Filter{
    
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest requestCastedHttpServlet =(HttpServletRequest) request;
        HttpServletResponse responseCastedHttpServlet =(HttpServletResponse) response;
        
        if(requestCastedHttpServlet.getSession().getAttribute("user") != null){
            chain.doFilter(request, response);
        
        }else{
            responseCastedHttpServlet.sendRedirect("sign-in.html");
        }
    }    
}
