<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<style type="text/css">
	.contrySpan {
			display:-moz-inline-box;
			display:inline-block;
			width:180px; 
		}
</style>
<script type="text/javascript">
	$(function(){
		//默认初始化禁用和启用
		if($('input:radio:checked').val() == "1"){
			$('#code').show();
		} else {
			$('#code').hide();
		}
		
		//默认初始化（ALL 和 homeForbidden）
		$('.country[checked]').each(function(){
			if($(this).val() == "ALL"){
				$('.country').attr("checked",false);
				$('.country').attr("disabled","disabled");
				$(this).attr("checked",true);
				$(this).removeAttr("disabled");
			}
			
			if($(this).val() == "homeForbidden"){
				$('.country').attr("checked",false);
				$('.country').attr("disabled","disabled");
				$(this).attr("checked",true);
				$(this).removeAttr("disabled");
			}
		});
		
		//checkbox点击事件
		$('.country').click(function(){
			if($(this).val() == "ALL"){
				if($(this).attr("checked")){ //如果选中
					$('.country').each(function(){
						if($(this).val() != "ALL"){
							$(this).attr("checked",false);
							$(this).attr("disabled","disabled");
						}
					});
				} else { //取消
					$('.country').each(function(){
						if($(this).val() != "ALL"){
							$(this).removeAttr("disabled");
						}
					});
				}
			}
			
			if($(this).val() == "homeForbidden"){
				if($(this).attr("checked")){
					$('.country').each(function(){
						if($(this).val() != "homeForbidden"){
							$(this).attr("checked",false);
							$(this).attr("disabled","disabled");
						}
					});
				} else {
					$('.country').each(function(){
						if($(this).val() != "homeForbidden"){
							$(this).removeAttr("disabled");
						}
					});
				}
			}
		})
		
		//启用和禁用
		$('input[name="allow"]').click(function(){
			if($(this).val() == "1"){
				$('#code').show();
			} else {
				$('#code').hide();
			}
		})
		
		//异步提交
		$('#btn').click(function(){
			 $.jBox.tip("正在提交数据", 'loading');
			 $.ajax({
				type: 'post',
				url: '${ctx}/mifi/mifiDevice/updateMifilist',
				data:  $('#countryForm').serialize(),
				dataType: 'json',
				success: function(data){
					if(data){
						$.jBox.tip('修改成功。', 'success', {timeout: 2000})
						window.setTimeout(function () { $('#searchForm').submit();}, 3000);
					} else {
						$.jBox.tip("修改失败。");
					}
				}
			}); 
		})
	})
</script>
<html>
<body>
<c:if test="${not empty error }"><div style="margin:0  10px 5px 0;"> <label class="control-label" for="error">错误提示：</label><div  class="controls">没有数据</div></div></c:if>
<form id="countryForm"  action="${ctx }/mifi/mifiDevice/updateMifilist" method="post" style="margin:0  10px 5px 0;">
	<div style="margin:8px 0;">
		<label class="control-label" for="allow">设备编号：</label>
		<label  class="control-label" >${sn }</lable>
		<input type="hidden"  name="sn" value="${sn }" />
	</div>
	<div style="margin:8px 0;">
		<label class="control-label" for="allow">设备状态：</label>
		<label><input type="radio"  name="allow"  value="1"  <c:if test="${allow eq '1'}">checked="checked"</c:if>/>启用</label>&nbsp;
		<label><input type="radio"  name="allow"  value="0" <c:if test="${allow eq '0' }">checked="checked"</c:if>/>禁用</label>
	</div>
	<div id="code"  style="margin:8px 0;">
			<label class="control-label" for="mcces">国家：</label>
			<div style="white-space:normal;margin:2px 0  0 30px;">
				<span class="contrySpan">
						<input class="country" name="countryCode" type="checkbox"  value="ALL"  <c:if test="${not empty ALL }">checked="checked"</c:if>/>ALL
				</span>
				<span class="contrySpan">
						<input class="country"  name="countryCode" type="checkbox"  value="homeForbidden"  <c:if test="${not empty homeForbidden }">checked="checked"</c:if>/>homeForbidden
				</span>
				<c:forEach items="${country }" var="country">
					<span class="contrySpan">
						<input class="country"  name="countryCode"  type="checkbox"   value="${country[0] }"  <c:if test="${country[2] eq '1' }">checked="checked"</c:if>/>${country[1] }
					</span>
				</c:forEach>
			</div>
	</div>
	<div style="margin:10px 0 0  30px;">
		<label class="control-label" ></label>
		<label class="control-label"  for="tj"><input type="button"  class="btn btn-primary"  id="btn" value="修改"/> </label>
	</div>
</form>
</body>
</html>