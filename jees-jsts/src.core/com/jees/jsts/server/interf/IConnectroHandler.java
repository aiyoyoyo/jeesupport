package com.jees.jsts.server.interf;

public interface IConnectroHandler {
    int STATUS_DISCONNECT = 100;
    int STATUS_CONNECTING = 101;
    int STATUS_SUCCESS    = 200;

    void initialize( String _host, int _port );

    String getUrl();

    int getStatus();

    void setStatus( int _status );
}
