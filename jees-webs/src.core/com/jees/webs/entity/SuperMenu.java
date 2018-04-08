package com.jees.webs.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@MappedSuperclass
public class SuperMenu <ID extends Serializable, R extends SuperRole> {
    @Id
    @GeneratedValue( strategy = IDENTITY )
    @GenericGenerator( name = "generator" , strategy = "identity" )
    @Column( name = "id" , unique = true , nullable = false )
    private ID id;
    @Column( name = "tpl" , nullable = false )
    private String tpl;
    @Column( name = "name" , nullable = false )
    private String name;
    @Column( name = "url" , nullable = false )
    private String url;
    @Column( name = "visible" , nullable = false )
    private int visible;
    @Column( name = "parent_id" , nullable = false )
    private int parentId;
    @Column( name = "`index`" , nullable = false )
    private int index;
    @OneToMany( cascade = CascadeType.REMOVE , fetch = FetchType.EAGER )
    @MapKey( name = "id" )
    private Map<Integer, R> roles = new HashMap<>();

    public boolean isPermit() {
        return roles.size() == 0;
    }

    public List<String> getRoleNames(){
        return roles.values().stream().map( r -> r.getName() ).collect( Collectors.toList() );
    }
}
