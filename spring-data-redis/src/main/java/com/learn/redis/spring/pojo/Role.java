package com.learn.redis.spring.pojo;

public class Role implements java.io.Serializable {
    private static final long serialVersionUID = 7317716838446736679L;
    private String role;

    public Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return role;
    }
}
