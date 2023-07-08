<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/include/inc_declare.jsp"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>${pageTitle}</title>

<!-- common css -->
<link href="https://fonts.googleapis.com/css?family=Nanum+Gothic" rel="stylesheet">
<link href="/css/basic.css" rel="stylesheet" type="text/css">
<link href="/css/style.css" rel="stylesheet" type="text/css">

<!-- common js -->
<script type="text/javascript" src="/js/lib/jquery-1.11.2.js"></script>
<script type="text/javascript" src="/js/lib/jquery.validate.min.js"></script>
</head>

<body>

	<div class="wrap">

		<!-- header -->
		<tiles:insertAttribute name="header" />

		<!-- contents -->
		<div class="contents">
			<div class="contents_wrap">
				<tiles:insertAttribute name="contents" />
			</div>
		</div>
		<!-- footer -->
		<tiles:insertAttribute name="footer" />

	</div>

	<!-- common js -->
	<script type="text/javascript" src="/js/common.js"></script>
	<script type="text/javascript">
		common.init();
	</script>

</body>
</html>