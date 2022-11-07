package com.jees.webs.core.interf;

/**
 * 用于页面的EL变量
 */
public interface ISupportEL {
    String ROLE_SUPERMAN                = "SUPERMAN"; // 系统级管理员 > 应用级管理员
    String ROLE_ADMIN                   = "ADMIN";// 应用级管理员
    String ROLE_ANONYMOUS               = "anonymousUser";
    String ROLE_BLACK                   = "BLACK"; // 黑名单
    String App_EL                       = "APP";

    String Template_Name_EL             = "TPL_NAME";
    String Template_Object_EL           = "TEMPLATE";
    String Template_Current_EL          = "TPL";
    String Template_EL                  = "TPL_";
    String Template_Assets_EL           = "TPL_A_";
    String Assets_Current_EL            = "RES";

    String Session_User_EL              = "USER";
    String Session_Templates_EL         = "TEMPLATES";
    String Session_Menus_EL             = "MENUS";
    String Session_Breadcrumb_EL        = "BREADCRUMB";

    String Request_Menu_EL              = "MENU";
    String Request_Page_EL              = "PAGE";
    String Request_Actives_EL           = "ACTIVES";

    String Login_Err                    = "ERR";
}
