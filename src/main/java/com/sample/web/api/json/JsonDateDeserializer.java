package com.sample.web.api.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.DateDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Generated on behalf of C24 Technologies Ltd.
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@c24.biz
 * @since 26/10/2012
 */
public class JsonDateDeserializer extends DateDeserializer {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String date = jp.getText().trim();
        try {
            return format.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
