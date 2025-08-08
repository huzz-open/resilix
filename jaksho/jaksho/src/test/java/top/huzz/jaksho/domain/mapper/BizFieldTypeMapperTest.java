package top.huzz.jaksho.domain.mapper;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import top.huzz.jaksho.common.constant.BasicFieldType;
import top.huzz.jaksho.common.constant.CollectionType;
import top.huzz.jaksho.domain.entity.BizFieldType;

import java.time.LocalDateTime;

/**
 * @author chenji
 * @since 1.0.2
 */
@SpringBootTest
@ConfigurationPropertiesScan
@MapperScan("top.huzz.jaksho.domain.mapper")
@ComponentScan("top.huzz.jaksho")
public class BizFieldTypeMapperTest {
    @Resource
    private BizFieldTypeMapper bizFieldTypeMapper;

    @Test
    public void testCustomTypeJSONParse() {
        int id = 1;
        bizFieldTypeMapper.deleteById(id);
        try {
            BizFieldType bizFieldType = new BizFieldType();
            bizFieldType.setId(id);
            bizFieldType.setMinimum(0);
            bizFieldType.setMaximum(128);
            bizFieldType.setCollectionType(CollectionType.LIST);
            bizFieldType.setBasicFieldType(BasicFieldType.STRING);
            bizFieldType.setName("Str128");
            bizFieldType.setDescription("字符串类型");
            bizFieldType.setWorkspaceId(1);
            LocalDateTime now = LocalDateTime.now();
            bizFieldType.setCreateTime(now);
            bizFieldType.setUpdateTime(now);

            bizFieldTypeMapper.insert(bizFieldType);

            BizFieldType selectById = bizFieldTypeMapper.selectById(id);
            Assertions.assertNotNull(selectById);
            Assertions.assertEquals("Str128", selectById.getName());
            Assertions.assertEquals(BasicFieldType.STRING, selectById.getBasicFieldType());
            Assertions.assertEquals(CollectionType.LIST, selectById.getCollectionType());
        } finally {
            bizFieldTypeMapper.deleteById(id);
        }
    }
}
