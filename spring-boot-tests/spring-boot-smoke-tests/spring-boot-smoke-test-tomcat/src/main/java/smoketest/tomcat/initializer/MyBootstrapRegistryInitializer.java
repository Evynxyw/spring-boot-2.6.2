package smoketest.tomcat.initializer;

import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.BootstrapRegistryInitializer;
import org.springframework.core.annotation.Order;

public class MyBootstrapRegistryInitializer implements BootstrapRegistryInitializer {

	@Override
	public void initialize(BootstrapRegistry registry) {
		System.out.println("Bootstrap context initialized");
		registry.register(String.class, ctx -> "Hello, Bootstrap Context!");
	}
}