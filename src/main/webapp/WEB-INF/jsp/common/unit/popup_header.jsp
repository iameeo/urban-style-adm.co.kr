<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/include/inc_declare.jsp"%>

<%-- 로그인 체크 --%>
<c:if test="${not empty sessionScope.userLoginInfo}">
	${menuList}
</c:if>