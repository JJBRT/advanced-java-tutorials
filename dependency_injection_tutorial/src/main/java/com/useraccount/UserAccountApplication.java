package com.useraccount;

import org.di.framework.Injector;

public class UserAccountApplication {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		Injector.startApplication(UserAccountApplication.class);
		Injector.getService(UserAccountClientComponent.class).displayUserAccount();
		long endime = System.currentTimeMillis();
	}
}
