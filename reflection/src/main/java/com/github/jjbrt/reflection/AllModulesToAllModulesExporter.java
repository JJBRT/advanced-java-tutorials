package com.github.jjbrt.reflection;


import static org.burningwave.core.assembler.StaticComponentContainer.Modules;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


@SuppressWarnings("unchecked")
public class AllModulesToAllModulesExporter {
	
	public static void main(String[] args) {
        try {
			execute();
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
    }
	
	
    public static void execute() {
    	try {
    		Modules.exportAllToAll();
    		Class<?> bootClassLoaderClass = Class.forName("jdk.internal.loader.ClassLoaders$BootClassLoader");
			Constructor<? extends ClassLoader> constructor = 
    			(Constructor<? extends ClassLoader>)
    				Class.forName("jdk.internal.loader.ClassLoaders$PlatformClassLoader")
    					.getDeclaredConstructor(bootClassLoaderClass);
    		constructor.setAccessible(true);
    		Class<?> classLoadersClass = Class.forName("jdk.internal.loader.ClassLoaders");
    		Method bootClassLoaderRetriever = classLoadersClass.getDeclaredMethod("bootLoader");
    		bootClassLoaderRetriever.setAccessible(true);
    		ClassLoader newBuiltinclassLoader = constructor.newInstance(bootClassLoaderRetriever.invoke(classLoadersClass));
    		System.out.println(newBuiltinclassLoader + " instantiated");
    	} catch (Exception exc) {
    		exc.printStackTrace();
    	}
    }
	
	
}
