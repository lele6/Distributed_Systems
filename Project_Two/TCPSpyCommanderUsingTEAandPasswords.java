/*
 * This file is used to establish the server side.
 */
package project2task1;
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Le Le
 * @since October 7, 2016
 */
public class TCPSpyCommanderUsingTEAandPasswords {
    
    
    public static void main (String args[]) throws IOException {
        int count = 0; //count the number of visits
        
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

        try{
            int serverPort = 7896; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort);
            
            // to get the symmetric key from the commander
            System.out.println("Enter symmetric key for TEA (taking first sixteen bytes):");
            Scanner scanServer = new Scanner(System.in);
            String symmetricKey = scanServer.nextLine();
            byte[] symmetricKeyByte = symmetricKey.getBytes();
            
            // get the TEA
            TEA tea = new TEA(symmetricKeyByte);
            
            System.out.println("Waiting for spies to visit...");
            
            while(true) {
                Socket clientSocket = listenSocket.accept();
                count ++;
                Connection c = new Connection(clientSocket, symmetricKeyByte, tea, count, userAuthentication, userLocation, memberDescription);    
            }
        } catch(IOException e) {System.out.println("Listen socket:"+e.getMessage());}
    }
}

class Connection extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    byte[] symmetricKeyByte = new byte[16];
    TEA tea;
    int count = 0; // count the number of visits
    HashMap<String, String> userAuthentication; // user authentication map
    HashMap<String, String> userLocation; // user location portfolio
    HashMap<String, String> memberDescription; // member description map
    PasswordHash hash = new PasswordHash();
    
    public Connection (Socket aClientSocket, byte[] symmetricKey, TEA tea, int count, HashMap<String, String> userAuthentication,
            HashMap<String, String> userLocation, HashMap<String, String> memberDescription) {
        try {
            // get parameters from main class
            clientSocket = aClientSocket;
            in = new DataInputStream( clientSocket.getInputStream());
            out =new DataOutputStream( clientSocket.getOutputStream());
            this.symmetricKeyByte = symmetricKey;
            this.tea = tea;
            this.start();
            this.count = count;
            this.userAuthentication = userAuthentication;
            this.memberDescription = memberDescription;
            this.userLocation = userLocation;
        } catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
    }
    public void run(){
        try {	
            // get the length of user symmetric key 
            int lengthInt = in.readInt();

            // get the user symmetric key
            byte[] clientKeys = new byte[lengthInt];
            in.read(clientKeys);

            // decrypt the user symmetric key
            byte[] decryptedKeys = tea.decrypt(clientKeys);
            String decryptedKey = new String(decryptedKeys);
                
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
            
            // verify the symmetric key
            if (!decryptedKey.equals(new String(symmetricKeyByte))){
                String message = "Got visit " + count + " illegal symmetric key used. This may be an attack.";
                Exception a = new Exception(message);
                out.writeUTF("Not a valid symmetric key"); 
                throw a;
            }
            
            
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
            
            // give the feedback if the verification is right
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

