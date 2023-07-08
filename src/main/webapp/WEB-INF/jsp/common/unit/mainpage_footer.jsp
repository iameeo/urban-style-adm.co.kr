<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/include/inc_declare.jsp"%>

<div class="footer">
	<%-- 로그인 체크 --%>
	<c:if test="${not empty sessionScope.userLoginInfo}">
		<div class="footer_wrap">
			Copyright © <strong>URBAN-STYLE Corp.</strong> All rights reserved.
		</div>
	</c:if>
</div>