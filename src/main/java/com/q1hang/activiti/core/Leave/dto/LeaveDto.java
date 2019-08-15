package com.q1hang.activiti.core.Leave.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.q1hang.activiti.core.Leave.entity.BsLeave;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveDto {


    private Integer leaveId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 请假类型
     */
    private String leaveType;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endingTime;

    /**
     * 申请状态,0：驳回,1：审批中,2:审批通过
     */
    private Integer status;

    /**
     * 填写时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date recordTime;

    /**
     * 流程标识
     */
    private String processBusiness;

    /**
     * 驳回说明
     */
    private String rejectionNote;

    /**
     * 任务过程
     */
    private List<TaskDto> taskDtoList;


    public LeaveDto(BsLeave bsLeave){
        leaveId=bsLeave.getLeaveId();
        userId=bsLeave.getUserId();
        leaveType=bsLeave.getLeaveType();
        remarks=bsLeave.getRemarks();
        startTime=bsLeave.getStartTime();
        endingTime=bsLeave.getEndingTime();
        status=bsLeave.getStatus();
        recordTime=bsLeave.getRecordTime();
        processBusiness=bsLeave.getProcessBusiness();
        rejectionNote=bsLeave.getRejectionNote();
    }
}
