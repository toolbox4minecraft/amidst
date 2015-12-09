package amidst.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that the constructor or method it is attached to
 * can be called by the given thread at any time. However, it does not state
 * that it is the only calling thread.
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD })
@Repeatable(CalledByList.class)
public @interface CalledBy {
	AmidstThread value();
}
