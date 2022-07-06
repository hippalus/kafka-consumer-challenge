package com.company.vertical.events;

import java.io.Serializable;

public class MigrateUser implements Serializable {

    private long userId;

    public MigrateUser() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "MigrateUser{" +
                "userId='" + userId + '\'' +
                '}';
    }
}
