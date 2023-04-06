package com.jees.webs.security.struct;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 页面需要的角色授权
 */
@Getter
@Setter
public class PageAccess {
    private String url;

    private boolean anonymous = false;
    private Set<String> ips = new HashSet<>();
    private Set<String> roles = new HashSet<>();
    private Set<String> users = new HashSet<>();
    private Set<String> denyRoles = new HashSet<>();
    private Set<String> denyUsers = new HashSet<>();
    private Set<String> denyIps = new HashSet<>();
    private Map<String, PageAccess> elAccess = new ConcurrentHashMap<>();

    private void _add_data( Set<String> _sets, String[] _datas ){
        for( String data : _datas ){
            String real_data = data.toLowerCase().trim();
            if( !_sets.contains( real_data ) ){
                _sets.add( real_data );
            }
        }
    }

    public void addUsers(String[] _datas) {
        this._add_data( this.users, _datas );
    }
    public void addRoles(String[] _datas) {
        this._add_data( this.roles, _datas );
    }
    public void addIps(String[] _datas) {
        this._add_data( this.ips, _datas );
    }
    public void addDenyUsers(String[] _datas) {
        this._add_data( this.denyUsers, _datas );
    }
    public void addDenyRoles(String[] _datas) {
        this._add_data( this.denyRoles, _datas );
    }
    public void addDenyIps(String[] _datas) {
        this._add_data( this.denyIps, _datas );
    }
    public PageAccess getElPage(String _el) {
        return elAccess.get( _el );
    }
    public void addElPage(String _el, PageAccess _elPage) {
        elAccess.put( _el, _elPage );
    }

}
