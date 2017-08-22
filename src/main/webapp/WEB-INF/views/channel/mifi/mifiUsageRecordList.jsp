<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备使用记录</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<style type="text/css">
		#tableDiv {overflow:auto;}
			table th {white-space: nowrap;}
			table td {white-space: nowrap;}
		}
		</style>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
	    	return false;
	    }
		//查询条件清空
		$(function(){
			$('#btnClear').click(function(){
				$("#deviceSn").val('');
				$("#type option:checked").attr("selected", false);
				$("#s2id_type a span").text('--请选择--');
				$("#result option:checked").attr("selected", false);
				$("#s2id_result a span").text('--请选择--');
				$("#startDate").val('');
				$("#endDate").val('');
			});
			
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出使用记录吗？","系统提示",function(v,h,f){
					if(v == "ok"){
						$("#searchForm").attr("action","${ctx}/mifi/usageRecord/export").submit();
						$("#searchForm").attr("action","${ctx}/mifi/usageRecord/");
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
		<li class="active"><a href="${ctx}/mifi/usageRecord/list">设备使用记录列表</a></li>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="mifiUsageRecordCondition" action="${ctx}/mifi/usageRecord/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="isChannelAdmin" name="isChannelAdmin" type="hidden" value="${isChannelAdmin}" />
		<label>设备编号：</label><form:input id="imei" path="imei" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>设备批次号：</label><form:input id="bath" path="bath" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>卡所属渠道：</label> 
		<form:select path="eqCardSourceType" class="input-medium required">
        	<form:option value="" label="请选择"/>
        	<form:options items="${fns:getChannelList()}" itemLabel="channelName" itemValue="channelNameEn" htmlEscape="false"/>
        </form:select>
		<label>国家：</label>
		<select id="eqCountryCode" name="eqCountryCode" class="input-small">
			<option value="">--请选择--</option>
			<c:forEach var="item"  items="${mccList }">
				<option value="${item[2] }" <c:if test="${item[2] == mifiUsageRecordCondition.eqCountryCode }">selected</c:if> >${item[1] }</option>
			</c:forEach>
		</select>
		<label>区域：</label>
		<select id="inMcc" name="inMcc"  class="input-small">
			<option value="">--请选择--</option>
			<c:forEach var="item"  items="${regionList}">
				<option value="${item.mcces}" <c:if test="${item.mcces == mifiUsageRecordCondition.inMcc}">selected</c:if>>${item.name}</option>
			</c:forEach>
		</select>
		<label>时间：</label>
		<input id="beginDate" name="beginDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${condition.beginDate}"
			onclick="WdatePicker({skin:'twoer',dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endDate\')||\'%y-%M-{%d-1}\'}', isShowToday : false});" />&nbsp;到
		<input id="endDate" name="endDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${condition.endDate}"
				onclick="WdatePicker({skin:'twoer',dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'beginDate\')}',maxDate:'%y-%M-{%d-1}',  isShowToday : false});" />	
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return changeDisabled(false);"/>&nbsp;
		<shiro:hasPermission name="	mifi:usageRecord:export">
		<input id="btnExport" class="btn btn-primary" type="button" value="使用记录导出" />
		</shiro:hasPermission>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<div id="tableDiv">
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>设备编号</th>
				<th>设备批次号</th>
				<th>设备所属渠道</th>
				<th>卡所属渠道</th>
				<th>订单编号</th>
				<th>MCC</th>
				<th>设备状态</th>
				<th>时间</th>
				<th>所在地区中文名</th>
				<th>所在地区英文名</th>
				<th>卡槽编号</th>
				<th>卡槽位置</th>
				<th>设备服务器连接状态</th>
				<th>设备电量</th>
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
				<th>外设连接数量</th>
				<th>设备使用总流量(M)</th>
				<th>费用(￥)</th>
				</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="record">
			<tr>
				<td>${record.imei}</td>
				<td>${record.bath}</td>
				<td>${record.sourceType}|${fns:getLabelByTableAndWhere('om_channel','channel_name_en','channel_name',' and del_flag = 0 ',record.sourceType)}</td>
				<td>${record.cardSourceType}|${fns:getLabelByTableAndWhere('om_channel','channel_name_en','channel_name',' and del_flag = 0 ',record.cardSourceType)}</td>
				<td>${record.outOrderId}</td>
				<td>${record.mcc}</td>
				<td>${record.uestatus}|${fns:getDictLabel(record.uestatus, 'mifi_uestatus', '未配置状态')}</td>
				<td><fmt:formatDate value="${record.stampCreated}" pattern="yyyy-MM-dd"/></td>
				<td>${record.countryNameCn}</td>
				<td>${record.countryNameEn}</td>
				<td>${record.simBankId}</td>
				<td>${record.simId}</td>
				<td>${record.nwstatus}</td>
				<td>${record.powerInfo}</td>
				<td>${record.mainRejCause}</td>
				<td>${record.mainMcc}</td>
				<td>${record.mainMnc}</td>
				<td>${record.mainTac}</td>
				<td>${record.mainCallid}</td>
				<td>${record.mainRssi}</td>
				<td>${record.additionalMcc}</td>
				<td>${record.additionalMnc}</td>
				<td>${record.additionalTac}</td>
				<td>${record.additionalCellid}</td>
				<td>${record.additionalRssi}</td>
				<td>${record.devices}</td>
				<td>${record.datainfo}</td>
				<td>${record.cost}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	<!-- 数据列表 E -->
	
	<!-- 分页 S -->
	<div class="pagination">${page}</div>
	<!-- 分页 E -->
</body>
</html>