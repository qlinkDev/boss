<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>MIFI订单查询</title>
	<meta name="decorator" content="default" />
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		#tableDiv {overflow:auto;}
		table th {white-space: nowrap;}
		table td {white-space: nowrap;}
	}
	</style>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function() {
			// 导出
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出MIFI订单数据吗？", "系统提示", function(v,h,f){
					if(v == "ok"){
						var pageSizeDef = $("#pageSize").val();
						$("#pageSize").val(-1);
						$("#searchForm").attr("action","${ctx}/mifi/mifiOrderList/export").submit();
						$("#searchForm").attr("action","${ctx}/mifi/mifiOrderList/list");
						$("#pageSize").val(pageSizeDef);
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			//查询条件清空
			$('#btnClear').click(function(){
				$("#outOrderId").val('');
				$("#dsn").val('');
				$("#allowedMcc option:checked").attr("selected", false);
				$("#s2id_allowedMcc a span").text('--请选择--');
				$("#orderStarting option:checked").attr("selected", false);
				$("#s2id_orderStarting a span").text('--请选择--');
				$("#sourceType option:checked").attr("selected", false);
				$("#s2id_sourceType a span").text('--请选择--');
				$("#orderStatus option:checked").attr("selected", false);
				$("#s2id_orderStatus a span").text('--请选择--');
				$("#stockStatus option:checked").attr("selected", false);
				$("#s2id_stockStatus a span").text('--请选择--');
				$("#outOrderTimeStart").val('');
				$("#outOrderTimeEnd").val('');
				$("#startDateBegin").val('');
				$("#startDateEnd").val('');
				$("#endDateBegin").val('');
				$("#endDateEnd").val('');
			});
			
		});
		
		function page(n, s) {
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}
		
		// 订单延期
		function delayOrder(id) {
			$('#orderId').val(id);
			$.jBox($("#delayOrderBox").html(), {title:"订单延期", submit: delayOrderSubmit, bottomText: '请选择延期日期'});
		}
		var delayOrderSubmit = function (v, h, f) {
		    if (f.endDate == '') {
		        $.jBox.tip("请选择延期日期。", 'error', { focusId: "endDate" }); // 关闭设置 endDate 为焦点
		        return false;
		    }
		    var url = '${ctx}/mifi/mifiOrderList/delayOrder.json';
			$.post(
				url, 
				{
					orderId:f.orderId,
					endDate:f.endDate
				}, 
				function(data) {
					if (data.code == '1') {
						top.$.jBox.info(data.msg, '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						$('#searchForm').submit();
					} else if (data.code == '-2') {
						var $rechargeDiv = $('<div style="margin: 10px; text-align: center;"><p>'+data.msg+'</p><input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   充    值   " /></div>')
	        		    $('#rechargeForm').html($rechargeDiv); 
						$.jBox($("#rechargeBox").html(), {
							title : "余额不足",
							buttons : {
								"关闭" : true
							}
						});
					} else {
						if (isEmpty(data.msg))
							top.$.jBox.info('订单延期失败', '系统提示');
						else
							top.$.jBox.info(data.msg, '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
					}
				}
			);
		};
		
		// 订单取消
		function cancelOrder(id) {
			top.$.jBox.confirm("确认要取消该订单吗？", "系统提示", function(v,h,f){
				if(v == "ok"){
					var url = '${ctx}/mifi/mifiOrderList/cancelOrder.json';
					$.post(
						url, 
						{
							orderId:id
						}, 
						function(data) {
							if (data.code == '1') {
								top.$.jBox.info(data.msg, '系统提示');
								top.$('.jbox-body .jbox-icon').css('top','55px');
								$('#searchForm').submit();
							} else {
								if (isEmpty(data.msg))
									top.$.jBox.info('订单取消失败', '系统提示');
								else
									top.$.jBox.info(data.msg, '系统提示');
								top.$('.jbox-body .jbox-icon').css('top','55px');
							}
						}
					);
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		
		// 订单完成
		function finishOrder(id) {
			top.$.jBox.confirm("确认设置该订单为完成吗？", "系统提示", function(v,h,f){
				if(v == "ok"){
					var url = '${ctx}/mifi/mifiOrderList/finishOrder.json';
					$.post(
						url, 
						{
							orderId:id
						}, 
						function(data) {
							if (data.code == '1') {
								top.$.jBox.info(data.msg, '系统提示');
								top.$('.jbox-body .jbox-icon').css('top','55px');
								$('#searchForm').submit();
							} else {
								if (isEmpty(data.msg))
									top.$.jBox.info('设置订单完成失败', '系统提示');
								else
									top.$.jBox.info(data.msg, '系统提示');
								top.$('.jbox-body .jbox-icon').css('top','55px');
							}
						}
					);
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		
		// 订单删除
		function deleteOrder(id) {
			top.$.jBox.confirm("确认要删除该订单吗？", "系统提示", function(v,h,f){
				if(v == "ok"){
					var url = '${ctx}/mifi/mifiOrderList/deleteOrder.json';
					$.post(
						url, 
						{
							orderId:id
						}, 
						function(data) {
							if (data.code == '1') {
								top.$.jBox.info(data.msg, '系统提示');
								top.$('.jbox-body .jbox-icon').css('top','55px');
								$('#searchForm').submit();
							} else {
								if (isEmpty(data.msg))
									top.$.jBox.info('订单删除失败', '系统提示');
								else
									top.$.jBox.info(data.msg, '系统提示');
								top.$('.jbox-body .jbox-icon').css('top','55px');
							}
						}
					);
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		
		//跳入设备列表页面
		function mifeDevice(sn){
			$('#sn').val(sn);
			$('#mifiForm').submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/mifiOrderList/">MIFI订单列表</a></li>
	</ul>
	<form:form id="searchForm" action="${ctx}/mifi/mifiOrderList/"
		method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<input id="initTag" name="initTag" type="hidden" value="${initTag}" />
		<div>
			<label>订单编号：</label>
			<input id="outOrderId" name="outOrderId" type="text" maxlength="50" class="input-medium" value="${outOrderId}" /> 
			<label>设备编号：</label>
			<input id="dsn" name="dsn" type="text" maxlength="50" class="input-medium" value="${empty outOrderId ? dsn : ''}" /> 
			<label>目的地：</label>
			<select id="allowedMcc" name="allowedMcc"  class="input-small">
				<option value="">--请选择--</option>
				<c:forEach var="item"  items="${mccList }">
					<option value="${item[0] }" <c:if test="${item[0] == allowedMcc }">selected</c:if> >${item[1] }</option>
				</c:forEach>
			</select>
			<label>订单进行中：</label> 
			<select id="orderStarting" name="orderStarting" class="input-small">
				<option value="">--请选择--</option>
				<option value="1"  <c:if test="${orderStarting == 1 }">selected</c:if>>是</option>
			</select>
			<label>订单状态：</label> 
			<select id="orderStatus" name="orderStatus" class="input-small">
				<option value="">--请选择--</option>
				<c:forEach items="${fns:getDictList('mifi_order_status')}"
					var="orderStockStatus">
					<option value="${orderStockStatus.value}"
						<c:if test="${orderStockStatus.value==orderStatus}">selected</c:if>>${orderStockStatus.label}</option>
				</c:forEach>
			</select>
			<label>备货状态：</label> 
			<select id="stockStatus" name="stockStatus" class="input-small">
				<option value="">--请选择--</option>
				<c:forEach items="${fns:getDictList('order_stock_status')}"
					var="orderStockStatus">
					<option value="${orderStockStatus.value}"
						<c:if test="${orderStockStatus.value==stockStatus}">selected</c:if>>${orderStockStatus.label}</option>
				</c:forEach>
			</select> 
			<div style="margin-top: 10px;">
			<label>下单时间：</label> 
			<input id="outOrderTimeStart" name="outOrderTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate required" value="${outOrderTimeStart }" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />
			到
			<input id="outOrderTimeEnd" name="outOrderTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate required" value="${ outOrderTimeEnd}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />&nbsp;&nbsp; 
			<label>行程开始日期：</label> 
			<input id="startDateBegin" name="startDateBegin" type="text" readonly="readonly" maxlength="20" class="input-small Wdate required" value="${startDateBegin}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />
			到
			<input id="startDateEnd" name="startDateEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate required" value="${startDateEnd}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />&nbsp;&nbsp; 
			<label>行程结束日期：</label> 
			<input id="endDateBegin" name="endDateBegin" type="text" readonly="readonly" maxlength="20" class="input-small Wdate required" value="${endDateBegin }" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />
			到
			<input id="endDateEnd" name="endDateEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate required" value="${ endDateEnd}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />&nbsp;&nbsp; 
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" />&nbsp;
			<input id="btnClear" class="btn btn-primary" type="button" value="清空"/>&nbsp;
			<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}" />
	<div id="tableDiv">
		<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width:100%">
			<thead>
				<tr>
					<th>订单编号</th>
					<th>订单类型</th>
					<th>订单状态</th>
					<th>速度模式</th>
					<th>地区</th>
					<th>备货状态</th>
					<th>SIM卡数量</th>
					<th>行程开始时间</th>
					<th>行程结束时间</th>
					<th>订单时间</th>
					<th>订单完成时间</th>
					<th>取消/终止时间</th>
					<th>订单金额</th>
					<th>订单流量(M)</th>
					<th>设备序列号</th>
					<th>地区中文名</th>
					<th>地区英文名</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${page.list}" var="a">
					<tr>
						<td>${a.out_order_id}</td>
						<td>${a.order_status }|${fns:getDictLabel(a.order_status, 'mifi_order_status', '未知类型')}</td>
						<td>${a.order_type }|${fns:getDictLabel(a.order_type, 'mifi_order_type', '未知状态')}</td>
						<td>${a.limit_speed_flag }|${fns:getDictLabel(a.limit_speed_flag, 'order_speed_flag', '未知模式')}</td>
						<td>${a.allowed_mcc}</td>
						<td>${a.stock_status}|${fns:getDictLabel(a.stock_status, 'order_stock_status', '未知状态')}</td>
						<td>${a.equipment_cnt}</td>
						<td><fmt:formatDate value="${a.start_date}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td><fmt:formatDate value="${a.end_date}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td><fmt:formatDate value="${a.out_order_time}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td><fmt:formatDate value="${a.finish_time}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td><fmt:formatDate value="${a.cancel_time}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td>${a.reference_total_price}</td>
						<td>${a.flow}</td>
						<td>
							<c:forEach items="${fns:convertStringToList(a.dsn,',')}"  var="sn">
								<a href="javascript:void(0);"  onclick="mifeDevice('${sn}')"  title="查看设备">${sn }</a>
							</c:forEach>
						</td>
						<td>${a.allowed_mcc_cn}</td>
						<td>${a.allowed_mcc_en}</td>
						<td>
							<c:if test="${a.order_status=='1' || a.order_status=='2'}">
								<shiro:hasPermission name="mifi:mifiOrder:delay">
								<a href="javascript:void(0);" onclick="javascript:delayOrder('${a.order_id}');">延期</a>
								</shiro:hasPermission>
								<shiro:hasPermission name="mifi:mifiOrder:cancel">
								<a href="javascript:void(0);" onclick="javascript:cancelOrder('${a.order_id}');">取消</a>
								</shiro:hasPermission>
							</c:if>
							<c:if test="${a.order_status=='1'}">
								<shiro:hasPermission name="mifi:mifiOrder:finish">
								<a href="javascript:void(0);" onclick="javascript:finishOrder('${a.order_id}');">完成</a>
								</shiro:hasPermission>
							</c:if>
							<c:if test="${a.order_status=='9'}">
								<shiro:hasPermission name="mifi:mifiOrder:del">
								<a href="javascript:void(0);" onclick="javascript:deleteOrder('${a.order_id}');">删除</a>
								</shiro:hasPermission>
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div class="pagination">${page}</div>
	
	<!-- 订单延期 S -->	
	<div id="delayOrderBox" class="hide">
		<form id="delayOrderForm" action="${ctx}/mifi/mifiOrderList/delayOrder.json" method="post" style="padding-left:20px;text-align:center;">
			<input type="hidden" id="orderId" name="orderId" />
			<div class="msg-div">
				<p></p>
            	<div class="field">
            		<span>延期至：</span><input id="endDate" name="endDate" type="text" readonly="true" maxlength="20" class="input-small Wdate required" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
            	</div>
        	</div>
		</form>
	</div>
	<!-- 订单延期 E -->	
	<!-- 余额不足 S -->	
	<div id="rechargeBox" class="hide">
		<form id="rechargeForm" action="${ctx }/om/consumeRecord/recharge" method="post" target="_blank" class="form-search">
		</form>
	</div>
	<!-- 余额不足 S -->
	<!-- sn查询mifi设备 -->
	<div class="hide">
		<form id="mifiForm" action="${ctx}/mifi/mifiDevice/list" method="post">
			<input type="hidden"  id="sn" name="sn"  value="">
		</form>
	</div>
	<!-- sn查询mifi设备 -->
</body>
</html>