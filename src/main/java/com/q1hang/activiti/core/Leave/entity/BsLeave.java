package com.q1hang.activiti.core.Leave.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class BsLeave extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "leave_id", type = IdType.AUTO)
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
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endingTime;

    /**
     * 申请状态,0：驳回,1：审批中,2:审批通过
     */
    private Integer status;

    /**
     * 填写时间
     */
    private LocalDateTime recordTime;

    /**
     * 流程标识
     */
    private String processBusiness;

    /**
     * 驳回说明
     */
    private String rejectionNote;


}
