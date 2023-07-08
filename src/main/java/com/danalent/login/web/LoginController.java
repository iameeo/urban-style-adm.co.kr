package com.danalent.login.web;

import com.danalent.login.CustomUserDetails;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	private final String page_name = "LOGIN";
	private final String modual_prefix = "login/";

	@RequestMapping("/login/index.do")
	public String login(HttpServletRequest request, HttpSession session, Locale locale, Model model) throws Exception {
		String retUrl = "";
		logger.info("Welcome login! {}", session.getId());

		//godoLogin(session);
		
		// 로그인 유무
		if (session.getAttribute("userLoginInfo") != null) {
			retUrl = "redirect:/dashboard/index.do";
		} else {
			retUrl = modual_prefix + "index";
			
			/* Page Title & Gnb */
			model.addAttribute("page_name", page_name);			
			
		}

		return retUrl;
	}
	
	public void godoLogin(HttpSession session) throws IOException{
		String url = "http://gdadmin.urban-style.co.kr/base/login_ps.php";
		//mode=login&returnUrl=&managerId=urbanstyle&managerPw=tlrmacl123
		Response LoginCookies = Jsoup.connect(url).data("mode", "login", "returnUrl", "", "managerId", "urbanstyle", "managerPw", "tlrmacL12!").method(Method.POST).timeout(100000).execute();
		
		session.setAttribute("godoLogin", LoginCookies.cookies());
	}

	@RequestMapping("/login/logout.do")
	public String logout(HttpServletRequest request, HttpSession session, Locale locale, Model model) {
		CustomUserDetails userDetails = (CustomUserDetails) session.getAttribute("userLoginInfo");

		try {
			logger.info("Welcome logout! {}, {}", session.getId(), userDetails.getUsername());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		session.invalidate();

		return "redirect:/login/index.do";
	}

	@RequestMapping("/login/login_success.do")
	public String login_success(HttpServletRequest request, HttpSession session, Locale locale, Model model) {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();

		logger.info("Welcome login_success! {}, {}", session.getId(), userDetails.getUsername() + "/" + userDetails.getPassword());
		session.setAttribute("userLoginInfo", userDetails);

		return modual_prefix + "login_success";
	}

	@RequestMapping("/login/login_duplicate.do")
	public String login_duplicate(HttpServletRequest request, Locale locale, Model model) {
		logger.info("Welcome login_duplicate!");

		return modual_prefix + "login_duplicate";
	}
}