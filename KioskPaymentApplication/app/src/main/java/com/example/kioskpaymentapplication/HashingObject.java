package com.example.kioskpaymentapplication;

import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Object in charge of:
 *  - generating hashes & salts
 *  - comparing a new code with a given hash
 */
public class HashingObject {

    /**
     * Length of the salt
     */
    private int saltLength;

    /**
     * Generated salt of length saltLength
     */
    private String saltString;

    /**
     * code + salt
     */
    private String saltCode;

    /**
     * Generated hash based on saltCode using SHA-256
     */
    private String hashValue;

    /**
     * Last saltLength characters of the hashValue replaced with the salt itself
     */
    private String hashValueFinal;

    /**
     * Boolean which gives the result from checking if a given code is equal to a given hash code
     */
    boolean isLogin;

    /**
     * Constructor of a HasingObject when registering
     *  - Salt is created
     *  - Hash is created based on code + salt
     *  - Final hash is hash with last saltLength characters replaced by the salt
     * @param code
     *              Code that needs to be hashed
     * @throws NoSuchAlgorithmException
     *              If the SHA-256 algoruthm isn't found
     */
    public HashingObject(String code) throws NoSuchAlgorithmException{
        saltLength = 10;
        saltString = generateSalt();
        isLogin = false;
        System.out.println("Code to be hashed: " + code);
        System.out.println("Generated salt: " +saltString);
        saltCode = code + saltString;
        System.out.println("Salted code: " + saltCode);

        hashValue = generateHash();
        System.out.println("Sha256 hash: " + hashValue);

        hashValueFinal = pasteSaltToHash();
    }

    /**
     * Constructor of HashingObject when logging in
     *  - We generate hash based on the given code & salt of the hash
     *  - We replace the last saltLength digits with the salt of the hash
     * @param code
     *              The code to be checked
     * @param hash
     *              The hash to which the code needs to be checked
     */
    public HashingObject(String code, String hash) throws NoSuchAlgorithmException {
        this.saltLength = 10;
        isLogin = false;
        this.saltString = hash.substring(hash.length()-saltLength);
        this.saltCode = code + saltString;

        hashValue = generateHash();
        hashValueFinal = pasteSaltToHash();

        checkHashes(hashValueFinal, hash);
    }

    /**
     * When logging in, checks if the two hashes are equal
     * @param newHash
     *                  The generated has based on the code to be checked & the salt of the given hash
     * @param codeHash
     *                  The hash code retrieved from the database.
     */
    private void checkHashes(String newHash, String codeHash){
        if(newHash.equals(codeHash)){
            isLogin = true;
        }
        isLogin = false;
    }

    /**
     * To check if the hashes where equal
     * @return
     *          True if the code was equal to the hash
     */
    public boolean getAuthenticationSuccess(){
        return isLogin;
    }

    public String getGeneratedHash(){
        return hashValueFinal;
    }

    /**
     * Generates random alphanumeric string with length saltLenght
     * @return
     *          The generated string
     */
    private String generateSalt(){
        return  RandomStringUtils.randomAlphanumeric(saltLength);
    }

    /**
     * Generate a hash code based on the salted code
     * @return
     *          The hash code in String format
     * @throws NoSuchAlgorithmException
     *          If the SHA-256 algorithm isn't found
     */
    private String generateHash() throws NoSuchAlgorithmException{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = "failed".getBytes();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            hash = digest.digest(saltCode.getBytes(StandardCharsets.UTF_8));
        }
        return bytesToHex(hash);
    }

    private String generateHash2(String code) throws NoSuchAlgorithmException{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = "failed".getBytes();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            hash = digest.digest(code.getBytes(StandardCharsets.UTF_8));
        }
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String pasteSaltToHash(){
        String hashPart = hashValue.substring(0, 54);
        System.out.println("First part: " + hashPart);
        String saltPart = saltString;
        System.out.println("Second part: " + saltPart);
        String result = hashPart + saltPart;
        System.out.println("Result: " + result);
        return result;
    }

    private String pasteSaltToHash2(String hash, String salt){
        String hashPart = hash.substring(0, 54);
        System.out.println("First part: " + hashPart);
        System.out.println("Second part: " + salt);
        String result = hashPart + salt;
        System.out.println("Result: " + result);
        return result;
    }

}