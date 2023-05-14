package com.geektcp.common.spring.constant;


import com.geektcp.common.mosheh.constant.Status;

public enum ProgressStatus implements Status {

    COMPLETED(0, "finished"),
    PROCESSING(1, "processing"),
    FAILED(2, "failed");

    private int code;
    private String desc;

    ProgressStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

}
