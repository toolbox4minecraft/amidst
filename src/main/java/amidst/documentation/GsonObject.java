package amidst.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is added to classes which can be (de)serialised with GSON.
 * These classes must have a no-argument constructor, which will be used by GSON
 * to construct instances for deserialisation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
public @interface GsonObject {
	/**
	 * Whether or not to ignore unknown fields when deserialising an instance.
	 */
	public boolean ignoreUnknown() default false;
}
