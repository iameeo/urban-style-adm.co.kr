package com.danalent.error.web;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {
	String error_page_res = "common/error/";

	/**
	 * Resource Not Found
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/error/resourceNotFound.do")
	public String resourceNotFound(HttpServletResponse response) throws Exception {

		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		return error_page_res + "resourceNotFound";
	}

	/**
	 * Data Access Failure
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/error/dataAccessFailure.do")
	public String dataAccessFailure(HttpServletResponse response) throws Exception {

		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return error_page_res + "dataAccessFailure";
	}

	/**
	 * Uncaught Exception
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/error/uncaughtException.do")
	public String uncaughtException(HttpServletResponse response) throws Exception {

		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return error_page_res + "uncaughtException";
	}
}
