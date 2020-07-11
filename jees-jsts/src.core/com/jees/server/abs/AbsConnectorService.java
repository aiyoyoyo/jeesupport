package com.jees.server.abs;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.server.interf.IConnectorService;
import com.jees.server.support.Connector;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.*;

/**
 * 提供连接器的服务，用于服务器之间的通讯
 */
@Log4j2
public abstract class AbsConnectorService implements IConnectorService{
    @Getter
    List< Connector >          connectors;

    @Override
    public void onload() {
        connectors = new ArrayList<>();
        //连接中心服务器
        StringTokenizer st = CommonConfig.getStringTokenizer( "jees.jsts.connector.hosts" );

        while ( st.hasMoreTokens() ) {
            String connect_info = st.nextToken().trim();

            int idx0 = connect_info.indexOf( ":" );
            String host = connect_info.substring( 0 , idx0 );
            String port = connect_info.substring( idx0 + 1 );

            Connector sc = CommonContextHolder.getBean( Connector.class );
            sc.initialize( host , port );
            connectors.add( sc );
            sc.onload();
        }
    }

    @Override
    public void unload() {
    }

    @Override
    public void reload() {
        connectors.forEach( Connector::reload );
    }

    @Override
    public Connector getConnector ( int _index ) {
        if( _index < connectors.size() ) return connectors.get( _index );
        return null;
    }
}
