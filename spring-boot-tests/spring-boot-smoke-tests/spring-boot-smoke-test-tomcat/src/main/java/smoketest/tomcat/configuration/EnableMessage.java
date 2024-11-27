package smoketest.tomcat.configuration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({MessageSelector.class})
public @interface EnableMessage {
}
