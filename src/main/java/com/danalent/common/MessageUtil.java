package com.danalent.common;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class MessageUtil {
	private static MessageSourceAccessor messageSourceAccessor = null;

	public void setMessageSourceAccessor(MessageSourceAccessor msAcc) {
		this.messageSourceAccessor = msAcc;
	}

	public static Locale getLocale() {

		Locale defaultLocale = Locale.ENGLISH;

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String headerLocale = request.getHeader("Accept-Language");

		if (StringUtils.isNotBlank(headerLocale)) {
			if (headerLocale.indexOf(Locale.JAPAN.getLanguage()) != -1) {
				defaultLocale = Locale.JAPAN;
			} else if (headerLocale.indexOf(Locale.KOREA.getLanguage()) != -1) {
				defaultLocale = Locale.KOREA;
			}
		}

		return defaultLocale;
	}

	public static String getMessage(String key) {
		return messageSourceAccessor.getMessage(key, getLocale());
	}

	public static String getMessage(String key, Object... args) {
		return messageSourceAccessor.getMessage(key, args, getLocale());
	}

	public static String getMessage(Locale locale, String key, Object... args) {
		return messageSourceAccessor.getMessage(key, args, locale);
	}
}