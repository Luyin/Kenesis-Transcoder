package com.kenesis.transcoder.domain;

public class EncodeVO {
    private int fileid;
    private String status;
    private int progress;
    public int getFileid() {
        return fileid;
    }
    public void setFileid(int fileid) {
        this.fileid = fileid;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getProgress() {
        return progress;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }
    @Override
    public String toString() {
        return "EncodeVO [fileid=" + fileid + ", status=" + status + ", progress=" + progress + "]";
    }
}
