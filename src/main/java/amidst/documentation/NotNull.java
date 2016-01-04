package amidst.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is added to methods that will never return null.
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface NotNull {
}
