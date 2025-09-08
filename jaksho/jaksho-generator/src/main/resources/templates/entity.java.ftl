package ${package.Entity};

<#list importEntityFrameworkPackages as pkg>
import ${pkg};
</#list>

<#list importEntityJavaPackages as pkg>
import ${pkg};
</#list>
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * ${table.comment!}
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#list entityClassAnnotations as an>
${an.displayName}
</#list>
<#if superEntityClass??>
<#--  feature: 实现Serializable接口，避免waring  -->
public class ${entity} extends ${superEntityClass}<#if activeRecord><${entity}></#if> <#if entitySerialVersionUID>implements Serializable</#if> {
<#elseif activeRecord>
public class ${entity} extends Model<${entity}> {
<#elseif entitySerialVersionUID>
public class ${entity} implements Serializable {
<#else>
public class ${entity} {
</#if>
<#if entitySerialVersionUID>

    <#if entitySerialAnnotation>
    @Serial
    </#if>
    private static final long serialVersionUID = 1L;
</#if>
<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
    <#if field.keyFlag>
        <#assign keyPropertyName="${field.propertyName}"/>
    </#if>

    <#if field.comment!?length gt 0>
        <#if entityFieldUseJavaDoc>
    /**
     * ${field.comment}
     */
        </#if>
    </#if>
    <#list field.annotationAttributesList as an>
    ${an.displayName}
    </#list>
    private ${field.propertyType} ${field.propertyName};
</#list>
<#------------  END 字段循环遍历  ---------->
<#if !entityLombokModel>
    <#list table.fields as field>
        <#if field.propertyType == "boolean">
            <#assign getprefix="is"/>
        <#else>
            <#assign getprefix="get"/>
        </#if>

    public ${field.propertyType} ${getprefix}${field.capitalName}() {
        return ${field.propertyName};
    }

    <#if chainModel>
    public ${entity} set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
    <#else>
    public void set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
    </#if>
        this.${field.propertyName} = ${field.propertyName};
        <#if chainModel>
        return this;
        </#if>
    }
    </#list>
</#if>
<#if entityColumnConstant>
    <#list table.fields as field>

    public static final String ${field.name?upper_case} = "${field.name}";
    </#list>
</#if>
<#if activeRecord>

    @Override
    public Serializable pkVal() {
    <#if keyPropertyName??>
        return this.${keyPropertyName};
    <#else>
        return null;
    </#if>
    }
</#if>
<#if !entityLombokModel && entityToString>

    @Override
    public String toString() {
        return "${entity}{" +
    <#list table.fields as field>
        <#if field_index==0>
            "${field.propertyName} = " + ${field.propertyName} +
        <#else>
            ", ${field.propertyName} = " + ${field.propertyName} +
        </#if>
    </#list>
            "}";
    }
</#if>

    @Override
    @SuppressWarnings("all")
    public Map<String, Object> properties() {
        Map<String, Object> props = new LinkedHashMap<>();
        <#list table.fields as field>
        props.put("${field.propertyName}", ${field.propertyName});
        </#list>
        return props;
    }
}
