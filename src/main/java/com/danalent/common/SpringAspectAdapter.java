package com.danalent.common;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.util.UrlPathHelper;

import com.danalent.login.CustomUserDetails;

@Aspect
public class SpringAspectAdapter {
	protected Log log = LogFactory.getLog(SpringAspectAdapter.class);

	@Pointcut("execution(* com.danalent.*.web.*Controller.*(..))")
	private void pointCut() {}

	@Around(value = "pointCut()")
	public Object logPrint(ProceedingJoinPoint joinPoint) throws Throwable {
		Object retValue = joinPoint.proceed();

		if ((retValue != null) && (retValue.toString().indexOf("redirect:") < 0 && retValue.toString().indexOf("/error/") < 0)) {
			UrlPathHelper urlPathHelper = new UrlPathHelper();
			HttpServletRequest req = null;
			Model model = null;
			Map<String, Object> requestMap = null;

			for (Object obj : joinPoint.getArgs()) {
				if (obj instanceof HttpServletRequest) {
					req = (HttpServletRequest) obj;
				}
				if (obj instanceof Map && !(obj instanceof ModelMap)) {
					requestMap = (Map<String, Object>) obj;
				}
				if (obj instanceof Model) {
					model = (Model) obj;
				}
			}
			
			//공통 제목
			String pageTitle = "URBAN-STYLE SHOP CMS";
			model.addAttribute("pageTitle", pageTitle);
			
			if (requestMap != null) {
				/*
				 * HttpSession session = req.getSession(); CustomUserDetails
				 * userDetails = (CustomUserDetails)
				 * session.getAttribute("userLoginInfo");
				 * requestMap.put("userDetails", userDetails);
				 */
				
				String requestURL = urlPathHelper.getOriginatingRequestUri(req); // req.getRequestURI();
				String requestGnb = requestURL.substring(requestURL.indexOf("/", 0) + 1, requestURL.indexOf("/", 1));
				String requestSubGnb = requestURL.substring(requestURL.indexOf("/", 1) + 1, requestURL.length());

				model.addAttribute("menuList", MenuConfig.menuList(requestGnb, requestSubGnb));
				model.addAttribute("requestGnb", requestGnb);
			}
		}
		return retValue;
	}
}