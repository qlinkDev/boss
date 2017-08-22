<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备通信</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.control-group {
			margin-bottom: 20px;
			text-align: center;
		}
		.control-label {
		    width: 140px;
	    	text-align: right;
	        margin-right: 20px;
		}
	</style>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 修改
			$("#btnSubmit").click(function(){
				var imei = $('#imei').val();
				var status = $('#status option:selected').val();
				var statusText = $('#status option:selected').text();
				if (isEmpty(imei)) {
					top.$.jBox.prompt('请输入设备编号', '系统提示', 'info', {
						closed: function () { 
							$('#imei').focus();
						}
					});
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				if (isEmpty(status)) {
					top.$.jBox.prompt('请输入设备需要修改的状态', '系统提示', 'info', {
						closed: function () { 
							$('#status').focus();
						}
					});
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				top.$.jBox.confirm("确认要修改设备状态吗？","系统提示",function(v,h,f){
					if(v == "ok"){
						top.$.jBox.tip("正在修改设备状态...", 'loading');
						var url = '${ctx}/mifi/mifiDevice/mifiStatusController';
						$.post(url, {imei:imei, status:status, statusText:statusText}, function(data) {
							top.$.jBox.tip(data.msg, 'success'); 
						});
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/mifiDevice/simStatusControllerPage">SIM卡状态控制</a></li>
		<li class="active"><a href="${ctx}/mifi/mifiDevice/mifiStatusControllerPage">设备状态控制</a></li>
	</ul>
	<!-- tab e -->
	
	<div style="border: 1px solid #ddd; margin-top: 100px; margin-left: auto; margin-right: auto; width: 600px; height: 400px;">
		<div style="margin-top: 60px;">
			<div class="control-group">
				<label class="control-label" for="imei">设备编号：</label>
				<input type="text" id="imei" name="imei" maxlength="32" />
			</div>
			<div class="control-group">
				<label class="control-label" for="status">设备需要修改的状态：</label>
				<select id="status" name="status" class="input-medium" style="width: 220px;">
					<option value="">--请选择--</option>
					<option value="1">设备上报当前状态消息</option>
					<option value="2">设备关机</option>
					<option value="3">设备恢复出厂设置</option>
					<option value="4">设备重启</option>
					<option value="5">设置网络模式为自动模式</option>
					<option value="6">设置网络模式为3G Only</option>
					<option value="7">设置网络模式为4G Only</option>
					<option value="8">取消限速设置</option>
				</select>
			</div>
			<div class="form-actions" style="padding-left: 240px;">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="修 改"/>&nbsp;
			</div>
		</div>
	</div>
</body>
</html>