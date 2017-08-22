<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>主控版详情</title>
	<meta name="decorator" content="default"/>
	<style>
		p{margin:0;}
		.list{overfllow:hidden;display:inline-block;}
		.listblock{float:left;margin-left:20px;margin-bottom: 20px;border:1px solid #dedede;width:100px;height:120px;}
		.listblock p{text-align:center;}
		.listblock .colorbg{border-top:1px solid #dedede;margin:10px;width:80px;height:80px;line-height:80px;
							-webkit-box-shadow:2px 2px 2px black;-moz-box-shadow:2px 2px 2px black;
							-moz-border-radius: 50px; -webkit-border-radius: 50px; border-radius: 50px;}
		.colorbg_small{border-top:1px solid #dedede;margin:10px;width:20px;height:20px;line-height:20px;
							-webkit-box-shadow:2px 2px 2px black;-moz-box-shadow:2px 2px 2px black;
							-moz-border-radius: 50px; -webkit-border-radius: 50px; border-radius: 50px;}
	</style>
	<script type="text/javascript">
		$(document).ready(function() {
			// 设备运行状态统计
			$("#btnSubmit").click(function(){
				var simBankId = $('#simBankId option:selected').val();
				if (simBankId=='') {
					top.$.jBox.info('请选择主控版', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				var simBankIndex = $('#simBankId option:selected').index();
				simBankDetail(simBankIndex);
			});
		var simBankIndex=<%= request.getParameter("simBankId")%>
		if(simBankIndex!=null){
		simBankDetail(simBankIndex);
		}
		}); 
		
		// 异步获取主控版详情并显示
		function simBankDetail(simBankIndex) {
			// 异步获取主控版详情
			var url = '${ctx}/mifi/cardBasicInfo/simNodeDetail';
	    	var simBankId = $('option:eq('+simBankIndex+')').val();
			$.post(url, {simBankId:simBankId}, function(data) {
				if ('1'==data.code) {
					// 当前主控版
					$('#currentSimNode').text(data.simBankId);
					// 卡详情
        		    $('#simNodeDetail').html(''); 
					var detailTable = '<div style="margin:10px;"><i class="icon-qrcode"></i>主控版详情</div><div class="list">'    
				    var listMap = data.simIdList;
					var status = '';
        			$.each(listMap, function(i, map){ 
	        			detailTable += '<div class="listblock"><p>'+map.simId+'</p><p class="colorbg"';
	        			if ('0'==map.simStatus) {// 黑色
	        				detailTable += ' style="cursor: pointer; background:#333"';
	        				status = '没插卡';
	        			} else if ('1'==map.simStatus) {// 灰色
        					detailTable += ' style="cursor: pointer; background:#CCC"';
	        				status = '已插SIM卡未激活';
	        			} else if ('2'==map.simStatus) {// 蓝色
        					detailTable += ' style="cursor: pointer; background:#1a99e2"';
	        				status = 'SIM激活完成';
        				}else if ('3'==map.simStatus) {// 绿色
        					detailTable += ' style="cursor: pointer; background:green"';
	        				status = 'SIM激活并连接设备';
						}else if ('4'==map.simStatus) {// 红色
        					detailTable += ' style="cursor: pointer; background:#EF1A1A"';
	        				status = 'SIM因过期或超流量被block';
						}else if ('5'==map.simStatus) {// 蓝灰色
        					detailTable += ' style="cursor: pointer; background:#A3C3D4"';
	        				status = 'SIM初始化过程中';
						}else if ('6'==map.simStatus) {// 浅棕色
        					detailTable += ' style="cursor: pointer; background:#E4AC89"';
	        				status = 'SIM被网络拒绝';
						}else {
        					detailTable += ' style="cursor: pointer; background:yellow"';
	        				status = '状态未知道';
						}
	        			var params = "'"+map.simId+"', '"+status+"', '"+map.iccId+"', '"+map.type+"'";
	        			detailTable += ' onclick="javascript:showInfo('+params+');"></p></div>';
       				}); 
        			detailTable += '</div>';
        			var $detailTable = $(detailTable);
        		    $('#simNodeDetail').html($detailTable); 
        		    // 上下主控版处理
        		    var simBankListSize = $('#simBankListSize').val();
        		    if (simBankIndex == 1) {
        		    	$('#previous').addClass('disabled');
            		    $('#previous').html($('<a href="#">&larr; 上一主控版</a>')); 
            		    
        		    	$('#next').removeClass('disabled');
        		    	var nextSimbankId = $('option:eq(2)').val();
            		    $('#next').html($('<a href="#" onclick="javascript:simBankDetail('+nextSimbankId+')">下一主控版 &rarr;</a>')); 
        		    } else if (simBankIndex == simBankListSize) {
        		    	$('#previous').removeClass('disabled');
        		    	var index = simBankListSize - 1;
            		    $('#previous').html($('<a href="#"onclick="javascript:simBankDetail('+index+')">&larr; 上一主控版</a>')); 
        		    	
        		    	$('#next').addClass('disabled');
            		    $('#next').html($('<a href="#">下一主控版 &rarr;</a>')); 
        		    } else {
        		    	$('#previous').removeClass('disabled');
        		    	$('#next').removeClass('disabled');
        		    	var preIndex = simBankIndex - 1;
        		    	var nextIndex = simBankIndex + 1;
            		    $('#previous').html($('<a href="#"onclick="javascript:simBankDetail('+preIndex+')">&larr; 上一主控版</a>'));
            		    $('#next').html($('<a href="#" onclick="javascript:simBankDetail('+nextIndex+')">下一主控版 &rarr;</a>')); 
        		    }
        		    
					// 卡模块显示
					$('#simNodeDiv').show();
				} else{
					top.$.jBox.info(data.msg, '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}
			});
		}
		
		function showInfo(simId, status, iccId, type) {
			var msg = [];
	        msg.push('<p>卡号：'+iccId+'</p>');
	        msg.push('<p>卡状态：'+status+'</p>');
	        msg.push('<p>卡类型：'+type+'</p>');
			top.$.jBox.info(msg.join(''), '卡('+simId+')基本信息');
			top.$('.jbox-body .jbox-icon').css('top','55px');
			return false;
		}
	</script>
</head>
<body>
	<input type="hidden" id="simBankListSize" value="${simBankList.size() }">
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/cardBasicInfo/simNodeView">主控版详情</a></li>
	</ul>
	<!-- tab e -->
	
	<label>主控版：</label> 
	<select id="simBankId" name="simBankId" class="input-medium">
		<option value="">--请选择--</option>
		<c:forEach items="${simBankList }" var="simBank">
			<option value="${simBank.simBankId }" <c:if test="${simBank.simBankId==simBankId}">selected</c:if>>${simBank.simBankId }</option>
		</c:forEach>
	</select>
	&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查 看"/>

	<div id="simNodeDiv" style="display: none;">
		<div id="simNodeDetail">
		</div>
		<div style="width: 310px; margin-left: auto; margin-right: auto;">
			<ul class="pager">
		  		<li id="previous" class="previous">
			  	</li>
	    	 	<li class="active"><span id="currentSimNode"></span></li>
			  	<li id="next" class="next">
			  	</li>
			</ul>
		</div>
	</div>
</body>
</html>