package com.danalent.login;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
	private static final long serialVersionUID = -7413498804959401803L;
	private String username;
	private String password;
	private String authmethod;
	private String cpcode;
	private String cpnameorg;
	private String cuidx;
	private String cuname;
	private Boolean isMaster;

	public CustomUserDetails(String userName, String password, String authmethod, String cpcode, String cpnameorg, String cuidx, String cuname) {
		this.username = userName;
		this.password = password;
		this.authmethod = authmethod; 							// M = MASTER, S = BASIC USER
		this.cpcode = cpcode;
		this.cpnameorg = cpnameorg;
		this.cuidx = cuidx;
		this.cuname = cuname;
		this.isMaster = authmethod.equals("M") ? true : false;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		return authorities;
	}

	public Boolean getIsMaster() {
		return isMaster;
	}

	public void setIsMaster(Boolean isMaster) {
		this.isMaster = isMaster;
	}

	public String getAuthmethod() {
		return authmethod;
	}

	public void setAuthmethod(String authmethod) {
		this.authmethod = authmethod;
	}

	public String getCpcode() {
		return cpcode;
	}

	public void setCpcode(String cpcode) {
		this.cpcode = cpcode;
	}

	public String getCpnameorg() {
		return cpnameorg;
	}

	public void setCpnameorg(String cpnameorg) {
		this.cpnameorg = cpnameorg;
	}

	public String getCuidx() {
		return cuidx;
	}

	public void setCuidx(String cuidx) {
		this.cuidx = cuidx;
	}

	public String getCuname() {
		return cuname;
	}

	public void setCuname(String cuname) {
		this.cuname = cuname;
	}

	public String getPassword() {
		return this.password;
	}

	public String getUsername() {
		return this.username;
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}
}