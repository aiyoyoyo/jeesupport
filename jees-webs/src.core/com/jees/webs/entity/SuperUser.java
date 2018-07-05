package com.jees.webs.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@MappedSuperclass
public class SuperUser<ID extends Serializable, R extends SuperRole, M extends  SuperMenu> implements UserDetails {
    @Id
    @GeneratedValue( strategy = IDENTITY )
    @GenericGenerator( name = "generator" , strategy = "identity" )
    @Column( name = "id" , unique = true , nullable = false )
    private ID id;
    @Column( name = "username" , nullable = false )
    private String username;
    @Column( name = "password" , nullable = false )
    private String password;
    @Column( name = "enabled" , nullable = false )
    private boolean enabled;
    @Column( name = "locked" , nullable = false )
    private boolean locked;
    @ManyToMany(cascade=CascadeType.REMOVE,fetch=FetchType.LAZY)
    @JoinTable( name="user_role",joinColumns = {@JoinColumn(name="user_id")},
            inverseJoinColumns =@JoinColumn(name = "role_id"))
    @MapKey( name = "id" )
    private Map<Serializable, R> roles = new HashMap<>();
    @Transient
    private Set<M> menus = new HashSet<>();
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.values().stream().map( r -> new SimpleGrantedAuthority( r.getName() ) ).collect( Collectors.toList() );
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

    public Set<M> getMenus(){
        if( menus.size() == 0 ){
            roles.values().forEach( r ->{
                menus.addAll( r.getMenus().values() );
            } );
        }
        return menus;
    }

    public SuperUser build(){
        return new SuperUser();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SuperRole) return true;
        return false;
    }

    @Override
    public int hashCode() {
        int result = 20;
        result = result * 31 + id.hashCode();
        result = result * 31 + username.hashCode();
        return result;
    }
}
