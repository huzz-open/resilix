package top.huzz.jaksho.api.autoconfigure;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import top.huzz.jaksho.api.cache.TableDomainClassCache;
import top.huzz.jaksho.common.handler.CombineResultSetHandler;

import java.util.List;

/**
 * CombineResult 相关配置类
 * <p>
 * 负责注册自定义的 ResultSetHandler 来处理多表查询结果映射
 *
 * @author huzz
 * @since 1.0.2
 */
@ConditionalOnClass(SqlSessionFactory.class)
public class CombineResultConfiguration {

    @Resource
    private List<SqlSessionFactory> sqlSessionFactories;

    /**
     * 注册自定义的 ResultSetHandler
     */
    @PostConstruct
    public void registerCombineResultHandler() {
        if (sqlSessionFactories != null) {
            CombineResultSetHandler combineResultHandler = new CombineResultSetHandler(TableDomainClassCache::getDomainClass);

            for (SqlSessionFactory sqlSessionFactory : sqlSessionFactories) {
                // 将自定义的 ResultSetHandler 添加到 MyBatis 的拦截器链中
                sqlSessionFactory.getConfiguration().addInterceptor(combineResultHandler);
            }
        }
    }

}
