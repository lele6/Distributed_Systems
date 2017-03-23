package Flag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 *
 * @author Le Le
 * @since September 23, 2016
 * This file is the Model part of the MVC. In this case, the business logic involves
 * making a request to www.cia.gov and then screen scraping the HTML to get the picture tag,
 * picture URL, and picture description.
 */
public class FlagModel {
    private String flagTag; // the name of the country chosen
    private String flagURL; // the URL of the picture
    private String flagDescription; // the description of the country chosen
    
    /**
     * The method is used to get the tag of the country chosen
     * @return the tag
     */
    public String getFlagTag(){
        return flagTag;
    }
    
    /**
     * The method is used to get the description of the country chosen
     * @return the description
     */
    public String getFlagDescription(){
        return flagDescription;
    }
    
    /**
     * The method is used to get the URL of the flag picture
     * @return the URL
     */
    public String getFlagURL(){
        return flagURL;
    }
    
    /**
     * The method is used to get the URL, description and tag.
     * @param search
     * @throws UnsupportedEncodingException 
     */
    public void doFlagSearch(String search) throws UnsupportedEncodingException{
        
        // get the country code
        String pictureCode = search; 
        String countryCode;      
        countryCode = pictureCode.split("/")[1].split("\\.")[0];
        
        // concatenate the URL
        flagURL = "https://www.cia.gov/library/publications/the-world-factbook/graphics/flags/large/" + countryCode + "-lgflag.gif";       
        pictureCode = URLEncoder.encode(pictureCode, "UTF-8");
        
        // get the response
        String response = "";        
        String fetchURL = "https://www.cia.gov/library/publications/the-world-factbook/" + search;        
        response = fetch(fetchURL);
        
        // locate the index of description 
        int scrapeLeft = response.indexOf("<span class=\"flag_description_text\">");       
        if (scrapeLeft == -1){
            flagTag = null;
            flagURL = null;
            return;
        }
        // the start index of description
        scrapeLeft += " <span class=\"flag_description_text\">".length()-1;
        // the end index of description
        int scrapeRight = response.indexOf("</span>", scrapeLeft);
        // get the description
        flagDescription = response.substring(scrapeLeft, scrapeRight);

        
        // get the tag
        int tagStart = response.indexOf("<span class=\"region_name1 countryName \">");
        // the start index of tag
        tagStart += "<span class=\"region_name1 countryName \">".length();
        // the end index of tag
        int tagEnd = response.indexOf("</span>", tagStart);
        // get the tag
        flagTag = response.substring(tagStart, tagEnd);               
    }
    
    
    /*
     * Make an HTTP request to a given URL
     * 
     * @param urlString The URL of the request
     * @return A string of the response from the HTTP GET.  This is identical
     * to what would be returned from using curl on the command line.
     */
    private String fetch(String urlString) {
        String response = "";
        try {
            URL url = new URL(urlString);
            /*
             * Create an HttpURLConnection.  This is useful for setting headers
             * and for getting the path of the resource that is returned (which 
             * may be different than the URL above if redirected).
             * HttpsURLConnection (with an "s") can be used if required by the site.
             */
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                response += str;
            }
            in.close();
        } catch (IOException e) {
            flagURL = "https://www.cia.gov/library/publications/the-world-factbook/graphics/maps/xx-map.gif";
            flagTag = "World (Sorry, something gets wrong. Please try another)";
            flagDescription = "There is the world!";
        }
        return response;
    }
}
