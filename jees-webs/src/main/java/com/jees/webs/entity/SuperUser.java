package com.jees.webs.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.jees.tool.utils.JsonUtil;
import lombok.*;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Id;
import java.util.*;


@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@DataTransferObject
public class SuperUser implements UserDetails{
    @RemoteProperty
    String                        id;               // 用户ID
    @RemoteProperty
    String                        username;         // 用户名
    @RemoteProperty
    String                        password;         // 密码
    @RemoteProperty
    boolean                       accountNonLocked;  // 账号是否锁定
    @RemoteProperty
    boolean                       accountNonExpired; // 账号是否过期
    @RemoteProperty
    boolean                       credentialsNonExpired; // 密码是否过期
    @RemoteProperty
    boolean                       enabled;              // 是否有效
    @RemoteProperty
    @JSONField( serialize = false )
    Set< SimpleGrantedAuthority > authorities = new HashSet<>();
    // 扩展信息
    @RemoteProperty
    boolean                       thirdUser;            // 第三方用户
    @RemoteProperty
    Map< String, Object >         properties = new HashMap<>(); // 其他信息

    @Override
    public String toString(){
        return JsonUtil.toString( this );
    }

    public void addRole( String _role ){
        SimpleGrantedAuthority role = new SimpleGrantedAuthority(_role);
        if( !authorities.contains( role ) ){
            authorities.add( role );
        }
    }

    public String[] getRoles() {
        String[] roles = new String[authorities.size()];
        Iterator<SimpleGrantedAuthority> auth_it = authorities.iterator();
        int i = 0;
        while( i ++ < authorities.size() ){
            roles[i] = auth_it.next().toString();
        }
        return roles;
    }
}
