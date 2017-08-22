<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>SIM卡有效期流量查询</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	function page(n,s){
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
    	return false;
    }
	
	$(document).ready(function() {
		// 查看版本
		$('#btnSubmit').click(function() {
			// 设备编号
			var iccid = $('#iccid').val();
			var type = $('#typeArr option:selected').val();
			if (!iccid && !type) {
				top.$.jBox.info('请输入卡号或卡类型', '系统提示');
				top.$('.jbox-body .jbox-icon').css('top','55px');
				return false;
			}
			$('#btnSubmit').attr("disabled", "");
			$('#searchForm').submit();
	    	
		});
		
		$('#btnExport').click(function() {
			top.$.jBox.confirm("确认导出？", "操作提示", function(v, h, f) {
				if (v == "ok") {
					$("#searchForm").attr('action', "${ctx}/mifi/simNode/export");
					$("#searchForm").submit();
					$("#searchForm").attr('action', "${ctx}/mifi/simNode/list");
				}
			}, {buttonsFocus : 1});
			top.$('.jbox-body .jbox-icon').css('top', '55px');
			top.$('.jbox').css('top', '180px');
		});
		
		var typeArr = '${typeArr}';
		if(!!typeArr){
			typeArr = eval('('+ typeArr+ ')');
			$('#typeArr').select2().val(typeArr).trigger("change");
		}
	});
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/simNode/init">SIM卡有效期流量列表</a></li>
	</ul>
	<form:form id="searchForm" action="${ctx}/mifi/simNode/list"
		method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<div>
		<label>卡号：</label>
		<input id="iccid" name="iccid" type="text" class="input-medium" value="${iccid}" placeholder="SIM卡背面20位数字" />
		&nbsp;<label>卡类型：</label>
		<select id="typeArr" name="typeArr" class="input-xxlarge" multiple="multiple">
			<option value="null">未绑定卡</option>
			<c:forEach items="${fns:getListByTable('sim_card_type','card_type','card_type_name')}" var="cardTypeValue">
				<option value="${cardTypeValue.card_type}">${cardTypeValue.card_type_name}</option>
			</c:forEach>
		</select> 
		&nbsp; <label>有效天数下限：</label><input id="minRemainValidDay" name="minRemainValidDay" type="text"
			class="input-small" value="${minRemainValidDay}" />
		&nbsp; <label>有效天数上限：</label><input id="maxRemainValidDay" name="maxRemainValidDay" type="text"
			class="input-small" value="${maxRemainValidDay}" />
		&nbsp; <label>剩余高速流量下限(M)：</label><input id="minRemainCap" name="minRemainCap" type="text"
			class="input-small" value="${minRemainCap}" />
		&nbsp; <label>剩余高速流量上限(M)：</label><input id="maxRemainCap" name="maxRemainCap" type="text"
			class="input-small" value="${maxRemainCap}" />
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
		&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出" />
		</div>
	</form:form>
	<tags:message content="${message}" />
	<table id="contentTable"
		class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>卡号</th>
				<th>卡类型</th>
				<th>卡槽编号</th>
				<th>卡槽位置</th>
				<th>剩余有效天数(9999:永久有效)</th>
				<th>总有效天数</th>
				<th>首次使用时间</th>
				<th>激活时间</th>
				<th>总高速流量(M)</th>
				<th>已使用高速流量(M)</th>
				<th>剩余高速流量(M)</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="a">
				<tr>
					<td>${a.iccid}</td>
					<td><c:if test="${!empty a.type }">${a.type}|${fns:getLabelByTable('sim_card_type', 'card_type','card_type_name', a.type)}</c:if></td>
					<td>${a.simbankid}</td>
					<td>${a.simid}</td>
					<td>${a.remainValidDay}</td>
					<td>${a.SIMCARDVALIDDAY}</td>
					<td>${a.firstUseTime}</td>
					<td>${a.stamp_firstactive}</td>
					<td>${a.dataCap}</td>
					<td>${a.usedCap}</td>
					<td>${a.remainCap}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>