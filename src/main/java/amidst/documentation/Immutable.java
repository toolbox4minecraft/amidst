package amidst.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that instance of this type cannot be modified after
 * instantiation. This also implies that the type is thread safe.
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(ElementType.TYPE)
public @interface Immutable {
}
