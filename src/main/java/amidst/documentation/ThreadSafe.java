package amidst.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that the type it is attached to is thread-safe.
 * However, objects of this type might have a modifiable state. For unmodifiable
 * types, use the {@link Immutable} annotation. You can read this annotation as
 * if {@link CalledByAny} was used on all constructors and methods of this type.
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(ElementType.TYPE)
public @interface ThreadSafe {
}
