package com.jees.test.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;

@Getter
@Setter
@EqualsAndHashCode
public class RedisUser {
    @Id
    int id;
}
