<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>卡槽详情</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/dialog.jsp"%>
<style>
		#btnSubmit1{border:none;display:block;color:#0099FF;background-color:#FFFFFF}					
	</style>
<script type="text/javascript">
document.addEventListener('DOMContentLoaded', function() {
	document.getElementById('btnSubmit').addEventListener('click', function() {
		$.jBox.tip("正在查询...", 'loading', {persistent: true});
		$(this).attr("disabled",true);
		$("#searchForm").submit();
	}, false);
}, false);
function op(status){
	var  cc = 0 ;
	$.ajax({
		  type: 'POST',
		  url: "${ctx}/mifi/cardBasicInfo/simBankList",
		  success: function(data){
			  resultArray = data.results;
			  for(var i in resultArray){
				 var b= resultArray[i].simBankId ;
				 if(status==b){
					 cc=++i;
				 }
			  }
		window.location.href="${ctx}/mifi/cardBasicInfo/simNodeView?simBankId="+cc;
		  },
		  dataType: "json"
	});
	}

	function page(n,s){
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
    	return false;
    }
</script>
</head>
<body>
<div>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/cardBasicInfo/fi">卡槽详情</a></li>
	</ul>
	<form:form id="searchForm" action="${ctx}/mifi/cardBasicInfo/find"
		method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
				<input type="hidden" id="simBankListSize" value="${simBankList.size() }">
			
		<div>
			<label>主控版：</label> 
		<select id="simBankId" name="simBankId" class="input-medium">
		<option value="">--请选择--</option>
		<c:forEach items="${simBankList }" var="simBank">
			<option value="${simBank.simBankId }" <c:if test="${simBank.simBankId==simBankId}">selected</c:if>>${simBank.simBankId }</option>
		</c:forEach>
		</select>
		<label>卡类型：</label>
			<select id="type" name="type" class="input-medium">
				<option value="null">未绑定卡</option>
				<c:forEach items="${fns:getListByTable('sim_card_type','card_type','card_type_name')}" var="cardTypeValue">
					<option value="${cardTypeValue.card_type}" <c:if test="${cardTypeValue.card_type==type}">selected</c:if>>${cardTypeValue.card_type_name}</option>
				</c:forEach>
			</select>
	<label>是否在线 ：</label>
	<select id="live" name="live" class="input-medium">
			<option value="3" <c:if test="${live == 3}">selected</c:if>>--请选择--</option>
			<option value="1" <c:if test="${live == 1}">selected</c:if>>是</option>
			<option value="0" <c:if test="${live == 0}">selected</c:if>>否</option>
		</select>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
		</div>
	</form:form>
	<tags:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>卡板编号</th>
				<th>是否在线</th>
				<th>卡槽使用情况</th>
			</tr>
		</thead>
		<tbody>
				<c:forEach items="${page.list}" var="a"  varStatus="count">
				<tr>
					 <td><input  id="btnSubmit1" onclick="op(${a.SIMBANKID })" type="button" value="${a.SIMBANKID}"/></td> 
					<td><c:if test="${a.NWSTATUS==1}">是</c:if>
						<c:if test="${a.NWSTATUS==0}">否</c:if>
					</td>
					<td>总卡数为${a.co} 插卡数为${a.numb}</td>
				</tr>
				</c:forEach>
		</tbody>
	</table>
	
	
	<div class="pagination">${page}</div>
</div>
</body>
</html>