<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function() {
			// 查询
			$('#btnSubmit').click(function() {
				var channelNameEn = $('#channelNameEn').val();
				if (isEmpty(channelNameEn)) {
					top.$.jBox.info('请选择代理商', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				$.jBox.tip("正在查询...", 'loading', {persistent: true});
				$(this).attr("disabled",true);
				$('#searchForm').submit();
			});
		});
	
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
	    	return false;
	    }
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/om/price/goods">产品列表</a></li>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form id="searchForm" action="${ctx}/om/price/goods" method="post" class="breadcrumb form-search">
		<label>代理商：</label>
		<select id="channelNameEn" name="channelNameEn" class="input-medium">
			<option value="">请选择</option>
			<c:forEach items="${fns:getListByTableAndWhere('om_channel','channel_name_en','channel_name',' and del_flag = 0 ')}" var="sourceTypeValue">
				<option value="${sourceTypeValue.channel_name_en}" <c:if test="${sourceTypeValue.channel_name_en==channelNameEn}">selected</c:if>>${sourceTypeValue.channel_name}</option>
			</c:forEach>
		</select>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
	</form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>国家名称</th><th>国家编号</th><th>MCC</th><th>价格</th></tr></thead>
		<tbody>
		<c:forEach items="${listMap}" var="priceBaen">
			<tr>
				<td>${priceBaen.countryName}</td>
				<td>${priceBaen.countryCode}</td>
				<td>${priceBaen.mcces}</td>
				<td>${priceBaen.price}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<!-- 数据列表 E -->
	
</body>
</html>