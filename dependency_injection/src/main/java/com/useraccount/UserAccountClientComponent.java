package com.useraccount;

import org.di.framework.annotations.Autowired;
import org.di.framework.annotations.Component;
import org.di.framework.annotations.Qualifier;

import com.useraccount.services.AccountService;
import com.useraccount.services.UserService;

/**
 * Client class, havin userService and accountService expected to initialized by
 * CustomInjector.java
 */
@Component
public class UserAccountClientComponent {

	@Autowired
	private UserService userService;

	@Autowired
	@Qualifier(value = "accountServiceImpl")
	private AccountService accountService;

	public void displayUserAccount() {
		String username = userService.getUserName();
		Long accountNumber = accountService.getAccountNumber(username);
		System.out.println("\n\tUser Name: " + username + "\n\tAccount Number: " + accountNumber + "\n");
	}
}
