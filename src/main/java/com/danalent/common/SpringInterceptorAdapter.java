package com.danalent.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SpringInterceptorAdapter extends HandlerInterceptorAdapter {
	protected Log log = LogFactory.getLog(SpringInterceptorAdapter.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//HttpSession session  =  request.getSession(false);
		String currentUrl = request.getRequestURI();
		String referer = request.getHeader("Referer");
		
		if (log.isDebugEnabled()) {
            log.debug("======================================          START         ======================================");
            log.debug(" Request URI \t:  " + currentUrl);
            log.debug(" Referer URI \t:  " + referer);
        }
        
//       	//로그인 페이지 예외 처리
//       	if(currentUrl.indexOf("/login/") < 0 && session != null){
//       		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
//       		session.setAttribute("userLoginInfo", userDetails);
//       	}
       	
        return super.preHandle(request, response, handler);
    }

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("======================================           END          ======================================\n");
		}
	}
}