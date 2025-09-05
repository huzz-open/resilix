package top.huzz.jaksho.bizfun;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import top.huzz.jaksho.api.dto.ObjectBizFieldTypeRefDTO;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TreeDTOUtil 测试类
 * 
 * @author huzz
 * @since 1.0.2
 */
public class TreeDTOUtilTest {

	@Test
	public void testCheck_ValidData_ShouldPass() {
		// 准备测试数据
		ObjectBizFieldTypeRefDTO parent = new ObjectBizFieldTypeRefDTO();
		parent.setUlid("01234567890123456789012345678901");
		parent.setParentUlid(null);
		parent.setSortOrder(0);
		parent.setBizFieldDomainId(1);

		ObjectBizFieldTypeRefDTO child = new ObjectBizFieldTypeRefDTO();
		child.setUlid("01234567890123456789012345678902");
		child.setParentUlid("01234567890123456789012345678901");
		child.setSortOrder(0);
		child.setBizFieldDomainId(2);

		List<ObjectBizFieldTypeRefDTO> validList = Arrays.asList(parent, child);

		// 执行测试
		boolean result = TreeDTOUtil.check(validList, null, null, null);

		// 验证结果
		assertTrue(result);
	}

	@Test
	public void testCheck_WithMaxDepth_ShouldPass() {
		// 准备测试数据
		ObjectBizFieldTypeRefDTO parent = new ObjectBizFieldTypeRefDTO();
		parent.setUlid("01234567890123456789012345678901");
		parent.setParentUlid(null);
		parent.setSortOrder(0);
		parent.setBizFieldDomainId(1);

		ObjectBizFieldTypeRefDTO child = new ObjectBizFieldTypeRefDTO();
		child.setUlid("01234567890123456789012345678902");
		child.setParentUlid("01234567890123456789012345678901");
		child.setSortOrder(0);
		child.setBizFieldDomainId(2);

		List<ObjectBizFieldTypeRefDTO> validList = Arrays.asList(parent, child);

		// 执行测试（最大深度为3）
		boolean result = TreeDTOUtil.check(validList, 3, null, null);

		// 验证结果
		assertTrue(result);
	}

	@Test
	public void testCheck_WithFieldUnique_ShouldPass() {
		// 准备测试数据
		ObjectBizFieldTypeRefDTO parent = new ObjectBizFieldTypeRefDTO();
		parent.setUlid("01234567890123456789012345678901");
		parent.setParentUlid(null);
		parent.setSortOrder(0);
		parent.setBizFieldDomainId(1);

		ObjectBizFieldTypeRefDTO child = new ObjectBizFieldTypeRefDTO();
		child.setUlid("01234567890123456789012345678902");
		child.setParentUlid("01234567890123456789012345678901");
		child.setSortOrder(0);
		child.setBizFieldDomainId(2);

		List<ObjectBizFieldTypeRefDTO> validList = Arrays.asList(parent, child);

		// 执行测试（验证bizFieldDomainId在同一层级下唯一）
		boolean result = TreeDTOUtil.check(validList, null, ObjectBizFieldTypeRefDTO::getBizFieldDomainId, "bizFieldDomainId");

		// 验证结果
		assertTrue(result);
	}

	@Test
	public void testCheck_EmptyList_ShouldThrowException() {
		// 测试空列表
		assertThrows(ConstraintViolationException.class, () -> {
			TreeDTOUtil.check(Collections.emptyList(), null, null, null);
		});
	}

	@Test
	public void testCheck_NullList_ShouldThrowException() {
		// 测试null列表
		assertThrows(ConstraintViolationException.class, () -> {
			TreeDTOUtil.check(null, null, null, null);
		});
	}

	@Test
	public void testCheck_DuplicateUlid_ShouldThrowException() {
		// 准备重复ulid的测试数据
		ObjectBizFieldTypeRefDTO dto1 = new ObjectBizFieldTypeRefDTO();
		dto1.setUlid("01234567890123456789012345678901");
		dto1.setParentUlid(null);
		dto1.setSortOrder(0);
		dto1.setBizFieldDomainId(1);

		ObjectBizFieldTypeRefDTO dto2 = new ObjectBizFieldTypeRefDTO();
		dto2.setUlid("01234567890123456789012345678901"); // 重复的ulid
		dto2.setParentUlid(null);
		dto2.setSortOrder(1);
		dto2.setBizFieldDomainId(2);

		List<ObjectBizFieldTypeRefDTO> duplicateUlidList = Arrays.asList(dto1, dto2);

		// 执行测试并验证异常
		ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
			TreeDTOUtil.check(duplicateUlidList, null, null, null);
		});

		assertTrue(exception.getMessage().contains("ulid不能重复"));
	}

	@Test
	public void testCheck_ExceedMaxDepth_ShouldThrowException() {
		// 准备超过最大嵌套深度的测试数据
		ObjectBizFieldTypeRefDTO root = new ObjectBizFieldTypeRefDTO();
		root.setUlid("01234567890123456789012345678901");
		root.setParentUlid(null);
		root.setSortOrder(0);
		root.setBizFieldDomainId(1);

		ObjectBizFieldTypeRefDTO child1 = new ObjectBizFieldTypeRefDTO();
		child1.setUlid("01234567890123456789012345678902");
		child1.setParentUlid("01234567890123456789012345678901");
		child1.setSortOrder(0);
		child1.setBizFieldDomainId(2);

		ObjectBizFieldTypeRefDTO child2 = new ObjectBizFieldTypeRefDTO();
		child2.setUlid("01234567890123456789012345678903");
		child2.setParentUlid("01234567890123456789012345678902");
		child2.setSortOrder(0);
		child2.setBizFieldDomainId(3);

		List<ObjectBizFieldTypeRefDTO> exceedDepthList = Arrays.asList(root, child1, child2);

		// 执行测试并验证异常（最大深度为2）
		ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
			TreeDTOUtil.check(exceedDepthList, 2, null, null);
		});

		assertTrue(exception.getMessage().contains("嵌套层级超过最大限制"));
		assertTrue(exception.getMessage().contains("当前最大深度：3"));
		assertTrue(exception.getMessage().contains("允许的最大深度：2"));
	}

	@Test
	public void testCheck_DuplicateFieldInSameLevel_ShouldThrowException() {
		// 准备同一层级下重复bizFieldDomainId的测试数据
		ObjectBizFieldTypeRefDTO parent1 = new ObjectBizFieldTypeRefDTO();
		parent1.setUlid("01234567890123456789012345678901");
		parent1.setParentUlid(null);
		parent1.setSortOrder(0);
		parent1.setBizFieldDomainId(1);

		ObjectBizFieldTypeRefDTO parent2 = new ObjectBizFieldTypeRefDTO();
		parent2.setUlid("01234567890123456789012345678902");
		parent2.setParentUlid(null);
		parent2.setSortOrder(1);
		parent2.setBizFieldDomainId(1); // 与parent1相同的bizFieldDomainId

		List<ObjectBizFieldTypeRefDTO> duplicateFieldList = Arrays.asList(parent1, parent2);

		// 执行测试并验证异常
		ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
			TreeDTOUtil.check(duplicateFieldList, null, ObjectBizFieldTypeRefDTO::getBizFieldDomainId, "bizFieldDomainId");
		});

		assertTrue(exception.getMessage().contains("同一层级下bizFieldDomainId不能重复：1"));
	}

	@Test
	public void testCheck_AllValidations_ShouldPass() {
		// 准备完整的测试数据
		ObjectBizFieldTypeRefDTO parent = new ObjectBizFieldTypeRefDTO();
		parent.setUlid("01234567890123456789012345678901");
		parent.setParentUlid(null);
		parent.setSortOrder(0);
		parent.setBizFieldDomainId(1);

		ObjectBizFieldTypeRefDTO child = new ObjectBizFieldTypeRefDTO();
		child.setUlid("01234567890123456789012345678902");
		child.setParentUlid("01234567890123456789012345678901");
		child.setSortOrder(0);
		child.setBizFieldDomainId(2);

		List<ObjectBizFieldTypeRefDTO> validList = Arrays.asList(parent, child);

		// 执行测试（包含所有验证）
		boolean result = TreeDTOUtil.check(validList, 3, ObjectBizFieldTypeRefDTO::getBizFieldDomainId, "bizFieldDomainId");

		// 验证结果
		assertTrue(result);
	}
}
