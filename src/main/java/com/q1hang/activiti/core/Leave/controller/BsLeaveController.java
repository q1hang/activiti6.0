package com.q1hang.activiti.core.Leave.controller;


import com.google.common.collect.Maps;
import com.q1hang.activiti.core.Leave.dto.TaskDto;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  请假前端控制器
 * </p>
 *
 * @author qihang
 * @since 2019-08-13
 */
@RestController
@RequestMapping("/Leave/bs-leave")
public class BsLeaveController {


    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @RequestMapping("start/{business}")
    public String testLeave(@PathVariable String business){
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("leave.bpmn20.xml").deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();

        ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("day","2");
        ProcessInstance pi = processInstanceBuilder.businessKey(business).processDefinitionKey("leave")
                .variables(variables).start();

        String processId = pi.getId();
        System.out.println("流程创建成功，当前流程实例ID：" + processId);

        return processId;
    }


    @RequestMapping("select/{business}")
    public TaskDto testSelect(@PathVariable String business){
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(business).singleResult();
        return task==null?null:new TaskDto(task);
    }

    @RequestMapping("approval/{business}")
    public TaskDto testApproval(@PathVariable String business){
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(business).singleResult();
        if(task!=null){
            Map<String, Object> variables1 = new HashMap<>();
            variables1.put("InstructorOpinion", "yes");
            taskService.complete(task.getId(), variables1);
        }
        return new TaskDto(task);
    }



}

