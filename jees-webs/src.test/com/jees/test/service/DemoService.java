package com.jees.test.service;

import com.jees.test.entity.User;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.stereotype.Service;

@Service
@RemoteProxy
public class DemoService {
	@RemoteMethod
    public String hello() {
        return "hello";
    }
	@RemoteMethod
    public String echo(String string) {
        return string;
    }

    @RemoteMethod
    public User user() {
	    User user = new User();
	    user.setUsername("123123");
        return user;
    }

    @RemoteMethod
    public User user(User user) {
	    return user;
    }
}
