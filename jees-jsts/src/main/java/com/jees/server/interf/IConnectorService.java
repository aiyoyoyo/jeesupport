package com.jees.server.interf;

import com.jees.server.support.Connector;

import java.util.List;

public interface IConnectorService extends IServerBase{
    List< Connector > getConnectors();

    Connector getConnector( int _index );
}
