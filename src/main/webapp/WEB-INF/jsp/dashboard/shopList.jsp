<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/include/inc_declare.jsp"%>

<div>
	<table>
		<thead>
			<tr>
				<th>IDX</th>
				<th>SHOP URL</th>
				<th>SHOP NAME</th>
				<th>SHOP ID</th>
				<th>SHOP PW</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${combineShopList}" var="result" varStatus="status">
				<tr style="background-color:${result.isComplete == '0' ? '#eaeaea' : ''};">
					<td>${(status.index)+1}</td>
					<td><a href="${result.shop_url}" target="_blank">${result.shop_url}</a></td>
					<td>${result.shop_name}</td>
					<td>${result.shop_id}</td>
					<td>${result.shop_pw}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>