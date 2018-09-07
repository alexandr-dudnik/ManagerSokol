package com.sokolua.manager.data.network.res;

public class SendNewObjectRes {
    private String old_id;
    private String new_id;

    public SendNewObjectRes(String old_id, String new_id) {
        this.old_id = old_id;
        this.new_id = new_id;
    }

    public String getOldId() {
        return old_id;
    }

    public String getNewId() {
        return new_id;
    }
}
