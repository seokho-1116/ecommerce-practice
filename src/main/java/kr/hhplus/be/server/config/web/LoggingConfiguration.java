package kr.hhplus.be.server.config.web;

import kr.hhplus.be.server.interfaces.filter.RequestAndResponseLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfiguration {

	@Bean
	public RequestAndResponseLoggingFilter requestAndResponseLoggingFilter() {
		return new RequestAndResponseLoggingFilter();
	}
}