package com.jees.server.message;

import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息协议格式，注意属性的顺序会影响消息内容
 *
 * @author aiyoyoyo
 */
@Getter
@Setter
public class Message<ID> {
    private int id;
    private ID userId;
    private int requestId;
    private List<Integer> intData;
    private List<String> strData;
    private List<Boolean> booData;
    private List<Float> floData;
    private List<Double> dblData;
    private List<Long> lonData;
    private List<byte[]> bytData;

    public Message(int _id) {
        super();
        this.setId(_id);
    }

    public Message(int _id, Object... _params) {
        super();
        this.setId(_id);

        for (Object o : _params) {
            if (o instanceof Integer) this.add((Integer) o);
            else if (o instanceof String) this.add((String) o);
            else if (o instanceof Boolean) this.add((Boolean) o);
            else if (o instanceof Float) this.add((Float) o);
            else if (o instanceof Double) this.add((Double) o);
            else if (o instanceof Long) this.add((Long) o);
            else if (o instanceof Enum) {
                Enum e = (Enum<?>) o;
                this.add(e.name());
                this.add(e.ordinal());
            } else this.add(o.toString());
        }
    }

    public void add(Integer _obj) {
        if (this.intData == null) intData = new ArrayList<>();
        this.intData.add(_obj);
    }

    public void add(String _obj) {
        if (this.strData == null) strData = new ArrayList<>();
        this.strData.add(_obj);
    }

    public void add(Float _obj) {
        if (this.floData == null) floData = new ArrayList<>();
        this.floData.add(_obj);
    }

    public void add(Boolean _obj) {
        if (this.booData == null) booData = new ArrayList<>();
        this.booData.add(_obj);
    }

    public void add(Double _obj) {
        if (this.dblData == null) dblData = new ArrayList<>();
        this.dblData.add(_obj);
    }

    public void add(Long _obj) {
        if (this.lonData == null) lonData = new ArrayList<>();
        this.lonData.add(_obj);
    }

    public void add(byte[] _obj) {
        if (this.bytData == null) bytData = new ArrayList<>();
        this.bytData.add(_obj);
    }

    public Integer getInteger(int _idx) {
        if (this.intData == null || _idx >= this.intData.size()) return null;
        return this.intData.get(_idx);
    }

    public String getString(int _idx) {
        if (this.strData == null || _idx >= this.strData.size()) return null;
        return this.strData.get(_idx);
    }

    public Float getFloat(int _idx) {
        if (this.floData == null || _idx >= this.floData.size()) return null;
        return this.floData.get(_idx);
    }

    public Boolean getBoolean(int _idx) {
        if (this.booData == null || _idx >= this.booData.size()) return null;
        return this.booData.get(_idx);
    }

    public Double getDouble(int _idx) {
        if (this.dblData == null || _idx >= this.booData.size()) return null;
        return this.dblData.get(_idx);
    }

    public Long getLong(int _idx) {
        if (this.lonData == null || _idx >= this.lonData.size()) return null;
        return this.lonData.get(_idx);
    }

    public byte[] getBytes(int _idx) {
        if (this.bytData == null || _idx >= this.bytData.size()) return null;
        return this.bytData.get(_idx);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
