package com.geektcp.common.spring.model.po;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.geektcp.common.spring.jpa.JpaBasePoListener;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * @author tanghaiyang on 2020/9/19 11:54.
 */
@Data
@MappedSuperclass
@EntityListeners(JpaBasePoListener.class)
public class BasePo extends Po {

    @Column(name = "enable")
    @ApiModelProperty(hidden = true)
    @ExcelIgnore
    private Long enable;

    @Column(name = "create_by")
    @ApiModelProperty(hidden = true)
    @ExcelIgnore
    private String createBy;

    @Column(name = "create_date")
    @ApiModelProperty(hidden = true)
    @ExcelIgnore
    private Date createDate;

    @Column(name = "update_by")
    @ApiModelProperty(hidden = true)
    @ExcelIgnore
    private String updateBy;

    @Column(name = "update_date")
    @ApiModelProperty(hidden = true)
    @ExcelIgnore
    private Date updateDate;

}
