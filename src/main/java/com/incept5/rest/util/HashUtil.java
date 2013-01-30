package com.incept5.rest.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Generated on behalf of C24 Technologies Ltd.
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@c24.biz
 * @since 30/01/2013
 */
public class HashUtil {

    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Enter a String to sign");
            System.exit(-1);
        }
        System.out.println("Signed String: " + signString(args[0]));
    }

    private static String signString(String request) {
        byte[] digest = DigestUtils.sha256(request);
        return new String(Base64.encodeBase64(digest));
    }
}
