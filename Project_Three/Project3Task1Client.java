/*
 * This file is the client side of task1
 */
package project3task1client;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Le Le
 * @since October 28, 2016
 */
public class Project3Task1Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        // key information of sensor 1
        BigInteger e1 = new BigInteger("65537");
        BigInteger d1 = new
        BigInteger("339177647280468990599683753475404338964037287357290649"+
        "639740920420195763493261892674937712727426153831055473238029100"+
        "340967145378283022484846784794546119352371446685199413453480215"+
        "164979267671668216248690393620864946715883011485526549108913");
        BigInteger n1 = new
        BigInteger("268852025517901502623747873143657162103121815451557296"+
        "872758837706559866377091251333301800665424865065625091311087483"+
        "660777796686710629019261833666084998095639973296736997628150027"+
        "0286450313199586861977623503348237855579434471251977653662553");
        
        // key information of sensor 2
        BigInteger e2 = new BigInteger("65537");
        BigInteger d2 = new
        BigInteger("305679118102363797399361617781200619981373682448507786"+
        "561363052573589491549174231030689387363438511417331122526361260"+
        "146835784902878429654903788548172743687324748741638533947913984"+
        "4441975358720061511138956514526329810536684170025186041253009");
        BigInteger n2 = new
        BigInteger("337732730297800229110743334027792117465807222661763993"+
        "591585049421166520688137154256929554421795939153322483891804000"+
        "645095126745210227522476507556753472058426094894123004347330375"+
        "5275736138134129921285428767162606432396231528764021925639519"); 
        Date date = new Date();
        
        // details of the first call
        String sensorID1 = "1";
        String timeStamp1 = Long.toString(date.getTime());
        String type1 = "Celsius";
        String temperature1 = "23.8";
        String message1 = sensorID1 + timeStamp1 + type1 + temperature1;
        // get the signature
        String signature1 = getSignature(message1, d1, n1);
        // first call of highTemperature
        System.out.println(highTemperature(sensorID1, timeStamp1, type1, temperature1, signature1));
        
        // details of the second call
        String sensorID2 = "2";
        String timeStamp2 = Long.toString(date.getTime());
        String type2 = "Celsius";
        String temperature2 = "10";
        String message2 = sensorID2 + timeStamp2 + type2 + temperature2;
        // get the signature
        String signature2 = getSignature(message2, d2, n2);
        // second call of lowTemperature
        System.out.println(lowTemperature(sensorID2, timeStamp2, type2, temperature2, signature2));
        
        // details of the third call
        String timeStamp3 = Long.toString(date.getTime());
        String type3 = "Celsius";
        String temperature3 = "30";
        String message3 = sensorID1 + timeStamp3 + type3 + temperature3;
        // get the signature
        String signature3 = getSignature(message3, d1, n1);
        // third call of highTemperature
        System.out.println(highTemperature(sensorID1, timeStamp3, type3, temperature3, signature3));
        
        // details of the forth call
        String timeStamp4 = Long.toString(date.getTime());
        String type4 = "Celsius";
        String temperature4 = "35";
        String message4 = sensorID1 + timeStamp4 + type4 + temperature4;
        
        BigInteger nInvalid = new BigInteger("6000");
        
        // get the signature
        String signature4 = getSignature(message4, d1, nInvalid);
        // forth call of highTemperature
        System.out.println(highTemperature(sensorID1, timeStamp4, type4, temperature4, signature4));
        
        // call the getTemperatures method
        System.out.println();
        String temperatures = getTemperatures();
        System.out.println("The temperatures are:");
        System.out.println(temperatures);
        
        // call the lastTemperatures method for sensor 1
        String lastTemperature = getLastTemperature(sensorID1);
        System.out.println();
        System.out.println("The last temperature is:");
        System.out.println(lastTemperature);
        
    }
    
    /**
     * 
     * @param message the message to send to the server
     * @param d sensor's key
     * @param n sensor's key
     * @return 
     */
    private static String getSignature(String message, BigInteger d, BigInteger n){
        String signature = "";
        try {
            // hash the first call
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] messages = messageDigest.digest(message.getBytes());
            
            // copy the array to another with a bigger length
            byte[] messagesCopy = copyArray(messages);
            
            //create a BigInteger for the hash
            BigInteger b1 = new BigInteger(messagesCopy);
            // encrypt the BigInteger
            signature = b1.modPow(d, n).toString();    
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Project3Task1Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return signature;
    }
    
    /**
     * 
     * @param messages message to send
     * @return 
     */
    private static byte[] copyArray(byte[] messages){
        byte[] messagesCopy = new byte[messages.length+1];
        messagesCopy[0] = 1;
        for(int i=0; i< messages.length;i++){
            messagesCopy[i+1] = messages[i];
        }
        return messagesCopy;
    }
    
    /**
     * 
     * @param sensorID the sensor's ID
     * @param timeStamp the timestamp for the message
     * @param type the type of the temperature
     * @param temperature the reported temperature
     * @param signature the encrypted message
     * @return 
     */
    private static String highTemperature(java.lang.String sensorID, java.lang.String timeStamp, java.lang.String type, java.lang.String temperature, java.lang.String signature) {
        edu.cmu.andrew.lle1.Task1Server_Service service = new edu.cmu.andrew.lle1.Task1Server_Service();
        edu.cmu.andrew.lle1.Task1Server port = service.getTask1ServerPort();
        return port.highTemperature(sensorID, timeStamp, type, temperature, signature);
    }
    
    /**
     * 
     * @param sensorID the sensor's ID
     * @param timeStamp the timestamp for the message
     * @param type the type of the temperature
     * @param temperature the reported temperature
     * @param signature the encrypted message
     * @return 
     */
    private static String lowTemperature(java.lang.String sensorID, java.lang.String timeStamp, java.lang.String type, java.lang.String temperature, java.lang.String signature) {
        edu.cmu.andrew.lle1.Task1Server_Service service = new edu.cmu.andrew.lle1.Task1Server_Service();
        edu.cmu.andrew.lle1.Task1Server port = service.getTask1ServerPort();
        return port.lowTemperature(sensorID, timeStamp, type, temperature, signature);
    }
    
    /**
     * 
     * @return all the temperatures
     */
    private static String getTemperatures() {
        edu.cmu.andrew.lle1.Task1Server_Service service = new edu.cmu.andrew.lle1.Task1Server_Service();
        edu.cmu.andrew.lle1.Task1Server port = service.getTask1ServerPort();
        return port.getTemperatures();
    }
    
    /**
     * 
     * @param sensorID the sensor's ID
     * @return 
     */
    private static String getLastTemperature(java.lang.String sensorID) {
        edu.cmu.andrew.lle1.Task1Server_Service service = new edu.cmu.andrew.lle1.Task1Server_Service();
        edu.cmu.andrew.lle1.Task1Server port = service.getTask1ServerPort();
        return port.getLastTemperature(sensorID);
    }
    
    
}
