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
import org.apache.tomcat.jdbc.pool.DataSource;
import com.jiovan.spring.starter.datasourcebuilder.property.TomcatDataSourceProperties;


/**
 * {@link org.springframework.boot.jdbc.autoconfigure.DataSourceConfiguration} for Tomcat
 *
 * @author David Hsing
 */
@SuppressWarnings("unused")
public abstract class TomcatDataSourceBuilder extends AbstractDataSourceBuilder {
    /**
     * Returns a Tomcat JDBC {@link javax.sql.DataSource} configured from the given properties and connection details
     *
     * @param properties the Tomcat data source properties applied to the data source
     * @param details the JDBC connection details, or {@code null} to derive them from {@code properties}
     *
     * @return the configured Tomcat JDBC {@link javax.sql.DataSource}
     */

    public static DataSource buildDataSource(@Nonnull TomcatDataSourceProperties properties, @Nullable JdbcConnectionDetails details) {
        JdbcConnectionDetails alias = (details != null) ? details : JdbcConfigurationUtils.connectionDetails(properties);
        DataSource dataSource = JdbcConfigurationUtils.createDataSource(alias, DataSource.class, null);
        setDataSourceProperties(dataSource, properties);
        return dataSource;
    }
}
