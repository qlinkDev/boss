<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>渠道设备库存</title>
	<meta name="decorator" content="default"/>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/om/price/">库存列表</a></li>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="priceCondition" action="${ctx}/mifi/mifiManage/deviceStock" method="post" class="breadcrumb form-search">
		<input id="queryData" name="queryData" type="hidden" value="yes"/>
		<label>分组类型：</label>
		<select id="groupType" name="groupType" class="input-medium">
			<option value="channel" <c:if test="${groupType eq 'channel' }">selected</c:if>>渠道</option>
			<option value="inTime" <c:if test="${groupType eq 'inTime' }">selected</c:if>>入库时间</option>
		</select> &nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查  看"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>渠道</th><th>设备总数</th><th>库存数</th><th>使用数</th><th>使用率</th></tr></thead>
		<tbody>
		<c:if test="${!empty resultList }">
		<c:forEach items="${resultList}" var="map">
			<tr>
				<td>${map.name}</td>
				<td>${map.total}</td>
				<td>${map.stock}</td>
				<td>${map.used}</td>
				<td style="width: 30%;">
					<div class="progress progress-striped">
					  	<div class="bar" style="width: ${(map.used/map.total)*100}%;"></div>
					</div>
				</td>
			</tr>
		</c:forEach>
		</c:if>
		</tbody>
	</table>
	<!-- 数据列表 E -->
	
</body>
</html>