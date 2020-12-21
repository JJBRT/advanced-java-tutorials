package org.di.framework.annotations;

import java.lang.annotation.*;

/**
 *  Service field variables should use this annotation
 *  This annotation Can be used to avoid conflict if there are multiple implementations of the same interface
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Qualifier {
	String value() default "";
}