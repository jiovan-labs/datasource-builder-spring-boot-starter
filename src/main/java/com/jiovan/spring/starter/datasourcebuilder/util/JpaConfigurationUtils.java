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

package com.jiovan.spring.starter.datasourcebuilder.util;


import javax.sql.DataSource;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.transaction.autoconfigure.TransactionManagerCustomizers;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.util.StringUtils;


/**
 * Utilities for Spring Data JPA configuration
 *
 * @author David Hsing
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public abstract class JpaConfigurationUtils {
    /**
     * Returns a {@link org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager} configured from the given single-valued settings
     *
     * @param persistenceUnit the default persistence unit name
     * @param persistenceUnitRootLocation the default persistence unit root location
     * @param dataSource the data source used as the default or JTA data source
     * @param packageToScan the package to scan for entities, may be {@code null}
     * @param mappingResource the mapping resource, may be {@code null}
     * @param xmlLocation the persistence XML location, may be {@code null}
     * @param jta whether the data source is a JTA data source
     * @param resourceLoader the resource loader, may be {@code null}
     * @param cacheMode the shared cache mode, may be {@code null}
     * @param validationMode the validation mode, may be {@code null}
     * @param postProcessor the persistence unit post processor, may be {@code null}
     *
     * @return the configured {@link org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager}
     */

    @Nonnull
    public static PersistenceUnitManager defaultPersistenceUnitManager(@Nullable String persistenceUnit, @Nullable String persistenceUnitRootLocation, @Nullable DataSource dataSource, @Nullable String packageToScan, @Nullable String mappingResource, @Nullable String xmlLocation, boolean jta, @Nullable ResourceLoader resourceLoader, @Nullable SharedCacheMode cacheMode, @Nullable ValidationMode validationMode, @Nullable PersistenceUnitPostProcessor postProcessor) {
        return defaultPersistenceUnitManager(persistenceUnit, persistenceUnitRootLocation, dataSource, (StringUtils.hasText(packageToScan) ? new String[]{packageToScan} : null), (StringUtils.hasText(mappingResource) ? new String[]{mappingResource} : null), (StringUtils.hasText(xmlLocation) ? new String[]{xmlLocation} : null), jta, resourceLoader, cacheMode, validationMode, (postProcessor == null ? null : new PersistenceUnitPostProcessor[]{postProcessor}));
    }

    /**
     * Returns a {@link org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager} configured from the given array-valued settings
     *
     * @param persistenceUnit the default persistence unit name
     * @param persistenceUnitRootLocation the default persistence unit root location
     * @param dataSource the data source used as the default or JTA data source
     * @param packagesToScan the packages to scan for entities, may be {@code null}
     * @param mappingResources the mapping resources, may be {@code null}
     * @param xmlLocations the persistence XML locations, may be {@code null}
     * @param jta whether the data source is a JTA data source
     * @param resourceLoader the resource loader, may be {@code null}
     * @param cacheMode the shared cache mode, may be {@code null}
     * @param validationMode the validation mode, may be {@code null}
     * @param postProcessors the persistence unit post processors, may be {@code null}
     *
     * @return the configured {@link org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager}
     */

    @Nonnull
    @SuppressWarnings("DataFlowIssue")
    public static PersistenceUnitManager defaultPersistenceUnitManager(@Nullable String persistenceUnit, @Nullable String persistenceUnitRootLocation, @Nullable DataSource dataSource, @Nullable String[] packagesToScan, @Nullable String[] mappingResources, @Nullable String[] xmlLocations, boolean jta, @Nullable ResourceLoader resourceLoader, @Nullable SharedCacheMode cacheMode, @Nullable ValidationMode validationMode, @Nullable PersistenceUnitPostProcessor[] postProcessors) {
        DefaultPersistenceUnitManager manager = new DefaultPersistenceUnitManager();
        manager.setDefaultPersistenceUnitName(persistenceUnit);
        manager.setDefaultPersistenceUnitRootLocation(persistenceUnitRootLocation);
        if (jta) {
            manager.setDefaultJtaDataSource(dataSource);
        } else {
            manager.setDefaultDataSource(dataSource);
        }
        manager.setPackagesToScan(packagesToScan);
        manager.setMappingResources(mappingResources);
        if (xmlLocations != null) {
            manager.setPersistenceXmlLocations(xmlLocations);
        }
        if (resourceLoader != null) {
            manager.setResourceLoader(resourceLoader);
        }
        manager.setSharedCacheMode(cacheMode);
        manager.setValidationMode(validationMode);
        manager.setPersistenceUnitPostProcessors(postProcessors);
        manager.afterPropertiesSet();
        return manager;
    }

    /**
     * Returns an {@link org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter} configured from the given JPA properties
     *
     * @param properties the JPA properties providing the database, DDL generation and SQL display settings
     *
     * @return the configured {@link org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter}
     */

    @Nonnull
    public static EclipseLinkJpaVendorAdapter eclipseLinkJpaVendorAdapter(@Nonnull JpaProperties properties) {
        EclipseLinkJpaVendorAdapter adapter = new EclipseLinkJpaVendorAdapter();
        if (properties.getDatabase() != null) {
            adapter.setDatabase(properties.getDatabase());
        }
        adapter.setDatabasePlatform(properties.getDatabasePlatform());
        adapter.setGenerateDdl(properties.isGenerateDdl());
        adapter.setShowSql(properties.isShowSql());
        return adapter;
    }

    /**
     * Returns a {@link org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter} configured from the given JPA properties without preparing connections
     *
     * @param properties the JPA properties providing the database, DDL generation and SQL display settings
     *
     * @return the configured {@link org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter}
     */

    @Nonnull
    public static HibernateJpaVendorAdapter hibernateJpaVendorAdapter(@Nonnull JpaProperties properties) {
        return hibernateJpaVendorAdapter(properties, false);
    }

    /**
     * Returns a {@link org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter} configured from the given JPA properties
     *
     * @param properties the JPA properties providing the database, DDL generation and SQL display settings
     * @param prepareConnection whether the vendor adapter should prepare the connection before use
     *
     * @return the configured {@link org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter}
     *
     * @see org.springframework.boot.jpa.autoconfigure.JpaBaseConfiguration#jpaVendorAdapter()
     */
    @Nonnull
    public static HibernateJpaVendorAdapter hibernateJpaVendorAdapter(@Nonnull JpaProperties properties, boolean prepareConnection) {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        if (properties.getDatabase() != null) {
            adapter.setDatabase(properties.getDatabase());
        }
        adapter.setDatabasePlatform(properties.getDatabasePlatform());
        adapter.setGenerateDdl(properties.isGenerateDdl());
        adapter.setShowSql(properties.isShowSql());
        adapter.setPrepareConnection(prepareConnection);
        return adapter;
    }

    /**
     * Returns a {@link org.springframework.orm.jpa.JpaTransactionManager} without a specific entity manager factory, customized by the given providers
     *
     * @param customizers the provider of transaction manager customizers to apply
     *
     * @return the configured {@link org.springframework.orm.jpa.JpaTransactionManager}
     */

    @Nonnull
    public static JpaTransactionManager jpaTransactionManager(@Nonnull ObjectProvider<TransactionManagerCustomizers> customizers) {
        return jpaTransactionManager(null, customizers);
    }

    /**
     * Returns a {@link org.springframework.orm.jpa.JpaTransactionManager} for the given entity manager factory, customized by the given providers
     *
     * @param factory the entity manager factory, or {@code null} to create a manager without one
     * @param customizers the provider of transaction manager customizers to apply
     *
     * @return the configured {@link org.springframework.orm.jpa.JpaTransactionManager}
     */

    @Nonnull
    public static JpaTransactionManager jpaTransactionManager(@Nullable EntityManagerFactory factory, @Nonnull ObjectProvider<TransactionManagerCustomizers> customizers) {
        JpaTransactionManager transactionManager = (factory != null) ? new JpaTransactionManager(factory) : new JpaTransactionManager();
        customizers.ifAvailable(customizer -> customizer.customize(transactionManager));
        return transactionManager;
    }
}
