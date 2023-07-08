<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/include/inc_declare.jsp"%>

<div style="overflow:hidden;">
	<div style="float:left;width:80%;">
		<table>
			<thead>
				<tr>
					<th>순번</th>
					<th>상점</th>
					<th>갱신 날짜</th>
					<th>갱신 상품 수</th>
					<th>갱신 상품 이미지 수</th>
					<th>갱신 상품 이미지 변환 수</th>
					<th>남은 이미지 변환 수</th>
					<th>결과</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${combineProductList}" var="result" varStatus="status">
					<tr style="background-color:${result.isComplete == '0' ? '#5eb571' : ''};">
						<td>${(status.index)+1}</td>
						<td>${result.product_shop}</td>
						<td><button type="button" onclick="file_down('${result.product_regdate}','${result.product_shop}');">${result.product_regdate}</button></td>
						<td>${result.regCnt}</td>
						<td>${result.regImgCnt}</td>
						<td>${result.regConImgCnt}</td>
						<td>${result.regImgCnt - result.regConImgCnt}</td>
						<td>${result.isComplete} <input type="button" value="gdadmin" onclick="gdadminInsert('${result.product_shop}','${result.product_regdate}');" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div style="float:left;width:18%;margin:0% 1%;">
		<table>
			<thead>
				<tr>
					<th>순번</th>
					<th>상점</th>
					<th>결과</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${combineShopList}" var="result" varStatus="status">
					<tr class="item" data-seq="${result.seq}">
						<td>${(status.index)+1}</td>
						<td><a href="javascript:shop_data('${result.seq}');">${result.shop_name}</a></td>
						<td class="result_${result.seq}"></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>	
		<div>
			<input type="button" value="전체갱신" onclick="all_shop_data();" style="width:100%;" />
		</div>
		<div style="padding:10px 0px;">
			<input type="button" value="미갱신이미지갱신" onclick="naver_img_re();" style="width:100%;" />
		</div>
		<div>
			<c:if test="${empty access_token}">
				<a href="${apiURL}"><img height="50" src="http://static.nid.naver.com/oauth/small_g_in.PNG"/></a>
			</c:if>
		</div>
	</div>
</div>

<div class="dim" style="display:none;position:fixed;top:0px;left:0px;width:100%;height:100%;opacity:0.6;background-color:#ededee;"><img src="https://cdn-images-1.medium.com/max/1600/1*TW6dDGBgPuZyL19TVa2kUQ.gif"></div>

<script type="text/javascript">
	function all_shop_data(){
		$(".item").each(function(i){
			var shop_seq = $(this).attr("data-seq");
			shop_data(shop_seq);
		});
	}

	function shop_data(shop_seq) {
		$(".dim").fadeIn();
		$.ajax({
			type : "POST"
			,url : "/api/combineProductRegist.api?shopSeq="+shop_seq
			,dataType: "json"
			,success : function (data) {
				var resultColor = data.status ? "#5eb571" : "#b55e5e";
				$(".result_"+shop_seq).text(data.status +" : "+ data.insertCnt).css("color", resultColor);
				$(".dim").fadeOut();
			}
		});
	}
	
	function naver_img_re() {
		$(".dim").fadeIn();
		$.ajax({
			type : "POST"
			,url : "/api/naverBlogImgUpdate.api"
			,dataType: "json"
			,success : function (data) {
				$(".dim").fadeOut();
			}
		});
	}
	
	function file_down(regdate, shopType){
		window.open("/dashboard/popup/excel.do?regdate="+ regdate +"&shopType="+ shopType);
	}
	
	function gdadminInsert(shop_name, date){
		$(".dim").fadeIn();
		$.ajax({
			type : "POST"
			,url : "/api/gdadminInsert.api?shop_name="+ shop_name + "&date="+ date
			,dataType: "json"
			,success : function (data) {
				console.log(data);
				$(".dim").fadeOut();
			}
		});
	}
</script>