<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>工单管理管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(function(){
		 $.validator.addMethod("validateFileExt",function(value, element){
	        return checkFileExt(value);
	    },"格式不正确,支持的文件格式为：jpg,png,doc,docx,xls,xlsx,ppt,pptx,txt");
	    $.validator.addMethod("validateFileSize",function(value, element){
	        return checkFileSize();
	    },"所选择的文件太大，文件大小最多支持10M!");
	    
		$("#deviceSn").focus();
		if($("#userType").val() == "" ||  $("#userType").val() == "YOUYOUMOB"){
			$("#inputForm").validate();
		} else {
			$("#inputForm").validate({
				 rules : {
					 deviceSn : { 
			                remote : {
			                    type : "post",
			                    url : '${ctx}/mifi/wOrder/checkDeviceSn',
			                    data : {'deviceSn':function(){ return $("#deviceSn").val()}},
			                    dataFilter: function(data) {
			                        return data;
			                    }
			                }
			            },
			       },
				 messages:{
					 deviceSn:{
			               remote:"设备编号不存在"
			            }
			       }
			});
		}
		
		$("#btnSubmit").click(function(){
			$("#file").val();
			$("#inputForm").submit();
		});
		
		 /**
	     * 检查文件格式
	     */
	    function checkFileExt(param){
	        var temp = '';
	        if(param != ''){
	            temp = param;
	        } 
	        if(temp == ''){
	            return true;
	        }
	        var extname = temp.substring(temp.lastIndexOf(".")+1,temp.length);  
	        extname = extname.toLowerCase();//处理了大小写  
	        if(extname != "jpg" && extname != "png" && extname != "doc" && extname!= "docx" && extname!= "xls" && extname!= "xlsx" && extname!= "ppt" && extname!= "pptx" && extname!= "txt"){ 
	            return false;
	        }
	        return true;
	    }
	    
	    /**
	     * 检测文件大小
	     */
	    function checkFileSize(){
	       var file = document.getElementById("file").files;  
	       if(file.length > 0){
	           var size = file[0].size;  
	           if(size>10485760){ 
	               return false;
	           }
	       }
	       return true;
	    }
	    
	    /**
	     * 字符数统计
	     */
	    $('.limited').on("input",function(){
	         var limit = $(this).attr('maxlength') - $(this).val().length;
	         if($(this).val().length > Number($(this).attr('maxlength'))){
	             $(this).parent().find('.limit-text').addClass("has-error");
	             $(this).parent().find('.limit-text').html("您已经超出了"+Math.abs(limit)+" 字符");
	         }else{
	             $(this).parent().find('.limit-text').removeClass("has-error");
	             $(this).parent().find('.limit-text').html("还可以输入"+limit+" 字符");

	         }
	    });
	})
		
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/wOrder/list">工单列表</a></li>
		<li class="active"><a href="${ctx}/mifi/wOrder/form">工单添加</a></li>
	</ul>
	<br />
	<tags:message content="${message}"/>
	<input type="hidden"  id="userType"  value="${user.channelNameEn}"/>

	<form:form id="inputForm" modelAttribute="workOrder" action="${ctx}/mifi/wOrder/saveWo" method="post" enctype="multipart/form-data" class="form-horizontal">
		
		<div class="control-group">
			<label class="control-label">设备编号:</label>
			<div class="controls">
				<form:input path="deviceSn" htmlEscape="false" maxlength="20" class="required"  placeholder="请输入设备编号" />
				<form:errors path="deviceSn"  cssClass="error" style="color:red"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">问题详情:</label>
			<div class="controls">
				<form:textarea path="problemDesc" htmlEscape="false"  rows="10" maxlength="500" class="required input-xlarge limited"  style="margin: 0px;width: 415px;height: 208px;" placeholder="请输入文字" />
				<form:errors path="problemDesc"  cssClass="error" style="color:red"/>
				<span class="help-block content-help-block limit-text">建议不超过500字符</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">问题类型:</label>
			<div class="controls">
				<select id="problemType" name="problemType" class="input-small required">
					<option value="">--请选择--</option>
					<c:forEach var="item" items="${fns:getDictList('work_order_problem_type') }">
						<option value="${item.value}" ${item.value == condition.problemType ? 'selected':'' }>${item.label }</option>
					</c:forEach>
				</select>
				<form:errors path="problemType"  cssClass="error" style="color:red"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">问题优先级:</label>
			<div class="controls">
				<select id="level" name="level" class="input-small">
					<c:forEach var="item" items="${fns:getDictList('work_order_level') }">
						<option value="${item.value }" ${item.value == condition.level ? 'selected':'' }>${item.label }</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">添加附件:</label>
			<div class="controls">
				<input type="file" id="file" name="multipartFile"  class="file validateFileExt validateFileSize">
			</div>
				<div class="controls">
				<span>文件格式：jpg,png,doc,docx,xls,xlsx,ppt,pptx,txt；文件不超过10M</span>
				</div>
		</div>
		
		<div class="form-actions">
			<shiro:hasPermission name="mifi:workOrder:edit">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="保 存"/>&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>