package com.github.pagehelper.util; 

import com.github.pagehelper.Page; 
import com.github.pagehelper.PageRowBounds; 
import com.github.pagehelper.cache.Cache; 
import com.github.pagehelper.cache.CacheFactory; 
import com.github.pagehelper.dialect.*; 
import org.apache.ibatis.cache.CacheKey; 
import org.apache.ibatis.mapping.MappedStatement; 
import org.apache.ibatis.reflection.MetaObject; 
import org.apache.ibatis.session.RowBounds; 

import java.lang.reflect.Method; 
import java.util.HashMap; 
import java.util.Map; 
import java.util.Properties; 

/**
 * @author liuzh
 */
  class  BaseSqlUtil {
	
    

	
    //params参数映射
    

	
    

	
    //request获取方法
    

	
    

	
    

	

    static {
        try {
            requestClass = Class.forName("javax.servlet.ServletRequest");
            getParameterMap = requestClass.getMethod("getParameterMap", new Class[]{});
            hasRequest = true;
        } catch (Throwable e) {
            hasRequest = false;
        }
        //注册别名
        dialectAliasMap.put("hsqldb", HsqldbDialect.class);
        dialectAliasMap.put("h2", HsqldbDialect.class);
        dialectAliasMap.put("postgreSQL", HsqldbDialect.class);

        dialectAliasMap.put("mysql", MySqlDialect.class);
        dialectAliasMap.put("mariadb", MySqlDialect.class);
        dialectAliasMap.put("sqlite", MySqlDialect.class);

        dialectAliasMap.put("oracle", OracleDialect.class);
        dialectAliasMap.put("db2", Db2Dialect.class);
        dialectAliasMap.put("informix", InformixDialect.class);

        dialectAliasMap.put("sqlserver", SqlServerDialect.class);
        dialectAliasMap.put("sqlserver2012", SqlServer2012Dialect.class);
    }

	

    //缓存count查询的ms
    

	
    //RowBounds参数offset作为PageNum使用 - 默认不使用
    

	
    //RowBounds是否进行count查询 - 默认不查询
    

	
    //当设置为true的时候，如果pagesize设置为0（或RowBounds的limit=0），就不执行分页，返回全部结果
    

	
    //分页合理化
    

	
    //是否支持接口参数来传递分页参数，默认false
    

	

    <<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647893923300/fstmerge_var1_4388216067069696802
public static String fromJdbcUrl(String jdbcUrl) {
        for (String dialect : dialectAliasMap.keySet()) {
            if (jdbcUrl.indexOf(":" + dialect.toLowerCase() + ":") != -1) {
                return dialect;
            }
        }
        return null;
    }
=======
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647893923300/fstmerge_var2_3649538470593998761


	

    /**
     * 反射类
     *
     * @param className
     * @return
     * @throws Exception
     */
    

	

    

	

    /**
     * 获取Page参数
     *
     * @return
     */
    

	

    

	

    /**
     * 移除本地变量
     */
    

	

    /**
     * 获取orderBy
     *
     * @return
     */
    

	

    /**
     * 对象中获取分页参数
     *
     * @param params
     * @return
     */
    

	

    /**
     * 从对象中取参数
     *
     * @param paramsObject
     * @param paramName
     * @param required
     * @return
     */
    

	

    

	

    /**
     * 获取分页参数
     *
     * @param parameterObject
     * @param rowBounds
     * @return
     */
    <<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647893923381/fstmerge_var1_2690732245420813365
public Page getPage(Object parameterObject, RowBounds rowBounds) {
        Page page = getLocalPage();
        if (page == null || page.isOrderByOnly()) {
            Page oldPage = page;
            //这种情况下,page.isOrderByOnly()必然为true，所以不用写到条件中
            if ((rowBounds == null || rowBounds == RowBounds.DEFAULT) && page != null) {
                return oldPage;
            }
            if (rowBounds != RowBounds.DEFAULT) {
                if (offsetAsPageNum) {
                    page = new Page(rowBounds.getOffset(), rowBounds.getLimit(), rowBoundsWithCount);
                } else {
                    page = new Page(new int[]{rowBounds.getOffset(), rowBounds.getLimit()}, rowBoundsWithCount);
                    //offsetAsPageNum=false的时候，由于PageNum问题，不能使用reasonable，这里会强制为false
                    page.setReasonable(false);
                }
            } else {
                try {
                    page = getPageFromObject(parameterObject);
                } catch (Exception e) {
                    return null;
                }
            }
            if (oldPage != null) {
                page.setOrderBy(oldPage.getOrderBy());
            }
            setLocalPage(page);
        }
        //分页合理化
        if (page.getReasonable() == null) {
            page.setReasonable(reasonable);
        }
        //当设置为true的时候，如果pagesize设置为0（或RowBounds的limit=0），就不执行分页，返回全部结果
        if (page.getPageSizeZero() == null) {
            page.setPageSizeZero(pageSizeZero);
        }
        return page;
    }
=======
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647893923381/fstmerge_var2_1975868874359580198


	

    

	

    

	

    

	

    

	

    

	

    

	

    static {
        try {
            requestClass = Class.forName("javax.servlet.ServletRequest");
            getParameterMap = requestClass.getMethod("getParameterMap", new Class[]{});
            hasRequest = true;
        } catch (Throwable e) {
            hasRequest = false;
        }
        //注册别名
        dialectAliasMap.put("hsqldb", HsqldbDialect.class);
        dialectAliasMap.put("h2", HsqldbDialect.class);
        dialectAliasMap.put("postgresql", HsqldbDialect.class);

        dialectAliasMap.put("mysql", MySqlDialect.class);
        dialectAliasMap.put("mariadb", MySqlDialect.class);
        dialectAliasMap.put("sqlite", MySqlDialect.class);

        dialectAliasMap.put("oracle", OracleDialect.class);
        dialectAliasMap.put("db2", Db2Dialect.class);
        dialectAliasMap.put("informix", InformixDialect.class);

        dialectAliasMap.put("sqlserver", SqlServerDialect.class);
        dialectAliasMap.put("sqlserver2012", SqlServer2012Dialect.class);
        dialectAliasMap.put("derby", SqlServer2012Dialect.class);
    }


}
