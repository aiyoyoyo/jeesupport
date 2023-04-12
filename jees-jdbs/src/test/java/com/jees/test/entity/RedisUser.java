package com.jees.test.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
public class RedisUser {
    @Id
    int id;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss:SSS")
    Date date;
}
