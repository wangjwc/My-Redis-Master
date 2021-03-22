package com.learn.redis.spring.pojo;

import java.util.Date;
import java.util.List;

public class User implements java.io.Serializable {
    private static final long serialVersionUID = -8067922905221189448L;
    private long id;
    private String name;
    private List<Role> roles;
    private Date createTime;

    public User(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        String dateStr = new java.text.SimpleDateFormat("yyyy/MM/dd-hh:mm:ss").format(createTime);
        StringBuilder roleStr = new StringBuilder();
        roles.forEach(r -> roleStr.append(r.toString()).append(","));
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", roles=[" + roleStr + "]"+
                ", createTime=" + dateStr +
                '}';
    }
}
