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
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.jiovan.spring.starter.datasourcebuilder.property.C3p0DataSourceProperties;


/**
 * {@link org.springframework.boot.jdbc.autoconfigure.DataSourceConfiguration} for C3P0
 *
 * @author David Hsing
 */
@SuppressWarnings("unused")
public abstract class C3p0DataSourceBuilder extends AbstractDataSourceBuilder {
    /**
     * Returns a {@link com.mchange.v2.c3p0.ComboPooledDataSource} configured from the given properties and connection details
     *
     * @param properties the C3P0 data source properties applied to the data source
     * @param details the JDBC connection details, or {@code null} to derive them from {@code properties}
     *
     * @return the configured {@link com.mchange.v2.c3p0.ComboPooledDataSource}
     */

    public static ComboPooledDataSource buildDataSource(@Nonnull C3p0DataSourceProperties properties, @Nullable JdbcConnectionDetails details) {
        JdbcConnectionDetails alias = (details != null) ? details : JdbcConfigurationUtils.connectionDetails(properties);
        ComboPooledDataSource dataSource = JdbcConfigurationUtils.createDataSource(alias, ComboPooledDataSource.class, null);
        setDataSourceProperties(dataSource, properties);
        return dataSource;
    }
}
