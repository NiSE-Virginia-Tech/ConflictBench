package com.baomidou.mybatisplus.core.enums;

/**
 * MybatisPlus 支持 SQL 方法
 *
 * @author hubin
 * @since 2016-01-23
 */public enum SqlMethod {
  INSERT_ONE("insert", "\u63d2\u5165\u4e00\u6761\u6570\u636e\uff08\u9009\u62e9\u5b57\u6bb5\u63d2\u5165\uff09", "<script>\nINSERT INTO %s %s VALUES %s\n</script>"),
  DELETE_BY_ID("deleteById", "\u6839\u636eID \u5220\u9664\u4e00\u6761\u6570\u636e", "<script>\nDELETE FROM %s WHERE %s=#{%s}\n</script>"),
  DELETE_BY_MAP("deleteByMap", "\u6839\u636ecolumnMap \u6761\u4ef6\u5220\u9664\u8bb0\u5f55", "<script>\nDELETE FROM %s %s\n</script>"),
  DELETE("delete", "\u6839\u636e entity \u6761\u4ef6\u5220\u9664\u8bb0\u5f55", "<script>\nDELETE FROM %s %s %s\n</script>"),
  DELETE_BATCH_BY_IDS("deleteBatchIds", "\u6839\u636eID\u96c6\u5408\uff0c\u6279\u91cf\u5220\u9664\u6570\u636e", "<script>\nDELETE FROM %s WHERE %s IN (%s)\n</script>"),
  LOGIC_DELETE_BY_ID("deleteById", "\u6839\u636eID \u903b\u8f91\u5220\u9664\u4e00\u6761\u6570\u636e", "<script>\nUPDATE %s %s WHERE %s=#{%s} %s\n</script>"),
  LOGIC_DELETE_BY_MAP("deleteByMap", "\u6839\u636ecolumnMap \u6761\u4ef6\u903b\u8f91\u5220\u9664\u8bb0\u5f55", "<script>\nUPDATE %s %s %s\n</script>"),
  LOGIC_DELETE("delete", "\u6839\u636e entity \u6761\u4ef6\u903b\u8f91\u5220\u9664\u8bb0\u5f55", "<script>\nUPDATE %s %s %s %s\n</script>"),
  LOGIC_DELETE_BATCH_BY_IDS("deleteBatchIds", "\u6839\u636eID\u96c6\u5408\uff0c\u6279\u91cf\u903b\u8f91\u5220\u9664\u6570\u636e", "<script>\nUPDATE %s %s WHERE %s IN (%s) %s\n</script>"),
  UPDATE_BY_ID("updateById", "\u6839\u636eID \u9009\u62e9\u4fee\u6539\u6570\u636e", "<script>\nUPDATE %s %s WHERE %s=#{%s} %s\n</script>"),
  UPDATE("update", "\u6839\u636e whereEntity \u6761\u4ef6\uff0c\u66f4\u65b0\u8bb0\u5f55", "<script>\nUPDATE %s %s %s %s\n</script>"),
  LOGIC_UPDATE_BY_ID("updateById", "\u6839\u636eID \u4fee\u6539\u6570\u636e", "<script>\nUPDATE %s %s WHERE %s=#{%s} %s\n</script>"),
  SELECT_BY_ID("selectById", "\u6839\u636eID \u67e5\u8be2\u4e00\u6761\u6570\u636e", " %s SELECT %s FROM %s WHERE %s=#{%s}"),
  SELECT_BY_MAP("selectByMap", "\u6839\u636ecolumnMap \u67e5\u8be2\u4e00\u6761\u6570\u636e", "<script>\n %s SELECT %s FROM %s %s\n</script>"),
  SELECT_BATCH_BY_IDS("selectBatchIds", "\u6839\u636eID\u96c6\u5408\uff0c\u6279\u91cf\u67e5\u8be2\u6570\u636e", "<script>\n %s SELECT %s FROM %s WHERE %s IN (%s)\n</script>", "<script>\n %s SELECT %s FROM %s WHERE %s IN (%s)" + "\n</script>"),
  SELECT_ONE("selectOne", "\u67e5\u8be2\u6ee1\u8db3\u6761\u4ef6\u4e00\u6761\u6570\u636e", "<script>\n %s SELECT %s FROM %s %s %s\n</script>"),
  SELECT_COUNT("selectCount", "\u67e5\u8be2\u6ee1\u8db3\u6761\u4ef6\u603b\u8bb0\u5f55\u6570", "<script>\n %s SELECT COUNT(%s) FROM %s %s %s\n</script>"),
  SELECT_LIST("selectList", "\u67e5\u8be2\u6ee1\u8db3\u6761\u4ef6\u6240\u6709\u6570\u636e", "<script>\n %s SELECT %s FROM %s %s %s\n</script>"),
  SELECT_PAGE("selectPage", "\u67e5\u8be2\u6ee1\u8db3\u6761\u4ef6\u6240\u6709\u6570\u636e\uff08\u5e76\u7ffb\u9875\uff09", "<script>\n %s SELECT %s FROM %s %s %s\n</script>"),
  SELECT_MAPS("selectMaps", "\u67e5\u8be2\u6ee1\u8db3\u6761\u4ef6\u6240\u6709\u6570\u636e", "<script>\n %s SELECT %s FROM %s %s %s\n</script>"),
  SELECT_MAPS_PAGE("selectMapsPage", "\u67e5\u8be2\u6ee1\u8db3\u6761\u4ef6\u6240\u6709\u6570\u636e\uff08\u5e76\u7ffb\u9875\uff09", "<script>\n %s SELECT %s FROM %s %s %s\n</script>"),
  SELECT_OBJS("selectObjs", "\u67e5\u8be2\u6ee1\u8db3\u6761\u4ef6\u6240\u6709\u6570\u636e", "<script>\n %s SELECT %s FROM %s %s %s\n</script>"),
  LOGIC_SELECT_BY_ID("selectById", "\u6839\u636eID \u67e5\u8be2\u4e00\u6761\u6570\u636e", " %s SELECT %s FROM %s WHERE %s=#{%s} %s"),
  LOGIC_SELECT_BATCH_BY_IDS("selectBatchIds", "\u6839\u636eID\u96c6\u5408\uff0c\u6279\u91cf\u67e5\u8be2\u6570\u636e", "<script>\n %s SELECT %s FROM %s WHERE %s IN (%s) %s\n</script>")
  ;

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