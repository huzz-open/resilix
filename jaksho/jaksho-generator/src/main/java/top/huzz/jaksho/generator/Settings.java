package top.huzz.jaksho.generator;

import top.huzz.jaksho.common.constant.BasicFieldType;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库代码生成器的设置
 *
 * @author chenji
 * @since 1.0.0
 */
public class Settings {
    /**
     * 设置需要自动代码生成的表名，说明：
     * <p>带ac_前缀的表是系统的访问控制相关的表</p>
     */
    static final List<String> listTable = List.of(
            "sr_biz_field_type",
            "sr_object_biz_field_type_ref",

            "sr_api_definition",
            "sr_api_field"
    );

    public static Map<String, Class<?>> tableFiledTypeMap = new LinkedHashMap<>();

    static {
        tableFiledTypeMap.put(TypeRegistryDelegate.calcKey("sr_biz_field_type", "basic_field_type"), BasicFieldType.class);
    }

    static final List<String> listTableSuffix = List.of();    //设置 过滤 表的后缀
    static final List<String> listTablePrefix = List.of("ac_", "sr_"); //设置 过滤 表的前缀

    //基本信息
    static final String author = "huzz";    //作者
    static final String parent = "top.huzz.jaksho";   //父包名
    static final String module = "domain";   //模块包名
    static final String controller = "controller";
    static final String datasourceFile = "datasource.yaml";

    static final Path projectRootDir = Path.of(System.getProperty("user.dir"), "jaksho");
    static final Path domainRootPath = Path.of(projectRootDir.toString(), "jaksho-domain");
    static final Path domainJavaPath = Path.of(domainRootPath.toString(), "src", "main", "java");
    static final Path domainResourcesPath = Path.of(domainRootPath.toString(), "src", "main", "resources");
    static final Path domainResourcesMapperPath = Path.of(domainResourcesPath.toString(), "mapper");
}
