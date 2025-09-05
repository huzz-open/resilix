package top.huzz.jaksho.api.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;

/**
 * @author huzz
 * @since 1.0.2
 */
@Configuration
public class AppConfigUtil {
	@Getter
	private static AppConfig appConfig;

	public AppConfigUtil(AppConfig appConfig) {
		AppConfigUtil.appConfig = appConfig;
	}
}
