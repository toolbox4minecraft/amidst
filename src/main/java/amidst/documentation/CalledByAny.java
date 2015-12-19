package amidst.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is similar to the annotation {@link CalledBy}. However, it
 * specifies that not assumption was made that only a specific thread calls this
 * constructor or method.
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD })
public @interface CalledByAny {
}
