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


import java.security.PublicKey;
import java.util.Properties;
import jakarta.annotation.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import com.alibaba.druid.filter.config.ConfigFilter;
import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.pool.DruidDataSource;
import com.jiovan.spring.starter.datasourcebuilder.property.DruidDataSourceProperties;


/**
 * Utilities for decrypting Druid password
 *
 * @author David Hsing
 *
 * @see "com.alibaba.druid.filter.config.ConfigTools#decrypt(String)"
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public abstract class DruidDecryptUtils {
    /**
     * Returns the plain password of the given {@link com.alibaba.druid.pool.DruidDataSource} by decrypting its configured cipher password
     *
     * @param dataSource the data source holding the cipher password and connection properties, may be {@code null}
     *
     * @return the plain password, or {@code null} if {@code dataSource} is {@code null}
     *
     * @throws Exception if the cipher password cannot be decrypted
     */

    @Nullable
    public static String decryptPassword(@Nullable DruidDataSource dataSource) throws Exception {
        return dataSource == null ? null : decryptPassword(dataSource.getPassword(), dataSource.getConnectProperties());
    }

    /**
     * Returns the plain password of the given {@link com.jiovan.spring.starter.datasourcebuilder.property.DruidDataSourceProperties} by decrypting its configured cipher password
     *
     * @param properties the properties holding the cipher password and connection properties, may be {@code null}
     *
     * @return the plain password, or {@code null} if {@code properties} is {@code null}
     *
     * @throws Exception if the cipher password cannot be decrypted
     */

    @Nullable
    public static String decryptPassword(@Nullable DruidDataSourceProperties properties) throws Exception {
        return properties == null ? null : decryptPassword(properties.getPassword(), properties.getConnectionProperties());
    }

    /**
     * Return the plain password of {@link com.alibaba.druid.pool.DruidDataSource}
     *
     * @param cipherPassword a cipher password string
     * @param connectionProperties a {@link java.util.Properties} that contains fields of {@link com.alibaba.druid.filter.config.ConfigFilter} as keys
     *
     * @return the plain password of {@link com.alibaba.druid.pool.DruidDataSource}
     *
     * @throws java.security.NoSuchAlgorithmException if no Provider supports a KeyFactorySpi implementation for the specified algorithm
     * @throws java.security.spec.InvalidKeySpecException if the given key specification is inappropriate for key factory to produce a private key
     * @throws javax.crypto.NoSuchPaddingException if the given key padding is inappropriate for key factory to produce an {@link javax.crypto.Cipher} instance
     * @throws java.security.InvalidKeyException if the given key specification is inappropriate to init an {@link javax.crypto.Cipher} instance
     */
    @Nullable
    @SuppressWarnings("deprecation")
    public static String decryptPassword(@Nullable String cipherPassword, @Nullable Properties connectionProperties) throws Exception {
        if (!StringUtils.hasText(cipherPassword) || ObjectUtils.isEmpty(connectionProperties)) {
            return null;
        }
        ConfigFilter filter = new ConfigFilter();
        if (filter.isDecrypt(connectionProperties, null)) {
            PublicKey publicKey = filter.getPublicKey(connectionProperties, null);
            return ConfigTools.decrypt(publicKey, cipherPassword);
        }
        return cipherPassword;
    }

    /**
     * Returns the plain password decrypted with the connection properties given as a semicolon-separated string
     *
     * @param cipherPassword the cipher password string, may be {@code null} or blank
     * @param connectionProperties the semicolon-separated connection properties containing Druid config filter settings, may be {@code null}
     *
     * @return the plain password, or {@code null} if either argument is blank
     *
     * @throws Exception if the cipher password cannot be decrypted
     */

    @Nullable
    public static String decryptPassword(@Nullable String cipherPassword, @Nullable String connectionProperties) throws Exception {
        if (!StringUtils.hasText(cipherPassword) || !StringUtils.hasText(connectionProperties)) {
            return null;
        }
        String[] semicolons = StringUtils.split(connectionProperties, ";");    // $NON-NLS-1$
        if (ObjectUtils.isEmpty(semicolons)) {
            return null;
        }
        Properties properties = StringUtils.splitArrayElementsIntoProperties(semicolons, "=", " ");    // $NON-NLS-1$ // $NON-NLS-2$
        return decryptPassword(cipherPassword, properties);
    }
}
