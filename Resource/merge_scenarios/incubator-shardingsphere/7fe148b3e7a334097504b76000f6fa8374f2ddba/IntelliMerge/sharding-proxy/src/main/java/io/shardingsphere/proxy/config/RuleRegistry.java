/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.proxy.config;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.shardingsphere.core.constant.ShardingProperties;
import io.shardingsphere.core.constant.ShardingPropertiesConstant;
import io.shardingsphere.core.constant.TransactionType;
import io.shardingsphere.core.exception.ShardingException;
import io.shardingsphere.core.metadata.ShardingMetaData;
import io.shardingsphere.core.rule.MasterSlaveRule;
import io.shardingsphere.core.rule.ShardingRule;
import io.shardingsphere.core.yaml.proxy.YamlProxyConfiguration;
import io.shardingsphere.core.yaml.sharding.DataSourceParameter;
import io.shardingsphere.proxy.metadata.ProxyShardingMetaData;
import lombok.Getter;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;

/**
 * Sharding rule registry.
 *
 * @author zhangliang
 * @author zhangyonglun
 * @author panjuan
 * @author zhaojun
 */
@Getter
public final class RuleRegistry {

    private static final int MAX_EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    
    private static final RuleRegistry INSTANCE = new RuleRegistry();
    
    private final Map<String, DataSource> dataSourceMap;
    
    private final ShardingRule shardingRule;
    
    private final MasterSlaveRule masterSlaveRule;
    
    private final ShardingMetaData shardingMetaData;
    
    private final boolean isOnlyMasterSlave;
    
    private final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS));
    
    private final String proxyMode;
    
    private final boolean showSQL;
    
    private final TransactionType transactionType;
    
    private RuleRegistry() {
        YamlProxyConfiguration yamlProxyConfiguration;
        try {
            yamlProxyConfiguration = YamlProxyConfiguration.unmarshal(new File(getClass().getResource("/conf/config.yaml").getFile()));
        } catch (final IOException ex) {
            throw new ShardingException(ex);
        }
        transactionType = TransactionType.findByValue(yamlProxyConfiguration.getTransactionMode());
        dataSourceMap = ProxyRawDataSourceFactory.create(transactionType, yamlProxyConfiguration);
        shardingRule = yamlProxyConfiguration.obtainShardingRule(Collections.<String>emptyList());
        masterSlaveRule = yamlProxyConfiguration.obtainMasterSlaveRule();
        isOnlyMasterSlave = shardingRule.getTableRules().isEmpty() && !masterSlaveRule.getMasterDataSourceName().isEmpty();
        Properties properties = yamlProxyConfiguration.getShardingRule().getProps();
        ShardingProperties shardingProperties = new ShardingProperties(null == properties ? new Properties() : properties);
        proxyMode = shardingProperties.getValue(ShardingPropertiesConstant.PROXY_MODE);
        showSQL = shardingProperties.getValue(ShardingPropertiesConstant.SQL_SHOW);
        shardingMetaData = new ProxyShardingMetaData(executorService, dataSourceMap);
            if (!isOnlyMasterSlave) {
                shardingMetaData.init(shardingRule);
            }
            
    }
    
    /**
     * Get instance of sharding rule registry.
     *
     * @return instance of sharding rule registry
     */
    public static RuleRegistry getInstance() {
        return INSTANCE;
    }
}