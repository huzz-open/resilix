package top.huzz.jaksho.generator;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.type.ITypeConvertHandler;
import com.baomidou.mybatisplus.generator.type.TypeRegistry;
import jakarta.annotation.Nonnull;

import java.util.Objects;

/**
 * @author chenji
 * @since 1.0.2
 */
public class CustomTypeConvertHandler implements ITypeConvertHandler {
    volatile TypeRegistryDelegate delegate;

    @Override
    @Nonnull
    public IColumnType convert(GlobalConfig globalConfig, TypeRegistry typeRegistry, TableField.MetaInfo metaInfo) {
        return initDelegate(typeRegistry).getColumnType(metaInfo);
    }

    private TypeRegistryDelegate initDelegate(@Nonnull TypeRegistry typeRegistry) {
        Objects.requireNonNull(typeRegistry);
        if (delegate == null) {
            synchronized (this) {
                if (delegate == null) {
                    delegate = new TypeRegistryDelegate(typeRegistry);
                }
            }
        }
        return delegate;
    }
}
