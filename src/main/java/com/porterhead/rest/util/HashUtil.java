package com.porterhead.rest.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;


public class HashUtil {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Enter a String to sign");
            System.exit(-1);
        }
        System.out.println("Signed String: " + signString(args[0]));
    }

    /**
     * From a base 64 representation, returns the corresponding byte[]
     *
     * @param data String The base64 representation
     * @return byte[]
     * @throws java.io.IOException
     */
    public static byte[] base64ToByte(String data) throws IOException {
        return Base64.decodeBase64(data);
    }

    /**
     * From a byte[] returns a base 64 representation
     *
     * @param data byte[]
     * @return String
     * @throws IOException
     */
    public static String byteToBase64(byte[] data) {
        return new String(Base64.encodeBase64(data));
    }

    private static String signString(String request) {
        byte[] digest = DigestUtils.sha256(request);
        return new String(Base64.encodeBase64(digest));
    }


}
