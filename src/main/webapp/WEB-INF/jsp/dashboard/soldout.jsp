<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/include/inc_declare.jsp"%>

<div>
	<form name="imgConvertForm" id="imgConvertForm" action="imgReName.do" method="POST">
		<textarea id="naverText" name="naverText" style="font-size: 12px; opacity:0.5; color: #5d5d5d; width: 100%; height: 480px;"></textarea>
		<div class="asm">
		<button type="button" onclick="soldout();" class="ce ko">CONVERT</button>
	</form>
</div>

<script>

    function soldout(){
        var prd = $("#naverText").val();
		$.ajax({
			type : "POST"
			,url : "./godoSoldout.do"
			,data : {prd : prd}
			,dataType: "json"
			,success : function (data) {
				if(data.result){
					alert("완료")
				}
			}
		});
    }
</script>