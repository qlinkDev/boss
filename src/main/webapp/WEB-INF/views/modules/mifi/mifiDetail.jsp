<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>MIFI设备详细信息</title>
	<style type="text/css">
		.errorBlock{ background-color: #FFC6A5; border: solid 1px #ff0000; color: #ff0000; margin: 10px 10px 0 10px; padding:7px; font-weight: bold; }
		div.msg-div{ padding: 10px; }
        div.msg-div p{ padding: 0px; margin:5px 0 0 0; }
	</style>
	<style type="text/css">
	    #orderInfo th{
			width:10%;
		}
		#orderInfo td{
			width:40%;
		}
		#orderInfo {
			word-break:break-all;
			word-wrap:break-word;
		}
		.table-title {
			padding-bottom: 9px;
		}
	</style>
	<script type="text/javascript">
			// 查看设备状态
			function statusList(sn, date) {
				$('#statusFormSn').val(sn);
				$('#statusFormBeginDate').val(date);
				$('#statusFormEndDate').val(date);
				$('#statusForm').submit();
			}
	</script>
</head>
<body>
	<div class="table-title">
       <h5>设备基本信息：</h5>
    </div>
	<table id="deviceInfo" class="table table-striped table-bordered table-condensed">
		<tbody>
			<tr>
				<th>设备序列号</th>
				<td>${mifiDevice.sn}</td>
				<th>设备IMEI</th>
				<td colspan="3">${mifiDevice.imei}</td>
			</tr>
			<tr>
				<th>设备版本</th>
				<td>${mifiDevice.MAIN_VERSION}</td>
				<th>APN3G版本</th>
				<td>${mifiDevice.APN_3G_VERSION}</td>
				<th>APN4G版本</th>
				<td>${mifiDevice.APN_4G_VERSION}</td>
			</tr>
			<tr>
				<th>设备归属类型</th>
				<td>${mifiDevice.owner_type}|${fns:getDictLabel(mifiDevice.owner_type, 'mifi_owner_type', '未知归属类型')}</td>
				<th>设备归属代理商</th>
				<td><c:if test="${mifiDevice.owner_type ne '2'}">${mifiDevice.source_type}|${fns:getLabelByTable('om_channel','channel_name_en','channel_name',mifiDevice.source_type)}</c:if></td>
				<th>流量(M)</th>
				<td>${mifiDevice.datainfo}</td>
			</tr>
			<tr>
				<th>设备最后状态</th>
				<td><a href="javascript:void(0);" onclick="javascript:statusList('${mifiDevice.sn}', '<fmt:formatDate value="${mifiDevice.last_time }" pattern="yyyy-MM-dd"/>');">${mifiDevice.last_status}|${fns:getDictLabel(mifiDevice.last_status, 'mifi_uestatus', '未配置状态')}</a></td>
				<th>设备最后状态时间</th>
				<td><fmt:formatDate value="${mifiDevice.last_time }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<th>设备最后所在地区</th>
				<td>${mifiDevice.used_mcc}|${fns:getLabelByTable('mcc_def','mcc','country_name_cn',mifiDevice.used_mcc)}</td>
			</tr>
			<tr>
				<th>UEALLOWED</th>
				<td>${mifiDevice.UEALLOWED}</td>
				<th>UEALLOWEDMCC</th>
				<td>${mifiDevice.UEALLOWEDMCC}</td>
				<th>OWNER_MCC</th>
				<td>${mifiDevice.OWNER_MCC}</td>
			</tr>
			<tr>
				<th>SSID</th>
				<td>${mifiDevice.ssid}</td>
				<th>PASSWORD</th>
				<td colspan="3">${mifiDevice.pwd}</td>
			</tr>
			<tr>
				<th>供应商</th>
				<td>${mifiDevice.supplier}</td>
				<th>批次号</th>
				<td colspan="3">${mifiDevice.bath}</td>
			</tr>
			<tr>
				<th>序列号</th>
				<td>${mifiDevice.unique_no}</td>
				<th>入库时间</th>
				<td>${mifiDevice.in_time}</td>
				<th>入库用户</th>
				<td>${fns:getUserById(mifiDevice.in_user).name}</td>
			</tr>
		</tbody>
	</table>
	
	<div class="table-title">
       <h5>SIM卡信息：</h5>
    </div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<tbody>
			<tr>
				<th>卡号</th>
				<td>${sim.sn}</td>
				<th>卡状态</th>
				<td><c:if test="${!empty sim.USIMSTATUS }">${sim.USIMSTATUS}|${fns:getDictLabel(sim.USIMSTATUS, 'usimstatus', '未配置状态')}</c:if></td>
				<th>卡类型</th>
				<td><c:if test="${!empty sim.type }">${sim.type}|${fns:getLabelByTable('sim_card_type', 'card_type','card_type_name', sim.type)}</c:if></td>
				<th>激活时间</th>
				<td><fmt:formatDate value="${sim.stamp_firstactive}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			</tr>
			<tr>
				<th>卡槽编号</th>
				<td>${sim.sim_bank_id}</td>
				<th>卡槽位置</th>
				<td>${sim.sim_id}</td>
				<th>卡总流量(M)</th>
				<td>${sim.DATACAP}</td>
				<th>卡已使用流量(M)</th>
				<td>${sim.DATAUSED}</td>
			</tr>
			<tr>
				<th>imsi</th>
				<td>${sim.imsi}</td>
				<th>iccid</th>
				<td>${sim.iccid}</td>
				<th>pin</th>
				<td>${sim.pin}</td>
				<th>puk</th>
				<td>${sim.puk}</td>
			</tr>
			<tr>
				<th>批次号</th>
				<td>${sim.bath}</td>
				<th>供应商</th>
				<td>${sim.supplier}</td>
				<th>入库时间</th>
				<td>${sim.create_time}</td>
				<th>入库用户</th>
				<td>${sim.create_user}</td>
			</tr>
		</tbody>
	</table>
	
	<div class="table-title">
       <h5>设备订单信息：</h5>
    </div>
	<table id="orderInfo" class="table table-striped table-bordered table-condensed">
		<thead>
				<tr>
					<th>订单编号</th>
					<th>订单状态</th>
					<th>代理商</th>
					<th>地区</th>
					<th>备货状态</th>
					<th>SIM卡数量</th>
					<th>行程开始时间</th>
					<th>行程结束时间</th>
					<th>订单时间</th>
					<th>订单完成时间</th>
					<th>订单金额</th>
					<th>设备序列号</th>
					<th>地区中文名</th>
					<th>地区英文名</th>
				</tr>
			</thead>
			<tbody>
				<c:if test="${empty mifiOrder }"><tr><td colspan="14">暂无订单信息</td></tr></c:if>
				<c:forEach items="${mifiOrder}" var="a">
					<tr>
						<td>${a.out_order_id}</td>
						<td>${a.order_status }|${fns:getDictLabel(a.order_status, 'mifi_order_status', '未知状态')}</td>
						<td>${a.source_type}|${fns:getLabelByTableAndWhere('om_channel','channel_name_en','channel_name',' and del_flag = 0 ',a.source_type)}</td>
						<td>${a.allowed_mcc}</td>
						<td>${a.stock_status}|${fns:getDictLabel(a.stock_status, 'order_stock_status', '未知状态')}</td>
						<td>${a.equipment_cnt}</td>
						<td><fmt:formatDate value="${a.start_date}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td><fmt:formatDate value="${a.end_date}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td><fmt:formatDate value="${a.out_order_time}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td><fmt:formatDate value="${a.finish_time}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td>${a.reference_total_price}</td>
						<td>${a.dsn}</td>
						<td>${a.allowed_mcc_cn}</td>
						<td>${a.allowed_mcc_en}</td>
					</tr>
				</c:forEach>
			</tbody>
	</table>
	
	<div class="hide">
		<form id="statusForm" action="${ctx}/mifi/mifiStatus/" method="post" class="breadcrumb form-search">
			<input id="statusFormSn" name="imei" type="hidden" />
			<input id="statusFormBeginDate" name="beginDate" type="hidden" />
			<input id="statusFormEndDate" name="endDate" type="hidden" />
			<input name="initTag" type="hidden" value="yes" />
		</form>
	</div>
</body>
</html>
