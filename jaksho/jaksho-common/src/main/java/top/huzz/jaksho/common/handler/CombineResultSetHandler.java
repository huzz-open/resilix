package top.huzz.jaksho.common.handler;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import top.huzz.jaksho.common.entity.BasicProperties;
import top.huzz.jaksho.common.entity.CombineResult;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * 自定义结果集处理器，用于处理多表查询结果映射到 CombineResult
 * <p>
 * 该处理器会拦截返回类型为 CombineResult 的查询，自动将多表查询结果
 * 按照表名分组映射到对应的实体对象中
 *
 * @author huzz
 * @since 1.0.2
 */
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class CombineResultSetHandler implements Interceptor {

    private static volatile TypeHandlerRegistry typeHandlerRegistry;
    private final DomainClassProvider domainClassProvider;

    public CombineResultSetHandler(DomainClassProvider domainClassProvider) {
        this.domainClassProvider = domainClassProvider;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取原始的 ResultSetHandler
        DefaultResultSetHandler defaultResultSetHandler = (DefaultResultSetHandler) invocation.getTarget();

        // 获取 MappedStatement
        MappedStatement mappedStatement = getMappedStatement(defaultResultSetHandler);

        // 检查返回类型是否为 CombineResult
        if (isCombineResultQuery(mappedStatement)) {
            return handleCombineResult(defaultResultSetHandler, invocation);
        }

        // 非 CombineResult 查询，使用默认处理
        return invocation.proceed();
    }

    /**
     * 处理 CombineResult 查询
     */
    private Object handleCombineResult(DefaultResultSetHandler defaultResultSetHandler, Invocation invocation) throws Throwable {
        Statement statement = (Statement) invocation.getArgs()[0];
        ResultSet resultSet = statement.getResultSet();

        if (resultSet == null) {
            return Collections.emptyList();
        }

        List<CombineResult> results = new ArrayList<>();

        // 获取列信息
        int columnCount = resultSet.getMetaData().getColumnCount();
        String[] columnNames = new String[columnCount];
        String[] tableNames = new String[columnCount];

        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = resultSet.getMetaData().getColumnName(i);
            tableNames[i - 1] = resultSet.getMetaData().getTableName(i);
        }

        // 按表名分组列信息
        Map<String, List<ColumnInfo>> tableColumns = groupColumnsByTable(columnNames, tableNames);

        // 处理每一行数据
        while (resultSet.next()) {
            CombineResult combineResult = new CombineResult();

            // 为每个表创建对应的实体对象
            for (Map.Entry<String, List<ColumnInfo>> entry : tableColumns.entrySet()) {
                String tableName = entry.getKey();
                List<ColumnInfo> columns = entry.getValue();

                // 获取对应的实体类
                Class<? extends BasicProperties> entityClass = domainClassProvider.getDomainClass(tableName);
                BasicProperties entity = createEntityFromResultSet(entityClass, columns, resultSet, defaultResultSetHandler);
                combineResult.getDomains().put(tableName, entity);
            }

            results.add(combineResult);
        }

        return results;
    }

    /**
     * 按表名分组列信息
     */
    private Map<String, List<ColumnInfo>> groupColumnsByTable(String[] columnNames, String[] tableNames) {
        Map<String, List<ColumnInfo>> tableColumns = new LinkedHashMap<>();

        for (int i = 0; i < columnNames.length; i++) {
            String tableName = tableNames[i];
            String columnName = columnNames[i];

            tableColumns.computeIfAbsent(tableName, k -> new ArrayList<>())
                    .add(new ColumnInfo(columnName, i));
        }

        return tableColumns;
    }

    /**
     * 从 ResultSet 创建实体对象
     */
    private BasicProperties createEntityFromResultSet(Class<? extends BasicProperties> entityClass, List<ColumnInfo> columns, ResultSet resultSet, DefaultResultSetHandler defaultResultSetHandler) throws Throwable {
        BasicProperties entity = entityClass.getDeclaredConstructor().newInstance();
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        for (ColumnInfo columnInfo : columns) {
            String columnName = columnInfo.columnName();
            int columnIndex = columnInfo.columnIndex();

            // 使用 MyBatis 标准的列名转换方法
            String propertyName = StringUtils.underlineToCamel(columnName);

            // 检查是否有对应的 setter 方法
            if (!metaObject.hasSetter(propertyName)) {
                continue;
            }

            // 获取属性类型
            Class<?> propertyType = metaObject.getSetterType(propertyName);

            // 获取列值并进行类型转换
            Object value = getValueFromResultSet(resultSet, columnIndex + 1, propertyType, defaultResultSetHandler);

            if (value != null) {
                metaObject.setValue(propertyName, value);
            }
        }
        return entity;
    }

    /**
     * 从 ResultSet 获取值并进行类型转换
     */
    private Object getValueFromResultSet(ResultSet resultSet, int columnIndex, Class<?> targetType, DefaultResultSetHandler defaultResultSetHandler) throws Throwable {
        // 获取 JDBC 类型
        int jdbcType = resultSet.getMetaData().getColumnType(columnIndex);

        // 获取对应的 TypeHandler
        TypeHandler<?> typeHandler = getTypeHandler(targetType, JdbcType.forCode(jdbcType), defaultResultSetHandler);
        if (typeHandler != null) {
            return typeHandler.getResult(resultSet, columnIndex);
        }

        // 如果没有找到 TypeHandler，直接使用 getObject 方法
        return resultSet.getObject(columnIndex);
    }

    /**
     * 获取 TypeHandler
     */
    private TypeHandler<?> getTypeHandler(Class<?> targetType, JdbcType jdbcType, DefaultResultSetHandler defaultResultSetHandler) {
        if (typeHandlerRegistry == null) {
            synchronized (CombineResultSetHandler.class) {
                if (typeHandlerRegistry == null) {
                    Configuration configuration = getConfiguration(defaultResultSetHandler);
                    typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                }
            }
        }
        return typeHandlerRegistry.getTypeHandler(targetType, jdbcType);
    }

    /**
     * 获取 MyBatis Configuration
     */
    private Configuration getConfiguration(DefaultResultSetHandler defaultResultSetHandler) {
        // 通过反射从 DefaultResultSetHandler 获取 Configuration
        MetaObject metaObject = SystemMetaObject.forObject(defaultResultSetHandler);
        return (Configuration) metaObject.getValue("configuration");
    }


    /**
     * 检查是否为 CombineResult 查询
     */
    private boolean isCombineResultQuery(MappedStatement mappedStatement) {
        if (mappedStatement == null) {
            return false;
        }

        ResultMap resultMap = mappedStatement.getResultMaps().isEmpty() ? null : mappedStatement.getResultMaps().get(0);
        return resultMap != null && CombineResult.class.isAssignableFrom(resultMap.getType());
    }

    /**
     * 获取 MappedStatement
     */
    private MappedStatement getMappedStatement(DefaultResultSetHandler defaultResultSetHandler) {
        MetaObject metaObject = SystemMetaObject.forObject(defaultResultSetHandler);
        return (MappedStatement) metaObject.getValue("mappedStatement");
    }

    /**
     * 列信息内部类
     */
    private record ColumnInfo(String columnName, int columnIndex) {
    }
}