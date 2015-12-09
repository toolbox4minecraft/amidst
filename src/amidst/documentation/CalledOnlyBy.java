package amidst.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is similar to the annotation {@link CalledBy}, but it also
 * states that the given thread is the only thread that will ever call this
 * constructor of method.
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD })
public @interface CalledOnlyBy {
	AmidstThread value();
}
