package com.app.msqltext;

import com.app.annotation.Column;
import com.app.annotation.SQLite;
import com.app.data.SQLSupport;

@SQLite
public class TestV extends SQLSupport {
    private String name;
    private int version = 1;
    private boolean isEager = false;
    private long times = 2;
    private char cha = 1;
    @Column(defaultValue = "这是一段默认值")
    private String tag;
    private String ver;

    public String ver() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int version() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean eager() {
        return isEager;
    }

    public void setEager(boolean eager) {
        isEager = eager;
    }

    public long times() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }

    public char cha() {
        return cha;
    }

    public void setCha(char cha) {
        this.cha = cha;
    }

    public String tag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
