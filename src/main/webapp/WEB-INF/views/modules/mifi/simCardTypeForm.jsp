<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>SIM卡类型管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
		$.validator.addMethod("isRequiredNum",function(value,element){
		    var score = /^[0-9]*[1-9][0-9]*$/;
		    return this.optional(element) || (score.test(value)) || value == -1;
		},"<font color='#E47068'>请输入-1或正整数</font>");
		
		$.validator.addMethod("isRequiredCapNum",function(value,element){
		    var score = /^(?:[1-9][0-9]*(?:\.[0-9]+)?|0(?:\.[0-9]+)?)$/;
		    return this.optional(element) || (score.test(value));
		},"<font color='#E47068'>请输入正浮点数</font>");
		
		$.validator.addMethod("unsignedInteger",function(value,element){
		    var score = /^\d+$/;
		    return this.optional(element) || (value.match(score));
		},"<font color='#E47068'>请输入非负整数</font>");
		
		$.validator.addMethod("clearDayRangeOne",function(value,element){
		    return this.optional(element) || (value>=0&&value<=31);
		},"<font color='#E47068'>请输入一个介于 0 和 31 之间的值 </font>");
		
		$.validator.addMethod("clearDayRangeTwo",function(value,element){
		    return this.optional(element) || (value>=1&&value<=99);
		},"<font color='#E47068'>请输入一个介于 1 和 99 之间的值 </font>");
		
		$.validator.addMethod("validateApn",function(value, element){
			if(value.trim() != ""){
				  return checkAPN(value);
			} else {
				return true;
			}
	    },"<font color='#E47068'>请输入正确的格式</font>");
		
		$.validator.addMethod("validateMccNickname",function(value, element){
			if ('ABROAD_TO_HOME' == $('#usePeopleType').val()) {
				var mccNickname = $('#mccNickname').val();
				if (mccNickname.trim() == "")
					return false;
			}
			return true;
	    },"<font color='#E47068'>必填信息</font>");
		
		$(document).ready(function() {
			$("#cardType").focus();
			$("#inputForm").validate({
				rules: {
					cardType: {remote: "${ctx}/mifi/simCardType/checkCardType?oldCardType=" + encodeURIComponent('${simCardType.cardType}')},
					clearHour: {range:[0,23]}
				},
				messages: {
					cardType: {remote: "卡类型编码已存在!"}
				}
			});
			$(".country").click(function() {
				var thisChecked = $(this).attr("checked") == "checked";
				var mcc = "";
				var areaType = "";
				var checkedCnt = 0;
				var earthFlag = false;
				$(".country").each(function(i) {
					if ($(this).attr("checked") == "checked") {
						var value = $(this).val();
						if(mcc == ""){
							mcc = value;
						}else{
							mcc = mcc + ',' + value;
						}
						checkedCnt++;
						if("-1" == value){
							earthFlag = true;
						}
					}
				});
				if(checkedCnt == 1){
					areaType = "2";
				}else if(checkedCnt>1){
					areaType = "1";
				}
				if(earthFlag){
					areaType = "0";
					mcc = "";
				}
				if(earthFlag && checkedCnt > 1 && thisChecked){
					alert("[全球]与其他地区不可同时选择!");
					$(this).attr("checked",false);
				}else{
					$("#mcc").val(mcc);
					$("#areaType").val(areaType);
				}
			});
			
			if($("input[name='clearType']:checked").val() == 0){
				$("input[name='clearType'][value='0']").prop('checked', true);
				hideClearDef();
			}else{
				$("input[name='clearType'][value='1']").prop('checked', true);
				showClearDef();
			}
			
			$(".clearType").click(function(){
				if($("input[name='clearType']:checked").val() == 1){
					showClearDef();
				} else {
					hideClearDef();
				}
			});
			/* $("#validDays").change(function() {
				if("-1" == this.value){
					showClearDef();
				}else{
					hideClearDef();
				}
		    }); */
			$("#clearDay").change(function() {
				if(!this.value && "-1"==$("#validDays").val()){
					this.value = 0;
				}
		    });
			$("#clearHour").change(function() {
				if(!this.value && "-1"==$("#validDays").val()){
					this.value = 0;
				}
		    });
			
			// 卡使用人员类型
			$('#usePeopleType').change(function() {
				var val = $(this).val();
				if ('ALL' == val) {
					$('#mccNicknameDiv').hide();
				} else {
					$('#mccNicknameDiv').show();
				}
			});
			// MCC昵称初始化
			if ('ALL' == $('#usePeopleType').val()) {
				$('#mccNicknameDiv').hide();
			} else {
				$('#mccNicknameDiv').show();
			}
			

			// 所有渠道和单个渠道不能同时存在
			$('#allowedSourceALL').click(function() {
				if ($(this).attr('checked') == 'checked') {
					$('.allowedSourceOther').attr('checked', false);
				}
			})
			$('.allowedSourceOther').click(function() {
				if ($(this).attr('checked') == 'checked') {
					$('#allowedSourceALL').attr('checked', false);
				}
			})
			
			// 保存
			$('#btnSubmit').click(function() {
				var allowedSource = '';
				if ($('#allowedSourceALL').attr('checked') == 'checked') {
					allowedSource = ',ALL';
				} else {
					$('.allowedSourceOther').each(function(i) {
						if ($(this).attr('checked') == 'checked') {
							allowedSource += ',' + $(this).val();
						} 
					});
				}
				if (allowedSource == '') {
					top.$.jBox.info('请选择允许使用该类型卡的渠道', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				} else {
					allowedSource = allowedSource.substring(1);
					$('#allowedSource').val(allowedSource);
				}
				
				$('#inputForm').submit();
			});
		});
		
		function showClearDef(){
			$("#clearDay").removeClass("clearDayRangeTwo");
			$("#clearDay").addClass("required clearDayRangeOne");
			$("#clearHour").addClass("required");
			$('#clearDayTitle').text('流量清空日期:');
			$('#clearDayText').text('北京时间。如配置为1，表示每月1日清空该类型SIM卡的使用流量。0表示每月最后一天。');
			$('#clearHourText').text('北京时间。如配置为14，清空日期配置为2，表示每月2日14时清空该类型SIM卡的使用流量。');
		}
		function hideClearDef(){
			/* $("#clearDay").val("");
			$("#clearHour").val(""); */
			$("#clearDay").removeClass("required clearDayRangeOne");
			$("#clearDay").addClass("clearDayRangeTwo");
			$("#clearHour").removeClass("required");
			$('#clearDayTitle').text('流量清空间隔天数:');
			$('#clearDayText').text('1~99');
			$('#clearHourText').text('北京时间。如配置为14，间隔天数配置为2，表示每隔2天14时清空该类型SIM卡的使用流量。');
		}
		
		//apn格式验证（3G,name,user,pwd;4G,name,,pwd）
		function checkAPN(val){
			if(val.indexOf(";") > 0){ //两种APN信息
				var apn = val.split(";");
				if(apn.length > 2){
					return false;
				} else {
				 	var apn3G = apn[0];
				 	var apn4G = apn[1];
				 	if(chekApnDel(apn3G) && chekApnDel(apn4G)){
				 		return true;
				 	} else {
				 		return false;
				 	}
				}
			} else {
				return chekApnDel(val)
			}
		}
		
		function chekApnDel(apn){
			var info = apn.split(",");
			if(info.length === 4){
				var first = info[0].trim();
				//var second = info[1].trim();
				if(first){
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	</script>
<style type="text/css">
.contrySpan {
	display: -moz-inline-box;
	display: inline-block;
	width: 180px;
	margin-top: 5px;
}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/simCardType/init">卡类型管理</a></li>
		<li class="active"><a
			href="${ctx}/mifi/simCardType/form?id=${simCardType.id}">卡类型${not empty simCardType.id?'修改':'添加'}</a></li>
	</ul>
	<br />

	<form:form id="inputForm" modelAttribute="simCardType" action="${ctx}/mifi/simCardType/save" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<form:hidden path="mcc" />
		<form:hidden path="areaType" />
		<form:hidden path="allowedSource"/>
		<input type="hidden" name="oldSourceType" value="${simCardType.sourceType }">
		<input type="hidden" name="oldAllowedSource" value="${simCardType.allowedSource }">
		<tags:message content="${message}" />
		<div class="control-group">
			<label class="control-label" for="cardType">卡类型编码:</label>
			<div class="controls">
				<input id="oldCardType" name="oldCardType" type="hidden" value="${simCardType.cardType}">
				<form:input path="cardType" htmlEscape="false" maxlength="10" class="required" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="cardTypeName">卡类型名称:</label>
			<div class="controls">
				<form:input path="cardTypeName" htmlEscape="false" maxlength="50"
					class="required" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="sourceType">所属渠道:</label>
			<div class="controls">
				<form:select path="sourceType" class="input-medium required" style="width: 220px;">
		        	<form:option value="" label="请选择"/>
		        	<form:options items="${fns:getChannelList()}" itemLabel="channelName" itemValue="channelNameEn" htmlEscape="false"/>
		        </form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="validDays">有效天数(-1永久):</label>
			<div class="controls">
				<form:input path="validDays" htmlEscape="false" maxlength="4" class="required isRequiredNum" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="clearType">卡流量清空类型:</label>
			<div class="controls">
				<label><input  type="radio"  class="clearType"  name="clearType"  value="0"  ${simCardType.clearType == 0 ?  'checked' : '' } />自定义</label>
				<label><input  type="radio"  class="clearType"  name="clearType"  value="1"  ${simCardType.clearType == 1 ?  'checked' : '' } />月清空</label>
			</div>
		</div>
		<div id="divClearDay" class="control-group">
			<label class="control-label" for="clearDay" id="clearDayTitle"></label>
			<div class="controls">
				<form:input path="clearDay" htmlEscape="false" maxlength="2" class="unsignedInteger" />
				<span class="help-inline" id="clearDayText"></span>
			</div>
		</div>
		<div id="divClearHour" class="control-group">
			<label class="control-label" for="clearHour">流量清空时间:</label>
			<div class="controls">
				<form:input path="clearHour" htmlEscape="false" maxlength="2" class="unsignedInteger" />
				<span class="help-inline" id="clearHourText"></span>
			</div>
		</div>
		<!-- 
		<div class="control-group">
			<label class="control-label" for="activeTime">激活时间:</label>
			<div class="controls">
				<form:input path="activeTime" htmlEscape="false" class="required Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</div>
		</div>
		 -->
		<div class="control-group">
			<label class="control-label" for="dataCap">高速流量(GB):</label>
			<div class="controls">
				<form:input path="dataCap" htmlEscape="false" maxlength="6" class="required isRequiredCapNum" />
				(如果不限流量请输入'999999')
			</div>
		</div>
		
		<div class="control-group" id="allowedSourceDiv">
			<label class="control-label" for="channel">允许使用该类型卡的渠道:</label>
			<div class="controls">
				<span class="contrySpan">
					<label>
						<input id="allowedSourceALL" type="checkbox" value="ALL" <c:if test="${simCardType.allowedSource eq 'ALL' }">checked="checked"</c:if>/>所有渠道
					</label>
				</span>
				<c:forEach items="${channelList}" var="channel">
					<span class="contrySpan">
						<label>
							<input class="allowedSourceOther" type="checkbox" value="${channel[0] }" <c:if test="${channel[2] eq '1' }">checked="checked"</c:if> />${channel[1] }
						</label>
					</span>
				</c:forEach>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label" for="mcc">地区:</label>
			<div class="controls">
				<c:forEach items="${countryList}" var="country">
					<span class="contrySpan"> 
						<label>
							<input class="country" id="${country[2]}" type="checkbox" value="${country[0]}" data-name="${country[1]}" data-code="${country[2]}" <c:if test="${country[3] eq '1' }">checked="checked"</c:if> />${country[1]}
						</label>
					</span>
				</c:forEach>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="cardTypeDesc">卡类型描述:</label>
			<div class="controls">
				<form:input path="cardTypeDesc" htmlEscape="false" maxlength="255" class="required" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="cardTypeDesc">APN信息:</label>
			<div class="controls">
				<form:input path="apnInfo" htmlEscape="false" maxlength="255"  class="validateApn"/>
				(3G,name,user,pwd;4G,name,,pwd)
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="usePeopleType">卡使用人员类型:</label>
			<div class="controls">
				<select id="usePeopleType" name="usePeopleType" class="input-medium">
					<c:forEach items="${fns:getDictList('sim_use_people_type')}" var="type">
						<option <c:if test="${type.value eq simCardType.usePeopleType }">selected="selected"</c:if> value="${type.value}">${type.label}</option>
					</c:forEach>
				</select> 
			</div>
		</div>
		<div class="control-group" id="mccNicknameDiv">
			<label class="control-label" for="mccNickname">MCC昵称:</label>
			<div class="controls">
				<form:input path="mccNickname" htmlEscape="false" maxlength="50" class="validateMccNickname" />
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="保 存" />&nbsp; <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>