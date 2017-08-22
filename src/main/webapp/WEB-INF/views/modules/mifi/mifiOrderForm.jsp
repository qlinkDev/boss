<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>新建订单</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<style type="text/css">
		.contents {width: 800px; margin-bottom: 20px;}
		.labletext {padding-left: 10px; color: #999;}
		.c_pay_price {text-align: right; padding-top: 5px;  padding-right: 28px; padding-bottom: 0px; line-height: 28px;}
		.c_pay_price b {color: #ff1000; font-size: 24px;}
		.order_total_price {font-family: "microsoft yahei";}
	</style>
	<script type="text/javascript">
		var selectedDevieIds = '';
		$(function() {
			$("#inputForm").validate();
			
			// 初始化表单格式
	        initTableCheckbox();  
			
			// 设备可否下单检测
			$('#checkDevice').click(function() {
				// 设备编号
				var imei = $('#deviceNum').val().trim();
				if (isEmpty(imei)) {
					top.$.jBox.info('请输入完整的设备编号', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				if (selectedDevieIds.indexOf(imei) != -1) {
					top.$.jBox.info('设备已选择', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}

				// 出国时间
	        	var startDateStr = $('#startDate').val();
	        	if (isEmpty(startDateStr)) {			
					top.$.jBox.info('请选择出国时间', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					$('#endDate').val('');
					return false;
	        	}
				// 回国时间
	        	var endDateStr = $('#endDate').val();
	        	if (isEmpty(endDateStr)) {			
					top.$.jBox.info('请选择回国时间', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
	        	}
	        	
	        	// 异步判断设备可否下单
	        	var url = '${ctx}/mifi/mifiManage/checkDevice.json';
            	$.post(url, {imei:imei, startDate:startDateStr, endDate:endDateStr}, function(data) {
            		if ('success' == data.code) {
        				var divHtml = "<div id='"+imei+"' style='margin-top: 5px;'><input class='deviceItem' type='text' disabled='disabled' value='"+imei+"' /><button style='margin-left: 5px;' type='button' class='btn btn-danger' onclick='javascript:removeDevice("+imei+");'>删除</button></div>";
        				$('#selectedDeviceList').append(divHtml);
        				selectedDevieIds += imei;
        				$('#deviceNum').val('');
            		} else {
    					top.$.jBox.info(data.message, '系统提示');
    					top.$('.jbox-body .jbox-icon').css('top','55px');
    					return false;
            		}
            	});
				
			});
			
			// 选择设备
			/* $('#selectDevice').click(function() {
				
				var imei = $('#deviceList').find("option:selected").val();
				if (isEmpty(imei)) {
					top.$.jBox.info('请选择设备', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				
				if (selectedDevieIds.indexOf(imei) != -1) {
					top.$.jBox.info('设备已选择', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}

				var divHtml = "<div id='"+imei+"' style='margin-top: 5px;'><input class='deviceItem' type='text' disabled='disabled' value='"+imei+"' /><button style='margin-left: 5px;' type='button' class='btn btn-danger' onclick='javascript:removeDevice("+imei+");'>删除</button></div>";
				$('#selectedDeviceList').append(divHtml);
				
				selectedDevieIds += imei;
			}); */
			
			// 预览价格
			$('#btnPreview').click(function() {
				// 国家编号
				var regionIdPrices = "";	
			    var $tbr = $('table tbody tr');  // 内容行
			    $tbr.find("input[name='checkItem']").each(function(i) {
					if ($(this).attr('checked') == 'checked') {
						regionIdPrices += "," + $(this).attr('data-regionIdPrice');
					}
			    });
			    if (isEmpty(regionIdPrices)) {
					top.$.jBox.info('请选择产品', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
			    }
			    regionIdPrices = regionIdPrices.substring(1);
				// 出国时间
	        	var startDateStr = $('#startDate').val();
	        	if (isEmpty(startDateStr)) {			
					top.$.jBox.info('请选择出国时间', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					$('#endDate').val('');
					return false;
	        	}
				// 回国时间
	        	var endDateStr = $('#endDate').val();
	        	if (isEmpty(endDateStr)) {			
					top.$.jBox.info('请选择回国时间', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
	        	}
	        	// 设备
	        	var imeis = "";
	        	$('.deviceItem').each(function(i) {
	        		imeis += "," + $(this).val();
	        	});
	        	if (isEmpty(imeis)) {
					top.$.jBox.info('请选择设备', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
	        	}
	        	imeis = imeis.substring(1);

				var url = '${ctx}/mifi/mifiOrderList/preview.json';
				$.post(
					url, 
					{
						regionIdPrices:regionIdPrices,
						startDate:startDateStr,
						endDate:endDateStr,
						imeis:imeis
					}, 
					function(data) {
						if (data.code == '1') {
						    var priceTable = '<table class="table table-bordered table-hover"><thead><tr><th>产品名称</th><th>行程</th><th>租用天数</th><th>设备数量</th><th>价格</th></tr></thead><tbody>'
						    var listMap = data.listMap;
		        			$.each(listMap, function(i, map){     
			        			priceTable += '<tr><td>'+map.goodsName+'</td><td>'+map.trip+'</td><td>'+map.days+'</td><td>'+map.devices+'</td><td>'+map.totalPrice+'</td></tr>';
		       				}); 
		        			priceTable += '</tbody></table>';
		        			priceTable += '<div class="c_pay_price"><span>应付金额：</span><b><span class="order_total_price">￥'+data.totalMoney+'</span></b></div>';
		        			priceTable += '<div class="c_pay_price">当前余额：￥'+data.balance+'</div>';
		        			var $priceTable = $(priceTable);
		        		    $('#priceList').html($priceTable); 
						} else {
							if (isEmpty(data.msg))
								top.$.jBox.info('预览价格失败', '系统提示');
							else
								top.$.jBox.info(data.msg, '系统提示');
							top.$('.jbox-body .jbox-icon').css('top','55px');
						}
					}
				);
	        	
			});
			
			// 提交订单
			$('#btnSubmit').click(function() {
				$('#btnSubmit').attr('disabled', true);
				// 国家编号
				var countryCodes = "";	
			    var $tbr = $('table tbody tr');  // 内容行
			    $tbr.find("input[name='checkItem']").each(function(i) {
					if ($(this).attr('checked') == 'checked') {
						countryCodes += "," + $(this).attr('data-countryCode');
					}
			    });
			    if (isEmpty(countryCodes)) {
					top.$.jBox.info('请选择产品', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					$('#btnSubmit').attr('disabled', false);
					return false;
			    }
				countryCodes = countryCodes.substring(1);
				// 出国时间
	        	var startDateStr = $('#startDate').val();
	        	if (isEmpty(startDateStr)) {			
					top.$.jBox.info('请选择出国时间', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					$('#endDate').val('');
					$('#btnSubmit').attr('disabled', false);
					return false;
	        	}
				// 回国时间
	        	var endDateStr = $('#endDate').val();
	        	if (isEmpty(endDateStr)) {			
					top.$.jBox.info('请选择回国时间', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					$('#btnSubmit').attr('disabled', false);
					return false;
	        	}
	        	// 设备
	        	var imeis = "";
	        	$('.deviceItem').each(function(i) {
	        		imeis += "," + $(this).val();
	        	});
	        	if (isEmpty(imeis)) {
					top.$.jBox.info('请选择设备', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					$('#btnSubmit').attr('disabled', false);
					return false;
	        	}
	        	imeis = imeis.substring(1);
	        	// 客户姓名
	        	var userName = $('#userName').val();
	        	if (isEmpty(userName)) {
					top.$.jBox.info('请输入客户姓名', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					$('#btnSubmit').attr('disabled', false);
					return false;
	        	}
	        	// 电话号码
	        	var phone = $('#phone').val();
	        	if (isEmpty(phone)) {
					top.$.jBox.info('请输入电话号码', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					$('#btnSubmit').attr('disabled', false);
					return false;
	        	}
	        	if (!isPhone(phone) && !isWordPhone(phone)) {
					top.$.jBox.info('电话号码格式错误', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					$('#btnSubmit').attr('disabled', false);
					return false;
	        	}
	        	// 邮箱
	        	var email = $('#email').val();
				if (!isEmpty(email) && !isEmail(email)) {
					top.$.jBox.info('邮箱格式错误', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					$('#btnSubmit').attr('disabled', false);
					return false;
				}
				var passportNo = $('#passportNo').val();
				var passportPy = $('#passportPy').val();
				var remarks = $('#remarks').val();
				var limitSpeedFlag = $('input[name="limitSpeedFlag"]:checked').val();

				var url = '${ctx}/mifi/mifiOrderList/createOrder.json';
				$.post(
					url, 
					{
						countryCodes:countryCodes,
						startDate:startDateStr,
						endDate:endDateStr,
						limitSpeedFlag:limitSpeedFlag,
						imeis:imeis,
						userName:userName,
						phone:phone,
						email:email,
						passportNo:passportNo,
						passportPy:passportPy,
						remarks:remarks
					}, 
					function(data) {
						if (data.code == '1') {
							top.$.jBox.info(data.msg, '系统提示');
							top.$('.jbox-body .jbox-icon').css('top','55px');
							window.location.href="${ctx}/mifi/mifiOrderList?initTag=1";
						} else if (data.code == '-2') {
							var $rechargeDiv = $('<div style="margin: 10px; text-align: center;"><p>'+data.msg+'</p><input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   充    值   " /></div>')
		        		    $('#rechargeForm').html($rechargeDiv); 
							$.jBox($("#rechargeBox").html(), {
								title : "余额不足",
								buttons : {
									"关闭" : true
								}
							});
							$('#btnSubmit').attr('disabled', false);
						} else {
							if (isEmpty(data.msg))
								top.$.jBox.info('下单失败', '系统提示');
							else
								top.$.jBox.info(data.msg, '系统提示');
							top.$('.jbox-body .jbox-icon').css('top','55px');
							$('#btnSubmit').attr('disabled', false);
						}
					}
				);
	        	
			});
		});
		
		// 根据出国时间和回国时间选择设备
		function getDeviceList() {
			
        	var startDate = $('#startDate').val();
        	var endDate = $('#endDate').val();
        	
        	// 出国时间不能为空
        	if (isEmpty(startDate)) {
				top.$.jBox.info('请选择出国时间', '系统提示');
				top.$('.jbox-body .jbox-icon').css('top','55px');
				$('#endDate').val('');
				return false;
        	}
    		
    		// 清除设备列表、已选择的设备
   			$("#s2id_deviceList a span").text('请选择');
   			$('#deviceList').empty();
   			$('#selectedDeviceList').html('');
   			selectedDevieIds = '';
        	
        	// 异步取设备列表
            $('#deviceList').attr('disabled', true);
        	$.post('${ctx}/mifi/mifiManage/device.json', {startDate:startDate, endDate:endDate}, function(data) {
        		if (!isEmpty(data)) {
        			$.each(data, function(i, item){     
       				   $('#deviceList').append("<option value='"+item.imei+"'>"+item.imei+"</option>"); 
       				});   
        		}
                $('#deviceList').attr('disabled', false);
        	});
        	
        	return true;
		};
		
		// 如果行程开始时间重新选择，清除行程结束时间、设备列表、已选择的设备
		function clearEndDate() {
			$('#endDate').val('');
			//$("#s2id_deviceList a span").text('请选择');
			//$('#deviceList').empty();
			$('#selectedDeviceList').html('');
			selectedDevieIds = '';
		}
		
		// 删除已选择的设备
		function removeDevice(id) {
			$('#' + id).remove();
			selectedDevieIds = selectedDevieIds.replace(id, '');
		}
		
		// table checkbox
		function initTableCheckbox() {  
		    var $thr = $('table thead tr');  
		    var $checkAllTh = $thr.find('th:eq(0)');  
		    /*“全选/反选”复选框*/  
		    var $checkAll = $thr.find('input');  
		    $checkAll.click(function(event){  
		        /*将所有行的选中状态设成全选框的选中状态*/  
		        $tbr.find('input').prop('checked',$(this).prop('checked'));  
		        /*并调整所有选中行的CSS样式*/  
		        if ($(this).prop('checked')) {  
		            $tbr.find('input').parent().parent().addClass('warning');  
		        } else{  
		            $tbr.find('input').parent().parent().removeClass('warning');  
		        }  
		        /*阻止向上冒泡，以防再次触发点击操作*/  
		        event.stopPropagation();  
		    });  
		    /*点击全选框所在单元格时也触发全选框的点击操作*/  
		    $checkAllTh.click(function(){  
		        $(this).find('input').click();  
		    });  
		    var $tbr = $('table tbody tr');  
		    /*点击每一行的选中复选框时*/  
		    $tbr.find('input').click(function(event){  
		        /*调整选中行的CSS样式*/  
		        $(this).parent().parent().toggleClass('warning');  
		        /*如果已经被选中行的行数等于表格的数据行数，将全选框设为选中状态，否则设为未选中状态*/  
		        $checkAll.prop('checked',$tbr.find('input:checked').length == $tbr.length ? true : false);  
		        /*阻止向上冒泡，以防再次触发点击操作*/  
		        event.stopPropagation();  
		    });  
		    /*点击每一行时也触发该行的选中操作*/  
		    $tbr.click(function(){  
		        $(this).find('input').click();  
		    });  
		}  
		
		/**
		 * 判断是否为空
		 * @param o
		 * @returns {Boolean}
		 */
		function isEmpty(o){
			if(o==null||o=='undefined'||o==''){
				return true;
			}else{
				return false;
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/mifiOrderList/form?id=${mifiOrder.orderId}">新建订单</a></li>
	</ul><br/>
	
	<tags:message content="${message}"/>
	
	<form:form id="inputForm" modelAttribute="mifiOrder" action="${ctx}/mifi/mifiOrderList/save" method="post" class="form-horizontal">
		<form:hidden path="orderId"/>
		<i class="icon-shopping-cart"></i>选择产品
		<hr style="margin-top: 5px;" />
		<div class="control-group">
			<label class="control-label">可选产品:</label>
			<div class="controls" style="width: 800px;">
				<c:if test="${!empty countryList }" var="haveProduce">
				<table class="table table-bordered table-hover">
					<thead><tr><th style="width: 20px;"><input type="checkbox" id="checkAll" name="checkAll" /></th><th>区域</th><th>国家</th><th>价格</th></tr></thead>
					<tbody>
						<c:forEach items="${countryList }" var="countryMap">
						<tr>
							<td><input type="checkbox" name="checkItem" data-countryCode="${countryMap.countryCode }" data-regionIdPrice="${countryMap.regionId }#${countryMap.price }" /></td>
							<td>
								${countryMap.regionName }
							</td>
							<td>${countryMap.countryName }</td>
							<td style="color: red;">${countryMap.price }</td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
				</c:if>
				<c:if test="${!haveProduce }">
					<label class="control-label">暂无产品</label>
				</c:if>
			</div>
		</div>
		<i class="icon-filter"></i>速度模式
		<hr style="margin-top: 5px;" />
		<div class="control-group">
			<label class="control-label">速度模式:</label>
			<div class="controls">
				<label><input type="radio" name="limitSpeedFlag"  value="0" checked="checked" />非低速</label>&nbsp;
				<label><input type="radio" name="limitSpeedFlag"  value="1" />低速</label>
			</div>
		</div>
		<i class="icon-briefcase"></i>设备信息
		<hr style="margin-top: 5px;" />
		<div class="control-group">
			<label class="control-label">出国时间:</label>
			<div class="controls contents">
			  	<input id="startDate" type="text" readonly="true" maxlength="20" class="Wdate" onclick="WdatePicker({onpicked:function(dp){clearEndDate();},dateFmt:'yyyy-MM-dd',isShowClear:true,minDate:'%y-%M-%d'});"/>
			  	<label class="labletext">请选择出国时间</label>
			</div>
			<label class="control-label">回国时间:</label>
			<div class="controls contents">
			  	<input id="endDate" type="text" readonly="true" maxlength="20" class="Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true,minDate:'#F{$dp.$D(\'startDate\',{d:1})}'});"/>
			  	<label class="labletext">请选择回国时间</label>
			</div>
			
			<label class="control-label">添加设备:</label>
			<div class="controls contents">
				<input id="deviceNum" type="text" name="deviceNum" maxlength="20" />
			  	<button id="checkDevice" type="button" class="btn btn-success">添加</button>
			</div>
			
			<label class="control-label">已选择设备:</label>
			<div id="selectedDeviceList" class="controls contents">
			</div>
		</div>
		<i class="icon-user"></i>客户信息
		<hr style="margin-top: 5px;" />
		<div class="control-group">
			<label class="control-label">客户姓名:</label>
			<div class="controls contents">
				<input id="userName" type="text" name="userName" maxlength="50" />
				<label class="labletext">请填写客户真实姓名</label>
			</div>
			<label class="control-label">电话号码:</label>
			<div class="controls contents">
				<input id="phone" type="text" name="phone" maxlength="20" />
				<label class="labletext">请填写电话号码，用于发送订单信息，售后跟踪</label>
			</div>
			<label class="control-label">客户邮箱:</label>
			<div class="controls contents">
				<input id="email" type="text" name="email" maxlength="50" />
				<label class="labletext">（选填）请填写用户邮箱，用于发送订单信息，售后跟踪</label>
			</div>
			<label class="control-label">护照号码:</label>
			<div class="controls contents">
				<input id="passportNo" type="text" name="passportNo" maxlength="20" />
				<label class="labletext">（选填）</label>
			</div>
			<label class="control-label">护照拼音:</label>
			<div class="controls contents">
				<input id="passportPy" type="text" name="passportPy" maxlength="100" />
				<label class="labletext">（选填）</label>
			</div>
			<label class="control-label">订单备注:</label>
			<div class="controls contents">
				<textarea id="remarks" name="remarks" rows="3" maxlength="200" class="input-xlarge"></textarea>
				<label class="labletext">请填写订单备注，方便内部协调处理</label>
			</div>
		</div>
		<i class="icon-qrcode"></i>价格清单
		<hr style="margin-top: 5px;" />
		<div class="control-group">
			<div class="controls" id="priceList" style="width: 800px;">
			</div>
		</div>
		<div class="form-actions">
			<input id="btnPreview" class="btn btn-primary" type="button" value="预览价格"/>&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="button" value="提交订单"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="取 消" onclick="history.go(-1)"/>
			<c:if test="${!empty fns:getUser().channelNameEn }">
			</c:if>
		</div>
	</form:form>
	
	<div id="rechargeBox" class="hide">
		<form id="rechargeForm" action="${ctx }/om/consumeRecord/recharge" method="post" target="_blank" class="form-search">
			
		</form>
	</div>
</body>
</html>