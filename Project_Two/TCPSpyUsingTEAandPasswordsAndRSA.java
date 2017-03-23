/*
 * This file is used to establish the client side.
 */
package project2task2;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Le Le
 * @since October 7, 2016
 */
public class TCPSpyUsingTEAandPasswordsAndRSA {

    public static void main (String args[]) throws IOException {
        // arguments supply message and hostname
        Socket s = null;
        try{
            int serverPort = 7896;
            s = new Socket("localhost", serverPort);    
            DataInputStream in = new DataInputStream( s.getInputStream());
            DataOutputStream out =new DataOutputStream( s.getOutputStream());
            
            // new Scanner to get data from the client input
            Scanner scanClient = new Scanner(System.in);
                       
            // generate the random number to be the key
            Random rnd = new Random();
            BigInteger key = new BigInteger(16*8,rnd);
            // n is the modulus for both the private and public keys
            BigInteger n = new BigInteger("2849296365346296270031687289060103689300203464867342389233303603115485234469361340919737960149488675299490425184983240829130981545253101846719878426995010037521641016779151319064450580012697970748287360611492968742673931532127517617868028069"); 
            // e is the exponent of the public key
            BigInteger e = new BigInteger ("65537");
             // c is the encrypted key  
            BigInteger c = key.modPow(e, n); 

            // Use the random generated key to initiate TEA
            TEA tea = new TEA(key.toByteArray());

            // transfer the encrypted key to the commander
            out.writeInt(c.toByteArray().length);
            out.write(c.toByteArray());
            
            // get the user-id 
            System.out.print("Enter our ID:");
            String userId = scanClient.nextLine();
            byte[] userIdByte = userId.getBytes();
            // encrypt the user-id
            byte[] userIdEncrypted = tea.encrypt(userIdByte);
            // transfer the length and the content of the user-id to the commander
            out.writeInt(userIdEncrypted.length);
            out.write(userIdEncrypted);
            
            // get the user password
            System.out.print("Enter your Password:");
            String password = scanClient.nextLine();
            byte[] passwordByte = password.getBytes();
            // encrypt the user password 
            byte[] passwordEncrypted = tea.encrypt(passwordByte);
            // transfer the length and the content of the user password to the commander
            out.writeInt(passwordEncrypted.length);
            out.write(passwordEncrypted);
            
            // get the user location
            System.out.print("Enter your location:");
            String location = scanClient.nextLine();
            byte[] locationByte = location.getBytes();
            // encrypt the user location
            byte[] locationEncrypted = tea.encrypt(locationByte);
            // transfer the length and the content of the user location to the commander
            out.writeInt(locationEncrypted.length);
            out.write(locationEncrypted);
            out.flush();
            String message = in.readUTF();
            System.out.println(message);
            out.close();
            in.close();
        }
        catch (UnknownHostException e)
            {System.out.println("Socket:"+e.getMessage());
        }catch (EOFException e)
            {System.out.println("EOF:"+e.getMessage());
        }catch (IOException e)
            {System.out.println("readline:"+e.getMessage());
        }finally 
            {if(s!=null) try {s.close();}
                catch (IOException e)
                    {System.out.println("close:"+e.getMessage());}
            }
     }
    
}
