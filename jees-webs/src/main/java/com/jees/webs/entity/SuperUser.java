package com.jees.webs.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.jees.tool.utils.JsonUtil;
import lombok.*;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Id;
import java.util.HashSet;
import java.util.Set;


@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@DataTransferObject
public class SuperUser<R extends SuperRole, M extends  SuperMenu> implements UserDetails{
    @Id
    @RemoteProperty
    long                          id;
    @RemoteProperty
    String                        username;
    @RemoteProperty
    String                        password;
    @RemoteProperty
    boolean                       enabled;
    @RemoteProperty
    boolean                       locked;
    @RemoteProperty
    Set< Integer >                roles       = new HashSet<>();
    @RemoteProperty
    @JSONField( serialize = false )
    Set< SimpleGrantedAuthority > authorities = new HashSet<>();
    @RemoteProperty
    @JSONField( serialize = false )
    Set< M >                      menus       = new HashSet<>();

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled(){
        return enabled;
    }

    @Override
    public String toString(){
        return JsonUtil.toString( this );
    }

    public void addRole( R _role ){
        if( !roles.contains( _role.getId() ) ){
            roles.add( _role.getId() );
            authorities.add( new SimpleGrantedAuthority(_role.getName() ) );
        }
    }

    public long getRoles(){
        return roles.stream().mapToLong(Integer::longValue).sum();
    }
}
