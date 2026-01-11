package com.template.app.handler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

@MappedTypes({Object.class})
public class JsonTypeHanlder extends JacksonTypeHandler {
    
    public JsonTypeHanlder(Class<?> type) {
        super(type);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, this.toJson(parameter), Types.OTHER);
    }
}
