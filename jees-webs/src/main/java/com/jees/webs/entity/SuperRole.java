package com.jees.webs.entity;

import com.jees.tool.utils.JsonUtil;
import lombok.*;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;

import javax.persistence.Id;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@DataTransferObject
public class SuperRole<M extends SuperMenu, U extends SuperUser> {
    @Id
    @RemoteProperty
    int id;
    @RemoteProperty
    String name;
    @RemoteProperty
    Set<String> users;

    @Override
    public String toString() {
        return JsonUtil.toString(this);
    }
}
