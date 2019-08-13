package com.q1hang.activiti.common.config;


import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import com.q1hang.activiti.common.util.JsonData;
import com.q1hang.activiti.common.util.JsonMapper;

@Aspect
@RestControllerAdvice(value = "com.q1hang.activiti.core")
public class DataResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (o instanceof JsonData) {
            return o;
        } else if (o instanceof String) {
            //对String类型作处理 不然会报错
            JsonData<Object> dataResponse = new JsonData<>(200, "success", o);
            return JsonMapper.obj2String(dataResponse);
        } else {
            return new JsonData<>(200, "success", o);
        }
    }
}
