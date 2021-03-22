package com.learn.redis.spring;

import com.learn.redis.spring.pojo.Role;
import com.learn.redis.spring.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:/spring/spring-application.xml"})
public class RedisTemplateTest {
    @Autowired
    private RedisTemplate<String, User> template;

    @Autowired
    private RedisTemplate<Long, User> template2;
    @Test
    public void test() {
        User user = new User(1001,"user1");
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("role1"));
        roles.add(new Role("role2"));
        roles.add(new Role("role2"));
        user.setRoles(roles);
        user.setCreateTime(new Date());
        template.opsForHash().put("User", user.getId(), user);

        User user2 = (User) template.opsForHash().get("User", user.getId());

        System.out.println(user2.getName());
        System.out.println(user2.toString());
    }

    @Test
    public void test2() throws InterruptedException {
        User user = new User(1001,"user1");
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("role1"));
        roles.add(new Role("role2"));
        roles.add(new Role("role2"));
        user.setRoles(roles);
        user.setCreateTime(new Date());

        ValueOperations<Long, User> op = template2.opsForValue();
        op.set(user.getId(), user, 1, TimeUnit.SECONDS);

        System.out.println(op.get(user.getId()));
        Thread.sleep(1000);
        System.out.println(op.get(user.getId()));
    }


}