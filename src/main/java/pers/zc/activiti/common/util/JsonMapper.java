package pers.zc.activiti.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;

@Slf4j
public class JsonMapper {

    private static ObjectMapper objectMapper=new ObjectMapper();

    static {
        //config
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }

    public static <T> String obj2String(T src){
        if (src == null) {
            return null;
        }
        try{
            return src instanceof String ?(String)src:objectMapper.writeValueAsString(src);
        }catch (Exception e){
            log.warn("parse object to String exception,error:{}",e);
            return null;
        }
    }

    public static <T> T string2Obj(String src, TypeReference<T> typeReference ){
        if (StringUtils.isEmpty(src)||typeReference==null) {
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class)?src:objectMapper.readValue(src,typeReference));
        }catch (Exception e){
            log.warn("parse String to Object exception,Stirng:{},TypeReference<T>:{},error:{}",src,typeReference,e);
            return null;
        }
    }

    public static <T> T string2Obj(String str,Class<T> clazz){
        if (StringUtils.isEmpty(str) || clazz==null){
            return null;
        }
        try {
            return clazz.equals(String.class)?(T)str : objectMapper.readValue(str,clazz);
        } catch (IOException e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }
}
