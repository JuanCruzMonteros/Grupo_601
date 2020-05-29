package data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostError {

    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("env")
    @Expose
    private String env;
    @SerializedName("msg")
    @Expose
    private String msg;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "PostError{" +
                "state='" + state + '\'' +
                ", env='" + env + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

}