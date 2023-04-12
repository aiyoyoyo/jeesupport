package com.jees.webs.entity;

import com.jees.webs.dao.IdentityDao;
import lombok.*;

import javax.persistence.Id;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Identity {
    @Id
    IdentityDao.IdentityType id;

    long nextId;

    public void next() {
        nextId++;
    }
}
