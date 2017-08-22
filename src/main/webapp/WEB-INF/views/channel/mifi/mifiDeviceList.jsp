<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>MIFI设备查询</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<style type="text/css">
.sort{color:#0663A2;cursor:pointer;}
#tableDiv {overflow:auto;}
table th {white-space: nowrap;}
table td {white-space: nowrap;}
</style>
<script type="text/javascript">
	$(document).ready(function() {
		// 设备数据导出
		$("#btnExport").click(function() {
			top.$.jBox.confirm("确认要导出设备数据吗？", "系统提示", function(v, h, f) {
				if (v == "ok") {
					$("#searchForm").attr("action", "${ctx}/mifi/mifiDevice/export").submit();
					$("#searchForm").attr("action", "${ctx}/mifi/mifiDevice/list");
				}
			}, {
				buttonsFocus : 1
			});
			top.$('.jbox-body .jbox-icon').css('top', '55px');
		});
		// 设备归属地修改
		$("#btnImportOwnerMcc").click(function() {
			$.jBox($("#importOwnerMccBox").html(), {
				title : "设备归属地修改",
				buttons : {
					"关闭" : true
				},
				bottomText : "导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"
			});
		});
	});
	
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
	
	// 查看设备状态
	function statusList(sn, date) {
		$('#statusFormSn').val(sn);
		$('#statusFormBeginDate').val(date);
		$('#statusFormEndDate').val(date);
		$('#statusForm').submit();
	}
	
	//修改设备使用地
	function modify(sn){
		var submit = function (v, h, f) {
		    
		    return true;
		};
		$.jBox("get:${ctx}/mifi/mifiDevice/getCountry?sn="+sn,{title:"修改设备使用地", width:800, persistent:true, buttons: { '关闭': 'ok' }});
	}
	
	//查看设备订单
	function mifiOrder(sn){
		$('#dsn').val(sn);
		$('#mifiOrderForm').submit();
	}
	
	//查看设备详情
	function detail(sn){
		$.jBox("get:${ctx}/mifi/mifiDevice/detail?sn="+sn,{title:"MIFI设备", width:'auto', persistent:true,showScrolling:true, buttons: { '关闭': 'ok' }});
	}
</script>
</head>
<body>
	<div id="importOwnerMccBox" class="hide">
		<form id="importForm" action="${ctx}/mifi/mifiDevice/importOwnerMcc" method="post" enctype="multipart/form-data" style="padding-left: 20px; text-align: center;" class="form-search" onsubmit="loading('正在导入，请稍等...');">
			<br /> <input id="uploadOwnerMccFile" name="file" type="file" style="width: 330px" /><br />
			<br /> <input id="btnImportOwnerMccSubmit" class="btn btn-primary" type="submit" value="   导    入   " /> 
			<a href="${ctx}/mifi/mifiDevice/download/template/ownerMcc">下载模板</a>
		</form>
	</div>
	
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/mifiDevice/list">MIFI设备列表</a></li>
	</ul>
	<form:form id="searchForm" action="${ctx}/mifi/mifiDevice/list"
		method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<input id="initTag" name="initTag" type="hidden" value="${initTag}" />
		<div>
			<label>设备序列号：</label>
			<input id="sn" name="sn" type="text" maxlength="50" class="input-small required" value="${sn}" /> 
			<label>批次号：</label>
			<input id="batchNo" name="bath" type="text" maxlength="50" class="input-small required" value="${bath}" />&nbsp;&nbsp; 
			<label>归属地：</label>
			<select id="ownerMcc" name="ownerMcc"  class="input-small">
				<option value="">--请选择--</option>
				<c:forEach var="item"  items="${mccList }">
					<option value="${item[0] }" <c:if test="${item[0] == allowedMcc }">selected</c:if> >${item[1] }</option>
				</c:forEach>
			</select>&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /> &nbsp;
			<input id="btnExport" class="btn btn-primary" type="button" value="导出" /> &nbsp;
			
			<shiro:hasPermission name="mifi:mifiDevice:ownerMcc">
			<input id="btnImportOwnerMcc" class="btn btn-primary" type="button" value="设备归属地修改"/>
			</shiro:hasPermission>
		</div>
	</form:form>
	<tags:message content="${message}" />
	<div id="tableDiv">
		<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width:100%">
			<thead>
				<tr>
					<th style="display: none">唯一标识</th>
					<th>设备序列号</th>
					<th>设备IMEI</th>
					<th>设备版本</th>
					<th>APN3G版本</th>
					<th>APN4G版本</th>
					<th>设备归属类型</th>
					<th>流量(M)</th>
					<th>设备最后状态</th>
					<th>设备最后状态时间</th>
					<th>设备最后所在地区</th>
					<th>UEALLOWED</th>
					<th>UEALLOWEDMCC</th>
					<th>OWNER_MCC</th>
					<th>SSID</th>
					<th>PASSWORD</th>
					<th>供应商</th>
					<th>批次号</th>
					<th>序列号</th>
					<th>入库时间</th>
					<th>入库用户</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${page.list}" var="a">
					<tr>
						<td style="display: none">${a.id}</td>
						<td><a href="javascript:void(0);"  onclick="mifiOrder('${a.sn}');" title="查看设备订单">${a.sn}</a></td>
						<td><a href="javascript:void(0);"  onclick="mifiOrder('${a.imei}');" title="查看设备订单">${a.imei}</a></td>
						<td>${a.MAIN_VERSION}</td>
						<td>${a.APN_3G_VERSION}</td>
						<td>${a.APN_4G_VERSION}</td>
						<td>${a.owner_type}|${fns:getDictLabel(a.owner_type, 'mifi_owner_type', '未知归属类型')}</td>
						<td>${a.datainfo}</td>
						<td><a href="javascript:void(0);" onclick="javascript:statusList('${a.sn}', '<fmt:formatDate value="${a.last_time }" pattern="yyyy-MM-dd"/>');">${a.last_status}|${fns:getDictLabel(a.last_status, 'mifi_uestatus', '未配置状态')}</a></td>
						<td><fmt:formatDate value="${a.last_time }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td>${a.used_mcc}|${fns:getLabelByTable('mcc_def','mcc','country_name_cn',a.used_mcc)}</td>
						<td>${a.UEALLOWED}</td>
						<td>${a.UEALLOWEDMCC}</td>
						<td>${a.OWNER_MCC}|${fns:getLabelByTable('mcc_def','mcc','country_name_cn',a.OWNER_MCC)}</td>
						<td>${a.ssid}</td>
						<td>${a.pwd}</td>
						<td>${a.supplier}</td>
						<td>${a.bath}</td>
						<td>${a.unique_no}</td>
						<td>${a.in_time}</td>
						<td>${fns:getUserById(a.in_user).name}</td>
						<td>
							<shiro:hasPermission name="mifi:mifiDevice:edit">
							<a href="${ctx}/mifi/mifiDevice/form?id=${a.id}">修改</a>
							</shiro:hasPermission>
							
							<shiro:hasPermission name="mifi:mifiDevice:usePlace">
							&nbsp;<a href="javascript:void(0);" onclick="modify(${a.sn});">修改设备使用地</a>
							</shiro:hasPermission>
						
							&nbsp;<a href="javascript:void(0);" onclick="detail(${a.sn})">查看详情</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div class="pagination">${page}</div>
	
	<form id="statusForm" action="${ctx}/mifi/mifiStatus/" method="post" class="breadcrumb form-search">
		<input id="statusFormSn" name="imei" type="hidden" />
		<input id="statusFormBeginDate" name="beginDate" type="hidden" />
		<input id="statusFormEndDate" name="endDate" type="hidden" />
		<input name="initTag" type="hidden" value="yes" />
	</form>
	<!-- 设备订单列表 -->
	<div class="hide">
		<form id="mifiOrderForm" action="${ctx }/mifi/mifiOrderList/list" method="post">
				<input  type="text"  id="dsn"  name="dsn" value=""/>
				<input id="initTag" name="initTag" type="hidden"  value=""/>
		</form>
	</div>
	<!-- 设备订单列表 -->
</body>
</html>