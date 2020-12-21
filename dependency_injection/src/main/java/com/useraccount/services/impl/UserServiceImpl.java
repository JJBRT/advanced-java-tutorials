package com.useraccount.services.impl;

import org.di.framework.annotations.Component;

import com.useraccount.services.UserService;

@Component
public class UserServiceImpl implements UserService {

	@Override
	public String getUserName() {
		return "username";
	}
}
