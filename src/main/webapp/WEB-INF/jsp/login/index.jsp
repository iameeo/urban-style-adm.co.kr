<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/include/inc_declare.jsp"%>

<%-- 
    JSP Name : main.jsp
    Description : 메인페이지
    author : 이재호
    since 2014.12.10.
    version 1.0
    Modification Information
       since          author              description
    ===========    =============    ===========================
    2014.12.10.        이재호			                 최초 생성
--%>

<form name="login_form" method="post" action="loginProcess.do">
	<div>
		${pageTitle} / ${page_name}
		
		<input type="text" value="iameeo_admin" name="id" placeholder="USER ID" />
		<input type="password" value="wndnjsWkd2" name="pw" placeholder="USER PW" />
		<button type="button" onclick="login_chk();">LOGIN</button>
	</div>
</form>

<script type="text/javascript">

	//페이지 접속시 아이디로 포커스
	$("input[name$='id']").focus();

	//로그인 체크
	function login_chk() {
		var id = $("input[name='id']").val();
		var pw = $("input[name='pw']").val();

		if (id.length <= 0 && pw.length <= 0) {
			alert("아이디 및 비밀번호를 확인해주세요.");
		} else {
			$("form[name='login_form']").submit();
		}
	}
</script>