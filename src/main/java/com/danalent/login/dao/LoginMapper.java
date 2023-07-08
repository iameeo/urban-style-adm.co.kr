package com.danalent.login.dao;

import java.util.HashMap;

import org.springframework.stereotype.Repository;

@Repository("loginMapper")
public abstract interface LoginMapper {

	// User Detail
	public LoginVo selectUserDetail(HashMap<String, String> paramMap);
}