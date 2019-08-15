package com.q1hang.activiti.core.Leave.controller;



import com.google.common.collect.Lists;
import com.q1hang.activiti.common.exception.ParamException;
import com.q1hang.activiti.core.Leave.dto.StartDto;
import com.q1hang.activiti.core.Leave.dto.TaskDto;
import com.q1hang.activiti.core.Leave.entity.BsLeave;
import com.q1hang.activiti.core.Leave.service.impl.BsLeaveServiceImpl;
import com.q1hang.activiti.core.Leave.service.impl.BsProcessStatusServiceImpl;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        repositoryService.createDeployment().addClasspathResource("leave.bpmn20.xml").deploy();
    }

    /**
     * 禁止请假流程
     */
    @RequestMapping("stop")
    public String stopStratProcess(){
        repositoryService.suspendProcessDefinitionByKey("leave");
        return "已禁止";
    }

    /**
     * 恢复请假流程
     */
    @RequestMapping("recover")
    public String recoverStratProcess(){
        repositoryService.activateProcessDefinitionByKey("leave");
        return "已恢复";
    }

    /**
     * 开启一个请假流程
     * @param startDto
     * @return
     */
    @PostMapping("start")
    public String testLeave(@RequestBody StartDto startDto){
        Double day=(startDto.getEndingTime().getTime()-startDto.getStartTime().getTime())/86400000.0;
        if(day<=0){
            return "日期不正确";
        }
        //判断business是否已经有该名称的流程正在流转
        String business=startDto.getBusiness();

        Task isExist = taskService.createTaskQuery().processInstanceBusinessKey(business).singleResult();
        if(isExist!=null){
            return "该business: "+business+"已经存在.";
        }


        //保存请假信息
        BsLeave leave=new BsLeave();
        leave.setUserId(1);//TODO 用户ID
        leave.setLeaveType("事假");
        leave.setRemarks("家里有事");
        leave.setProcessBusiness(business);
        leave.setStatus(1);
        leave.setStartTime(new Date());
        leave.setEndingTime(new Date());
        leave.setRecordTime(new Date());
        bsLeaveService.save(leave);
        //开启流程
        bsLeaveService.startProcess(business,day);
        //设置办理人
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(business).singleResult();
        bsLeaveService.setAss(task,1);//TODO 用户ID
        return task==null?"流程启动失败":"流程启动成功";
    }

    /**
     * 当前business流程任务
     * @param business
     * @return
     */
    @RequestMapping("selectBusiness/{business}")
    public TaskDto testSelectBusiness(@PathVariable String business){
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(business).singleResult();
        return task==null?null:new TaskDto(task).pending();
    }

    /**
     * 查看办理人的待办流程任务
     * @param assignee
     * @return
     */
    @RequestMapping("NTBDWbyAssignee/{assignee}")
    public List<TaskDto> testSelectAssignee1(@PathVariable String assignee){
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).listPage(0, 100);
        if (CollectionUtils.isEmpty(tasks)) {
            return Lists.newArrayList();
        }else{
            List<TaskDto> collect = tasks.stream().map(x -> new TaskDto(x).pending()).collect(Collectors.toList());
            return collect;
        }
    }


    /**
     * 查看办理人的已办流程任务
     * @param assignee
     * @return
     */
    @RequestMapping("ADbyAssignee/{assignee}")
    public List<TaskDto> testAlreadydone(@PathVariable String assignee){
        return bsLeaveService.ADbyAssignee(assignee);
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
        TaskDto task = bsLeaveService.Approval(business,1, varname, opinion); //TODO 用户ID
        return task;
    }

    @RequestMapping("listOfProcess/{business}")
    public List<TaskDto> listOfProcess(@PathVariable String business){
        return bsLeaveService.listOfProcess(business);
    }

    //

}

