package com.github.jjbrt.reflection;

import static org.burningwave.core.assembler.StaticComponentContainer.Resources;
import static org.burningwave.core.assembler.StaticComponentContainer.Methods;
import org.burningwave.core.io.FileSystemItem;

public class MethodHandleInvoker {
    
	
    public static void main(String[] args) {
        try {
			execute();
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
    }
	
	
    public static void execute() {
    	//loading the byte code
    	FileSystemItem currentPath = Resources.get(MethodHandleInvoker.class).getParent();
    	FileSystemItem fieldsHandlerClassFile = currentPath.findFirstInChildren(
    		FileSystemItem.Criteria.forAllFileThat(
    			fileSystemItem -> fileSystemItem.getName().equals("FieldsHandler.class")
    		)
    	);
    	
    	byte[] byteCode = fieldsHandlerClassFile.toByteArray();
    	
    	//Calling defineClass method
    	Class<?> fieldsHandlerClass = Methods.invokeDirect(
    	    Thread.currentThread().getContextClassLoader(),
    	    "defineClass",
    	    "com.github.jjbrt.reflection.FieldsHandler",
    	    byteCode,
    	    0,
    	    byteCode.length,
    	    null
    	);
    	
    	//Calling execute method of com.github.jjbrt.reflection.FieldsHandler class
    	Methods.invokeStatic(fieldsHandlerClass, "execute");
    }
    
}
