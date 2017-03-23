/*
 * This file is to compute hash for password
 */
package project2task1;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Le Le
 * @since October 7, 2016
 */
public class PasswordHash {
    public PasswordHash(){
        
    }
    public byte[] computeHashes(String userData) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        // convert userData to byte format for MessageDigest
        byte[] byteUserData = userData.getBytes();
        
        // based on userChoice of which hash 
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        
        //get the hashes
        byte[] hashes = messageDigest.digest(byteUserData);
        return hashes;
    }
    
    // Convert hashes on Base 64
    public String printBase(byte[] hashes) {
        return DatatypeConverter.printBase64Binary(hashes);
    }
    
}
