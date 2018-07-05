package com.jees.webs.entity;

import lombok.*;
import org.directwebremoting.annotations.RemoteProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@MappedSuperclass
public class SuperMenu <ID extends Serializable, R extends SuperRole> {
    @Id
    @GeneratedValue( strategy = IDENTITY )
    @GenericGenerator( name = "generator" , strategy = "identity" )
    @Column( name = "id" , unique = true , nullable = false )
    @RemoteProperty
    private ID id;
    @Column( name = "tpl" , nullable = false )
    @RemoteProperty
    private String tpl;
    @Column( name = "`name`" , nullable = false )
    @RemoteProperty
    private String name;
    @Column( name = "url" , nullable = false )
    @RemoteProperty
    private String url;
    @Column( name = "visible" , nullable = false )
    @RemoteProperty
    private int visible;
    @Column( name = "parent_id", columnDefinition = "INT default 0")
    @RemoteProperty
    private int parentId;
    @Column( name = "`index`" , nullable = false )
    @RemoteProperty
    private int index;
    @ManyToMany( cascade = CascadeType.REMOVE , fetch = FetchType.LAZY)
    @JoinTable( name="menu_role",joinColumns = {@JoinColumn(name="menu_id")},
            inverseJoinColumns = @JoinColumn(name = "role_id")  )
    @MapKey( name = "id" )
    private Map< Serializable, R > roles = new HashMap<>();
    @Transient
    private Set<SuperMenu> menus = new HashSet<>();
    public boolean isPermit(){ return roles.size() == 0; }
    public boolean isRoot(){
        return parentId == 0;
    }
    public boolean hasParent(){
        return parentId != 0;
    }
    public boolean hasMenus(){
        return menus.size() != 0;
    }
    public void addMenu( SuperMenu _m ){
        menus.add( _m );
    }
    public List<String> getRoleNames(){ return roles.values().stream().map( r -> r.getName() ).collect( Collectors.toList() ); }

    public SuperMenu build(){
        return new SuperMenu();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SuperMenu) return true;
        return false;
    }

    @Override
    public int hashCode() {
        int result = 20;
        result = result * 31 + id.hashCode();
        result = result * 31 + tpl.hashCode();
        result = result * 31 + name.hashCode();
        result = result * 31 + url.hashCode();
        result = result * 31 + visible;
        result = result * 31 + parentId;
        result = result * 31 + index;
        return result;
    }
}
