package smoketest.tomcat.listener;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplicationRunListener;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Duration;

public class MyApplicationRunListener implements SpringApplicationRunListener {

	public MyApplicationRunListener(SpringApplication application, String[] args) {
		// 初始化逻辑
	}

	@Override
	public void starting(ConfigurableBootstrapContext bootstrapContext) {
		System.out.println("Application is starting");
	}

	@Override
	public void started(ConfigurableApplicationContext context, Duration timeTaken) {
		SpringApplicationRunListener.super.started(context, timeTaken);
	}

	@Override
	public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
		System.out.println("Environment is prepared");
	}

	@Override
	public void contextPrepared(ConfigurableApplicationContext context) {
		System.out.println("Context is prepared");
	}

	@Override
	public void contextLoaded(ConfigurableApplicationContext context) {
		System.out.println("Context is loaded");
	}

	@Override
	public void failed(ConfigurableApplicationContext context, Throwable exception) {
		System.out.println("Application failed to start");
	}
}
