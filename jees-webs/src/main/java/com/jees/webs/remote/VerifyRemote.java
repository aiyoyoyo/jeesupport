package com.jees.webs.remote;

import com.jees.webs.core.interf.ISupportEL;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;

@Log4j2
@RemoteProxy
public class VerifyRemote implements ISupportEL {

    @SneakyThrows
    @RemoteMethod
    public void test1() {
//        localConfig.loadConfig();

        // users
//        localConfig.addItem("users", "tester", "1234" );
//        localConfig.changeItem("users", "tester", "4321" );
//        localConfig.removeItem("users", "tester", null );
        // roles
//        localConfig.addItem("roles", "tester1", "tester" );
//        localConfig.changeItem("roles", "tester", "john,tester" );
//        localConfig.removeItem("roles", "tester", null );
        // black
//        localConfig.changeItem("black", "user", "john,tester" );
//        localConfig.changeItem("black", "ip", "10.10.10.10,10.10.10.*,10.10.*" );
//        localConfig.changeItem("black", "role", "black" );
        // page
//        localConfig.addPage("/test12" );
//        localConfig.removePage("/test1" );
//        localConfig.changeItem( "/test1", "user", "john" );
//        localConfig.changeItem( "/test1", "role", "manager" );
//        localConfig.changeItem( "/test1", "deny", "lina" );
//        localConfig.changeItem( "/test1", "anonymous", "true" );
    }

    public void changeUserPassword(String _username, String _password) throws Exception {
//        String password = this.verifyService.encodeString(_password);
//        localConfig.backup();
//        localConfig.loadConfig();
//        localConfig.changeItem("users", _username, password );
//        verifyService.updateUser( _username, _password );
    }
}
