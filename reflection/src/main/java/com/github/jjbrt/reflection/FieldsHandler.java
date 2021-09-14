package com.github.jjbrt.reflection;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;


public class FieldsHandler {
    
	
    public static void main(String[] args) {
        try {
			execute();
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
    }
	
	
    public static void execute() {
    	org.burningwave.core.classes.Fields fields =
    		    org.burningwave.core.assembler.StaticComponentContainer.Fields;

		ClassLoader classLoader =
		    Thread.currentThread().getContextClassLoader();

		//Fast access by memory address
		Collection<Class<?>> loadedClasses =
		    fields.getDirect(classLoader, "classes");
		
		loadedClasses.forEach((cls) -> {
			System.out.println(cls.getName());
		});
		
		//Access by Reflection
		loadedClasses = fields.get(classLoader, "classes");

		//Get all field values of an object 
		//through memory address access
		Map<Field, ?> values =
		    fields.getAllDirect(classLoader);

		//Get all field values of an object
		//through reflection access
		values = fields.getAll(classLoader);
		
		values.forEach((name, value) -> {
			System.out.println(name + " =\n\t\t " + value);
		});
    }
   
}
