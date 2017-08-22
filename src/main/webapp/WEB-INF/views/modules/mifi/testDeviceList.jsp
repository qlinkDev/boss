<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>测试设备管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.errorBlock {
			    background-color: #FFC6A5;
			    border: solid 1px #ff0000;
			    color: #ff0000;
			    margin: 10px 10px 0 10px;
			    padding: 7px;
			    font-weight: bold;
    		}
	</style>
	<script type="text/javascript">
	
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
	    	return false;
	    }
		
		// 归还设备
		function sendBack(id, userName, remark) {
			$('#deviceId').val(id);
			$.jBox($("#sendBackBox").html(), {title:"设备归还", submit: submit, bottomText: '请确认归还的设备无损坏'});
		}
		
		var submit = function (v, h, f) {
		    if (f.returnUserName == '') {
		        $.jBox.tip("请输入归还人的姓名。", 'error', { focusId: "returnUserName" }); // 关闭设置 returnUserName 为焦点
		        return false;
		    }
		    if (f.returnUserName.length > 50) {
		        $.jBox.tip("归还人姓名长度不能大于50。", 'error', { focusId: "returnUserName" }); // 关闭设置 returnUserName 为焦点
		        return false;
		    }
			$('#sendBackForm').submit();
		};
		
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/testDevice/">测试设备列表</a></li>
		<shiro:hasPermission name="mifi:test:edit"><li><a href="${ctx}/mifi/testDevice/form">测试设备添加</a></li></shiro:hasPermission>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="testDeviceCondition" action="${ctx}/mifi/testDevice/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>设备编号：</label><form:input path="likeImei" htmlEscape="false" maxlength="32" class="input-small"/>
		<label>借出人：</label><form:input path="likeLendUserName" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>状态：</label>
		<form:select id="eqStatus" path="eqStatus" class="input-small">
			<form:option value="" label="请选择"/>
			<form:option value="LEND" label="借出"/>
			<form:option value="RETURN" label="归还"/>
		</form:select>
		<label>借出开始日期：</label><form:input id="geLendDate" path="geLendDate" type="text" readonly="true" maxlength="20" class="input-small Wdate"
								   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		<label>借出结束日期：</label><form:input id="leLendDate" path="leLendDate" type="text" readonly="true" maxlength="20" class="input-small Wdate"
								   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>设备编号</th><th>状态</th><th>借出人</th><th>借出时间</th><th>归还人</th><th>归还时间</th><th>说明</th><shiro:hasPermission name="mifi:test:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="device">
			<tr>
				<td>${device.imei }</td>
				<td>${device.status.name }</td>
				<td>${device.lendUserName }</td>
				<td><fmt:formatDate value="${device.lendDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${device.returnUserName }</td>
				<td><fmt:formatDate value="${device.returnDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${device.remarks }</td>
				<shiro:hasPermission name="mifi:test:edit"><td>
					<c:if test="${device.status eq 'LEND' }">
    					<a href="javascript:void(0);" onclick="javacript:sendBack('${device.id }', '${device.lendUserName }', '${device.remarks }');">归还</a>
					</c:if>
					<c:if test="${device.status eq 'RETURN' }">
						<a href="${ctx}/mifi/testDevice/delete?id=${device.id}" onclick="return confirmx('确认要删除该测试设备吗？', this.href)">删除</a>
					</c:if>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<!-- 数据列表 E -->
	
	<!-- 分页 S -->
	<div class="pagination">${page}</div>
	<!-- 分页 E -->

	<!-- 归还测试设备 S -->	
	<div id="sendBackBox" class="hide">
		<form id="sendBackForm" action="${ctx}/mifi/testDevice/sendBack" method="post" style="padding-left:20px;text-align:center;">
			<input type="hidden" id="deviceId" name="id" />
			<div class="msg-div">
				<p></p>
            	<div class="field">
            		<span>归还人：</span><input type="text" id="returnUserName" name="returnUserName" />
            	</div>
        	</div>
		</form>
	</div>
	<!-- 归还测试设备 E -->	
</body>
</html>