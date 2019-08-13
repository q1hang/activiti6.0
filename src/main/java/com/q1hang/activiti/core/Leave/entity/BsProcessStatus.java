package com.q1hang.activiti.core.Leave.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
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
     * 流程实例ID
     */
    private String processId;

    /**
     * 流程标识
     */
    private String processBusiness;

    /**
     * 审批人
     */
    private Integer approver;

    /**
     * 审批时间
     */
    private LocalDateTime createTime;

    /**
     * 审批结果,0：驳回,1：审批中,2:审批通过
     */
    private Integer approveResult;

    /**
     * 审批备注
     */
    private String approveRemark;


}
