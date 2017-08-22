<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>通信记录</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
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
			
			document.getElementById('btnSubmit').addEventListener('click', function() {
				$.jBox.tip("正在查询...", 'loading', {persistent: true});
				$(this).attr("disabled",true);
				$("#searchForm").submit();
			}, false);
		});
		
		function message(id, sn, type){
			$.jBox.tip('正在通信，请稍后...','loading');
			$.ajax({
				type: 'post',
				url: '${ctx}/mifi/cRecord/message',
				data: {'id':id, 'sn':sn, 'type':type},
				dataType: 'json',
				success: function(data){
					var tip = "error";
					if(data.data.code == "0"){
						tip = "success";
					}
					$.jBox.tip('通信结果：'+data.data.msg, tip, { timeout: 1000, closed: function () { $('#searchForm').submit();} });
				}
			});
		}
		
		
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/cRecord">通信记录列表</a></li>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="communicationRecordCondition" action="${ctx}/mifi/cRecord/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>编号：</label><form:input id="deviceSn" path="deviceSn" htmlEscape="false" maxlength="50" class="input-medium"/>
		<label>通信类型：</label>
		<select id="type" name="type" class="input-small">
			<option value="">--请选择--</option>
			<c:forEach var="item" items="${fns:getDictList('communication_record_type') }">
				<option value="${item.value}" ${item.value == condition.type ? 'selected':'' }>${item.label }</option>
			</c:forEach>
		</select>
		<label>通信结果：</label>
		<select id="result" name="result" class="input-small">
			<option value="">--请选择--</option>
			<c:forEach var="item" items="${fns:getDictList('communication_record_result') }">
				<option value="${item.value }" ${item.value == condition.result ? 'selected':'' }>${item.label }</option>
			</c:forEach>
		</select>
		<label>创建时间：</label>
		<input id="startDate" name="startDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${condition.startDate}"
				onclick="WdatePicker({skin:'twoer',dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endDate\')||\'%y-%M-%d\'}'});" />&nbsp;到
		<input id="endDate" name="endDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${condition.endDate}"
				onclick="WdatePicker({skin:'twoer',dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'startDate\')}',maxDate:'%y-%M-%d'});" />		
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>&nbsp;
		<input id="btnClear" class="btn btn-primary" type="button" value="清空"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>编号</th>
				<th>通信类型</th>
				<th>通信结果</th>
				<th>结果说明</th>
				<th>创建时间</th>
				<th>创建者</th>
				<th>操作</th>
				</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="record">
			<tr>
				<td style="word-wrap: break-word;width: 800px;"><div style="width: 800px;">${record.deviceSn}</div></td>
				<td>${fns:getDictLabel(record.type,'communication_record_type','')}</td>
				<td>${fns:getDictLabel(record.result,'communication_record_result','')}</td>
				<td>${record.remarks}</td>
				<td>${record.createDate}</td>
				<td>${record.createBy.loginName}</td>
				<td>
					<c:if test="${record.result eq '2' }">
   					<a href="javascript:void(0)" onclick="message('${record.id }','${record.deviceSn}','${record.type }');">手动通信</a>
   					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<!-- 数据列表 E -->
	
	<!-- 分页 S -->
	<div class="pagination">${page}</div>
	<!-- 分页 E -->
</body>
</html>