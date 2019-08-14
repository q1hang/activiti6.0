package com.q1hang.activiti.core.Leave.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author qihang
 * @since 2019-08-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class BsProcessStatus extends Model {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;


    /**
     * 流程标识
     */
    private String processBusiness;

    /**
     * 审批人
     */
    private String approver;

    /**
     * 审批时间
     */
    private Date createTime;

    /**
     * 审批结果,0：驳回,1：审批中,2:审批通过
     */
    private Integer approveResult;

    /**
     * 审批备注
     */
    private String approveRemark;


}

