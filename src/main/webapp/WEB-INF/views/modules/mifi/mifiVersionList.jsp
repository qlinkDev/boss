<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>mifi设备版本</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<style type="text/css">
		#tableDiv {overflow:auto;}
		table th {white-space: nowrap;}
		table td {white-space: nowrap;}
	}
	</style>
	<script type="text/javascript">
	document.addEventListener('DOMContentLoaded', function() {
		document.getElementById('btnSubmit').addEventListener('click', function() {
			$.jBox.tip("正在查询...", 'loading', {persistent: true});
			$(this).attr("disabled",true);
			$("#searchForm").submit();
		}, false);
	}, false);
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
	    	return false;
	    }
		$(document).ready(function() {
			$("#updatauploadlogflag").click(function(){
				$.jBox("iframe:${ctx}//mifi/version/formss",{title:"批量修改", width:400 ,height :250, persistent:true, buttons: { '关闭': 'ok' }});
			 });
			$("#updataspeedlimitflag").click(function(){
				$.jBox("iframe:${ctx}//mifi/version/forms",{title:"批量修改", width:400 ,height :250, persistent:true, buttons: { '关闭': 'ok' }});
			});
			document.getElementById('btnSubmit').addEventListener('click', function() {
				$("#searchForm").submit();
			}, false);
		});
	    $("#btnSubmit").click(function(){
			$("#pageNo").val(1);
		});
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/version/list">设备版本列表</a></li>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<div align="left">
		<form:form id="searchForm" modelAttribute="mifiVersion" action="${ctx}/mifi/version/list" method="post" class="breadcrumb form-search">
		<input  id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input  id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>IMEI ：</label><form:input path="IMEI_6200" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
		<input id="updatauploadlogflag"  class="btn btn-primary"  type="button" value="修改updateflag"/>
		<input id="updataspeedlimitflag" class="btn btn-primary"  type="button" value="修改speedlimitflag"/>
		</form:form>
	</div>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<div id="tableDiv">
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>IMEI_6200</th>
				<th>MAIN_VERSION</th>
				<th>YYM_VERSION</th>
				<th>YY_DAEMON_VERSION</th>
				<th>APN_3G_VERSION</th>
				<th>APN_4G_VERSION</th>
				<th>COPS_CONF_VERSION</th>
				<th>YY_UPDATE_VERSION</th>
				<th>stamp_created</th>
				<th>stamp_update</th>
				<th>UPDATEFLAG</th>
				<th title="限速标志">SPEEDLIMITFLAG</th>
				<th>Lcd_version_Type</th>
				<th>Log_file_Type</th>
				<th title="低速订单标志">speedlimit_type</th>
				<th>操作</th>
			</tr>
		<tbody>
		<c:forEach items="${page.list}" var="mifiVersion">
			<tr>
				<td>${mifiVersion.IMEI_6200}</td>
				<td>${mifiVersion.MAIN_VERSION}</td>
				<td>${mifiVersion.YYM_VERSION}</td>
				<td>${mifiVersion.YY_DAEMON_VERSION}</td>
				<td>${mifiVersion.APN_3G_VERSION}</td>
				<td>${mifiVersion.APN_4G_VERSION}</td>
				<td>${mifiVersion.COPS_CONF_VERSION}</td>
				<td>${mifiVersion.YY_UPDATE_VERSION}</td>
				<td><fmt:formatDate value="${mifiVersion.stamp_created}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><fmt:formatDate value="${mifiVersion.stamp_update}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${mifiVersion.UPDATEFLAG}</td>
				<td>${mifiVersion.SPEEDLIMITFLAG}</td>
				<td>${mifiVersion.lcd_version_Type}</td>
				<td>${mifiVersion.log_file_Type}</td>
				<td>${mifiVersion.speedlimitType}</td>
				<td>
					<a href="${ctx}//mifi/version/form?id=${mifiVersion.id}">修改</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	<!-- 数据列表 E -->
	
	<!-- 分页 S -->
	<div class="pagination">${page}</div>
	<!-- 分页 E -->
</body>
</html>