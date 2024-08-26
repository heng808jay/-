package com.access.accessauth.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("v_zfxg_qjsq_jgb")
public class ApplyJob {

    @TableField(value = "XH")
    private String xh;

    @TableField(value = "XM")
    private String xm;

    @TableField(value = "QJKSSJ")
    private Date qjkssj;

    @TableField(value = "QJJSSJ")
    private Date qjjssj;

    @TableField(value = "ZJHM")
    private String zjhm;

}
