package top.huzz.jaksho.api.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huzz
 * @since 1.0.2
 */
@Getter
@Setter
public abstract class AbstractPageQuery<T> implements PageQuery<T> {
	/**
	 * 当前页码，从1开始
	 */
	private long current = 1;
	/**
	 * 每页大小
	 */
	private long pageSize = 10;

	@Override
	public Page<T> toPage() {
		return Page.of(current, pageSize);
	}
}
