<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>充值</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function() {
			// 充值
			$('#btnSubmit').click(function() {
				var money = $('#money').val();
				if (!isEmpty(money)) {
					if (!isMoney(money)) {		
						top.$.jBox.info('请输入正确的金额', '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						$dp.$('endDate').value('');
						return false;
					}
				}
				$('#rechargeForm').submit();
			});
		});
	</script>
</head>
<body>
	
	<div style="border: 1px solid #ddd; margin-top: 100px; margin-left: auto; margin-right: auto; width: 600px; height: 400px;">
		<div style="margin-top: 100px;">
			<form id="rechargeForm" action="${pageContext.request.contextPath}/f/om/recharge" method="post" target="_blank" class="form-horizontal">
		       	<input type="hidden" name="channelId" value="${channel.id }" />
		       	<input type="hidden" name="phone" value="${channel.channelName }" />
				<div class="control-group" style="margin-bottom: 20px;">
					<label class="control-label" for="name">当前余额:</label>
					<div class="controls">${channel.balance }</div>
				</div>
				<div class="control-group" style="margin-bottom: 60px;">
					<label class="control-label" for="name">充值金额:</label>
					<div class="controls">
						<input type="text" id="money" name="money" maxlength="10" class="required"/>
					</div>
				</div>
				<div class="form-actions" style="padding-left: 200px;">
					<input id="btnSubmit" class="btn btn-primary" type="button" value="充 值"/>&nbsp;
					<input id="btnCancel" class="btn" type="button" value="取 消" onclick="javascript:window.close();"/>
				</div>
			</form>
		</div>
	</div>
</body>
</html>