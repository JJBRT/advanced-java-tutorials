package com.github.jjbrt.reflection;

import static org.burningwave.core.assembler.StaticComponentContainer.Modules;
import static org.burningwave.core.assembler.StaticComponentContainer.Resources;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ModulesExporter {
	
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
    	    Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
    	    method.setAccessible(true);
    	    method.invoke(
    	    	new URLClassLoader(new URL[] {}),
    	    	Resources.getClassPath(ModulesExporter.class).getURL()
    	    );
    	} catch (Exception exc) {
    		exc.printStackTrace();
    	}
    }
	
	
}
