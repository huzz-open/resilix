package top.huzz.jaksho.common.entity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author huzz
 * @since 1.0.2
 */
@Getter
@Setter
public final class PageResult<T> {
    private List<T> rows;
    private long total;

    private PageResult() {
    }

    public static <T> PageResult<T> of(List<T> rows, IPage<T> page) {
        return of(rows, page.getTotal());
    }

    public static <T> PageResult<T> of(List<T> rows, long total) {
        PageResult<T> result = new PageResult<>();
        result.setRows(rows);
        result.setTotal(total);
        return result;
    }

}
