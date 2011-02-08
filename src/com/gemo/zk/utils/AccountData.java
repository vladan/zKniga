package com.gemo.zk.utils;

public class AccountData {
    private String mName;
    private String mType;

    public AccountData(String name, String type) {
        mName = name;
        mType = type;
    }

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }

    public String toString() {
        return mName;
    }
}