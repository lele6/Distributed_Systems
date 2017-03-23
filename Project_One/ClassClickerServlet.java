package Project1Task2;

import java.io.IOException;
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
@WebServlet(name = "ClassClickerServlet",
        urlPatterns = {"/", "/submit", "/getResults"})
public class ClassClickerServlet extends HttpServlet {
    ClassClickerModel ccm = null; // the business model for the application
    
    //Initiate this servlet through instantiating the model that it will use
    @Override
    public void init(){
        ccm = new ClassClickerModel();
    }
    
    /**
     * This method is used to reply to HTTP POST requests 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        // determine what type of device our user is
        String deviceType = request.getHeader("User-Agent");

        boolean mobile;
        // prepare the appropriate DOCTYPE for the view pages
        if (deviceType != null && ((deviceType.indexOf("Android") != -1) || (deviceType.indexOf("iPhone") != -1))) {
            mobile = true;
            /*
             * This is the latest XHTML Mobile doctype. To see the difference it
             * makes, comment it out so that a default desktop doctype is used
             * and view on an Android or iPhone.
             */
            request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
        } else {
            mobile = false;
            request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        }

        // get the answer parameter if it exists
        String result = request.getParameter("answer");

        // the next page after taking action
        String nextPage;

         /*
         * Check if the result parameter is present.
         * If not, then stay on the same page for the next input
         * If there is a result parameter, then record the answer and send it to the "submit" page.
         */
        if(result != null){
            ccm.recognizeAnswer(result);
            // pass the result to the view
            request.setAttribute("prevAnswer", result);
            nextPage = "submit.jsp";
        }
        else{
            // no result then display the inital page
            nextPage = "index.jsp";
        }
        RequestDispatcher page = request.getRequestDispatcher(nextPage);
        page.forward(request, response);    
    }
    /**
     * This method is used to reply to HTTP GET requests 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        // determine what type of device our user is
        String deviceType = request.getHeader("User-Agent");

        boolean mobile;
        // prepare the appropriate DOCTYPE for the view pages
        if (deviceType != null && ((deviceType.indexOf("Android") != -1) || (deviceType.indexOf("iPhone") != -1))) {
            mobile = true;
            /*
             * This is the latest XHTML Mobile doctype. To see the difference it
             * makes, comment it out so that a default desktop doctype is used
             * and view on an Android or iPhone.
             */
            request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
        } else {
            mobile = false;
            request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        }
        
        if(request.getServletPath().equals("/getResults")) {
            // the next page after taking action
            String nextPage = "results.jsp";

            // pass the result to the view
            request.setAttribute("countA", ccm.getCountA()+"");

            request.setAttribute("countB", ccm.getCountB()+"");

            request.setAttribute("countC", ccm.getCountC()+"");

            request.setAttribute("countD", ccm.getCountD()+"");


            // Transfer control over the nextPage based on answer
            RequestDispatcher page = request.getRequestDispatcher(nextPage);
            page.forward(request, response);

            // clear the result after submit
            ccm.setCountA(0);
            ccm.setCountB(0);
            ccm.setCountC(0);
            ccm.setCountD(0);
        }
        else if(request.getServletPath().equals("/submit")){
            RequestDispatcher page = request.getRequestDispatcher("submit.jsp");
            page.forward(request, response);
            
        }else{
            RequestDispatcher page = request.getRequestDispatcher("index.jsp");
            page.forward(request, response);
        }
    }
}
