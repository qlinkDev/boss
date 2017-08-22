<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>SIM卡管理</title>
	<meta name="decorator" content="default" />
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#inputForm").validate();
			$('#btnSubmit').click(function() {
				
				// 充值流量
				var dataCap = $('#dataCap').val();
				if (!isEmpty(dataCap) && !(dataCap>0)) {
					top.$.jBox.info('请输入正确的充值流量', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				
				$('#inputForm').submit();
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/cardBasicInfo/list">SIM卡列表</a></li>
		<li class="active"><a
			href="${ctx}/mifi/cardBasicInfo/form?id=${cardBasicInfo.id}">SIM卡${not empty cardBasicInfo.id?'修改':'添加'}</a></li>
	</ul>
	<br />

	<form:form id="inputForm" modelAttribute="cardBasicInfo" action="${ctx}/mifi/cardBasicInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<tags:message content="${message}" />
		<div class="control-group">
			<label class="control-label" for="sn">卡号:</label>
			<div class="controls">
				<form:input path="sn" htmlEscape="false" maxlength="50" class="required" readOnly="true" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="type">卡类型名称:</label>
			<div class="controls">
				<select id="type" name="type" class="required">
					<option value="">--请选择--</option>
					<c:forEach
						items="${fns:getListByTable('sim_card_type','card_type','card_type_name')}"
						var="cardType">
						<option value="${cardType.card_type}"
							<c:if test="${cardType.card_type==cardBasicInfo.type}">selected</c:if>>${cardType.card_type_name}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="activeTime">激活时间：</label>
			<div class="controls">
				<input id="activeTime" name="activeTime" type="text" readonly="readonly" class="Wdate" value="<fmt:formatDate value="${cardBasicInfo.activeTime}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="dataCap">充值流量：</label>
			<div class="controls">
				<form:input path="dataCap" htmlEscape="false" maxlength="10" class="number" />
				(M)
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="保 存" />&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>