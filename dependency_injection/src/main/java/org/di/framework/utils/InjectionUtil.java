package org.di.framework.utils;

import static org.burningwave.core.assembler.StaticComponentContainer.Fields;

import java.lang.reflect.Field;
import java.util.Collection;

import org.burningwave.core.classes.FieldCriteria;
import org.di.framework.Injector;
import org.di.framework.annotations.Autowired;
import org.di.framework.annotations.Qualifier;

public class InjectionUtil {

	private InjectionUtil() {
		super();
	}

	/**
	 * Perform injection recursively, for each service inside the Client class
	 */
	public static void autowire(Injector injector, Class<?> classz, Object classInstance)
			throws InstantiationException, IllegalAccessException {
		Collection<Field> fields = Fields.findAllAndMakeThemAccessible(
			FieldCriteria.forEntireClassHierarchy().allThoseThatMatch(field ->
				field.isAnnotationPresent(Autowired.class)
			), 
			classz
		);
		for (Field field : fields) {
			String qualifier = field.isAnnotationPresent(Qualifier.class)
					? field.getAnnotation(Qualifier.class).value()
					: null;
			Object fieldInstance = injector.getBeanInstance(field.getType(), field.getName(), qualifier);
			field.set(classInstance, fieldInstance);
			autowire(injector, fieldInstance.getClass(), fieldInstance);
		}
	}

}
