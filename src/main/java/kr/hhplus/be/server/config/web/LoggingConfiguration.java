package kr.hhplus.be.server.config.web;

import kr.hhplus.be.server.interfaces.filter.LoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfiguration {

	@Bean
	public LoggingFilter requestAndResponseLoggingFilter() {
		return new LoggingFilter();
	}
}