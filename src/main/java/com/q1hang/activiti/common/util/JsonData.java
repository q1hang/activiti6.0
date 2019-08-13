package com.q1hang.activiti.common.util;

import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
public class JsonData<D> {

    private int status;
    private String msg;
    private D data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    public JsonData(int status, String msg, D data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    public JsonData(){}

    public static <D> JsonData<D> success(D data){
        JsonData jsonData=new JsonData();
        jsonData.setStatus(200);
        jsonData.setData(data);
        return jsonData;
    }

    public static  <D> JsonData<D> success(D data,String msg){
        JsonData jsonData=new JsonData();
        jsonData.setStatus(200);
        jsonData.setMsg(msg);
        jsonData.setData(data);
        return jsonData;
    }

    public static JsonData success(String msg){
        JsonData jsonData=new JsonData();
        jsonData.setStatus(200);
        jsonData.setMsg(msg);
        return jsonData;
    }

    public static JsonData error(String msg){
        JsonData jsonData=new JsonData();
        jsonData.setStatus(500);
        jsonData.setMsg(msg);
        return jsonData;
    }

    public Map<String,Object> toMap(){
        HashMap<String,Object> result=new HashMap<>();
        result.put("status",status);
        result.put("msg",msg);
        result.put("data",data);
        return result;
    }

}
