package com.jees.webs.entity;

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
public class SuperRole < M extends SuperMenu, U extends SuperUser >{
    public enum Authority{
        None,
        Read,
        Write,
    }

    @Getter
    @Setter
    public static class R2M{
        int       menuId;
        Authority access;
    }

    @Id @RemoteProperty
    int id;
    @RemoteProperty
    String name;
    @RemoteProperty
    List< Integer > menus = new ArrayList<>();

    @Override
    public String toString(){
        return JsonUtil.toString( this );
    }

    public void addMenu( M _menu ){
        menus.add( _menu.getId() );
        _menu.addRole( this );
    }
}
