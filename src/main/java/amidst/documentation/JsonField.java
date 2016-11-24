package amidst.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation gives information on the fields of a JSON class.
 * Used by amidst.util.StrictTypeAdapterFactory to enforce required fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.FIELD })
public @interface JsonField {
	
	/**
	 * Whether or not this field is optional
	 */
	public boolean optional() default false;
	
	/**
	 * Other fields which must be present when this field is present
	 */
	public String[] require() default {};
}
