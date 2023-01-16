package com.jees.test.entity;

import org.directwebremoting.annotations.DataTransferObject;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Description: TODO
 * @Package: com.jees.test.entity
 * @ClassName: Test
 * @Author: 刘甜
 * @Date: 2022/11/23 13:39
 * @Version: 1.0
 */
@Entity
@Table(
        name = "test"
)
@DataTransferObject
public class Test {
    @Id
    public String id;
}
