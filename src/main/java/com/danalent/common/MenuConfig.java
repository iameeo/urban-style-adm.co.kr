package com.danalent.common;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuConfig {
	private static final Logger logger = LoggerFactory.getLogger(MenuConfig.class);

	public static StringBuilder menuList(String requestGnb, String requestSubGnb) {
		logger.info("MenuConfig");
		
		List<String> nav = new ArrayList<String>();
		
		nav.add("index.do");
		nav.add("shopList.do");
		nav.add("rename.do");
		nav.add("retitle.do");
		nav.add("soldout.do");
		
		StringBuilder sb = new StringBuilder();

		// 1Depth Start
		sb.append("<nav>");
		sb.append("<ul>");	
		
		for(int i=0; i < nav.size(); i++){
			
			String currentPageCheck = "";
			if(requestSubGnb.equals(nav.get(i))){
				currentPageCheck = "on";
			}
			
			sb.append("<li class='"+ currentPageCheck +"'>");
			sb.append("<a href='/dashboard/"+ nav.get(i) +"'>"+ nav.get(i).split("\\.")[0].toUpperCase() +"</a>");
			sb.append("</li>");
		}

		sb.append("<li>");
		sb.append("<a href='/login/logout.do'>LOGOUT</a>");
		sb.append("</li>");
		sb.append("</ul>");
		sb.append("</nav>");
		
		return sb;
	}
}