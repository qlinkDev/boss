<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>生成统计任务</title>
	<meta name="decorator" content="default" />
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function(){
			// 统计
			$('#btnCount').click(function() {
				$('#btnCount').attr('disabled', true);
				// 开始时间
				var startDate = $('#startDate').val();
				if (isEmpty(startDate)) {
					top.$.jBox.info('请选择统计开始时间', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					$('#btnSubmit').attr('disabled', false);
					return false;
				}
				var endDate = $('#endDate').val();
				if (isEmpty(endDate)) {
					top.$.jBox.info('请选择统计结束时间', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					$('#btnSubmit').attr('disabled', false);
					return false;
				}
				var regionId = $('#regionId').val();
				if (isEmpty(regionId)) {
					top.$.jBox.info('请选择统计区域', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					$('#btnSubmit').attr('disabled', false);
					return false;
				}
				
				var url = '${ctx}/mifi/flowCount/count.json';
				$.post(url, {startDate:startDate, endDate:endDate, regionId:regionId}, function(data) {
					if ('success' == data.status) {
						top.$.jBox.info(data.message, '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						window.location.href = '${ctx}/mifi/flowCount?queryData=yes';
					} else {
						top.$.jBox.info(data.message, '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						$('#btnCount').attr('disabled', false);
					}
				});
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/flowCount/">流量统计任务列表</a></li>
		<li class="active"><a href="${ctx}/mifi/flowCount/form">生成统计任务</a></li>
	</ul>
	<br />
	<tags:message content="${message}"/>
	<form:form id="inputForm" action="" method="post" enctype="multipart/form-data" class="form-horizontal">
		<div class="control-group">
			<label class="control-label">开始时间:</label>
			<div class="controls">
				<input id="startDate" type="text" readonly="true" maxlength="20" class="input-medium Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true,maxDate:'#F{$dp.$D(\'endDate\')||\'%y-%M-%d\'}'});"/>
			</div>
		</div>
	
		<div class="control-group">
			<label class="control-label">结束时间:</label>
			<div class="controls">
				<input id="endDate" type="text" readonly="true" maxlength="20" class="input-medium Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true,minDate:'#F{$dp.$D(\'startDate\')}',maxDate:'%y-%M-%d'});"/>
			</div>
		</div>
	
		<div class="control-group">
			<label class="control-label">区域:</label>
			<div class="controls">
				<select id="regionId" class="input-medium">
					<option value="">请选择</option>
					<c:forEach items="${fns:getRegionList()}" var="region">
						<option <c:if test="${price.region.id eq region.id }">selected="selected"</c:if> value="${region.id }">${region.name }</option>
					</c:forEach>
				</select>
			</div>
		</div>
		
		<div class="form-actions">
			<shiro:hasPermission name="mifi:flowCount:view">
			<input id="btnCount" class="btn btn-primary" type="button" value="统 计"/>&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>