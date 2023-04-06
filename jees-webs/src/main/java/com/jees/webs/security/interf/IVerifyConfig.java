package com.jees.webs.security.interf;

import com.jees.webs.entity.SuperUser;
import com.jees.webs.security.struct.PageAccess;

import java.util.Map;
import java.util.Set;

public interface IVerifyConfig{
    void initialize();
    SuperUser findUserByUsername(String _username);
    Set<String> getBlackUsers();
    Set<String> getBlackRoles();
    Set<String> getBlackIps();
    Set<String> getAnonymous();
    Map<String, PageAccess> getAuths();
    PageAccess findPageByUri( String[] _uri );
}
