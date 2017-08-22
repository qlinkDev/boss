<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>SIM卡用量统计</title>
	<meta name="decorator" content="default" />
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<style type="text/css">.sort{color:#0663A2;cursor:pointer;}</style>
	<script type="text/javascript">
		$(function() {
			// 查询
			$('#btnSubmit').click(function() {
				top.$.jBox.confirm("此操作耗时较长,占用服务器资源较大,有一定概率引起设备自爆,为了服务器及自身人身安全请慎重,是否继续？","系统提示",function(v,h,f){
					if(v == "ok"){
						$.jBox.tip("正在查询...", 'loading', {persistent: true});
						$(this).attr("disabled",true);
						$('#searchForm').submit();
					}
				}, {buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			var typeArr = '${typeArr}';
			if(!!typeArr){
				typeArr = eval('('+ typeArr+ ')');
				$('#typeArr').select2().val(typeArr).trigger("change");
			}
		});
		
		function page(n, s) {
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/cardTypeStat/init">SIM卡用量统计</a></li>
		<li><a href="${ctx}/mifi/cardTypeStat/cardStatisticsByCountryPage">SIM卡国家用量统计</a></li>
	</ul>
	<form:form id="searchForm" action="${ctx}/mifi/cardTypeStat/list"
		method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<div>
			<label>卡类型：</label>
			<select id="typeArr" name="typeArr" class="input-xxlarge" multiple="multiple">
				<option value="">--请选择--</option>
				<c:forEach items="${fns:getListByTable('sim_card_type','card_type','card_type_name')}" var="cardTypeValue">
					<option value="${cardTypeValue.card_type}">${cardTypeValue.card_type_name}</option>
				</c:forEach>
			</select>
			&nbsp;&nbsp; <input id="btnSubmit" class="btn btn-primary" type="button" value="查询" />
				<div style="margin-top: 10px;">
					<label> 使用中[状态3]：${map.usedCnt }</label>
					<label> 空闲[状态2]：${map.freeCnt }</label>
					<label> 被拒[状态6]：${map.refuse }</label>
					<label> 被锁[状态4]：${map.block }</label>
				</div>
		</div>
	</form:form>
	<tags:message content="${message}" />
	<table id="contentTable"
		class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>卡类型</th>
				<th>使用中[状态3]</th>
				<th>空闲[状态2]</th>
				<th>被拒[状态6]</th>
				<th>被锁[状态4]</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="a">
				<tr>
					<td>${a.card_type}|${a.card_type_name}</td>
					<td>${a.usedCnt}</td>
					<td>${a.freeCnt}</td>
					<td>${a.refuse}</td>
					<td>${a.block}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>