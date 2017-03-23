package Flag;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Le Le
 * @since September 23, 2016
 * This file is the controller part of the MVC. It gets request from users and sends back response.
 */
@WebServlet(name = "FlagServlet",
        urlPatterns = {"/getFlag"})
public class FlagServlet extends HttpServlet{
    FlagModel fm = null; // the business model for the application
    
    //Initiate this servlet through instantiating the model that it will use
    @Override
    public void init(){
        fm = new FlagModel();
    }
    
    /**
     * This method is used to reply to HTTP POST requests 
     * @param request
     * @param response
     * @throws UnsupportedEncodingException
     * @throws ServletException
     * @throws IOException 
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, ServletException, IOException {
        
        // get the selecter_links parameter if it exists
        String search = request.getParameter("selecter_links");
        
        // the next page after taking action
        String nextPage;
        
        /*
         * Check if the selecter_links parameter is present.
         * If not, then stay on the same page for the next input
         * If there is a selecter_links parameter, then send the results back
         */
        if(search != null){
            fm.doFlagSearch(search);
            // pass the result to the view
            request.setAttribute("flagTag", fm.getFlagTag());
            request.setAttribute("flagDescription", fm.getFlagDescription());
            request.setAttribute("flagURL", fm.getFlagURL());
            nextPage = "result.jsp";
        }
        else{
            // no parameter then display the inital page
            nextPage = "index.jsp";
        }
        
        // Transfer control over the the correct "view"
        RequestDispatcher view = request.getRequestDispatcher(nextPage);
        view.forward(request, response);
    }
}
