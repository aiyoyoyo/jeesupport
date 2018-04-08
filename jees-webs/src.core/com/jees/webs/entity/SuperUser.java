package com.jees.webs.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@MappedSuperclass
public class SuperUser<ID extends Serializable, R extends SuperRole> implements UserDetails {
    @Id
    @GeneratedValue( strategy = IDENTITY )
    @GenericGenerator( name = "generator" , strategy = "identity" )
    @Column( name = "id" , unique = true , nullable = false )
    private ID id;
    @Column( name = "username" , unique = true, nullable = false )
    private String username;
    @Column( name = "password" , nullable = false )
    private String password;
    @Column( name = "enabled" , nullable = false )
    private boolean enabled;
    @Column( name = "locked" , nullable = false )
    private boolean locked;
    @OneToMany( cascade = CascadeType.REMOVE , fetch = FetchType.EAGER )
    @MapKey( name = "id" )
    private Map<Integer, R> roles = new HashMap<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.values().stream().map( r -> new SimpleGrantedAuthority( r.getName() ) ).collect(Collectors.toList() );
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void addRole( R _role ){
        roles.put( _role.getId(), _role );
    }
}
