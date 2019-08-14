package com.q1hang.activiti.core.Leave.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.activiti.engine.task.Task;

import java.util.Date;

@Data
public class TaskDto {

    //流程实例id
    public String processInstanceId;

    //任务id
    public String taskId;

    //任务名称
    public String taskName;

    //任务办理人
    public String assignee;

    //任务创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Date createTime;

    //流程任务状态
    public String status;


    public TaskDto(Task task){
        processInstanceId=task.getProcessInstanceId();
        taskId=task.getId();
        taskName=task.getName();
        assignee=task.getAssignee();
        createTime=task.getCreateTime();
    }

    public TaskDto pass(){
        setStatus("pass");
        return this;
    }

    public TaskDto pending(){
        setStatus("pending");
        return this;

}
    public TaskDto reject(){
        setStatus("reject");
        return this;
    }
}
