package com.q1hang.activiti.core.bpm.service;

import com.q1hang.activiti.core.bpm.dto.TaskDto;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Activitiservice {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    public List<TaskDto> setSingleAssignee(String business) {

        //根据bpmn文件部署流程
        repositoryService.createDeployment().addClasspathResource("singleAssignee.bpmn").deploy();
        // 设置User Task1受理人变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("user1", "007");
        //采用key来启动流程定义并设置流程变量，返回流程实例
        ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder();
        ProcessInstance pi = processInstanceBuilder.businessKey(business).processDefinitionKey("singleAssignee")
                .variables(variables).start();

        String processId = pi.getId();
        System.out.println("流程创建成功，当前流程实例ID：" + processId);


        List<TaskDto> result=new ArrayList<>();
        // 注意 这里需要拿007来查询，key-value需要拿value来获取任务
        List<Task> list = taskService.createTaskQuery().taskAssignee("007").list();
        if (list != null && list.size() > 0) {
            for (Task task : list) {
                result.add(new TaskDto(task).pass());
            }
        }
        // 设置User Task2的受理人变量
        Map<String, Object> variables1 = new HashMap<>();
        variables1.put("user2", "Kevin");
        // 因为007只有一个代办的任务，直接完成任务，并赋值下一个节点的受理人user2为Kevin办理
        taskService.complete(list.get(0).getId(), variables1);
        System.out.println("User Task1被完成了，此时流程已流转到User Task2");
        return result;
    }
}

