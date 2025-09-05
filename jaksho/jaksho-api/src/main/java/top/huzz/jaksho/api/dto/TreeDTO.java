package top.huzz.jaksho.api.dto;

/**
 * @author huzz
 * @since 1.0.2
 */
public interface TreeDTO {
	/**
	 * @return 节点唯一标识
	 */
	String getUlid();

	/**
	 * @return 父节点唯一标识
	 */
	String getParentUlid();

	/**
	 * @return 排序字段
	 */
	Integer getSortOrder();
}
