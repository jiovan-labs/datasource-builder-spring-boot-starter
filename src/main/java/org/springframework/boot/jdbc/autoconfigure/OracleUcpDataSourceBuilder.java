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


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import com.jiovan.spring.starter.datasourcebuilder.property.OracleUcpDataSourceProperties;
import oracle.ucp.jdbc.PoolDataSourceImpl;
import oracle.ucp.jdbc.PoolXADataSourceImpl;


/**
 * {@link org.springframework.boot.autoconfigure.jdbc.DataSourceConfiguration} for Oracle UCP
 *
 * @author David Hsing
 */
@SuppressWarnings("unused")
public abstract class OracleUcpDataSourceBuilder extends AbstractDataSourceBuilder {
    /**
     * Returns an {@link oracle.ucp.jdbc.PoolDataSourceImpl} configured from the given properties and connection details
     *
     * @param properties the Oracle UCP data source properties applied to the data source
     * @param details the JDBC connection details, or {@code null} to derive them from {@code properties}
     *
     * @return the configured {@link oracle.ucp.jdbc.PoolDataSourceImpl}
     */

    public static PoolDataSourceImpl buildDataSource(@Nonnull OracleUcpDataSourceProperties properties, @Nullable JdbcConnectionDetails details) {
        JdbcConnectionDetails alias = (details != null) ? details : JdbcConfigurationUtils.connectionDetails(properties);
        PoolDataSourceImpl dataSource = JdbcConfigurationUtils.createDataSource(alias, PoolDataSourceImpl.class, null);
        setDataSourceProperties(dataSource, properties);
        return dataSource;
    }

    /**
     * Returns an {@link oracle.ucp.jdbc.PoolXADataSourceImpl} configured from the given properties and connection details
     *
     * @param properties the Oracle UCP data source properties applied to the XA data source
     * @param details the JDBC connection details, or {@code null} to derive them from {@code properties}
     *
     * @return the configured {@link oracle.ucp.jdbc.PoolXADataSourceImpl}
     */

    public static PoolXADataSourceImpl buildXaDataSource(@Nonnull OracleUcpDataSourceProperties properties, @Nullable JdbcConnectionDetails details) {
        JdbcConnectionDetails alias = (details != null) ? details : JdbcConfigurationUtils.connectionDetails(properties);
        PoolXADataSourceImpl dataSource = JdbcConfigurationUtils.createDataSource(alias, PoolXADataSourceImpl.class, null);
        setDataSourceProperties(dataSource, properties);
        return dataSource;
    }
}
