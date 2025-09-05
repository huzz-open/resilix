package top.huzz.jaksho.api.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author huzz
 * @since 1.0.2
 */
public interface PageQuery<T> {
	Page<T> toPage();
}
