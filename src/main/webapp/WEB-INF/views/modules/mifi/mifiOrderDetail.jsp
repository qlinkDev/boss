<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单详情</title>
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
	<c:if test="${'fail' eq result }">
	<div class="table-title">
       <h5>获取订单详情失败!</h5>
    </div>
	</c:if>
	<c:if test="${'success' eq result }">
	<div class="table-title">
       <h5>订单基本信息：</h5>
    </div>
	<table id="orderInfo" class="table table-striped table-bordered table-condensed">
		<tbody>
			<tr>
				<th>订单编号</th>
				<td>${order.out_order_id}</td>
				<th>订单状态</th>
				<td>${order.order_status }|${fns:getDictLabel(order.order_status, 'mifi_order_status', '未知状态')}</td>
				<th>备货状态</th>
				<td>${order.stock_status}|${fns:getDictLabel(order.stock_status, 'order_stock_status', '未知状态')}</td>
			</tr>
			<tr>
				<th>订单设备</th>
				<td colspan="3">${order.dsn}</td>
				<th>SIM卡数量</th>
				<td>${order.equipment_cnt}</td>
			</tr>
			<tr>
				<th>代理商</th>
				<td colspan="3">${order.source_type}|${fns:getLabelByTableAndWhere('om_channel','channel_name_en','channel_name',' and del_flag = 0 ',order.source_type)}</td>
				<th>订单金额</th>
				<td>${order.reference_total_price}</td>
			</tr>
			<tr>
				<th>行程开始时间</th>
				<td><fmt:formatDate value="${order.start_date}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<th>行程结束时间</th>
				<td><fmt:formatDate value="${order.end_date}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<th>订单时间</th>
				<td><fmt:formatDate value="${order.out_order_time}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			</tr>
			<tr>
				<th>订单完成时间</th>
				<td colspan="5"><fmt:formatDate value="${order.finish_time}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			</tr>
			<tr>
				<th>地区MCC</th>
				<td colspan="5">${order.allowed_mcc}</td>
			</tr>
			<tr>
				<th>地区中文名</th>
				<td colspan="5">${order.allowed_mcc_cn}</td>
			</tr>
			<tr>
				<th>地区英文名</th>
				<td colspan="5">${order.allowed_mcc_en}</td>
			</tr>
		</tbody>
	</table>
	
	<c:if test="${!empty customer }">
	<div class="table-title">
       <h5>订单客户信息：</h5>
    </div>
	<table id="customerTable" class="table table-striped table-bordered table-condensed">
		<tbody>
			<tr>
				<th>客户姓名</th>
				<td>${customer.name}</td>
				<th>电话号码</th>
				<td>${customer.phone}</td>
				<th>客户邮箱</th>
				<td>${customer.email}</td>
			</tr>
			<tr>
				<th>护照编号</th>
				<td colspan="2">${customer.passportNo}</td>
				<th>护照拼音</th>
				<td colspan="2">${customer.passportPy}</td>
			</tr>
		</tbody>
	</table>
	</c:if>
	</c:if>
	
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
