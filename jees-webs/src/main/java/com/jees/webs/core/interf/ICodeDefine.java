package com.jees.webs.core.interf;

import com.jees.tool.support.ProxyInterface;
import com.jees.webs.core.annotation.CodeDesc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public interface ICodeDefine {
    // 默认正常 200 ~ 399 用于成功码
    int SuccessCode = 200;
    // 登录代码
    // 登录成功
    @CodeDesc("登录成功")
    int Login_Success = 201;
    // 默认错误 400~599之间用于错误码
    int ErrorCode = 400;
    // 未找到用户信息
    @CodeDesc("账号没有注册")
    int Login_NotFoundUser = 401;
    @CodeDesc("密码验证失败")
    int Login_PasswordInvalid = 402;
    @CodeDesc("文件不存在或者已被删除")
    int File_NotFound = 404;
    @CodeDesc("账号或者IP被加入了黑名单")
    int User_IsBlack = 405;
    @CodeDesc("用户没有访问权限")
    int User_IsDeny = 406;
    @CodeDesc("服务器出现问题，需要稍后访问")
    int Server_ErrorState = 500;    // 没有授权配置
    @CodeDesc("页面缺少授权信息")
    int Page_NotAccess = 504;

    Map<Integer,String> sCodeDesc = new HashMap<>();
    static String getCodeDesc( int _code ){
        if( sCodeDesc.isEmpty() ) {
            Class cls;
            try {
                cls = Class.forName("com.jees.webs.core.interf.ICodeDefine");
                Object ir = ProxyInterface.newInstance(new Class[]{cls});
                Field[] fields = cls.getDeclaredFields();
                for (Field f : fields) {
                    try {
                        sCodeDesc.put(f.getInt(ir), f.getAnnotation(CodeDesc.class).value());
                    } catch (Exception e) {
                    }
                }
            } catch (ClassNotFoundException e) {
            }
        }
        return sCodeDesc.getOrDefault( _code, "" );
    }

}
