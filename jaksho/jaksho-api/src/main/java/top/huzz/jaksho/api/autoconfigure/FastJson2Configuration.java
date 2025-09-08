package top.huzz.jaksho.api.autoconfigure;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.writer.ObjectWriter;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import top.huzz.jaksho.api.config.AppConfig;
import top.huzz.jaksho.common.entity.CombineResult;
import top.huzz.jaksho.api.json.CombineResultSerializer;


/**
 * FastJson2 配置类
 * <p>
 * 注册自定义的序列化器和反序列化器
 *
 * @author huzz
 * @since 1.0.2
 */
@ConditionalOnClass(JSON.class)
public class FastJson2Configuration {

    @Resource
    private AppConfig appConfig;

    @PostConstruct
    public void configureFastJson2() {
        // 注册 CombineResult 的序列化器
        ObjectWriter<CombineResult> serializer = new CombineResultSerializer(appConfig);
        JSON.register(CombineResult.class, serializer);
    }
}
