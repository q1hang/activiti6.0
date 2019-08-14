package com.q1hang.activiti.core.Leave.controller;


import com.google.common.collect.Maps;
import com.q1hang.activiti.common.exception.ParamException;
import com.q1hang.activiti.core.Leave.dto.TaskDto;
import com.q1hang.activiti.core.Leave.entity.BsLeave;
import com.q1hang.activiti.core.Leave.entity.BsProcessStatus;
import com.q1hang.activiti.core.Leave.service.IBsLeaveService;
import com.q1hang.activiti.core.Leave.service.impl.BsLeaveServiceImpl;
import com.q1hang.activiti.core.Leave.service.impl.BsProcessStatusServiceImpl;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.Date;
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
    @Autowired
    private BsLeaveServiceImpl bsLeaveService;
    @Autowired
    private BsProcessStatusServiceImpl bsProcessStatusService;

    /**
     * 部署请假流程
     */
    @RequestMapping("deploy")
    public void deploy(){
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("leave.bpmn20.xml").deploy();
    }

    /**
     * 开启一个请假流程
     * @param business
     * @return
     */
    @RequestMapping("start/{business}")
    public String testLeave(@PathVariable String business,@RequestParam Double day){
        if(day<=0){
            return "day参数不正确";
        }
        //判断business是否已经有该名称的流程正在流转
        Task isExist = taskService.createTaskQuery().processInstanceBusinessKey(business).singleResult();
        if(isExist!=null){
            return "该business: "+business+"已经存在.";
        }
        BsLeave leave=new BsLeave();
        leave.setUserId(1);
        leave.setLeaveType("事假");
        leave.setRemarks("家里有事");
        leave.setProcessBusiness(business);
        leave.setStatus(1);
        leave.setStartTime(new Date());
        leave.setEndingTime(new Date());
        leave.setRecordTime(new Date());
        bsLeaveService.save(leave);



        ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("day",day);
        ProcessInstance pi = processInstanceBuilder.businessKey(business).processDefinitionKey("leave")
                .variables(variables).start();
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(business).singleResult();

        return task==null?"流程启动失败":"流程启动成功";
    }

    /**
     * 当前business流程任务
     * @param business
     * @return
     */
    @RequestMapping("select/{business}")
    public TaskDto testSelect(@PathVariable String business){
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(business).singleResult();
        return task==null?null:new TaskDto(task).pending();
    }

    /**
     * 执行流程
     * @param business
     * @param varname
     * @param opinion
     * @return
     */
    @RequestMapping("approval/{business}")
    public TaskDto testApproval(@PathVariable String business, @RequestParam String varname,@RequestParam String opinion){
        if(opinion=="yes"||opinion=="Yes"||opinion=="no"||opinion=="No") {
            throw new ParamException("InstructorOpinion 参数异常");
        }
        TaskDto task = bsLeaveService.Approval(business, varname, opinion);
        return task;
    }

    public void list(){

    }
    

    //打印待办

    //打印已办

    //打印被驳回


}

