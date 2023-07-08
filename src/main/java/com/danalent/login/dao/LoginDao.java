package com.danalent.login.dao;

import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

@Service("loginDao")
public class LoginDao {

	@Resource(name = "loginMapper")
	private LoginMapper loginMapper;

	// User Detail
	public LoginVo selectUserDetail(HashMap<String, String> paramMap) {
		return loginMapper.selectUserDetail(paramMap);
	};
}