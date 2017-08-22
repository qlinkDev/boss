<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备通信</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.control-group {
			margin-bottom: 20px;
			text-align: center;
		}
		.control-label {
		    width: 120px;
	    	text-align: right;
	        margin-right: 20px;
		}
	</style>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 修改
			$("#btnSubmit").click(function(){
				var simBankId = $('#simBankId').val();
				var simId = $('#simId').val();
				var status = $('#status option:selected').val();
				var statusText = $('#status option:selected').text();
				if (isEmpty(simBankId)) {
					top.$.jBox.prompt('请输入主控板编号', '系统提示', 'info', {
						closed: function () { 
							$('#simBankId').focus();
						}
					});
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				if (isEmpty(simId)) {
					top.$.jBox.prompt('请输入卡槽编号', '系统提示', 'info', {
						closed: function () { 
							$('#simId').focus();
						}
					});
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				if (isEmpty(status)) {
					top.$.jBox.prompt('请输入卡需要修改的状态', '系统提示', 'info', {
						closed: function () { 
							$('#status').focus();
						}
					});
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				top.$.jBox.confirm("确认要修改SIM卡状态吗？","系统提示",function(v,h,f){
					if(v == "ok"){
						top.$.jBox.tip("正在修改卡状态...", 'loading');
						var url = '${ctx}/mifi/mifiDevice/simStatusController';
						$.post(url, {simBankId:simBankId, simId:simId, status:status, statusText:statusText}, function(data) {
							top.$.jBox.tip(data.msg, 'success'); 
						});
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			// 批量修改卡状态
			$("#batchUpdate").click(function() {
				$.jBox($("#batchUpdatteBox").html(), {
					title : "卡状态修改",
					buttons : {
						"关闭" : true
					},
					bottomText : "导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"
				});
			});
		});
	</script>
</head>
<body>
	<div id="batchUpdatteBox" class="hide">
		<form id="importForm" action="${ctx}/mifi/cardBasicInfo/importStatusFile" method="post" enctype="multipart/form-data" style="padding-left: 20px; text-align: center;" class="form-search" onsubmit="loading('正在导入，请稍等...');">
			<br /> <input id="uploadFile" name="file" type="file" style="width: 330px" /><br />
			<br /> <input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   " /> 
			<a href="${ctx}/mifi/cardBasicInfo/download/template/status">下载模板</a>
		</form>
	</div>
	
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/mifiDevice/simStatusControllerPage">SIM卡状态控制</a></li>
		<li><a href="${ctx}/mifi/mifiDevice/mifiStatusControllerPage">设备状态控制</a></li>
	</ul>
	<!-- tab e -->
	
	<div>
		<shiro:hasPermission name="mifi:cardBasicInfo:status">
			<input id="batchUpdate" class="btn btn-primary" type="button" value="批量修改"/>
		</shiro:hasPermission>
	</div>
	
	<tags:message content="${message}" />
	
	<div style="border: 1px solid #ddd; margin-top: 100px; margin-left: auto; margin-right: auto; width: 600px; height: 400px;">
		<div style="margin-top: 60px;">
			<div class="control-group">
				<label class="control-label" for="simBankId">主控板编号：</label>
				<input type="text" id="simBankId" name="simBankId" maxlength="10" />
			</div>
			<div class="control-group">
				<label class="control-label" for="simId">卡槽编号：</label>
				<input type="text" id="simId" name="simId" maxlength="10" />
			</div>
			<div class="control-group">
				<label class="control-label" for="status">卡需要修改的状态：</label>
				<select id="status" name="status" class="input-medium" style="width: 220px;">
					<option value="">--请选择--</option>
					<c:forEach items="${fns:getDictList('usimstatus')}" var="simUeStatus">
						<option value="${simUeStatus.value}">${simUeStatus.value}|${simUeStatus.label}</option>
					</c:forEach>
					<option value="7">7|卡通信特殊状态</option>
				</select>
			</div>
			<div class="form-actions" style="padding-left: 240px;">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="修 改"/>&nbsp;
			</div>
		</div>
	</div>
</body>
</html>