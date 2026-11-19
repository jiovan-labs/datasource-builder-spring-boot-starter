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


import javax.sql.DataSource;
import javax.sql.XADataSource;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.jdbc.XADataSourceWrapper;
import org.springframework.boot.transaction.autoconfigure.TransactionManagerCustomizers;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import com.jiovan.plexus.common.toolkit.util.ArrayUtilsWraps;
import com.jiovan.plexus.common.toolkit.util.MethodUtilsWraps;


/**
 * Utilities for {@link org.springframework.jdbc.datasource.DataSourceTransactionManager}
 *
 * @author David Hsing
 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager
 * @see JndiDataSourceAutoConfiguration
 * @see XADataSourceAutoConfiguration
 * @see DataSourceTransactionManagerAutoConfiguration
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public abstract class JdbcConfigurationUtils {
    /**
     * Returns a data source of the given type created from the specified connection details
     *
     * @param <T> the data source type
     * @param details the JDBC connection details used to create the data source
     * @param clazz the class of the data source to create
     * @param loader the class loader used to create the data source, may be {@code null}
     *
     * @return the created data source
     */

    @SuppressWarnings("unchecked")
    public static <T extends DataSource> T createDataSource(@Nonnull JdbcConnectionDetails details, @Nonnull Class<T> clazz, @Nullable ClassLoader loader) {
        Object args = ArrayUtilsWraps.toArray(details, clazz, loader);
        return (T) MethodUtilsWraps.invokeStaticMethod(DataSourceConfiguration.class, "createDataSource", args);
    }

    /**
     * Returns a {@code JdbcConnectionDetails} backed by the given {@code DataSourceProperties}
     *
     * @param properties the data source properties backing the connection details
     *
     * @return the connection details exposing the connection settings of {@code properties}
     */

    @Nonnull
    public static JdbcConnectionDetails connectionDetails(@Nonnull DataSourceProperties properties) {
        return new PropertiesJdbcConnectionDetails(properties);
    }

    /**
     * Returns a generic {@link javax.sql.DataSource} configured from the given properties and connection details
     *
     * @param properties the data source properties
     * @param details the JDBC connection details, or {@code null} to derive them from {@code properties}
     *
     * @return the configured generic {@link javax.sql.DataSource}
     */

    @Nonnull
    public static DataSource genericDataSource(@Nonnull DataSourceProperties properties, @Nullable JdbcConnectionDetails details) {
        JdbcConnectionDetails alias = (details != null) ? details : connectionDetails(properties);
        return new DataSourceConfiguration.Generic().dataSource(properties, alias);
    }

    /**
     * Returns a {@link javax.sql.DataSource} looked up from JNDI using the given properties
     *
     * @param properties the data source properties containing the JNDI name
     * @param context the application context used to perform the JNDI lookup
     *
     * @return the JNDI-backed {@link javax.sql.DataSource}
     */

    @Nonnull
    public static DataSource jndiDataSource(@Nonnull DataSourceProperties properties, @Nonnull ApplicationContext context) {
        return new JndiDataSourceAutoConfiguration().dataSource(properties, context);
    }

    /**
     * Returns an XA-aware {@link javax.sql.DataSource} created by the given configuration
     *
     * @param configuration the XA data source auto-configuration
     * @param wrapper the wrapper used to wrap the XA data source
     * @param properties the data source properties
     * @param details the JDBC connection details
     * @param xaDataSource the provider of an existing XA data source, if any
     *
     * @return the configured XA-aware {@link javax.sql.DataSource}
     *
     * @throws Exception if the XA data source cannot be created
     */

    @Nonnull
    public static DataSource xaDataSource(@Nonnull XADataSourceAutoConfiguration configuration, @Nonnull XADataSourceWrapper wrapper, @Nonnull DataSourceProperties properties, @Nonnull JdbcConnectionDetails details, @Nonnull ObjectProvider<XADataSource> xaDataSource) throws Exception {
        return configuration.dataSource(wrapper, properties, details, xaDataSource);
    }

    /**
     * Returns a {@link org.springframework.jdbc.datasource.DataSourceTransactionManager} configured with the given data source and customizers
     *
     * @param environment the environment used by the transaction manager configuration
     * @param dataSource the data source managed by the transaction manager
     * @param customizers the provider of transaction manager customizers to apply
     *
     * @return the configured {@link org.springframework.jdbc.datasource.DataSourceTransactionManager}
     */

    @Nonnull
    public static DataSourceTransactionManager transactionManager(@Nonnull Environment environment, @Nonnull DataSource dataSource, @Nonnull ObjectProvider<TransactionManagerCustomizers> customizers) {
        return new DataSourceTransactionManagerAutoConfiguration.JdbcTransactionManagerConfiguration().transactionManager(environment, dataSource, customizers);
    }
}
