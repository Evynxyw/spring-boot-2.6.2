package smoketest.tomcat.listener.events;

import org.springframework.context.ApplicationEvent;

public class RegisterUserEvent extends ApplicationEvent {

	public RegisterUserEvent(String message) {
		super(message);
	}
}
