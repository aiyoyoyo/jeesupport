package com.jees.jsts.server.interf;

import com.jees.core.socket.support.ISocketBase;
import com.jees.jsts.server.support.Connector;

import java.util.List;
import java.util.Set;

public interface IConnectorService extends ISocketBase{
    List< Connector > getConnectors();

    Connector getConnector( int _index );
}
