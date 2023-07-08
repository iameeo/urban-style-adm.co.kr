<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/include/inc_declare.jsp"%>

<div>
	<form name="imgConvertForm" id="imgConvertForm" action="imgReName.do" method="POST">
		<textarea id="naverText" name="naverText" style="font-size: 12px; opacity:0.5; color: #5d5d5d; width: 100%; height: 480px;"></textarea>
		<div class="asm">
		<button type="button" onclick="img_convert();" class="ce ko">CONVERT</button>
	</form>
</div>

<script type="text/javascript">
	function img_convert(){
		if($("#naverText").val() == ""){
			alert("상세 이미지를 네이버에 올린 후 HTML 형식으로 붙여넣어 주세요.");
		}else{
			var formData = $("#imgConvertForm").serialize();
			$.ajax({
				type : "POST"
				,url : "./imgReName.do"
				,data : formData
				,dataType: "json"
				,success : function (data) {
					if(data.result){
						alert("변경이 완료 되었습니다.");
					}else{
						alert("오류가 발생 되었습니다.");
					}
				}
			});
		}
	}
</script>