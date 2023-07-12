/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.enums; 

/**
 * MybatisPlus 支持 SQL 方法
 *
 * @author hubin
 * @since 2016-01-23
 */
public enum  SqlMethod {
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>") , 
    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>"); 

    private final String method; 
    private final String desc; 
    private final String sql; 

    SqlMethod(String method, String desc, String sql) {
        this.method = method;
        this.desc = desc;
        this.sql = sql;
    }
 

    public String getMethod() {
        return method;
    }
 

    public String getDesc() {
        return desc;
    }
 

    public String getSql() {
        return sql;
    }
}
