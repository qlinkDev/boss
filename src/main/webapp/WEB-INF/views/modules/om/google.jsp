<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>谷歌定位</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			// 谷歌定位
			$("#btnSubmit").click(function(){
				
				var homeMobileCountryCode = $("input[name='homeMobileCountryCode']").val();
				var homeMobileNetworkCode = $("input[name='homeMobileNetworkCode']").val();
				var radioType = $("input[name='radioType']").val();
				var carrier = $("input[name='carrier']").val();
				var considerIp = $("input[name='considerIp']").val();
				var cellId = $("input[name='cellId']").val();
				var locationAreaCode = $("input[name='locationAreaCode']").val();
				var mobileCountryCode = $("input[name='mobileCountryCode']").val();
				var mobileNetworkCode = $("input[name='mobileNetworkCode']").val();

				if ($('#cellTowers').attr("checked")=='checked'){
					$("input[name='haveCellTowers']").val('1');
					if (isEmpty(cellId)) {
						top.$.jBox.info('请输入小区唯一标识符', '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						return false;
					}
					if (isEmpty(locationAreaCode)) {
						top.$.jBox.info('请输入位置区域代码/网络 ID', '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						return false;
					}
					if (isEmpty(mobileCountryCode)) {
						top.$.jBox.info('请输入国家代码 (MCC)', '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						return false;
					}
					if (isEmpty(mobileNetworkCode)) {
						top.$.jBox.info('请输入网络代码', '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						return false;
					}
				} else {
					$("input[name='haveCellTowers']").val('0');
				} 
				
				// 定位
				var haveCellTowers = $("input[name='haveCellTowers']").val();
				var url = '${ctx}/om/google/position.json';
				$.post(
					url, 
					{
						homeMobileCountryCode:homeMobileCountryCode,
						homeMobileNetworkCode:homeMobileNetworkCode,
						radioType:radioType,
						carrier:carrier,
						considerIp:considerIp,
						haveCellTowers:haveCellTowers,
						cellId:cellId,
						locationAreaCode:locationAreaCode,
						mobileCountryCode:mobileCountryCode,
						mobileNetworkCode:mobileNetworkCode
					}, 
					function(data) {
						if (data.code == '1') {
							$("input[name='lat']").val(data.lat);
							$("input[name='lng']").val(data.lng);
							$("input[name='accuracy']").val(data.accuracy);
							$('#positionForm').submit();
						} else {
							if (isEmpty(data.msg))
								top.$.jBox.info('定位失败', '系统提示');
							else
								top.$.jBox.info(data.msg, '系统提示');
							top.$('.jbox-body .jbox-icon').css('top','55px');
						}
					}
				);
			});
		});
		
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
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/om/google/google">谷歌定位</a></li>
	</ul>
	<!-- tab e -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<form id="inputForm" class="form-horizontal">
		<input type="hidden" name="considerIp" value="false" />
		<input type="hidden" name="haveCellTowers" value="1" />
		<div class="control-group">
			<label class="control-label" for="homeMobileCountryCode">国家代码 (MCC):</label>
			<div class="controls">
				<input type="text" name="homeMobileCountryCode"/>
				(设备的家庭网络的移动国家代码 (MCC))
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="homeMobileNetworkCode">网络代码 (MNC):</label>
			<div class="controls">
				<input type="text" name="homeMobileNetworkCode"/>
				(设备的家庭网络的移动网络代码 (MNC))
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="radioType">网络类型:</label>
			<div class="controls">
				<input type="text" name="radioType"/>
				(移动无线网络类型。支持的值有 lte、gsm、cdma 和 wcdma。虽然此字段是可选的，但如果提供了相应的值，就应该将此字段包括在内，以获得更精确的结果)
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="carrier">运营商名称:</label>
			<div class="controls">
				<input type="text" name="carrier"/>
			</div>
		</div>
		<div class="control-group">
			<input type="checkbox" id="cellTowers" checked="checked" />
			<label class="control-label" for="cellTowers">电话基站参数</label>
		</div>
		<div class="control-group">
			<label class="control-label" for="cellId">小区唯一标识符:</label>
			<div class="controls">
				<input type="text" name="cellId"/>
				<br />(小区的唯一标识符。在 GSM 上，这就是小区 ID (CID)；CDMA 网络使用的是基站 ID (BID)。WCDMA 网络使用 UTRAN/GERAN 小区标识 (UC-Id)，这是一个 32 位的值，由无线网络控制器 (RNC) 和小区 ID 连接而成。在 WCDMA 网络中，如果只指定 16 位的小区 ID 值，返回的结果可能会不准确)
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="locationAreaCode">位置区域代码/网络 ID:</label>
			<div class="controls">
				<input type="text" name="locationAreaCode"/>
				(GSM 和 WCDMA 网络的位置区域代码 (LAC)。CDMA 网络的网络 ID (NID))
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="mobileCountryCode">国家代码 (MCC):</label>
			<div class="controls">
				<input type="text" name="mobileCountryCode"/>
				(移动电话基站的移动国家代码 (MCC))
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="mobileNetworkCode">网络代码 (MNC):</label>
			<div class="controls">
				<input type="text" name="mobileNetworkCode"/>
				(移动电话基站的移动网络代码。对于 GSM 和 WCDMA，这就是 MNC；CDMA 使用的是系统 ID (SID)。)
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="定 位"/>
		</div>
	</form>
	
	<form id="positionForm" action="${ctx}/om/google/showPosition" method="post" target="_blank" class="form-horizontal">
		<input type="hidden" name="lat" />
		<input type="hidden" name="lng" />
		<input type="hidden" name="accuracy" />
	</form>
</body>
</html>