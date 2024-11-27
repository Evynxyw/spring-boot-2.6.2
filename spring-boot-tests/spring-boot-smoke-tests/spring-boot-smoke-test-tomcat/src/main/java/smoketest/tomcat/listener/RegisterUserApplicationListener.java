package smoketest.tomcat.listener;

import org.springframework.context.ApplicationListener;
import smoketest.tomcat.listener.events.RegisterUserEvent;

public class RegisterUserApplicationListener implements ApplicationListener<RegisterUserEvent> {
	@Override
	public void onApplicationEvent(RegisterUserEvent event) {
		System.out.println("register user: " + event.getSource());
	}
}
