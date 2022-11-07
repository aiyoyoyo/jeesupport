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
    private Set<String> roles = new HashSet<>();
    private Set<String> users = new HashSet<>();
    private Set<String> denys = new HashSet<>();
    private Map<String, PageAccess> elAccess = new ConcurrentHashMap<>();

    private void _add_data( Set<String> _sets, String[] _datas ){
        for( String data : _datas ){
            String real_data = data.toLowerCase().trim();
            if( !_sets.contains( real_data ) ){
                _sets.add( real_data );
            }
        }
    }

    public void addRoles(String[] _datas) {
        this._add_data( this.roles, _datas );
    }

    public void addDenys(String[] _datas) {
        this._add_data( this.denys, _datas );
    }

    public void addUsers(String[] _datas) {
        this._add_data( this.users, _datas );
    }

    public PageAccess getElPage(String _el) {
        return elAccess.get( _el );
    }

    public void addElPage(String _el, PageAccess _elPage) {
        elAccess.put( _el, _elPage );
    }
}
