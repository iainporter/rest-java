package com.porterhead.rest.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

import static org.springframework.util.Assert.hasText;

/**
 * Author: Iain porter
 */
public class StringUtil {

    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}$");

	public static void minLength(String str, int len) throws IllegalArgumentException {
		hasText(str);
		if (str.length() < len) {
			throw new IllegalArgumentException();
		}
	}

	public static void maxLength(String str, int len) throws IllegalArgumentException {
		hasText(str);
		if (str.length() > len) {
			throw new IllegalArgumentException();
		}
	}

	public static void validEmail(String email) throws IllegalArgumentException {
		minLength(email, 4);
        maxLength(email, 255);
		if (!email.contains("@") || StringUtils.containsWhitespace(email)) {
			throw new IllegalArgumentException();
		}
	}

    public static boolean isValidUuid(String uuid) {
        return UUID_PATTERN.matcher(uuid).matches();
    }

}
