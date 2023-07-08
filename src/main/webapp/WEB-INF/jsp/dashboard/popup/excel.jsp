<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/include/inc_declare.jsp" %>

<table>
	<colgroup>
		<col width="10%" />
		<col width="10%" />
		<col width="10%" />
		<col width="10%" />
		<col width="10%" />
		<col width="10%" />
		<col width="10%" />
		<col width="10%" />
		<col width="10%" />
		<col width="10%" />
		<col width="10%" />
		<col width="10%" />
		<col width="10%" />
		<col width="10%" />
		<col width="10%" />
	</colgroup>
	<tr>
		<th style="text-align:center;background-color:#eaeaea;">순번</th>
		<th style="text-align:center;background-color:#eaeaea;">번호</th>
		<th style="text-align:center;background-color:#eaeaea;">사이트</th>
		<th style="text-align:center;background-color:#eaeaea;">상품코드</th>
		<th style="text-align:center;background-color:#eaeaea;">상품명</th>
		<th style="text-align:center;background-color:#eaeaea;">상품명 최종</th>
		<th style="text-align:center;background-color:#eaeaea;">상품가격</th>
		<th style="text-align:center;background-color:#eaeaea;">상품사이즈</th>
		<th style="text-align:center;background-color:#eaeaea;">상품색상</th>
		<th style="text-align:center;background-color:#eaeaea;">타이틀</th>
		<th style="text-align:center;background-color:#eaeaea;">상세</th>
		<th style="text-align:center;background-color:#eaeaea;">등록시간</th>
		<th style="text-align:center;background-color:#eaeaea;">기타1</th>
		<th style="text-align:center;background-color:#eaeaea;">기타2</th>
		<th style="text-align:center;background-color:#eaeaea;">카테고리</th>
		<th style="text-align:center;background-color:#eaeaea;">상세(지그)</th>
	</tr>
	<c:forEach items="${excelList}" var="result" varStatus="status">
		<tr>
			<td style="text-align:center;">${(status.index)+1}</td>
			<td style="text-align:center;">${result.seq}</td>
			<td style="text-align:center;">${result.product_shop}</td>
			<td style="text-align:center;">${result.product_code}</td>
			<td style="text-align:center;">${result.product_title}</td>
			<td style="text-align:center;">${result.product_new_title}</td>
			<td style="text-align:center;">${result.product_price}</td>
			<td style="text-align:center;mso-number-format:'\@';">${result.product_size}</td>
			<td style="text-align:center;">${result.product_color}</td>
			<td style="text-align:center;">${result.product_title_img}</td>
			<td style="text-align:center;mso-number-format:'\@';">${fn:replace(fn:replace(result.product_detail_img,'< ','&lt;'),' >','>')}</td>
			<td style="text-align:center;">${result.product_regdate}</td>
			<td style="text-align:center;">${result.product_price2}</td>
			<td style="text-align:center;">${result.product_price3}</td>
			<td style="text-align:center;">${result.product_category}</td>
			<td style="text-align:center;mso-number-format:'\@';">${fn:replace(fn:replace(result.product_detail_img_zig,'< ','&lt;'),' >','>')}</td>
		</tr>
	</c:forEach>
</table>