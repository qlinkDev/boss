<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>MIFI设备管理</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/WEB-INF/views/include/dialog.jsp"%>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					disableButtons();
					form.submit();
				}
			});
			$("#deviceId").rules("add",{required:true});
			$("#customerServiceProfile").rules("add",{required:true});
			
			$("#btnQuery").click(function(){
				$("#customerServiceProfile").rules("remove");
				if(!$("#inputForm").valid()){
					return;
				}
				var deviceId = $('#deviceId').val();
				loading('正在查询，请稍等...');
				disableButtons();
				$.post('${ctx}/mifi/deviceProfile/query', {deviceId: deviceId}, function(data,status,xhr){
					top.$.jBox.closeTip();
					enableButtons();
					if('success' != status || !data){
						$.jBox.tip("查询失败，请稍后再试", 'error');
						return;
					}
					if('MIFI2.0_CTS_PAYG_Tier3' == data || 'YYM_MIFI2.0_CTS_PAYG' == data){
						$('#deviceProfile').html(data);
						return;
					}
					$.jBox.tip("查询失败，返回码" + data, 'error');
				});
				$("#customerServiceProfile").rules("add",{required:true});
			});
			
			$("#btnBatch").click(function(){
				var messageShowed = 0;
				$.jBox.confirm("该操作可能消耗很长时间，确认？", "操作提示", function(v,h,f){
					if(v == "ok"){
						loading('正在查询，请稍等...');
						disableButtons();
						$.post('${ctx}/mifi/deviceProfile/batch', {}, function(data,status,xhr){
							top.$.jBox.closeTip();
							enableButtons();
							if('success' != status || !data){
								$.jBox.tip("操作失败，请稍后再试", 'error');
							}else{
								$.jBox.tip(data, 'error');
							}
							messageShowed = 1;
						});
						setTimeout(function(){
							if(!messageShowed){
								top.$.jBox.closeTip();
								enableButtons();
								$.jBox.tip("请求已提交，系统正在后台执行。您可以前往其他页面，或关闭窗口，不影响执行结果", 'info');
							}
						}, 10000);
					}
				},{buttonsFocus:1});
			});
			
			$('#btnExport').click(function() {
				top.$.jBox.confirm("确认导出消费等级5的设备数据？", "操作提示", function(v,
						h, f) {
					if (v == "ok") {
						$("#deviceId").rules("remove");
						$("#customerServiceProfile").rules("remove");
						loading('正在查询，请稍等...');
						disableButtons();
						$("#inputForm").attr('action', "${ctx}/mifi/deviceProfile/export");
						$("#inputForm").submit();
						setTimeout(function(){
							$("#inputForm").attr('action', "${ctx}/mifi/deviceProfile/save");
						top.$.jBox.closeTip();
						enableButtons();
						}, 10000);
					}
				}, {
					buttonsFocus : 1
				});
				top.$('.jbox-body .jbox-icon').css('top', '55px');
				top.$('.jbox').css('top', '180px');
			});
		});
		
		function disableButtons(){
			$('#btnSubmit').attr("disabled", "");
			$('#btnExport').attr("disabled", "");
			$('#btnQuery').attr("disabled", "");
			$('#btnBatch').attr("disabled", "");
		}
		
		function enableButtons(){
			$('#btnSubmit').removeAttr("disabled");
			$('#btnExport').removeAttr("disabled");
			$('#btnQuery').removeAttr("disabled");
			$('#btnBatch').removeAttr("disabled");
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/deviceProfile">MIFI设备消费等级修改</a></li>
	</ul><br/>
	
	<form id="inputForm" action="${ctx}/mifi/deviceProfile/save" method="post" class="form-horizontal">
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label" for="deviceId">设备编号:</label>
			<div class="controls">
				<input type="text" id="deviceId" name="deviceId" maxlength="50" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">当前消费级别:</label>
			<div class="controls">
				<span id="deviceProfile"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="sourceType">设置消费级别:</label>
			<div class="controls">
				<select id="customerServiceProfile" name="customerServiceProfile" class="input-medium" style="width: 220px;">
		        	<option value="">请选择</option>
					<option value="MIFI2.0_CTS_PAYG_Tier3">MIFI2.0_CTS_PAYG_Tier3(Tier3)</option>
					<option value="YYM_MIFI2.0_CTS_PAYG">YYM_MIFI2.0_CTS_PAYG(Tier5)</option>
		        </select>
			</div>
		</div>
		
		
		<div class="form-actions">
			<input id="btnQuery" class="btn btn-primary" type="button" value="查 询"/>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="修 改"/>
			<input id="btnExport" class="btn btn-primary" type="button" value="导 出"/>
			<input id="btnBatch" class="btn btn-primary" type="button" value="批处理"/>
		</div>
	</form>
</body>
</html>