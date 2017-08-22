<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备运行状态统计</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			// 设备运行状态统计
			$("#btnSubmit").click(function(){
				var dateStr = $('#dateStr').val();
				if (dateStr=='') {
					top.$.jBox.info('统计时间不能为空', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				var sourceType = $('#sourceType option:selected').val();
				top.$.jBox.confirm("确认要统计设备运行状态吗？","系统提示",function(v,h,f){
					if(v == "ok"){
						top.$.jBox.tip("正在统计并发送邮件...", 'loading');
						var url = '${ctx}/mifi/mifiStatus/statusCount';
						$.post(url, {dateStr:dateStr,sourceType:sourceType}, function(data) {
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
		<li class="active"><a href="${ctx}/mifi/mifiStatus/statusCountPage">设备运行状态统计</a></li>
	</ul>
	<!-- tab e -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	<div style="border-bottom: 1px solid #ddd; margin-top: 10px;">
		<label>统计日期：</label><input id="dateStr" type="text" readonly="true" maxlength="20" class="input-small Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		<label>代理商：</label> 
		<select id="sourceType" name="sourceType" class="input-medium">
				<option value="">--请选择--</option>
				<c:forEach items="${fns:getListByTableAndWhere('om_channel','channel_name_en','channel_name',' and del_flag = 0')}" var="sourceTypeValue">
					<option value="${sourceTypeValue.channel_name_en}" <c:if test="${sourceTypeValue.channel_name_en==sourceType}">selected</c:if>>${sourceTypeValue.channel_name}</option>
				</c:forEach>
		</select>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="统 计"/>
	</div>
</body>
</html>