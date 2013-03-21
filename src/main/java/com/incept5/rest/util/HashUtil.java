package com.incept5.rest.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

/**
 * Generated on behalf of C24 Technologies Ltd.
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@c24.biz
 * @since 30/01/2013
 */
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
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(data);
    }

    /**
     * From a byte[] returns a base 64 representation
     *
     * @param data byte[]
     * @return String
     * @throws IOException
     */
    public static String byteToBase64(byte[] data) {
        BASE64Encoder endecoder = new BASE64Encoder();
        return endecoder.encode(data);
    }

    private static String signString(String request) {
        byte[] digest = DigestUtils.sha256(request);
        return new String(Base64.encodeBase64(digest));
    }


}
