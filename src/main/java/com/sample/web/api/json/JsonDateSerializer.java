package com.sample.web.api.json;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.util.Date;

/**
 * Serialize Dates in ISO 8061 format
 *
 * <code>2012-07-02T07:21:49Z</code>
 *
 * @author: Iain Porter
 */
public class JsonDateSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date date, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();
        jgen.writeString(formatter.print(new DateTime(date)));
    }
}
