package com.jees.webs.config;

import com.alibaba.fastjson.JSON;
import com.jees.common.CommonConfig;
import com.jees.tool.crypto.AESUtils;
import com.jees.tool.utils.FileUtil;
import com.jees.tool.utils.JsonUtil;
import com.jees.webs.abs.AbsInstallService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.directwebremoting.annotations.DataTransferObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Log4j2
@Component
public class InstallConfig{

    public static final int InstallRedisSucc = 1;
    public static final int InstallAdminSucc = 2;
    public static final int InstallServerSucc = 4;
    public static final int InstallFinish = 8;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @DataTransferObject
    public static class InstallStep{
        int step;

        String RedisHost;
        int    RedisPort;
        String RedisPassword;
        int    RedisDatabase;

        String AdminAccount;
        String AdminPassword;
        String AdminGroup;

        int ServerPort;
        String InstallPath;

        @Override
        public String toString(){
            String str = JsonUtil.toString( this );
            try{
                String key = AESUtils.s_genkeys( "superman" );
                str = AESUtils.s_encrypt( key, str );
            }catch ( Exception e ){
                e.printStackTrace();
            }
            return str;
        }

        public static InstallStep toClass( String _str ){
            String str = null;
            try{
                String key = AESUtils.s_genkeys( "superman" );
                str = AESUtils.s_decrypt( key, _str );
            }catch( Exception e ){
                e.printStackTrace();
            }
            return JSON.parseObject( str, InstallStep.class );
        }
    }

    File installFile;
    @Getter
    InstallStep installStep;
    @Getter
    boolean installed = _check_install_();
    @Autowired
    AbsInstallService absInstallService;

    public void installFailure( int _step ) throws IOException {
        if( ( installStep.step & _step ) == _step ){
            installStep.step -= _step;
        }
        FileUtil.write( installStep.toString(), installFile );
    }

    public int getStep(){
        if( installStep.step > CommonConfig.getInteger( "jees.webs.install.step", 0 ) )
            return CommonConfig.getInteger( "jees.webs.install.step", 0 );
        return installStep.step;
    }

    private boolean _check_install_(){
        try{
            installFile = FileUtil.load( CommonConfig.getString( "jees.webs.install.file", "classpath:.install" ), true );
            if( installFile.exists() ){
                installStep = InstallStep.toClass( FileUtil.read( installFile ) );
            }else{
                installFile.createNewFile();
                installStep = new InstallStep();
                FileUtil.write( installStep.toString(), installFile );
            }
        }catch( Exception e ){
            e.printStackTrace();
            System.exit( 0 );
        }

        if( installFile.exists() ){
            return (installStep.step & InstallFinish) == InstallFinish;
        }

        return false;
    }

    public void installRedis( String _host, int _port, String _password, int _db ) throws IOException {
        installStep.RedisHost = _host;
        installStep.RedisPort = _port;
        installStep.RedisPassword = _password;
        installStep.RedisDatabase = _db;

        if( (installStep.step & InstallRedisSucc) != InstallRedisSucc ){
            installStep.step += InstallRedisSucc;
        }

        FileUtil.write( installStep.toString(), installFile );
    }

    public void installAdmin( String _account, String _password, String _group ) throws IOException {
        installStep.AdminAccount = _account;
        installStep.AdminPassword = _password;
        installStep.AdminGroup = _group;
        if( (installStep.step & InstallAdminSucc) != InstallAdminSucc ){
            installStep.step += InstallAdminSucc;
        }
        FileUtil.write( installStep.toString(), installFile );
    }

    public void installServer( int _port, String _path ) throws IOException {
        installStep.ServerPort = _port;
        installStep.InstallPath = _path;
        if( (installStep.step & InstallServerSucc) != InstallServerSucc ){
            installStep.step += InstallServerSucc;
        }
        FileUtil.write( installStep.toString(), installFile );
    }

    public String finish(){
        String msg = "";
        try{
            // 处理install.cfg
            msg = "配置路径[" + installStep.InstallPath + "]下找不到install.cfg文件。";
            File cfg_file = FileUtil.load( installStep.InstallPath + "/install.cfg", true );
            String cfg_str = FileUtil.read( cfg_file );
            cfg_str = cfg_str.replace( "{redis.host}", installStep.RedisHost );
            cfg_str = cfg_str.replace( "{redis.port}", "" + installStep.RedisPort );
            cfg_str = cfg_str.replace( "{redis.database}", "" + installStep.RedisDatabase );
            cfg_str = cfg_str.replace( "{redis.password}", installStep.RedisPassword );
            cfg_str = cfg_str.replace( "{server.port}", "" + installStep.ServerPort );
            msg = "配置路径[" + installStep.InstallPath + "]下写入application.yml失败。";
            FileUtil.write( cfg_str, installStep.InstallPath + "/application.yml", true );
            // 处理管理员账号
            msg = "无法保存管理员账号到数据库，请确认Redis配置是否正确。";
            absInstallService.install( installStep.AdminAccount, installStep.AdminPassword, installStep.AdminGroup );

            msg = "基础设置保存中失败！请检查系统环境。";
            if( (installStep.step & InstallFinish) != InstallFinish ){
                installStep.step += InstallFinish;
                msg = "";
            }
            FileUtil.write( installStep.toString(), installFile );
        }catch( Exception e ){
            if( msg.isEmpty() ){
                msg = "其他错误原因：" + e.toString();
            }
        }

        return msg;
    }
}
