package com.jees.jsts.server.support;

import com.jees.common.CommonConfig;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

@Configuration
public class WSSSLConfig {

//    @Bean
    public SslContext SslContext() throws SSLException {
        if( CommonConfig.getBoolean( "jees.jsts.websocket.ssl.enable", false ) ) {
            File file = new File( CommonConfig.getString( "jees.jsts.websocket.ssl.file" ) );
            File key = new File( CommonConfig.getString( "jees.jsts.websocket.ssl.keyfile" ) );

            return SslContextBuilder.forServer(file, key).build();
        }

        return null;
    }

    @Bean
    public SSLContext SSLContext() throws Exception {
        if( CommonConfig.getBoolean( "jees.jsts.websocket.ssl.enable", false ) ){
            String keyStorePath = CommonConfig.getString( "jees.jsts.websocket.ssl.file" );
            String keyPassword = CommonConfig.getString( "jees.jsts.websocket.ssl.pass" );

            KeyManagerFactory kmf = KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
            KeyStore ks = KeyStore.getInstance( CommonConfig.getString( "jees.jsts.websocket.ssl.type" ) );
            ks.load( new FileInputStream( keyStorePath ), keyPassword.toCharArray() );
            kmf.init( ks, keyPassword.toCharArray() );

            SSLContext sslContext = SSLContext.getInstance( "SSL" );
            sslContext.init( kmf.getKeyManagers(), null, null);

            return sslContext;
        }
        return SSLContext.getInstance( "SSL" );
    }
}
