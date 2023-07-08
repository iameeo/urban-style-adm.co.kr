<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/include/inc_declare.jsp"%>

<div class="header">
	<%-- 로그인 체크 --%>
	<c:if test="${not empty sessionScope.userLoginInfo}">
		<div class="header_wrap">
			${menuList}
		</div>
	</c:if>
</div>