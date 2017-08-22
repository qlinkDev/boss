<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备与卡绑定管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#imei").focus();
			$("#inputForm").validate();
			
			// 保存
			$('#btnSubmit').click(function() {
				var imei = $('#imei').val();
				if (isEmpty(imei)) {
					top.$.jBox.info('请输入设备编号', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				var simBankId = $('#simBankId').val();
				if (isEmpty(simBankId)) {
					top.$.jBox.info('请输入simBankId', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				var simId = $('#simId').val();
				if (isEmpty(simId)) {
					top.$.jBox.info('请输入simId', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				var startDate = $('#startDate').val();
				if (isEmpty(startDate)) {
					top.$.jBox.info('请选择生效时间', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				var endDate = $('#endDateStr').val();
				if (isEmpty(endDate)) {
					top.$.jBox.info('请选择失效时间', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				
				// 检测
				var url = '${ctx}/mifi/mifitest/ajaxCheck.json';
				$.post(url, {imei:imei, simBankId:simBankId, simId:simId, startDate:startDate, endDate:endDate}, function(data) {
					if ('success' == data.status) {
						$('#endDate').val(endDate);
						$("#inputForm").submit();
					} else {
						top.$.jBox.confirm(data.message + '是否继续绑定？', "系统提示", function(v, h, f){
							if (v == "ok") {
								if (!isEmpty(data.endDateStr))
									$('#endDate').val(data.endDateStr);
								else
									$('#endDate').val(endDate);
								$("#inputForm").submit();
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
					}
				});
			});
		});
	</script>
	<style type="text/css">
		.contrySpan {
			display:-moz-inline-box;
			display:inline-block;
			width:180px; 
		}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/mifitest/">设备与卡绑定列表</a></li>
		<li class="active"><a href="${ctx}/mifi/mifitest/form?id=${mifiTest.id}">设备与卡绑定<shiro:hasPermission name="mifi:mifiTest:edit">${not empty region.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mifi:mifiTest:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	
	<tags:message content="${message}"/>
	
	<form:form id="inputForm" modelAttribute="mifiTest" action="${ctx}/mifi/mifitest/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="endDate"/>
		<div class="control-group">
			<label class="control-label" for="imei">设备编号:</label>
			<div class="controls">
				<form:input path="imei" htmlEscape="false" minlength="1" maxlength="32" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="simBankId">simbankid:</label>
			<div class="controls">
				<form:input path="simBankId" htmlEscape="false" minlength="1" maxlength="10" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="simId">simid:</label>
			<div class="controls">
				<form:input path="simId" htmlEscape="false" minlength="1" maxlength="3" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="startDate">生效时间:</label>
			<div class="controls">
			  	<input id="startDate" name="startDate" value="<fmt:formatDate value="${mifiTest.startDate }" pattern="yyyy-MM-dd HH:mm:ss"/>" type="text" readonly="true" maxlength="22" class="Wdate required" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="endDate">失效时间:</label>
			<div class="controls">
			  	<input id="endDateStr" name="endDateStr" value="<fmt:formatDate value="${mifiTest.endDate }" pattern="yyyy-MM-dd HH:mm:ss"/>" type="text" readonly="true" maxlength="22" class="Wdate required" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="remarks">说明:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mifi:mifiTest:edit">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="保 存"/>&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>