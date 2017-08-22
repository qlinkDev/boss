<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>工单管理管理</title>
<meta name="decorator" content="default" />
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
<script type="text/javascript">
	$(function(){
	    
		$("#btnSubmit").click(function(){
			$.jBox.tip('正在处理，请稍后...','loading',{persistent: true});
			var param = $("#endDate").val();
			$("#btnSubmit").attr('disabled', true)
			$.ajax({
				type: 'post',
				url: '${ctx}/mifi/usageRecordSegmentLog/stat',
				data: {'schedTime':param},
				dataType: 'json',
				success: function(data){
					var tip = "error";
					if(data.code == "1"){
						tip = "success";
					}
					$.jBox.tip('统计结果：'+data.msg, tip,{ closed: function () { location.href =  '${ctx}/mifi/usageRecordSegmentLog'; } });
					//$("#btnSubmit").removeAttr('disabled');
				}
			});
		});
		
	})
		
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/mifi/usageRecordSegmentLog/list">统计日志列表</a></li>
		<li class="active"><a href="${ctx}/mifi/usageRecordSegmentLog/form">统计指定日期</a></li>
	</ul>
	<br />
	<tags:message content="${message}"/>
	<form:form id="inputForm"  method="post" enctype="multipart/form-data" class="form-horizontal">
		
		<div class="control-group">
			<label class="control-label">统计日期:</label>
			<div class="controls">
				<input id="endDate" name="endDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${sched}"
				onclick="WdatePicker({skin:'twoer',dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d-1}', isShowToday : false});" />
			</div>
		</div>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="统 计"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>