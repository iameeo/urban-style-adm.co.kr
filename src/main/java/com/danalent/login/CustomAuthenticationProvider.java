package com.danalent.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.danalent.common.SHA256;
import com.danalent.login.dao.LoginDao;
import com.danalent.login.dao.LoginVo;

public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Resource(name = "loginDao")
	private LoginDao loginDao;

	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String user_id = (String) authentication.getPrincipal();
		String user_pw = (String) authentication.getCredentials();

		List<SimpleGrantedAuthority> roles = new ArrayList<SimpleGrantedAuthority>();
		roles.add(new SimpleGrantedAuthority("ROLE_USER"));

		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(user_id, user_pw, roles);

		if( user_id.equals("iameeo_admin") && user_pw.equals("wndnjsWkd2")){
			result.setDetails(new CustomUserDetails(user_id,user_pw,"1","2","3","4","관리자"));
		}else{
			result = null;
		}
		
		return result;
	}
	
	/*
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String user_id = (String) authentication.getPrincipal();
		String user_pw = (String) authentication.getCredentials();

		HashMap<String, String> authMap = new HashMap<String, String>();
		authMap.put("userid", user_id);
		authMap.put("userpw", SHA256.sha256_Convert(user_pw));
		LoginVo suggestDetail = this.loginDao.selectUserDetail(authMap);

		List<SimpleGrantedAuthority> roles = new ArrayList<SimpleGrantedAuthority>();
		roles.add(new SimpleGrantedAuthority("ROLE_USER"));

		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(user_id, user_pw, roles);
		
		if(suggestDetail == null){
			result = null;
		}else{
			result.setDetails(new CustomUserDetails(
					user_id,
					user_pw,
					suggestDetail.getCu_right_sub(),
					suggestDetail.getCp_code(),
					suggestDetail.getCp_name_org(),
					suggestDetail.getCu_idx(),
					suggestDetail.getCu_name()
			));
		}

		return result;
	}
	*/
}