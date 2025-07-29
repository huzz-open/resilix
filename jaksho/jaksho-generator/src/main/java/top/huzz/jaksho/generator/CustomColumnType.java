package top.huzz.jaksho.generator;

import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import lombok.Getter;

/**
 * @author chenji
 * @since 1.0.2
 */
@Getter
public abstract class CustomColumnType implements IColumnType {
    // 添加一个自定义flag，为了可以方便的在模板中识别自定义字段
    private final boolean flag = true;
}
