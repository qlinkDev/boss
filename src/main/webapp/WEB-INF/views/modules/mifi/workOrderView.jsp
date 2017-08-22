<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>工单详情</title>
<base href="${pageContext.request.contextPath}">
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(function(){
		$("#deviceSn").focus();
		$("#inputForm").validate();
		
		$("#btnSubmit").click(function(){
			$("#inputForm").submit();
		});
	})
		
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/wOrder/list">工单列表</a></li>
		<li class="active"><a href="#">工单详情</a></li>
	</ul>
	<br />
	<tags:message content="${message}"/>
	<div align="center">
		<table style="width: 50%"  id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th colspan="2" style="text-align:center;"><h3>工单基本信息</h3></th>
			</tr>
		</thead>
		<tr>
			<td  style="text-align:right; ">工单编号：</td>
			<td>${workOrder.wid }</td>
		</tr>
		<tr>
			<td style="text-align:right; ">设备编号：</td>
			<td>${workOrder.deviceSn }</td>
		</tr>
		<tr>
			<td style="text-align:right; ">问题类型：</td>
			<td>${fns:getDictLabel(workOrder.problemType,'work_order_problem_type','')}</td>
		</tr>
		<tr>
			<td style="text-align:right; ">优先级：</td>
			<td>${fns:getDictLabel(workOrder.level,'work_order_level','')}</td>
		</tr>
		<tr>
			<td style="text-align:right; width: 15%">问题详情：</td>
			<td>${workOrder.problemDesc }</td>
		</tr>
		<tr>
			<td style="text-align:right; ">创建者：</td>
			<td>${workOrder.createBy.loginName }</td>
		</tr>
		<tr>
			<td style="text-align:right; ">创建时间：</td>
			<td><fmt:formatDate value="${workOrder.createTime}" type="both" pattern="yyyy年MM月dd日  HH时mm分ss秒"/></td>
		</tr>
		<c:if test="${not empty workOrder.attachPath  }">
		<tr>
			<td style="text-align:right; ">附件：</td>
			<td>	<a href="${workOrder.attachPath }" target="_blank">预览</a></td>
		</tr>
		</c:if>		
		<thead>
			<tr>
				<th colspan="2" style="text-align:center;"><h3>处理信息</h3></th>
			</tr>
		</thead>
		<tr>
			<td style="text-align:right; ">工单处理状态：</td>
			<td>${fns:getDictLabel(workOrder.status,'work_order_status','')}</td>
		</tr>
		<tr>
			<td style="text-align:right; ">工单问题诊断：</td>
			<td>${fns:getDictLabel(workOrder.pDiagnosisType,'work_order_problem_diagnosis_type','正在为您处理，请耐心等待.....')}</td>
		</tr>
		<thead>
			<tr>
				<th colspan="2" style="text-align:center;"><h3>工单会话记录(${messages.size() }条)</h3></th>
			</tr>
		</thead>
		<c:choose>
			<c:when test="${not empty messages}">
					<c:forEach items="${messages}" var="item">
						<tr>
							<td style="text-align:right; ">${item.userName}</td>
							<td>
								<p>${item.content}</p>
								<p><fmt:formatDate value="${ item.createTime}" type="both"/></p>
							</td>
						</tr>
					</c:forEach>
			</c:when>
			<c:otherwise>
			<tr>
				<th colspan="2" style="text-align:center;">无消息记录</th>
			</tr>
			</c:otherwise>
		</c:choose>
	</table>
	</div>

	<form:form  class="form-horizontal">
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>