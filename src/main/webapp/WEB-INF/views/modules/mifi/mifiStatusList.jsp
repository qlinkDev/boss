<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>MIFI设备状态变更历史查询</title>
<meta name="decorator" content="default" />
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		var searchFlag = true;
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出设备状态数据吗？","系统提示",function(v,h,f){
					if(v == "ok"){
						var dis = 
						changeDisabled(false);
						$("#searchForm").attr("action","${ctx}/mifi/mifiStatus/export").submit();
						changeDisabled($("#isChannelAdmin").val());
						$("#searchForm").attr("action","${ctx}/mifi/mifiStatus/");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			$("#btnExport2").click(function(){
				top.$.jBox.confirm("确认要导出使用记录吗？","系统提示",function(v,h,f){
					if(v == "ok"){
						var dis = 
						changeDisabled(false);
						$("#searchForm").attr("action","${ctx}/mifi/mifiStatus/export2").submit();
						changeDisabled($("#isChannelAdmin").val());
						$("#searchForm").attr("action","${ctx}/mifi/mifiStatus/");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			// 查询
			$('#btnSubmit').click(function() {
				if (searchFlag) {
					var imei = $('#imei').val();
					if (isEmpty(imei)) {
						top.$.jBox.info('请输入设备序列号', '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						return false;
					}
					$.jBox.tip("正在查询...", 'loading', {persistent: true});
					$(this).attr("disabled",true);
					$('#searchForm').submit();
					searchFlag = false;
				} 
			});
			
			// 获取国家名称
			$('.countryName').click(function() {
				var mcc = $(this).attr('data-mcc');
				var url = '${ctx}/mifi/mifiStatus/getCountryNameByMcc';
				$.post(url, {mcc:mcc}, function(data) {
					if (data.code == '1') {
						top.$.jBox.info(data.countryName, '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						return false;
					} else {
						top.$.jBox.info(data.msg, '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						return false;
					}
				});
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
	    	return false;
	    }
		
		function changeDisabled(f){
			if(f){
				$("#sourceType").attr("disabled",true);
			}else{
				$("#sourceType").removeAttr("disabled");
			}
			return true;
		}
		
		// 取卡信息
		function getCardInfo(simBankId, simId) {
			var url = '${ctx}/mifi/cardBasicInfo/cardInfo.json';
			$.post(url, {simBankId:simBankId, simId:simId}, function(data) {
				if ('success' == data.status) {
					var msg = [];
			        msg.push('<p>卡号：'+data.iccId+'</p>');
			        msg.push('<p>卡状态：'+data.simStatus+'</p>');
			        msg.push('<p>卡类型：'+data.type+'</p>');
					top.$.jBox.info(msg.join(''), '卡(' + simBankId + '_' +simId + ')基本信息');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				} else {
					top.$.jBox.info(data.message, '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
			}, 'json');
		}
	</script>
<style type="text/css">
#tableDiv {overflow:auto;}
table th {white-space: nowrap;}
table td {white-space: nowrap;}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/mifiStatus/">MIFI设备状态变化列表</a></li>
	</ul>
	<form:form id="searchForm" action="${ctx}/mifi/mifiStatus/"
		method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<input id="initTag" name="initTag" type="hidden" value="${initTag}" />
		<input id="isChannelAdmin" name="isChannelAdmin" type="hidden" value="${isChannelAdmin}" />
		<div>
			<label>设备序列号：</label><input id="imei" name="imei" type="text" maxlength="50" class="input-small required" value="${imei}" /> &nbsp; 
			<label>设备批次号：</label><input id="bath" name="bath" type="text" maxlength="50" class="input-small required" value="${bath}" /> &nbsp; 
			<label>代理商：</label> 
			<select id="sourceType" name="sourceType" class="input-small" <c:if test="${isChannelAdmin}">disabled="true"</c:if> >
				<option value="">--请选择--</option>
				<c:forEach
					items="${fns:getListByTableAndWhere('om_channel','channel_name_en','channel_name',' and del_flag = 0 ')}"
					var="sourceTypeValue">
					<option value="${sourceTypeValue.channel_name_en}"
						<c:if test="${sourceTypeValue.channel_name_en==sourceType}">selected</c:if>>${sourceTypeValue.channel_name}</option>
				</c:forEach>
			</select>
			&nbsp; <label>设备状态：</label> 
			<select id="ueStatus" name="ueStatus" class="input-medium">
				<option value="">--请选择--</option>
				<c:forEach items="${fns:getDictList('mifi_uestatus')}" var="mifiUeStatus">
					<option value="${mifiUeStatus.value}"
						<c:if test="${mifiUeStatus.value==ueStatus}">selected</c:if>>${mifiUeStatus.value}|${mifiUeStatus.label}</option>
				</c:forEach>
			</select>
			&nbsp; <label>国家：</label>
			<select id="mcc" name="mcc"  class="input-small">
				<option value="">--请选择--</option>
				<c:forEach var="item"  items="${mccList }">
					<option value="${item[0] }" <c:if test="${item[0] == mcc }">selected</c:if> >${item[1] }</option>
				</c:forEach>
			</select>
			<div style="margin-top: 10px;">
			<label>开始日期：</label><input id="beginDate" name="beginDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${beginDate}"
				onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />
			<label>结束日期：</label><input id="endDate" name="endDate" type="text"
				readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${endDate}"
				onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />&nbsp;&nbsp;
				<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="return changeDisabled(false);"/> &nbsp;
				<input id="btnExport" class="btn btn-primary" type="button" value="导出" />&nbsp;
				<shiro:hasPermission name="mifi:mifiStatus:export">
				<input id="btnExport2" class="btn btn-primary" type="button" value="使用记录导出" />
				</shiro:hasPermission>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}" />
	<div id="tableDiv">
		<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width:100%">
			<thead>
				<tr>
					<th>设备序列号</th>
					<th>代理商</th>
					<th>设备状态</th>
					<th>外设连接数量</th>
					<th>设备使用总流量(M)</th>
					<th>时间</th>
					<th>所在地区中文名</th>
					<th>所在地区英文名</th>
					<th>卡槽编号</th>
					<th>卡槽位置</th>
					<th>设备服务器连接状态</th>
					<th>设备电量</th>
					<th>网络制式</th>
					<th>主卡被网络拒绝原因</th>
					<th>主卡注册国家区域码</th>
					<th>主卡注册运营商编码</th>
					<th>主卡位置跟踪区域码</th>
					<th>主卡所处基站编号</th>
					<th>主卡接收信号强度</th>
					<th>副卡注册国家区域码</th>
					<th>副卡注册运营商编码</th>
					<th>副卡位置跟踪区域码</th>
					<th>副卡所处基站编号</th>
					<th>副卡接收信号强度</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${page.list}" var="a">
					<tr>
						<td>${a.imei}</td>
						<td>${a.source_type}|${fns:getLabelByTableAndWhere('om_channel','channel_name_en','channel_name',' and del_flag = 0 ',a.source_type)}</td>
						<td>${a.uestatus}|${fns:getDictLabel(a.uestatus, 'mifi_uestatus', '未配置状态')}</td>
						<td>${a.devices}</td>
						<td>${a.datainfo}</td>
						<td><fmt:formatDate value="${a.stamp_created }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td>
							<c:if test="${empty a.country_name_cn }">
								${a.mcc }|<a href="javascript:void(0);" data-mcc="${a.mcc }" class="countryName">获取国家</a>
							</c:if>
							${a.mcc }|${a.country_name_cn}
						</td>
						<td>${a.country_name_en}</td>
						<td>${a.sim_bank_id}</td>
						<td><a style="cursor: pointer;" onclick="javascript:getCardInfo('${a.sim_bank_id}', '${a.sim_id}');">${a.sim_id}</a></td>
						<td>${a.nwstatus}</td>
						<td>${a.power_info}</td>
						<td>${a.reg_info}</td>
						<td>${a.rej_cause_9215}</td>
						<td>${a.mcc_9215}</td>
						<td>${a.mnc_9215}</td>
						<td>${a.tac_9215}</td>
						<td>${a.callid_9215}</td>
						<td>${a.rssi_9215}</td>
						<td>${a.mcc_6200}</td>
						<td>${a.mnc_6200}</td>
						<td>${a.tac_6200}</td>
						<td>${a.cellid_6200}</td>
						<td>${a.rssi_6200}</td>
					</tr>
				</c:forEach>
			</tbody>
			<tr>
					<th>设备序列号</th>
					<th>代理商</th>
					<th>设备状态</th>
					<th>外设连接数量</th>
					<th>设备使用总流量(M)</th>
					<th>时间</th>
					<th>所在地区中文名</th>
					<th>所在地区英文名</th>
					<th>卡槽编号</th>
					<th>卡槽位置</th>
					<th>设备服务器连接状态</th>
					<th>设备电量</th>
					<th>网络制式</th>
					<th>主卡被网络拒绝原因</th>
					<th>主卡注册国家区域码</th>
					<th>主卡注册运营商编码</th>
					<th>主卡位置跟踪区域码</th>
					<th>主卡所处基站编号</th>
					<th>主卡接收信号强度</th>
					<th>副卡注册国家区域码</th>
					<th>副卡注册运营商编码</th>
					<th>副卡位置跟踪区域码</th>
					<th>副卡所处基站编号</th>
					<th>副卡接收信号强度</th>
				</tr>
		</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>