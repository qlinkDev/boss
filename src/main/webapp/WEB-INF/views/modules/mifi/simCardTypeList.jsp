<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>SIM卡类型管理</title>
<meta name="decorator" content="default" />
<style type="text/css">.sort{color:#0663A2;cursor:pointer;}</style>
<script type="text/javascript">
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/simCardType/init">卡类型管理</a></li>
		<li><a href="${ctx}/mifi/simCardType/form">卡类型添加</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="simCardType" action="${ctx}/mifi/simCardType/list"
		method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<div>
			<label>类型名称：</label><form:input path="cardTypeName" maxlength="50" class="input-small required" />
			<label>类型编号：</label><form:input path="cardType" maxlength="50" class="input-small required" />
			<label>所属渠道：</label>
			<form:select path="sourceType" class="input-medium required">
	        	<form:option value="" label="请选择"/>
	        	<form:options items="${fns:getChannelList()}" itemLabel="channelName" itemValue="channelNameEn" htmlEscape="false"/>
	        </form:select>
			&nbsp;&nbsp; <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /> 
		</div>
	</form:form>
	<tags:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>卡类型编码</th>
				<th>卡类型名称</th>
				<th>所属渠道</th>
				<th>有效天数(-1:长久)</th>
				<!-- 
				<th>激活时间</th>
				 -->
				<th>高速流量(GB)</th>
				<th>卡区域类型</th>
				<th width="500">可使用地区</th>
				<th>卡类型描述</th>
				<th>创建时间</th>
				<th>创建用户</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="a">
				<tr>
					<td>${a.cardType}</td>
					<td>${a.cardTypeName}</td>
					<td>${a.sourceType}|${fns:getLabelByTableAndWhere('om_channel', 'channel_name_en', 'channel_name', ' and del_flag = 0 ', a.sourceType)}</td>
					<td>${a.validDays}</td>
					<!-- 
					<td>${a.activeTime}</td>
					 -->
					<td>${a.dataCap}</td>
					<td>${a.areaTypeName}</td>
					<td>${a.allowedMccCn}</td>
					<td>${a.cardTypeDesc}</td>
					<td>${a.createTime}</td>
					<td>${a.createUser}</td>
					<td><a href="${ctx}/mifi/simCardType/form?id=${a.id}">修改</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>