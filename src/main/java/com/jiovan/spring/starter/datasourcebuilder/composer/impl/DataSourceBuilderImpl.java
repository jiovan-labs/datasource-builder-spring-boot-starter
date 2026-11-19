/*
 * Copyright (c) 2020 Jiovan Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jiovan.spring.starter.datasourcebuilder.composer.impl;


import java.util.Arrays;
import javax.sql.DataSource;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.jdbc.autoconfigure.AbstractDataSourceBuilder;
import org.springframework.boot.jdbc.autoconfigure.JdbcConfigurationUtils;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.jdbc.XADataSourceWrapper;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jdbc.autoconfigure.JdbcConnectionDetails;
import org.springframework.boot.jdbc.autoconfigure.XADataSourceAutoConfiguration;
import org.springframework.boot.transaction.autoconfigure.TransactionManagerCustomizers;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import com.jiovan.plexus.common.toolkit.util.ValueEnumWraps;
import com.jiovan.plexus.spring.toolkit.support.SingletonObjectProvider;
import com.jiovan.spring.starter.datasourcebuilder.composer.DataSourceBuilder;
import com.jiovan.spring.starter.datasourcebuilder.constant.DataSourcePoolConst;
import com.jiovan.spring.starter.datasourcebuilder.enumeration.DataSourcePoolType;
import com.jiovan.spring.starter.datasourcebuilder.property.C3p0DataSourceProperties;
import com.jiovan.spring.starter.datasourcebuilder.property.Dbcp2DataSourceProperties;
import com.jiovan.spring.starter.datasourcebuilder.property.DruidDataSourceProperties;
import com.jiovan.spring.starter.datasourcebuilder.property.HikariDataSourceProperties;
import com.jiovan.spring.starter.datasourcebuilder.property.OracleUcpDataSourceProperties;
import com.jiovan.spring.starter.datasourcebuilder.property.TomcatDataSourceProperties;
import com.jiovan.spring.starter.datasourcebuilder.util.JpaConfigurationUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


/**
 * Composer implementation for datasource builder
 *
 * @author David Hsing
 * @see org.springframework.boot.jdbc.DataSourceBuilder
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Setter
@SuppressWarnings("unused")
public class DataSourceBuilderImpl implements DataSourceBuilder {
    private final ApplicationContext applicationContext;
    private final Environment environment;
    private ClassLoader classLoader;

    @Nullable
    @Override
    public DataSource dataSource(@Nonnull DataSourceProperties properties) {
        if (StringUtils.hasText(properties.getJndiName())) {
            return JdbcConfigurationUtils.jndiDataSource(properties, applicationContext);
        }
        if (!StringUtils.hasText(properties.getUrl())) {
            return null;
        }
        JdbcConnectionDetails details = JdbcConfigurationUtils.connectionDetails(properties);
        return AbstractDataSourceBuilder.buildDataSource(properties, details);
    }

    @Nullable
    @Override
    public DataSource dataSource(@Nonnull DataSourceProperties properties, @Nonnull DataSourcePoolType type) {
        if (StringUtils.hasText(properties.getJndiName())) {
            return JdbcConfigurationUtils.jndiDataSource(properties, applicationContext);
        }
        if (!StringUtils.hasText(properties.getUrl())) {
            return null;
        }
        JdbcConnectionDetails details = JdbcConfigurationUtils.connectionDetails(properties);
        return AbstractDataSourceBuilder.buildDataSource(properties, details, type.getValue());
    }

    @Nullable
    @Override
    public DataSource dataSource(@Nonnull String prefix, @Nonnull Class<? extends DataSourceProperties> clazz) {
        DataSourceProperties properties = dataSourceProperties(prefix, clazz);
        return properties == null ? null : dataSource(properties);
    }

    @Nullable
    @Override
    public DataSourceProperties dataSourceProperties(@Nonnull Environment environment, @Nonnull String prefix) {
        if (!StringUtils.hasText(prefix)) {
            return null;
        }
        String type = environment.getProperty(String.format("%s.type", prefix));    // $NON-NLS-1$
        if (!StringUtils.hasText(type)) {
            type = Arrays.stream(DataSourcePoolType.values()).map(DataSourcePoolType::getValue).filter(value -> ClassUtils.isPresent(value, null)).findFirst().orElse(null);
        }
        return dataSourceProperties(environment, prefix, ValueEnumWraps.ofValueIgnoreCase(DataSourcePoolType.class, type));
    }

    @Nullable
    @Override
    public DataSourceProperties dataSourceProperties(@Nonnull Environment environment, @Nonnull String prefix, @Nullable DataSourcePoolType type) {
        if (!StringUtils.hasText(prefix)) {
            return null;
        }
        String jndiName = environment.getProperty(String.format("%s.jndi-name", prefix));    // $NON-NLS-1$
        if (StringUtils.hasText(jndiName)) {
            return dataSourceProperties(environment, prefix, DataSourceProperties.class);
        }
        if (type == null) {
            return dataSourceProperties(environment, prefix, DataSourceProperties.class);
        }
        return switch (type.getValue()) {
            case DataSourcePoolConst.C3P0 -> dataSourceProperties(environment, prefix, C3p0DataSourceProperties.class);
            case DataSourcePoolConst.DBCP2 -> dataSourceProperties(environment, prefix, Dbcp2DataSourceProperties.class);
            case DataSourcePoolConst.DRUID -> dataSourceProperties(environment, prefix, DruidDataSourceProperties.class);
            case DataSourcePoolConst.HIKARI -> dataSourceProperties(environment, prefix, HikariDataSourceProperties.class);
            case DataSourcePoolConst.ORACLE_UCP, DataSourcePoolConst.ORACLE_UCP_XA -> dataSourceProperties(environment, prefix, OracleUcpDataSourceProperties.class);
            case DataSourcePoolConst.TOMCAT -> dataSourceProperties(environment, prefix, TomcatDataSourceProperties.class);
            default -> dataSourceProperties(environment, prefix, DataSourceProperties.class);
        };
    }

    @Nullable
    @Override
    public DataSourceProperties dataSourceProperties(@Nonnull String prefix, @Nonnull Class<? extends DataSourceProperties> clazz) {
        return dataSourceProperties(environment, prefix, clazz);
    }

    /**
     * @see "https://www.jb51.net/article/153392.htm"
     * @see "https://github.com/abel533/mapper-boot-starter/blob/master/mapper-spring-boot-autoconfigure/src/main/java/tk/mybatis/spring/mapper/SpringBootBindUtil.java"
     */
    @Nullable
    @Override
    public DataSourceProperties dataSourceProperties(@Nonnull Environment environment, @Nonnull String prefix, @Nonnull Class<? extends DataSourceProperties> clazz) {
        return !StringUtils.hasText(prefix) ? null : Binder.get(environment).bind(prefix, clazz).orElse(null);
    }

    /**
     * @see org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration
     */
    @Nonnull
    @Override
    public DataSourceTransactionManager jdbcTransactionManager(@Nonnull DataSource dataSource, @Nonnull ObjectProvider<TransactionManagerCustomizers> customizers) {
        return JdbcConfigurationUtils.transactionManager(environment, dataSource, customizers);
    }

    @Nonnull
    @Override
    public JpaTransactionManager jpaTransactionManager(@Nonnull ObjectProvider<TransactionManagerCustomizers> customizers) {
        return JpaConfigurationUtils.jpaTransactionManager(customizers);
    }

    /**
     * @see org.springframework.boot.jpa.autoconfigure.JpaBaseConfiguration#transactionManager
     */
    @Nonnull
    @Override
    public JpaTransactionManager jpaTransactionManager(@Nullable EntityManagerFactory factory, @Nonnull ObjectProvider<TransactionManagerCustomizers> customizers) {
        return JpaConfigurationUtils.jpaTransactionManager(factory, customizers);
    }

    @Nonnull
    @Override
    public DataSource xaDataSource(@Nonnull XADataSourceWrapper wrapper, @Nonnull DataSourceProperties properties) throws Exception {
        return xaDataSource(wrapper, properties, null);
    }

    @Nonnull
    @Override
    public DataSource xaDataSource(@Nonnull XADataSourceWrapper wrapper, @Nonnull DataSourceProperties properties, @Nullable JdbcConnectionDetails details) throws Exception {
        XADataSourceAutoConfiguration configuration = new XADataSourceAutoConfiguration();
        configuration.setBeanClassLoader(classLoader);
        JdbcConnectionDetails alias = (details != null) ? details : JdbcConfigurationUtils.connectionDetails(properties);
        return JdbcConfigurationUtils.xaDataSource(configuration, wrapper, properties, alias, SingletonObjectProvider.empty());
    }
}
