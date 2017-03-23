/*
 * This file is used to establish the server side.
 */
package project2task2;
import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Le Le
 * @since October 7, 2016
 */
public class TCPSpyCommanderUsingTEAandPasswordsAndRSA {
    
    
    public static void main (String args[]) throws IOException {
        int count = 0; // count the number of visits
        
        // create user portfolio
        HashMap<String, String> userAuthentication = new HashMap<>();
        userAuthentication.put("jamesb", "2q49gzRTi/gbD0jc24bwbw==");
        userAuthentication.put("joem", "YgwByzmr3QK8QtKyOBYKxw==");
        userAuthentication.put("mikem", "rfxvj3mppOt4nh8W5ZHD6w==");
        userAuthentication.put("seanb", "ud725zjIT4KobfKtzJLg9A==");
        
        // create all members description
        HashMap<String, String> memberDescription = new HashMap<>();
        memberDescription.put("jamesb", "Spy");
        memberDescription.put("joem", "Spy");
        memberDescription.put("mikem", "Spy");
        memberDescription.put("seanb", "Spy Commander");
        
        // create the location portfolio
        HashMap<String, String> userLocation = new HashMap<>();
        userLocation.put("jamesb", "-79.945289,40.44431,0.00000");
        userLocation.put("joem", "-79.945289,40.44431,0.00000");
        userLocation.put("mikem", "-79.945289,40.44431,0.00000");
        userLocation.put("seanb", "-79.945289,40.44431,0.00000");
        
        BigInteger n = new BigInteger("2849296365346296270031687289060103689300203464867342389233303603115485234469361340919737960149488675299490425184983240829130981545253101846719878426995010037521641016779151319064450580012697970748287360611492968742673931532127517617868028069"); // n is the modulus for both the private and public keys
        BigInteger e = new BigInteger ("65537"); // e is the exponent of the public key
        BigInteger d = new BigInteger ("466325172264589068653735719707320630658009709998430115307634076125191794328674943050568524048452256454557491196333830372094648112079707896196435115454846155684619918791090722945179635896013345738270989987557576170601695916978862877452484481"); // d is the exponent of the private key
        
        try{
            int serverPort = 7896; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort);
            
            Scanner scanServer = new Scanner(System.in);
            System.out.println("Waiting for spies to visit...");
            
            while(true) {
                Socket clientSocket = listenSocket.accept();
                count ++;
                Connection c = new Connection(clientSocket,count, userAuthentication, userLocation, memberDescription, n, d, e);
    
            }
        } catch(IOException x) 
        {System.out.println("Listen socket:"+x.getMessage());}
    }
}

class Connection extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    TEA tea;
    int count = 0;
    HashMap<String, String> userAuthentication;// user authentication map
    HashMap<String, String> userLocation; // user location portfolio
    HashMap<String, String> memberDescription;  // member description map
    PasswordHash hash = new PasswordHash();
    BigInteger n;
    BigInteger d;
    BigInteger e;
    
    public Connection (Socket aClientSocket, int count, HashMap<String, String> userAuthentication,
            HashMap<String, String> userLocation, HashMap<String, String> memberDescription, BigInteger n, BigInteger d, BigInteger e) {
        try {
            // get parameters from main class
            clientSocket = aClientSocket;
            in = new DataInputStream( clientSocket.getInputStream());
            out =new DataOutputStream( clientSocket.getOutputStream());
            this.start();
            this.count = count;
            this.userAuthentication = userAuthentication; 
            this.memberDescription = memberDescription;
            this.userLocation = userLocation; 
            this.n = n;
            this.d = d;
            this.e = e;
        } catch(IOException x) {System.out.println("Connection:"+x.getMessage());}
    }
    public void run(){
        try {	
            // get the length of user encrypted key 
            int lengthInt = in.readInt();
            byte[] clientKeys = new byte[lengthInt];
            // get the content of the encrypted key
            in.read(clientKeys);
            
            BigInteger clientKey = new BigInteger(clientKeys);
            
            BigInteger decryptedKey = clientKey.modPow(d, n);

            // Initiate the TEA
            tea = new TEA(decryptedKey.toByteArray());
                           
            // get the length and content of user-id
            int userIdLen = in.readInt();
            byte[] userIdByte = new byte[userIdLen];
            in.read(userIdByte);
            // decrypt the user-id
            byte[] decryptedUserIds = tea.decrypt(userIdByte);
            String decryptedUserId = new String(decryptedUserIds);
            
            // get the length and content of password
            int userPasswordLen = in.readInt();
            byte[] userPasswordByte = new byte[userPasswordLen];
            in.read(userPasswordByte);
            
            // decrypt the user password
            byte[] decryptedUserPasswords = tea.decrypt(userPasswordByte);
            String decryptedUserPasswordWait = new String(decryptedUserPasswords);
            String salt;
            switch(decryptedUserId) {
                case "jamesb":
                    salt = "hi";
                    break;
                case "joem":
                    salt = "hello";
                    break;
                case "mikem":
                    salt = "happy";
                    break;
                case "seanb":
                    salt = "lol";
                    break;
                default:
                    salt = " ";
                    break;
            }
            // get the decrypted password after salt
            String passwordWithSalt = decryptedUserPasswordWait + salt;
            String decryptedUserPassword = hash.printBase(hash.computeHashes(passwordWithSalt));           
            
            // get the length and content of location
            int userLocationLen = in.readInt();
            byte[] userLocationByte = new byte[userLocationLen];
            in.read(userLocationByte);
            // decrypt the user location
            byte[] decryptedUserLocations = tea.decrypt(userLocationByte);
            String decryptedUserLocation = new String(decryptedUserLocations);
                       
            //verify if the user name exists
            if (!userAuthentication.containsKey(decryptedUserId)){
                String message = "Got visit " + count + " illegal user-id. This may be an attack.";
                Exception b = new Exception(message);
                out.writeUTF("Not a valid user-id or password");               
                throw b;
            } else{ // verify the username and password
                if (!userAuthentication.get(decryptedUserId).equals(decryptedUserPassword)){
                    String message = "Got visit " + count + " from " + decryptedUserId + ". Illegal Password attempt. This may be an attack.";
                    Exception b = new Exception(message);
                    out.writeUTF("Not a valid user-id or password"); 
                    throw b;
                }
            }
            
            // update the location and create the string
            userLocation.put(decryptedUserId, decryptedUserLocation);
            String secretXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<kml xmlns=\"http://earth.google.com/kml/2.2\"\n" +
                                "><Document>\n" + "<Style id=\"style1\">\n" + "<IconStyle>\n" + "<Icon>\n" +
                                "<href>http://maps.gstatic.com/intl/en_ALL/mapfiles/ms/micons/bluedot.\n" +
                                "png</href>\n" + "</Icon>\n" + "</IconStyle>\n" + "</Style>";
            
            for (String user_id: userLocation.keySet()){
                secretXML += "<Placemark>\n" + "<name>" + user_id + "</name>\n" + "<description>" + memberDescription.get(user_id) + "</description>\n" +
                             "<styleUrl>#style1</styleUrl>\n" + "<Point>\n" + "<coordinates>" + userLocation.get(user_id) + "</coordinates>\n" +
                              "</Point>\n" + "</Placemark>";
            }
            
            // write the updated location to the file
            BufferedWriter writer = new BufferedWriter( new FileWriter("SecretAgents.kml"));
            writer.write(secretXML);
            
            // give the feedback 
            out.writeUTF("Thank you. Your location was securely transmitted to intelligence Headquarters.");
            out.flush();
            String messageSuccess = "Got visit " + count + " from " + decryptedUserId;
            System.out.println(messageSuccess);
            writer.close();
            in.close();
            out.close();
            
        }catch (EOFException e){
            //System.out.println("EOF:"+e.getMessage());
        } catch(IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally{ 
            try {clientSocket.close();}catch (IOException e){/*close failed*/}
        }
    }   
}

