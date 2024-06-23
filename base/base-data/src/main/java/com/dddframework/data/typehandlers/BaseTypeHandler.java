package com.dddframework.data.typehandlers;

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseTypeHandler<T> extends org.apache.ibatis.type.BaseTypeHandler<T> {

    /**
     * 获取实际的类型
     */
    public Class<T> type() {
        return (Class<T>) ReflectionKit.getSuperClassGenericType(this.getClass(), 0);
    }

    /**
     * 把指定类型转换为字符串类型，对应写库
     */
    protected abstract String convert(T obj);

    /**
     * 把字符串类型解析成指定类型，对应读库
     */
    protected abstract T parse(String result);

    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) return;
        ps.setString(i, this.convert(parameter));
    }

    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String str = rs.getString(columnName);
        return str == null || str.isEmpty() ? null : this.parse(str);
    }

    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String str = rs.getString(columnIndex);
        return str == null || str.isEmpty() ? null : this.parse(str);
    }

    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String str = cs.getString(columnIndex);
        return str == null || str.isEmpty() ? null : this.parse(str);
    }

}