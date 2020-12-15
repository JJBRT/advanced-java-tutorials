package com.useraccount.services.impl;

import org.di.framework.annotations.Component;

import com.useraccount.services.AccountService;

@Component
public class AccountServiceImpl implements AccountService {

	@Override
	public Long getAccountNumber(String userName) {
		return 12345689L;
	}
}
