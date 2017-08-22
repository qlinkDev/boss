<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>SIM卡国家用量统计</title>
	<meta name="decorator" content="default" />
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function() {
			// 查询
			$('#btnSubmit').click(function() {
				top.$.jBox.confirm("此操作耗时较长,占用服务器资源较大,有一定概率引起设备自爆,为了服务器及自身人身安全请慎重,是否继续？","系统提示",function(v,h,f){
					if(v == "ok"){
						$.jBox.tip("正在查询...", 'loading', {persistent: true});
						$(this).attr("disabled",true);
						$('#mccOptionEmpty').attr("selected","");//保证mcc数组不为空
						$('#searchForm').submit();
					}
				}, {buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			
			var mccs = '${mccs}';
			if(!!mccs){
				mccs = eval('('+ mccs+ ')');
				$('#mccs').select2().val(mccs).trigger("change");
			}
		});
	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/cardTypeStat/init">SIM卡用量统计</a></li>
		<li class="active"><a href="${ctx}/mifi/cardTypeStat/cardStatisticsByCountryPage">SIM卡国家用量统计</a></li>
	</ul>
	<form:form id="searchForm" action="${ctx}/mifi/cardTypeStat/cardStatisticsByCountry" method="post" class="breadcrumb form-search">
		<div>
			<label>国家：</label>
			<select id="mccs" name="mccs" class="input-xxlarge" multiple="multiple">
				<option id="mccOptionEmpty" value="">--请选择--</option>
				<c:forEach var="item" items="${mccList}">
					<option value="${item[0]}">${item[1]}</option>
				</c:forEach>
			</select>
			<label>时间：</label>
			<input id="startDate" name="startDate"
					type="text" readonly="readonly" maxlength="20"
					class="input-small Wdate required" value="${startDate}"
					onclick="WdatePicker({skin:'twoer',dateFmt:'yyyy-MM-dd',isShowToday:true,isShowClear:false,minDate:'%y-%M-%d'});" />&nbsp;到
			<input id="endDate" name="endDate"
					type="text" readonly="readonly" maxlength="20"
					class="input-small Wdate required" value="${endDate}"
					onclick="WdatePicker({skin:'twoer',dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'startDate\')}',maxDate:'%y-%M-{%d+6}',isShowToday:true,isShowClear:false});" />	
			&nbsp;&nbsp; 
			<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" />
		</div>
	</form:form>
	<tags:message content="${message}" />
	<table id="contentTable"
		class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>国家</th>
				<th>使用中[状态3]</th>
				<th>空闲[状态2]</th>
				<th>被拒[状态6]</th>
				<th>被锁[状态4]</th>
				<c:forEach items="${dateList }" var="dateStr">
				<th>${dateStr }</th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${rows}" var="map">
				<tr>
					<td>${map.value.countryName}</td>
					<td>${map.value.usedCnt }</td>
					<td>${map.value.freeCnt }</td>
					<td>${map.value.refuse }</td>
					<td>${map.value.block }</td>
					<c:forEach items="${dateList }" var="dateStr">
						<td>
							<c:forEach items="${map.value }" var="dateMap">
								<c:if test="${dateMap.key == dateStr }">${dateMap.value }</c:if>
							</c:forEach>
						</td>
					</c:forEach>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>