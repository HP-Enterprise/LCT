package com.hp.lct.entity;


/**
 * Created by jackl on 2016/11/15.
 */
public class ObjectResult {
    private int status;
    private Object msg;

    public ObjectResult() {
    }

    public ObjectResult(int status, Object msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
