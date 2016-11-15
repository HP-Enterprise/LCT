package com.hp.lct.entity;

/**
 * Created by jackl on 2016/11/15.
 */
public class ObjectResult {
    private boolean status;
    private String msg;

    public ObjectResult() {
    }

    public ObjectResult(boolean status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
