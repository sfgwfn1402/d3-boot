package com.dddframework.data.plugin;

import com.baomidou.mybatisplus.core.MybatisDefaultParameterHandler;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Optional;

/**
 * 分页插件
 *
 * @author Jensen
 * @公众号 架构师修行录
 */
@Slf4j(topic = "### BASE-DATA : PaginationPlugin ###")
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PaginationPlugin extends AbstractSqlParserHandler implements Interceptor {
    public PaginationPlugin() {
        log.debug("Loading PaginationPlugin");
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        // SQL解析
        this.sqlParser(metaObject);
        // 先判断是不是SELECT操作，并跳过存储过程
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (SqlCommandType.SELECT != mappedStatement.getSqlCommandType() || StatementType.CALLABLE == mappedStatement.getStatementType()) {
            return invocation.proceed();
        }
        // 针对定义了rowBounds，做为mapper接口方法的参数
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        Object paramObj = boundSql.getParameterObject();
        // 获取参数里的page对象
        IPage<?> page = findPage(paramObj).orElse(null);
        // 不需要分页的场合，根据返回对象是否为Page来判断是否分页
        if (null == page || page.getSize() < 0) {
            return invocation.proceed();
        }
        String originalSql = boundSql.getSql();
        // 构造分页 SQL 语句
        String paginationSql = getPaginationSql(originalSql, page.getCurrent(), page.getSize());
        // 替换原始 SQL 语句
        metaObject.setValue("delegate.boundSql.sql", paginationSql);
        Object result = invocation.proceed();
        // 构造 COUNT SQL 语句
        if (page.getSize() >= 0) {
            long total = count(getCountSql(originalSql), mappedStatement, boundSql, (Connection) invocation.getArgs()[0]);
            page.setTotal(total);
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return target instanceof StatementHandler ? Plugin.wrap(target, this) : target;
    }

    /**
     * 查找分页参数
     *
     * @param parameterObject 参数对象
     * @return 分页参数
     */
    protected static Optional<IPage> findPage(Object parameterObject) {
        if (parameterObject != null) {
            if (parameterObject instanceof Map) {
                Map<?, ?> parameterMap = (Map<?, ?>) parameterObject;
                for (Map.Entry entry : parameterMap.entrySet()) {
                    if (entry.getValue() != null && entry.getValue() instanceof IPage) {
                        return Optional.of((IPage) entry.getValue());
                    }
                }
            } else if (parameterObject instanceof IPage) {
                return Optional.of((IPage) parameterObject);
            }
        }
        return Optional.empty();
    }

    /**
     * 查询总记录条数
     *
     * @param sql             count sql
     * @param mappedStatement MappedStatement
     * @param boundSql        BoundSql
     * @param connection      Connection
     */
    protected static long count(String sql, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            DefaultParameterHandler parameterHandler = new MybatisDefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            long total = 0;
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    total = resultSet.getLong(1);
                }
            }
            return total;
        } catch (Exception e) {
            throw ExceptionUtils.mpe("Error: Method queryTotal execution error of sql : \n %s \n", e, sql);
        }
    }

    /**
     * 获取 COUNT SQL 语句
     *
     * @param originalSql ignore
     * @return ignore
     */
    protected static String getCountSql(String originalSql) {
        return String.format("SELECT COUNT(1) FROM ( %s ) TOTAL", originalSql);
    }

    // 获取分页 SQL 语句
    protected static String getPaginationSql(String originalSql, long pageNum, long pageSize) {
        return String.format("%s LIMIT %s, %s", originalSql, (pageNum - 1) * pageSize, pageSize);
    }
}
