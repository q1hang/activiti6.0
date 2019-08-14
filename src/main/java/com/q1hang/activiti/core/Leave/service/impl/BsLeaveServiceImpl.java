package com.q1hang.activiti.core.Leave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.q1hang.activiti.core.Leave.dto.StartDto;
import com.q1hang.activiti.core.Leave.dto.TaskDto;
import com.q1hang.activiti.core.Leave.entity.BsProcessStatus;
import com.q1hang.activiti.core.Leave.service.IBsLeaveService;
import com.q1hang.activiti.core.Leave.entity.BsLeave;
import com.q1hang.activiti.core.Leave.mapper.BsLeaveMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q1hang.activiti.core.User.entity.SysAdministrators;
import com.q1hang.activiti.core.User.entity.SysUser;
import com.q1hang.activiti.core.User.service.impl.SysAdministratorsServiceImpl;
import com.q1hang.activiti.core.User.service.impl.SysUserServiceImpl;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qihang
 * @since 2019-08-13
 */
@Service
public class BsLeaveServiceImpl extends ServiceImpl<BsLeaveMapper, BsLeave> implements IBsLeaveService {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BsProcessStatusServiceImpl bsProcessStatusService;
    @Autowired
    private SysUserServiceImpl sysUserService;
    @Autowired
    private SysAdministratorsServiceImpl sysAdministratorsService;
    @Autowired
    private HistoryService historyService;


    /**
     * 开启流程
     * @param business
     * @param day
     */
    public void startProcess(String business ,Double day){
        ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("day",day);
        ProcessInstance pi = processInstanceBuilder.businessKey(business).processDefinitionKey("leave")
                .variables(variables).start();
    }


    /**
     * 执行流程
     * @param business
     * @param varName
     * @param opinion
     * @return
     */
    public TaskDto Approval(String business, Integer userId,String varName, String opinion){
        //TODO 完成登录功能后才能实现验证身份

        Task task = taskService.createTaskQuery().processInstanceBusinessKey(business).singleResult();
        if(task!=null){
            Map<String, Object> variables1 = new HashMap<>();
            variables1.put(varName, opinion);
            taskService.complete(task.getId(), variables1);
        }
        Task nextTask = taskService.createTaskQuery().processInstanceBusinessKey(business).singleResult();
        //设置办理人
        if(nextTask!=null)
            setAss(nextTask,userId);

        //保存Task任务信息
        BsProcessStatus bsProcessStatus=new BsProcessStatus();
        bsProcessStatus.setTaskId(task.getId());
        bsProcessStatus.setTaskName(task.getName());
        bsProcessStatus.setApproveRemark("审批说明");
        bsProcessStatus.setProcessBusiness(business);
        bsProcessStatus.setApprover(task.getAssignee());
        bsProcessStatus.setCreateTime(new Date());
        //判断是否结束流程，记录审批结果
        if(opinion.equals("yes")||opinion.equals("Yes")){
            bsProcessStatus.setApproveResult(2);
            bsProcessStatusService.save(bsProcessStatus);
            if(nextTask==null){
                BsLeave process_business=new BsLeave();
                process_business.setStatus(2);
                update(process_business,new QueryWrapper<BsLeave>().eq("process_business",business));
            }
            return new TaskDto(task).pass();
        }else {
            bsProcessStatus.setApproveResult(0);
            bsProcessStatusService.save(bsProcessStatus);
            if(nextTask==null){
                BsLeave process_business=new BsLeave();
                process_business.setStatus(0);
                update(process_business,new QueryWrapper<BsLeave>().eq("process_business",business));
            }
            return new TaskDto(task).reject();
        }
    }

    /**
     * 给任务设置办理人
     * @param task
     * @param userId
     */
    public void setAss(Task task,Integer userId){
        SysUser user = sysUserService.getById(userId);
        System.out.println(user.toString());
        if(task.getTaskDefinitionKey().equals("Instructor")){
            QueryWrapper<SysAdministrators> wrapper = new QueryWrapper<SysAdministrators>()
                    .eq("department", user.getDepartment())
                    .eq("position","Instructor");
            SysAdministrators admin = sysAdministratorsService.getOne(wrapper,false);
            taskService.setAssignee(task.getId(),admin.getFullName());
        }else if (task.getTaskDefinitionKey().equals("President")){
            QueryWrapper<SysAdministrators> wrapper = new QueryWrapper<SysAdministrators>()
                    .eq("department", user.getDepartment())
                    .eq("position","President");
            SysAdministrators admin = sysAdministratorsService.getOne(wrapper,false);
            taskService.setAssignee(task.getId(),admin.getFullName());
        }else {
            taskService.setAssignee(task.getId(),user.getFullName());
        }
    }


    /**
     * 展示某个办理人的所有已办
     * @param assignee
     * @return
     */
    public List<TaskDto> ADbyAssignee(String assignee){
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().taskAssignee(assignee).listPage(0, 100);
        if (CollectionUtils.isEmpty(historicTaskInstances)) {
            return Lists.newArrayList();
        }else{
            List<TaskDto> collect = historicTaskInstances.stream().map(x -> new TaskDto(x).pass()).collect(Collectors.toList());
            //TODO 这个状态是拒绝还是通过需要重新写
            return collect;
        }
    }

    /**
     * 展示某个business所有任务
     * @param business
     * @return
     */
    public List<TaskDto> listOfProcess(String business){
        List<HistoricTaskInstance> allTasks = historyService.createHistoricTaskInstanceQuery().processInstanceBusinessKey(business).listPage(0, 100);
        if (CollectionUtils.isEmpty(allTasks)) {
            return Lists.newArrayList();
        }else{
            List<TaskDto> collect = allTasks.stream().map(x -> new TaskDto(x).pass()).collect(Collectors.toList());
            //TODO 这个状态是拒绝还是通过需要重新写
            return collect;
        }
    }

}
