package com.jees.webs.security.struct;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: TODO
 * @Package: com.jees.webs.security.struct
 * @ClassName: PageInfo
 * @Author: 刘甜
 * @Date: 2023/4/7 16:14
 * @Version: 1.0
 */
@Getter
@Setter
public class PageInfo {
    String uri;
    String title;
    Map attr = new HashMap();
}
