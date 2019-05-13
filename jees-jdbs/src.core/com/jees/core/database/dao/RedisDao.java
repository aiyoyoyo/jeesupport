package com.jees.core.database.dao;

import com.jees.core.database.support.AbsRedisDao;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class RedisDao<ID, T> extends AbsRedisDao<ID, T> {
}
