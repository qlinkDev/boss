<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>调度日志记录</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<style type="text/css">
		#tableDiv {overflow:auto;}
			table th {white-space: nowrap;}
			table td {white-space: nowrap;}
		}
		.alert-success{
			color:#669533;
			background-color:#d5ecbf;
			border-color:#d2e6ab;
		}
		</style>
	<script type="text/javascript">
	document.addEventListener('DOMContentLoaded', function() {
		document.getElementById('btnSubmit').addEventListener('click', function() {
			$.jBox.tip("正在查询...", 'loading', {persistent: true});
			$(this).attr("disabled",true);
			$("#searchForm").submit();
	    }, false);
	}, false);
	
	function page(n, s) {
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
			
		});
		
		function scheduled(id, param){
			$.jBox.tip('正在通信，请稍后...','loading',{persistent: true});
			$.ajax({
				type: 'post',
				url: '${ctx}/mifi/usageRecordSegmentLog/getResult',
				data: {'id':id, 'schedTime':param},
				dataType: 'json',
				success: function(data){
					var tip = "error";
					if(data.code == "1"){
						tip = "success";
					}
					$.jBox.tip('统计结果：'+data.msg, tip, { timeout: 1000, closed: function () { $('#searchForm').submit();} });
				}
			});
		}
		
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/usageRecordSegmentLog/list">统计日志列表</a></li>
		<li><a href="${ctx}/mifi/usageRecordSegmentLog/form">统计指定日期</a></li>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="mifiUsageRecordSegmentLogCondition" action="${ctx}/mifi/usageRecordSegmentLog/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>执行结果：</label> 
		<select id="result" name="result" class="input-small">
			<option value="">--请选择--</option>
			<option value="0"  ${condition.result == 0 ? 'selected' : '' }>执行成功</option>
			<option value="1" ${condition.result == 1 ? 'selected' : '' }>执行失败</option>
		</select>
		<label>统计时间：</label>
		<input id="beginDate" name="beginDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${condition.beginDate}"
				onclick="WdatePicker({skin:'twoer',dateFmt:'yyyy-MM-dd',minDate:'2016-08-01', maxDate:'#F{$dp.$D(\'endDate\')||\'%y-%M-%d\'}'});" />&nbsp;到
		<input id="endDate" name="endDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${condition.endDate}"
				onclick="WdatePicker({skin:'twoer',dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'beginDate\')}',maxDate:'%y-%M-%d'});" />		
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" />
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<c:if test="${not empty lose}">
	<div id="messageBox" class="alert-success"><button data-dismiss="alert" class="close">×</button>${lose}</div> 
	</c:if> 
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<div id="tableDiv">
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>统计日期</th>
				<th>使用数</th>
				<th>执行日期</th>
				<th>结果</th>
				<th>操作</th>
				</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="recordLog">
			<tr>
				<td><fmt:formatDate value="${recordLog.stampCreated}" pattern="yyyy-MM-dd" /> </td>
				<td>${recordLog.count}</td>
				<td><fmt:formatDate value="${recordLog.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				<td>${recordLog.result == 1 ? '执行失败' : '执行成功'}</td>
				<td>
					<c:if test="${recordLog.result  == 1 }"><a  disabled = 'true'  href="javascript:void(0)" onclick="scheduled(' ${recordLog.id }',' <fmt:formatDate value="${recordLog.stampCreated}" pattern="yyyy-MM-dd" />');">手动统计</a></c:if>
				</td>
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