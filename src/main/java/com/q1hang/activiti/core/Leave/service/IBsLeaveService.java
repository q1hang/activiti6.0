package com.q1hang.activiti.core.Leave.service;

import com.q1hang.activiti.core.Leave.dto.TaskDto;
import com.q1hang.activiti.core.Leave.entity.BsLeave;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qihang
 * @since 2019-08-13
 */
public interface IBsLeaveService extends IService<BsLeave> {

    public TaskDto Approval(String business, String varName, String opinion);

}
