<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/include/inc_declare.jsp"%>

<div>
	<table>
		<thead>
			<tr>
				<th>IDX</th>
				<th>SHOP NAME</th>
				<th>IMG</th>
				<th>TITLE</th>
				<th>NEW TITLE</th>
				<th>CATEGORY</th>
				<th>etc.</th>
			</tr>
		</thead>
			<tbody>
				<c:forEach items="${reTitleList}" var="result" varStatus="status">
					<form name="frm_${result.seq}" action="/api/reTitleUpdate.api" method="post">
						<input type="hidden" name="scrollValue" value="${scrollValue}" />
						<input type="hidden" name="product_seq" value="${result.seq}" />
						<tr>
							<td>${(status.index)+1}</td>
							<td>${result.product_shop}</td>
							<td>${result.product_title}</td>
							<td><img src="${result.product_thumbImg}" style="width:200px;height:200px;" /></td>
							<td><input type="text" value="${result.product_new_title}" name="product_new_title"/></td>
							<td>
								<select name="product_category">
									<option value="">=카테고리선택=</option>
									<option value="001">CLOTHES</option>
									<option value="002">ANKLE/WALKER</option>
									<option value="003">FLAT/LOFER</option>
									<option value="004">PUMPS/HEEL</option>
									<option value="005">SNEAKERS</option>
									<option value="006">SANDAL</option>
									<option value="007">MULE/SLIPPER</option>
									<option value="008">BOOTS</option>
									<option value="009">ACC</option>
								</select>								
							</td>
							<td><input type="submit" value="submit" onclick="heightPosition();" /></td>
						</tr>
					</form>
				</c:forEach>
			</tbody>
	</table>
</div>

<script>

	var paramScroll = "${scrollValue}";
	
	if(paramScroll != ""){
		$("html, body").scrollTop(parseInt(paramScroll));
	}

	function heightPosition(){
		var scrollValue = $(document).scrollTop();
		$("input[name=scrollValue]").val(scrollValue);
	}
</script>