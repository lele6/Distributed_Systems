package ComputeHashes;
/**
 *
 * @author Le Le
 * @since September 23, 2016
 * This document is used to compute two hashes with two encoding methods.
 */
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

@WebServlet(name = "ComputeHashes",
        urlPatterns = {"/ComputeHashes"})
public class ComputeHashes extends HttpServlet{
    
    /**
     * The doGet function corresponding to GET 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws UnsupportedEncodingException 
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, UnsupportedEncodingException {
        //get the userData parameter if it exists
        String userData = request.getParameter("userData");
        
        //get the userChoice parameter if it exists
        String userChoice = request.getParameter("userChoice");
        
        // the view after the clicking action
        String nextPage;
        
        /*
         * Check if the userData parameter is present.
         * If not, then stay on the same page for the next input
         * If there is a userData parameter, then compute the corresponding hash and return the result.
         */
        if(userData != null){
            try {
                // pass the result to the view
                request.setAttribute("userChoice", userChoice);
                request.setAttribute("userData", userData);
                request.setAttribute("base64Hash", printBase(computeHashes(userData, userChoice)));
                request.setAttribute("hexHash", printHex(computeHashes(userData, userChoice)));
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(ComputeHashes.class.getName()).log(Level.SEVERE, null, ex);
            }
            nextPage = "results.jsp";
        }
        else{
            // no userData then display the inital page
            nextPage = "index.jsp";
        }
        
        // Transfer control over the nextPage based on userData
        RequestDispatcher page = request.getRequestDispatcher(nextPage);
        page.forward(request, response);
    }
    
    /**
     * This method is used to compute hashes for userData
     * @param userData 
     * @param userChoice
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException 
     */
    public byte[] computeHashes(String userData, String userChoice) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        // convert userData to byte format for MessageDigest
        byte[] byteUserData = userData.getBytes();
        
        // based on userChoice of which hash 
        MessageDigest messageDigest = MessageDigest.getInstance(userChoice);
        
        //get the hashes
        byte[] hashes = messageDigest.digest(byteUserData);
        return hashes;
    }
    
    /**
     * The method is used to get hash of Base64 encoding
     * @param hashes hashes for print
     * @return 
     */
    public String printBase(byte[] hashes) {
        return DatatypeConverter.printBase64Binary(hashes);
    }
    
    /**
     * The method is used to get hash of hexadecimal encoding
     * @param hashes hashes for print
     * @return 
     */
    public String printHex(byte[] hashes) {
        return DatatypeConverter.printHexBinary(hashes);
    }
}
