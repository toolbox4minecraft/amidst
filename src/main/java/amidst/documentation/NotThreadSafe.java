package amidst.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that the type it is attached was not designed with
 * thread safety in mind. Thus, it should be used from only a single thread or
 * if it is used in a multi-threaded environment, the using class has to ensure
 * thread safety.
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(ElementType.TYPE)
public @interface NotThreadSafe {
}
