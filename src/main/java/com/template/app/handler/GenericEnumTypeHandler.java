package com.template.app.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import com.template.app.base.BaseEnum;

@MappedJdbcTypes(JdbcType.INTEGER)
@MappedTypes(BaseEnum.class)
public class GenericEnumTypeHandler<E extends Enum<E> & BaseEnum> extends BaseTypeHandler<E> {

    private final Map<Integer, E> codeMap = new HashMap<>();

    public GenericEnumTypeHandler(Class<E> type) {
        try {
            for (E e : type.getEnumConstants()) {
                codeMap.put(e.getCode(), e);
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot initialize EnumTypeHandler for " + type.getSimpleName(), ex);
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return codeMap.get(code);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return codeMap.get(code);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        }
        return codeMap.get(code);
    } 
}
