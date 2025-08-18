package top.huzz.jaksho.api.helper;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import top.huzz.jaksho.common.able.Saver;
import top.huzz.jaksho.common.able.SaverBuilder;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author huzz
 * @since 1.0.2
 */
@Slf4j
@Configuration
public class SaveHelper implements ApplicationContextAware {

	private static SaverBuilder<Object, Integer> saverBuilder;

	/**
	 * 保存主对象及其级联对象
	 *
	 * @param mainObject      主对象
	 * @param cascadedObjects 级联对象列表
	 * @return 主对象的ID
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static int save(@Nonnull Object mainObject, List<?> cascadedObjects, BiConsumer<?, Integer> cascadedCreatedObjectIdSetter) {
		Objects.requireNonNull(mainObject);
		// 获取主对象的Saver
		Saver<Object, Integer> mainSaver = getSaver(mainObject);
		// 保存主对象
		int id = mainSaver.save(mainObject);

		// 如果有级联对象，则保存它们
		if (CollectionUtils.isNotEmpty(cascadedObjects)) {
			for (Object cascadedObject : cascadedObjects) {
				((BiConsumer) cascadedCreatedObjectIdSetter).accept(cascadedObject, id);
				Saver<Object, Integer> saver = getSaver(cascadedObject);
				saver.save(cascadedObject);
			}
		}

		return id;
	}

	@Nonnull
	protected static Saver<Object, Integer> getSaver(Object toBeSaved) {
		Objects.requireNonNull(toBeSaved);
		Saver<Object, Integer> saver = saverBuilder.getSaver(toBeSaved);
		Objects.requireNonNull(saver, "Saver cannot be null for object: " + toBeSaved.getClass().getName());
		log.info("Using saver: {} for object: {}", saver.getClass().getName(), toBeSaved.getClass().getName());
		return saver;
	}


	@Override
	@SuppressWarnings("unchecked")
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		saverBuilder = applicationContext.getBean(SaverBuilder.class);
	}
}
