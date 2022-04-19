package com.jees.webs.support;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;

public interface IVerifyService {

    void initialize();

    UserDetails findUserByUsername(String _username);

}
