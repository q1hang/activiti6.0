package com.q1hang.activiti.core.SysUser.entity;

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
public class SysUser extends Model {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String fullName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
