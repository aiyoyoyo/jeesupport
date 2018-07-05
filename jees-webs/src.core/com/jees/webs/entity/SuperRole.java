package com.jees.webs.entity;

import lombok.Getter;
import lombok.Setter;
import org.directwebremoting.annotations.RemoteProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@MappedSuperclass
public class SuperRole <ID extends Serializable, M extends SuperMenu, U extends SuperUser >{
    @Id
    @GeneratedValue( strategy = IDENTITY )
    @GenericGenerator( name = "generator" , strategy = "identity" )
    @Column( name = "id" , unique = true , nullable = false )
    @RemoteProperty
    protected ID id;
    @Column( name = "`name`" , nullable = false )
    @RemoteProperty
    protected String name;
    @ManyToMany(cascade=CascadeType.REMOVE,fetch=FetchType.LAZY)
    @JoinTable( name="menu_role",joinColumns = {@JoinColumn(name="role_id")},
            inverseJoinColumns =@JoinColumn(name = "menu_id"))
    @MapKey( name = "id" )
    @RemoteProperty
    protected Map<Serializable, M> menus = new HashMap<>();
    @ManyToMany(cascade=CascadeType.REMOVE,fetch=FetchType.LAZY)
    @JoinTable( name="user_role",joinColumns = {@JoinColumn(name="role_id")},
            inverseJoinColumns =@JoinColumn(name = "user_id"))
    @MapKey( name = "id" )
    private Map<Serializable, U> users = new HashMap<>();

    public SuperRole build(){
        return new SuperRole();
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
        result = result * 31 + name.hashCode();
        return result;
    }
}
