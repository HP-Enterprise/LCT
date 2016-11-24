package com.hp.lct.entity;

/**
 * Created by jackl on 2016/11/14.
 */
public class MsgBean {

    private LctMsgBody body;
    private LctMsgHead head;

    public MsgBean(){

    }

    public MsgBean(LctMsgHead head,LctMsgBody body) {
        this.body = body;
        this.head = head;
    }

    public LctMsgBody getBody() {
        return body;
    }

    public void setBody(LctMsgBody body) {
        this.body = body;
    }

    public LctMsgHead getHead() {
        return head;
    }

    public void setHead(LctMsgHead head) {
        this.head = head;
    }
}
