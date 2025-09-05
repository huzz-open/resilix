package top.huzz.jaksho.bizfun;

import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import top.huzz.jaksho.api.dto.TreeDTO;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huzz
 * @since 1.0.2
 */
public class TreeDTOUtil {

	private static final String ROOT_KEY = "ROOT";

	/**
	 * 验证上下文，包含公共计算结果
	 */
	private static class ValidationContext<T extends TreeDTO> {
		private final List<T> list;
		private final Set<String> allUlids;
		private final Map<String, List<T>> groupedByParent;
		private final Map<String, List<String>> parentChildMap;
		private final Set<String> rootNodes;

		public ValidationContext(List<T> list) {
			this.list = list;
			this.allUlids = list.stream()
					.map(T::getUlid)
					.collect(Collectors.toSet());
			this.groupedByParent = list.stream()
					.collect(Collectors.groupingBy(
							dto -> StringUtils.isBlank(dto.getParentUlid()) ? ROOT_KEY : dto.getParentUlid()
					));
			this.parentChildMap = new HashMap<>();
			this.rootNodes = new HashSet<>();

			// 构建父子关系映射和根节点集合
			for (T dto : list) {
				String parentUlid = dto.getParentUlid();
				if (StringUtils.isBlank(parentUlid)) {
					rootNodes.add(dto.getUlid());
				} else {
					parentChildMap.computeIfAbsent(parentUlid, k -> new ArrayList<>()).add(dto.getUlid());
				}
			}
		}
	}

	/**
	 * 通用的树结构校验方法
	 *
	 * @param treeDTOS       树结构数据列表
	 * @param maxDepth       最大嵌套深度，如果为null则不进行深度校验
	 * @param fieldExtractor 用于提取需要在同一层级下唯一的字段的函数
	 * @param fieldName      提取字段的名称，用于错误信息中
	 * @return 校验是否通过
	 * @throws ConstraintViolationException 校验失败时抛出异常
	 */
	public static <T extends TreeDTO> boolean check(List<T> treeDTOS,
													@Nullable Integer maxDepth,
													@Nullable Function<T, Object> fieldExtractor,
													@Nullable String fieldName) {
		if (treeDTOS == null || treeDTOS.isEmpty()) {
			throw new ConstraintViolationException("树结构数据列表不能为空", Collections.emptySet());
		}

		// 创建验证上下文，包含公共计算结果
		ValidationContext<T> context = new ValidationContext<>(treeDTOS);

		// 1. 验证ulid必传且不重复
		validateUlidRequiredAndUnique(context);

		// 2. 验证parentUlid引用关系
		validateParentUlidReferences(context);

		// 3. 验证sortOrder从0开始且连续不重复
		validateSortOrder(context);

		// 4. 如果指定了最大深度，则进行深度校验
		if (maxDepth != null) {
			validateMaxNestingDepth(context, maxDepth);
		}

		// 5. 验证同一层级下某种属性不能相同
		if (fieldExtractor != null && StringUtils.isNotBlank(fieldName)) {
			validateFieldUniqueInSameLevel(context, fieldExtractor, fieldName);
		}

		return true;
	}

	/**
	 * 验证ulid必传且不重复
	 */
	private static <T extends TreeDTO> void validateUlidRequiredAndUnique(ValidationContext<T> context) {
		Set<String> ulidSet = new HashSet<>();

		for (T dto : context.list) {
			// ulid必传验证
			if (StringUtils.isBlank(dto.getUlid())) {
				throw new ConstraintViolationException("ulid不能为空", Collections.emptySet());
			}

			// ulid不重复验证
			if (!ulidSet.add(dto.getUlid())) {
				throw new ConstraintViolationException("ulid不能重复：" + dto.getUlid(), Collections.emptySet());
			}
		}
	}

	/**
	 * 验证parentUlid引用关系
	 */
	private static <T extends TreeDTO> void validateParentUlidReferences(ValidationContext<T> context) {
		for (T dto : context.list) {
			String parentUlid = dto.getParentUlid();

			// parentUlid可以为空（表示顶层字段）
			if (StringUtils.isNotBlank(parentUlid)) {
				// 如果parentUlid不为空，则必须存在于当前列表中
				if (!context.allUlids.contains(parentUlid)) {
					throw new ConstraintViolationException(
							"parentUlid引用不存在：" + parentUlid + "（ulid：" + dto.getUlid() + "）",
							Collections.emptySet()
					);
				}

				// 不能引用自己
				if (parentUlid.equals(dto.getUlid())) {
					throw new ConstraintViolationException(
							"parentUlid不能引用自己：" + dto.getUlid(),
							Collections.emptySet()
					);
				}
			}
		}
	}

	/**
	 * 验证sortOrder从0开始且连续不重复
	 */
	private static <T extends TreeDTO> void validateSortOrder(ValidationContext<T> context) {
		for (Map.Entry<String, ? extends List<T>> entry : context.groupedByParent.entrySet()) {
			String parentKey = entry.getKey();
			List<T> sameLevelItems = entry.getValue();

			// 按sortOrder排序
			sameLevelItems.sort(Comparator.comparing(TreeDTO::getSortOrder));

			// 验证sortOrder从0开始且连续
			for (int i = 0; i < sameLevelItems.size(); i++) {
				T dto = sameLevelItems.get(i);
				Integer sortOrder = dto.getSortOrder();

				if (sortOrder == null) {
					throw new ConstraintViolationException(
							"sortOrder不能为空（ulid：" + dto.getUlid() + "）",
							Collections.emptySet()
					);
				}

				if (sortOrder != i) {
					throw new ConstraintViolationException(
							"sortOrder必须从0开始且连续，期望：" + i + "，实际：" + sortOrder +
									"（ulid：" + dto.getUlid() + "，parent：" + parentKey + "）",
							Collections.emptySet()
					);
				}
			}
		}
	}

	/**
	 * 验证最大嵌套层级
	 */
	private static <T extends TreeDTO> void validateMaxNestingDepth(ValidationContext<T> context, int maxDepth) {
		// 从每个根节点开始计算最大深度
		for (String rootNode : context.rootNodes) {
			int depth = calculateMaxDepth(rootNode, context.parentChildMap, 1);
			if (depth > maxDepth) {
				throw new ConstraintViolationException(
						"嵌套层级超过最大限制，当前最大深度：" + depth + "，允许的最大深度：" + maxDepth +
								"（根节点：" + rootNode + "）",
						Collections.emptySet()
				);
			}
		}
	}

	/**
	 * 验证同一层级下指定字段不能相同
	 */
	private static <T extends TreeDTO> void validateFieldUniqueInSameLevel(ValidationContext<T> context,
																		   Function<T, Object> fieldExtractor,
																		   String fieldName) {
		for (Map.Entry<String, List<T>> entry : context.groupedByParent.entrySet()) {
			String parentKey = entry.getKey();
			List<T> sameLevelItems = entry.getValue();

			// 检查同一层级下的指定字段是否重复
			Set<Object> fieldValueSet = new HashSet<>();
			for (T dto : sameLevelItems) {
				Object fieldValue = fieldExtractor.apply(dto);
				if (fieldValue != null) {
					if (!fieldValueSet.add(fieldValue)) {
						throw new ConstraintViolationException(
								"同一层级下" + fieldName + "不能重复：" + fieldValue +
										"（ulid：" + dto.getUlid() + "，parent：" + parentKey + "）",
								Collections.emptySet()
						);
					}
				}
			}
		}
	}

	/**
	 * 递归计算从指定节点开始的最大深度
	 */
	private static int calculateMaxDepth(String nodeUlid, Map<String, List<String>> parentChildMap, int currentDepth) {
		List<String> children = parentChildMap.get(nodeUlid);
		if (children == null || children.isEmpty()) {
			return currentDepth;
		}

		int maxChildDepth = currentDepth;
		for (String child : children) {
			int childDepth = calculateMaxDepth(child, parentChildMap, currentDepth + 1);
			maxChildDepth = Math.max(maxChildDepth, childDepth);
		}

		return maxChildDepth;
	}

}
