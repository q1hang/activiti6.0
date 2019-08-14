package com.q1hang.activiti.core.Leave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.q1hang.activiti.core.Leave.dto.TaskDto;
import com.q1hang.activiti.core.Leave.entity.BsProcessStatus;
import com.q1hang.activiti.core.Leave.service.IBsLeaveService;
import com.q1hang.activiti.core.Leave.entity.BsLeave;
import com.q1hang.activiti.core.Leave.mapper.BsLeaveMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    private TaskService taskService;
    @Autowired
    private BsProcessStatusServiceImpl bsProcessStatusService;

    public TaskDto Approval(String business, String varName, String opinion){
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(business).singleResult();
        if(task!=null){
            Map<String, Object> variables1 = new HashMap<>();
            variables1.put(varName, opinion);
            taskService.complete(task.getId(), variables1);
        }
        //保存Task任务信息
        BsProcessStatus bsProcessStatus=new BsProcessStatus();
        bsProcessStatus.setTaskId(task.getId());
        bsProcessStatus.setTaskName(task.getName());
        bsProcessStatus.setApproveRemark("审批说明");
        bsProcessStatus.setProcessBusiness(business);
        bsProcessStatus.setApprover(task.getAssignee());
        bsProcessStatus.setCreateTime(new Date());
        //判断是否结束流程，记录审批结果
        Task result = taskService.createTaskQuery().processInstanceBusinessKey(business).singleResult();
        if(opinion.equals("yes")||opinion.equals("Yes")){
            bsProcessStatus.setApproveResult(2);
            bsProcessStatusService.save(bsProcessStatus);
            if(result==null){
                BsLeave process_business=new BsLeave();
                process_business.setStatus(2);
                update(process_business,new QueryWrapper<BsLeave>().eq("process_business",business));
            }
            return new TaskDto(task).pass();
        }else {
            bsProcessStatus.setApproveResult(0);
            bsProcessStatusService.save(bsProcessStatus);
            if(result==null){
                BsLeave process_business=new BsLeave();
                process_business.setStatus(0);
                update(process_business,new QueryWrapper<BsLeave>().eq("process_business",business));
            }
            return new TaskDto(task).reject();
        }
    }

}
