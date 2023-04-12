package com.jees.webs.dao;

import com.jees.core.database.dao.RedisDao;
import com.jees.webs.entity.Identity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdentityDao {

    public enum IdentityType {
        UID,
        RID,
        MID,
    }

    @Autowired
    RedisDao<IdentityType, Identity> sDB;

    public Identity findBy(IdentityType _type, long _def) throws Exception {
        Identity ident = sDB.findById(_type, Identity.class);
        if (ident == null) {
            ident = new Identity(_type, _def);
            sDB.insert(ident);
        }

        return ident;
    }

    public void update(Identity _ident) {
        sDB.update(_ident);
    }

    public long build(IdentityType _type, long _def) throws Exception {
        Identity ident = findBy(_type, _def);
        long id = ident.getNextId();
        ident.next();
        update(ident);
        return id;
    }

    public long build(IdentityType _type) throws Exception {
        Identity ident = findBy(_type, 0L);
        long id = ident.getNextId();
        ident.next();
        update(ident);
        return id;
    }
}
