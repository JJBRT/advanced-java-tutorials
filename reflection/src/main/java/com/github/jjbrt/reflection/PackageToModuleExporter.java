package com.github.jjbrt.reflection;

import static org.burningwave.core.assembler.StaticComponentContainer.Modules;
import static org.burningwave.core.assembler.StaticComponentContainer.Resources;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;


public class PackageToModuleExporter {
	
	public static void main(String[] args) {
        try {
			execute();
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
    }
	
	
    public static void execute() {
    	try {
    		Modules.exportPackageToAllUnnamed("java.base", "java.net");
    		Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
    		method.setAccessible(true);
    		ClassLoader classLoader = new URLClassLoader(new URL[] {}, null);
    		method.invoke(
    		    classLoader,
    		    Resources.getClassPath(AllModulesToAllModulesExporter.class).getURL()
    		);
    		Class<?> fieldsHandlerClass = classLoader.loadClass(FieldsHandler.class.getName());
    		if (FieldsHandler.class != fieldsHandlerClass) {
    		    System.out.println(
    		        FieldsHandler.class.toString() + " and " + fieldsHandlerClass.toString() + 
    		        " are loaded from the same bytecode but they are different instances:" +
    		        "\n\t" + FieldsHandler.class + " is loaded by " + FieldsHandler.class.getClassLoader() +
    		        "\n\t" + fieldsHandlerClass + " is loaded by " + fieldsHandlerClass.getClassLoader()
    		    );
    		}
    	} catch (Exception exc) {
    		exc.printStackTrace();
    	}
    }
	
	
}
