package com.template.app.config.db;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.template.app.features.actionRule.enums.ActionRuleStatusEnum;
import com.template.app.features.actionRule.enums.RuleActionTypeEnum;
import com.template.app.features.actionRule.enums.RuleTargetTypeEnum;
import com.template.app.features.error.enums.ErrorCategoryEnum;
import com.template.app.features.ops.enums.EndpointStatusEnum;
import com.template.app.features.ops.enums.EndpointTypeEnum;
import com.template.app.features.ops.enums.MqAckStrategyEnum;
import com.template.app.features.sims.enums.SimStatusEnum;
import com.template.app.features.user.enums.UserStatusEnum;
import com.template.app.handler.GenericEnumTypeHandler;
import com.template.app.handler.UUIDTypeHandler;

import org.apache.ibatis.type.JdbcType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
        
        paginationInnerInterceptor.setOptimizeJoin(true); 
        
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            configuration.getTypeHandlerRegistry().register(UUIDTypeHandler.class);
            configuration.getTypeHandlerRegistry().register(SimStatusEnum.class, JdbcType.INTEGER, new GenericEnumTypeHandler<>(SimStatusEnum.class));
            configuration.getTypeHandlerRegistry().register(UserStatusEnum.class, new GenericEnumTypeHandler<>(UserStatusEnum.class));
            configuration.getTypeHandlerRegistry().register(EndpointTypeEnum.class, new GenericEnumTypeHandler<>(EndpointTypeEnum.class));
            configuration.getTypeHandlerRegistry().register(EndpointStatusEnum.class, new GenericEnumTypeHandler<>(EndpointStatusEnum.class));
            configuration.getTypeHandlerRegistry().register(MqAckStrategyEnum.class, new GenericEnumTypeHandler<>(MqAckStrategyEnum.class));
            configuration.getTypeHandlerRegistry().register(ErrorCategoryEnum.class, new GenericEnumTypeHandler<>(ErrorCategoryEnum.class));
            configuration.getTypeHandlerRegistry().register(ActionRuleStatusEnum.class, new GenericEnumTypeHandler<>(ActionRuleStatusEnum.class));
            configuration.getTypeHandlerRegistry().register(RuleActionTypeEnum.class, new GenericEnumTypeHandler<>(RuleActionTypeEnum.class));
            configuration.getTypeHandlerRegistry().register(RuleTargetTypeEnum.class, new GenericEnumTypeHandler<>(RuleTargetTypeEnum.class));
        };
    }
}
