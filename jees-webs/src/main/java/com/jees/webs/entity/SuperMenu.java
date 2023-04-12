package com.jees.webs.entity;

import com.jees.tool.utils.JsonUtil;
import lombok.*;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@DataTransferObject
public class SuperMenu {
    @RemoteProperty
    String id;          // 菜单ID
    @RemoteProperty
    String name;        // 菜单名称
    @RemoteProperty
    String url;         // 菜单路径
    @RemoteProperty
    String tpl;         // 模板路径
    @RemoteProperty
    boolean visible;    // 是否可见
    @RemoteProperty
    String parentId;    // 父节点
    @RemoteProperty
    int index;          // 顺序
    @RemoteProperty
    List<SuperMenu> menus = new ArrayList<>(); // 子菜单

    @Override
    public String toString() {
        return JsonUtil.toString(this);
    }

    public boolean isRoot() {
        return parentId.equalsIgnoreCase(id);
    }

    public boolean hasMenus() {
        return menus.size() > 0;
    }

    public void addMenu(SuperMenu _menu) {
        if (this.menus.contains(_menu)) return;
        this.menus.add(_menu);
    }
}
