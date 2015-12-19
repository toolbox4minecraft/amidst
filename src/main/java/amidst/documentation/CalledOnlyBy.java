package amidst.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that the constructor or method it is attached to
 * can ONLY be called by the given thread.
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD })
public @interface CalledOnlyBy {
	AmidstThread value();
}
