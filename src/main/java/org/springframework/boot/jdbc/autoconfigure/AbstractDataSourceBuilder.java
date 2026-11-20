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

package org.springframework.boot.jdbc.autoconfigure;


import java.lang.reflect.Field;
import javax.sql.DataSource;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import com.jiovan.spring.starter.datasourcebuilder.constant.DataSourcePoolConst;
import com.jiovan.spring.starter.datasourcebuilder.property.C3p0DataSourceProperties;
import com.jiovan.spring.starter.datasourcebuilder.property.Dbcp2DataSourceProperties;
import com.jiovan.spring.starter.datasourcebuilder.property.DruidDataSourceProperties;
import com.jiovan.spring.starter.datasourcebuilder.property.HikariDataSourceProperties;
import com.jiovan.spring.starter.datasourcebuilder.property.OracleUcpDataSourceProperties;
import com.jiovan.spring.starter.datasourcebuilder.property.TomcatDataSourceProperties;
import com.jiovan.spring.starter.datasourcebuilder.property.ViburDataSourceProperties;
import lombok.extern.slf4j.Slf4j;


/**
 * Abstract builder that extended from {@link org.springframework.boot.jdbc.autoconfigure.DataSourceConfiguration}
 *
 * @author David Hsing
 * @see org.springframework.boot.jdbc.autoconfigure.DataSourceConfiguration
 */
@Slf4j
@SuppressWarnings("unused")
public abstract class AbstractDataSourceBuilder extends DataSourceConfiguration {
    /**
     * Returns a {@link javax.sql.DataSource} of the type specified by the {@code properties}, falling back to a generic data source when no type is set
     *
     * @param properties the data source properties containing the pool type and URL
     * @param details the JDBC connection details, or {@code null} to derive them from {@code properties}
     *
     * @return the configured {@link javax.sql.DataSource}
     */

    public static DataSource buildDataSource(@Nonnull DataSourceProperties properties, @Nullable JdbcConnectionDetails details) {
        Class<? extends DataSource> type = properties.getType();
        if (type == null) {
            if (log.isWarnEnabled()) {
                log.warn("Data source with url '{}' is missing 'type' property, using generic type instead", properties.getUrl());
            }
            return JdbcConfigurationUtils.genericDataSource(properties, details);
        }
        return buildDataSource(properties, details, ClassUtils.getQualifiedName(type));
    }

    /**
     * Returns a {@link javax.sql.DataSource} built by the builder registered for the given {@code poolType}
     *
     * @param properties the data source properties applied to the data source
     * @param details the JDBC connection details, or {@code null} to derive them from {@code properties}
     * @param poolType the qualified class name of the connection pool to use
     *
     * @return the configured {@link javax.sql.DataSource}, or a generic one if {@code poolType} is unsupported
     */

    public static DataSource buildDataSource(@Nonnull DataSourceProperties properties, @Nullable JdbcConnectionDetails details, @Nonnull String poolType) {
        if (!StringUtils.hasText(poolType)) {
            return JdbcConfigurationUtils.genericDataSource(properties, details);
        }
        return switch (poolType) {
            case DataSourcePoolConst.C3P0 -> C3p0DataSourceBuilder.buildDataSource((C3p0DataSourceProperties) properties, details);
            case DataSourcePoolConst.DBCP2 -> Dbcp2DataSourceBuilder.buildDataSource((Dbcp2DataSourceProperties) properties, details);
            case DataSourcePoolConst.DRUID -> DruidDataSourceBuilder.buildDataSource((DruidDataSourceProperties) properties, details);
            case DataSourcePoolConst.HIKARI -> HikariDataSourceBuilder.buildDataSource((HikariDataSourceProperties) properties, details);
            case DataSourcePoolConst.ORACLE_UCP -> OracleUcpDataSourceBuilder.buildDataSource((OracleUcpDataSourceProperties) properties, details);
            case DataSourcePoolConst.ORACLE_UCP_XA -> OracleUcpDataSourceBuilder.buildXaDataSource((OracleUcpDataSourceProperties) properties, details);
            case DataSourcePoolConst.VIBUR -> ViburDataSourceBuilder.buildDataSource((ViburDataSourceProperties) properties, details);
            case DataSourcePoolConst.TOMCAT -> TomcatDataSourceBuilder.buildDataSource((TomcatDataSourceProperties) properties, details);
            default -> {
                if (log.isWarnEnabled()) {
                    log.warn("Data source with url '{}' is an unsupported type, using generic type instead", properties.getUrl());
                }
                yield JdbcConfigurationUtils.genericDataSource(properties, details);
            }
        };
    }

    /**
     * Copies non-empty values of the declared fields of {@code properties} into matching fields of the {@code dataSource}
     *
     * @param dataSource the data source whose fields are populated
     * @param properties the properties providing the field values
     */

    public static void setDataSourceProperties(@Nonnull DataSource dataSource, @Nonnull DataSourceProperties properties) {
        Field[] fields = properties.getClass().getDeclaredFields();
        if (ObjectUtils.isEmpty(fields)) {
            return;
        }
        for (Field field : fields) {
            try {
                ReflectionUtils.makeAccessible(field);
                Object value = ReflectionUtils.getField(field, properties);
                if (!ObjectUtils.isEmpty(value)) {
                    ReflectionUtils.setField(field, dataSource, value);
                }
            } catch (Exception ignored) {
            }
        }
    }
}
