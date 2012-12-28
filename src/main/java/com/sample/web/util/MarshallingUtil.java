package com.sample.web.util;

import com.sample.web.service.exception.ApplicationRuntimeException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 26/10/2012
 */
public class MarshallingUtil {

        private static Logger LOG = LoggerFactory.getLogger(MarshallingUtil.class);
       /**
        * Marshall the object to a Json String
        * Default is to NOT wrap the root value
        * @param obj
        * @return a json string
        */
       public static String marshalJson(Object obj) {
           return marshalJson(obj, false);
       }

       public static String marshalJson(Object obj, boolean wrapRootValue) {
           try {
               final ObjectMapper mapper = new ObjectMapper();
               String json = mapper.writeValueAsString(obj);
               return json;
           }
           catch (Exception e) {
               LOG.error("Error marshalling object to string.  obj:" + obj, e);
               throw new ApplicationRuntimeException("Error marshalling object to string");
           }
       }


       public static <T> T unmarshalJson(String src, Class<T> clazz) {
           try {
               final ObjectMapper mapper = new ObjectMapper();
               T obj = mapper.readValue(src, clazz);
               return obj;
           } catch (UnrecognizedPropertyException e) {
                LOG.error("Unrecognized property. Error unmarshalling String to Object.  src:" + src, e);
               throw new ApplicationRuntimeException("Unrecognized property while unmarshaling string to object");
           } catch (JsonMappingException e) {
                LOG.error("Error mapping json to object.  obj:" + src, e);
               throw new ApplicationRuntimeException("Error mapping json to object");
           } catch (Exception e) {
               LOG.error("Error unmarshalling string to object.  obj:" + src, e);
               throw new ApplicationRuntimeException("Error unmarshalling string to object");
           }
       }
   }

