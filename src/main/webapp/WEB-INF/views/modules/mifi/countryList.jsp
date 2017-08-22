<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>国家&MCC信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出国家MCC信息数据吗？", "系统提示", function(
						v, h, f) {
					if (v == "ok") {
						$("#searchForm").attr("action",
								"${ctx}/mifi/country/export")
								.submit();
						$("#searchForm").attr("action",
								"${ctx}/mifi/country/list");
					}
				}, {
					buttonsFocus : 1
				});
				top.$('.jbox-body .jbox-icon').css('top', '55px');
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
		<li class="active"><a href="${ctx}/mifi/country/">国家&MCC信息列表</a></li>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" action="${ctx}/mifi/country/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>国家编号：</label><input type="text" id="countryCode" name="countryCode" value="${countryCode }" maxlength="10" class="input-small"/>
		<label>国家名称：</label><input type="text" id="countryName" name="countryName" value="${countryName }" maxlength="20" class="input-small"/>
		<label>国家英文名：</label><input type="text" id="countryNameEn" name="countryNameEn" value="${countryNameEn }" maxlength="50" class="input-small"/>
		<label>MCC：</label><input type="text" id="mcc" name="mcc" value="${mcc }" maxlength="50" class="input-small"/>&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;
		<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>国家编号</th><th>国家名称</th><th>国家英文名</th><th>MCC</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="country">
			<tr>
				<td>${country.countryCode }</td>
				<td>${country.countryName }</td>
				<td>${country.countryNameEn }</td>
				<td>${country.mcces }</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<!-- 数据列表 E -->
	
	<!-- 分页 S -->
	<div class="pagination">${page}</div>
	<!-- 分页 E -->

</body>
</html>