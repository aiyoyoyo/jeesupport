package com.jees.webs.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.jees.tool.utils.JsonUtil;
import lombok.*;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@DataTransferObject
public class SuperMenu <R extends SuperRole> {
    @Id
    @RemoteProperty
    int id;
    @RemoteProperty
    String name;
    @RemoteProperty
    String tpl;
    @RemoteProperty
    String url;
    @RemoteProperty
    int visible;
    @RemoteProperty
    int parentId;
    @RemoteProperty
    int index;
    @RemoteProperty @JSONField(serialize = false)
    List<String> roles = new ArrayList<>();
    @RemoteProperty @JSONField(serialize = false)
    List<SuperMenu> menus = new ArrayList<>();

    @Override
    public String toString(){
        return JsonUtil.toString( this );
    }

    public boolean isRoot(){
        return parentId == id;
    }

    public boolean isPermit(){
        return roles.isEmpty();
    }

    public boolean hasMenus(){
        return menus.size() > 0;
    }

    public void addRole( R _role ){
        if( this.roles.contains( _role ) ) return;
        roles.add( _role.getName() );
    }

    public void addMenu( SuperMenu _menu ){
        if( this.menus.contains( _menu ) ) return;
        this.menus.add( _menu );
    }
}
