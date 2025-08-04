package top.huzz.jaksho.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * @author chenji
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class GeneratorRunner extends Settings implements ApplicationRunner {

    @Resource
    private DataSourceProperties dataSourceProperties;
    @Value("${since.version:}")
    private String sinceVersion;


    @Override
    public void run(ApplicationArguments args) {
        if (StringUtils.isEmpty(sinceVersion)) {
            // 报错，需要强制制定
            throw new IllegalArgumentException("请指定 since.version 参数");
        }
        create(sinceVersion, dataSourceProperties.getUrl(), dataSourceProperties.getUsername(), dataSourceProperties.getPassword());
    }

    static void create(String sinceVersion, String url, String username, String password) {
        DataSourceConfig.Builder dataSourceBuilder = new DataSourceConfig.Builder(url, username, password)
                .typeConvertHandler(new CustomTypeConvertHandler());
        //1、配置数据源
        FastAutoGenerator.create(dataSourceBuilder)
                //2、全局配置
                .globalConfig(builder -> {
                    builder.author(author) // 设置作者名
                            .outputDir(domainJavaPath.toString())   // 设置类的输出路径
                            .commentDate(sinceVersion::toString)   //注释日期
                            .dateType(DateType.ONLY_DATE)   //定义生成的实体类中日期的类型 TIME_PACK=LocalDateTime;ONLY_DATE=Date;
                            //.enableSwagger()   //开启 swagger 模式
                            .disableOpenDir();   //禁止打开输出目录，默认打开
                })
                //3、包配置
                .packageConfig(builder -> {
                    builder.parent(parent) // 设置父包名
                            .moduleName(module)   //设置模块包名
                            .entity("entity")   //pojo 实体类包名
                            .mapper("mapper")   //Mapper 包名
                            .xml("mapper.xml")  //Mapper XML 包名
                            .controller(controller) //Controller 包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, domainResourcesMapperPath.toString()));    //配置 mapper.xml 路径信息：项目的 resources 目录下
                })
                //4、策略配置
                .strategyConfig(builder -> {
                    builder
                            .enableCapitalMode()    //开启大写命名
                            .enableSkipView()   //创建实体类的时候跳过视图
                            .addInclude(listTable) // 设置需要生成的数据表名
                            .addTableSuffix(listTableSuffix) //设置 过滤 表的后缀
                            .addTablePrefix(listTablePrefix) // 设置 过滤 表的前缀

                            //4.1、实体类策略配置
                            .entityBuilder()
                            .enableSerialAnnotation()
                            .enableChainModel() //开启链式模型
                            .enableFileOverride() //开启文件覆盖
                            .enableTableFieldAnnotation() // 开启生成实体时生成字段注解
                            .enableColumnConstant()  //开启生成字段常量
                            .enableLombok() //开启 Lombok
                            .superClass(top.huzz.jaksho.common.entity.BasicProperties.class) // 指定Entity的父类.
                            .versionColumnName("version")   //乐观锁字段名(数据库)
                            .versionPropertyName("version") //乐观锁属性名(实体)
                            .logicDeleteColumnName("deleted")   //逻辑删除字段名(数据库)
                            .logicDeletePropertyName("deleteFlag")  //逻辑删除属性名(实体)
                            .naming(NamingStrategy.underline_to_camel)  //数据库表映射到实体的命名策略：默认是下划线转驼峰命。这里可以不设置
                            .columnNaming(NamingStrategy.underline_to_camel)    //数据库表字段映射到实体的命名策略：下划线转驼峰命。（默认是和naming一致，所以也可以不设置）
                            .addTableFills(
                                    new Column("create_time", FieldFill.INSERT),
                                    new Column("update_time", FieldFill.INSERT_UPDATE),
                                    new Column("workspace_id", FieldFill.INSERT)
                            )
                            //.idType(IdType.AUTO)    //设置主键自增

                            //4.2、Controller策略配置
                            .controllerBuilder()
                            .disable() // 不需要生成 Controller 类

                            //4.3、service 策略配置
                            .serviceBuilder()
                            .disable() // 不需要生成 Service 接口和实现类

                            //4.4、Mapper策略配置
                            .mapperBuilder()
                            .enableFileOverride() //开启文件覆盖
                            .superClass(BaseMapper.class)   //设置父类
                            .enableBaseResultMap()  //启用 BaseResultMap 生成
                            .enableBaseColumnList() //启用 BaseColumnList
                            .formatMapperFileName("%sMapper")   //格式化 mapper 文件名称
                            .mapperAnnotation(Mapper.class)       //开启 @Mapper 注解
                            .formatXmlFileName("%s") //格式化Xml文件名称
                            .formatMapperFileName("%sMapper");   //格式化Mapper文件名称
                })
                //5、模板
                .templateEngine(new FreemarkerTemplateEngine())
                //6、执行
                .execute();
    }
}
