package amidst.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is added to no argument-constructors that are used by gson to
 * deserialize object instances.
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(ElementType.CONSTRUCTOR)
public @interface GsonConstructor {
}
