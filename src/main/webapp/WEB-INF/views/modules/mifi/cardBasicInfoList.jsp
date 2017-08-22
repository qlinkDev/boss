<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>SIM卡查询</title>
	<meta name="decorator" content="default" />
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<style type="text/css">
		.sort {
			color: #0663A2;
			cursor: pointer;
		}
		#tableDiv {overflow:auto;}
		table th {white-space: nowrap;}
		table td {white-space: nowrap;}
		.cardTypeSpan {
			display:-moz-inline-box;
			display:inline-block;
			width:180px; 
			margin-top: 5px;
		}
	</style>
	<script type="text/javascript">
		$(document).ready(
				function() {
					$("#btnExport").click(
							function() {
								top.$.jBox.confirm("确认要导出卡数据吗？", "系统提示", function(
										v, h, f) {
									if (v == "ok") {
										$("#searchForm").attr("action",
												"${ctx}/mifi/cardBasicInfo/export")
												.submit();
										$("#searchForm").attr("action",
												"${ctx}/mifi/cardBasicInfo/list");
									}
								}, {
									buttonsFocus : 1
								});
								top.$('.jbox-body .jbox-icon').css('top', '55px');
							});
					$("#btnImport").click(function() {
						$.jBox($("#importBox").html(), {
							title : "导入数据",
							buttons : {
								"关闭" : true
							},
							bottomText : "导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"
						});
					});
					
					//卡数据恢复
					$("#btnRecovery").click(function() {
						$.jBox($("#importRecoveryBox").html(), {
							title : "导入要恢复的卡数据",
							buttons : {
								"关闭" : true
							},
							bottomText : "导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"
						});
					});
					
					//修改APN信息
					$("#btnApn").click(function() {
						$.jBox($("#importAPN").html(), {
							title : "导入要修改APN的卡数据",
							buttons : {
								"关闭" : true
							},
							bottomText : "导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"
						});
					});
					
					// 回收状态为3的卡
					$('#btnStatus3').click(function() {
						top.$.jBox.confirm("确认要回收状态为3但未绑定设备的卡吗？", "系统提示", function(v, h, f) {
							if (v == "ok") {
								$.jBox.tip("正在回收卡...", 'loading', {persistent: true});
								var url = '${ctx}/mifi/cardBasicInfo/recovery3.json';
								$.post(url, {}, function(data) {
									$.jBox.closeTip();
									if ('success' == data.status) {
										top.$.jBox.info('本次共回收[' + data.count + ']张卡', '系统提示');
									} else {
										top.$.jBox.info(data.message, '系统提示');
									}
									top.$('.jbox-body .jbox-icon').css('top','55px');
								});
							}
						}, {
							buttonsFocus : 1
						});
						top.$('.jbox-body .jbox-icon').css('top', '55px');
					});
					
					// 回收状态为6的卡
					$('#btnStatus6').click(function() {
						top.$.jBox.confirm("确认要回收状态为6未超流量未过期的卡吗？", "系统提示", function(v, h, f) {
							if (v == "ok") {
								$.jBox($("#recovery6Div").html(), {title:"回收状态为6的卡", width:'auto', height:'auto', bottomText: '被选定卡类型的所有状态为6的卡状态将重置为2，必须选择卡类型。', buttons: { '关闭': 'ok' }});
							}
						}, {
							buttonsFocus : 1
						});
						top.$('.jbox-body .jbox-icon').css('top', '55px');
					});
					
					var typeArr = '${typeArr}';
					if(!!typeArr){
						typeArr = eval('('+ typeArr+ ')');
						$('#typeArr').select2().val(typeArr).trigger("change");
					}
					
					var allowedSourceArr = '${allowedSourceArr}';
					if(!!allowedSourceArr){
						allowedSourceArr = eval('('+ allowedSourceArr+ ')');
						$('#allowedSourceArr').select2().val(allowedSourceArr).trigger("change");
					}
				});
		function page(n, s) {
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}
	
		
		// 重置激活时间
		function resetTime() {
			$.jBox($("#resetTimeDiv").html(), {title:"重置激活时间", width:'auto', height:'auto', bottomText: '被选定卡类型的所有卡的激活时间将被重置，默认重置全部卡。', buttons: { '关闭': 'ok' }});
		}
		
		function resetTimeSubmit() {
			$.jBox.tip("正在重置激活时间...", 'loading');
			var cardTypes = "";
			$('.cardType').each(function(i) {
				if ($(this).attr('checked') == 'checked') {
					cardTypes = cardTypes + ',' + $(this).val();
				}
			});
			
			var url = '${ctx}/mifi/cardBasicInfo/resetTime';
			$.post(url, {cardTypes:cardTypes}, function(data) {
				$.jBox.tip(data.msg)
			});
		};
		
		// 回收状态为6的卡
		function recovery6() {
			var cardTypes = "";
			$('.cardType').each(function(i) {
				if ($(this).attr('checked') == 'checked') {
					cardTypes = cardTypes + ',' + $(this).val();
				}
			});
			if (isEmpty(cardTypes)) {
				$.jBox.tip('请选择卡类型', 'warning');
				return false;
			}
			cardTypes = cardTypes.substring(1);
	
			$.jBox.tip("正在回收卡...", 'loading', {persistent: true});
			var url = '${ctx}/mifi/cardBasicInfo/recovery6.json';
			$.post(url, {cardTypes:cardTypes}, function(data) {
				$.jBox.closeTip();
				var msg = "";
				if ('success' == data.status) {
					msg = data.message + ', 本次共回收[' + data.count + ']张卡';
				} else {
					msg = data.message
				}
				$.jBox.tip(msg, data.status);
			});
		};
	</script>
</head>
<body>
	<!-- 回收状态为6的卡 start -->
	<div id="recovery6Div" class="hide">
		<div style="margin:10px 0 0 30px;">
		<c:forEach items="${fns:getListByTable('sim_card_type','card_type','card_type_name')}" var="cardTypeValue" varStatus="status">
		<span class="cardTypeSpan">
			<label>
				<input class="cardType" type="checkbox" value="${cardTypeValue.card_type }" />${cardTypeValue.card_type_name }
			</label>
		</span>
		<c:if test="${(status.index+1)%4 == 0 }">
		<br />
		</c:if>
		</c:forEach>
		</div>
		<div style="margin:10px 0 10px 30px;">
			<input type="button"  class="btn btn-primary" onclick="javascript:recovery6();" value="回  收"/>
		</div>
	</div>
	<!-- 回收状态为6的卡 end -->
	<!-- 激活时间重置 start -->
	<div id="resetTimeDiv" class="hide">
		<div style="margin:10px 0 0 30px;">
		<c:forEach items="${fns:getListByTable('sim_card_type','card_type','card_type_name')}" var="cardTypeValue" varStatus="status">
		<span class="cardTypeSpan">
			<label>
				<input class="cardType" type="checkbox" value="${cardTypeValue.card_type }" />${cardTypeValue.card_type_name }
			</label>
		</span>
		<c:if test="${(status.index+1)%4 == 0 }">
		<br />
		</c:if>
		</c:forEach>
		</div>
		<div style="margin:10px 0 10px 30px;">
			<input type="button"  class="btn btn-primary" onclick="javascript:resetTimeSubmit();" value="重  置"/>
		</div>
	</div>
	<!-- 激活时间重置 end -->
	<div id="importBox" class="hide">
		<form id="importForm" action="${ctx}/mifi/cardBasicInfo/import"
			method="post" enctype="multipart/form-data"
			style="padding-left: 20px; text-align: center;" class="form-search"
			onsubmit="loading('正在导入，请稍等...');">
			<br /> <input id="uploadFile" name="file" type="file"
				style="width: 330px" /><br /> <br /> <input id="btnImportSubmit"
				class="btn btn-primary" type="submit" value="   导    入   " /> <a
				href="${ctx}/mifi/cardBasicInfo/import/template">下载模板</a>
		</form>
	</div>
	<div id="importAPN" class="hide">
		<form id="importApnForm" action="${ctx }/mifi/cardBasicInfo/importApnFile"
			method="post" enctype="multipart/form-data"
			style="padding-left: 20px; text-align: center;" class="form-search"
			onsubmit="loading('正在导入，请稍等...');">
			<br /> <input id="uploadFile" name="file" type="file"
				style="width: 330px" /><br /> <br /> <input id="btnImportSubmit"
				class="btn btn-primary" type="submit" value="   导    入   " /> <a
				href="${ctx}/mifi/cardBasicInfo/import/APN">下载模板</a>
		</form>
	</div>
	<div id="importRecoveryBox" class="hide">
		<form id="importRecoveryForm" action="${ctx}/mifi/cardBasicInfo/importRecovery" method="post" enctype="multipart/form-data" style="padding-left: 20px; text-align: center;" class="form-search" onsubmit="loading('正在导入，请稍等...');">
			<input id="uploadFile" name="file" type="file" style="width: 330px"/><br/><br/><input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
		</form>
	</div>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/cardBasicInfo/list">SIM卡列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="cardBasicInfo" action="${ctx}/mifi/cardBasicInfo/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<div>
			<label>卡号：</label>
			<input id="sn" name="sn" type="text" maxlength="50" class="input-medium required" value="${cardBasicInfo.sn}" /> 
			<label>批次号：</label>
			<input id="bath" name="bath" type="text" maxlength="50" class="input-small required" value="${cardBasicInfo.bath}" />
			<label>卡类型：</label>
			<select id="typeArr" name="typeArr" class="input-xxlarge" multiple="multiple">
				<option value="noBind">未绑定卡</option>
				<c:forEach items="${fns:getListByTable('sim_card_type','card_type','card_type_name')}" var="cardTypeValue">
					<option value="${cardTypeValue.card_type}">${cardTypeValue.card_type_name}</option>
				</c:forEach>
			</select>
			<label>卡允许使用渠道：</label>
			<select id="allowedSourceArr" name="allowedSourceArr" class="input-xxlarge" multiple="multiple">
				<option value="ALL">所有渠道</option>
				<c:forEach items="${fns:getListByTableAndWhere('om_channel','channel_name_en','channel_name',' and del_flag = 0 ')}" var="sourceTypeValue">
					<option value="${sourceTypeValue.channel_name_en}">${sourceTypeValue.channel_name}</option>
				</c:forEach>
			</select>
			<div style="margin-top: 10px;">
		    <label>卡状态：</label>
			<select id="simStatus" name="simStatus" class="input-large">
				<option value="">请选择</option>
				<c:forEach items="${fns:getDictList('usimstatus')}" var="simStatusValue">
					<option value="${simStatusValue.value}" <c:if test="${simStatus eq simStatusValue.value }">selected</c:if>>${simStatusValue.value}|${simStatusValue.label}</option>
				</c:forEach>
			</select>
			<label>所属渠道：</label>
			<form:select path="sourceType" class="input-medium required">
	        	<form:option value="" label="请选择"/>
	        	<form:options items="${fns:getChannelList()}" itemLabel="channelName" itemValue="channelNameEn" htmlEscape="false"/>
	        </form:select>
			<label>卡槽编号：</label>
			<input id="simbankid" name="simbankid" type="text" maxlength="50" class="input-medium required" value="${simbankid}" /> 
			<label>卡槽位置：</label>
			<input id="simid" name="simid" type="text" maxlength="50" class="input-small required" value="${simid}" />
			<label>是否在卡槽上：</label>
			<form:select path="inBank" class="input-small">
				<option value="">--请选择--</option>
				<option value="1" <c:if test="${cardBasicInfo.inBank eq '1' }">selected</c:if>>是</option>
			</form:select> 
			<label>入库时间：</label> 
			<input id="createDateStart" name="createDateStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate required" value="${cardBasicInfo.createDateStart }" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />
			到
			<input id="createDateEnd" name="createDateEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate required" value="${cardBasicInfo.createDateEnd }" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />&nbsp;&nbsp; 
			</div>
			<div style="margin-top: 10px;">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" />&nbsp;
			<input id="btnExport" class="btn btn-primary" type="button" value="导出" />&nbsp;
			
			<shiro:hasPermission name="mifi:cardBasicInfo:import">
			<input id="btnImport" class="btn btn-primary" type="button" value="导入" />&nbsp;
			<input id="btnRecovery" class="btn btn-primary" type="button" value="卡数据恢复" />
			</shiro:hasPermission>
			
			<shiro:hasPermission name="mifi:cardBasicInfo:time">
			<input id="btnResetTime" class="btn btn-primary" type="button" value="激活时间重置" onclick="javascript:resetTime();" />&nbsp;
			</shiro:hasPermission>
			
			<shiro:hasPermission name="mifi:cardBasicInfo:apnInfo">
			<input id="btnApn" class="btn btn-primary" type="button" value="APN信息修改" />&nbsp;
			</shiro:hasPermission>
			
			<shiro:hasPermission name="mifi:cardBasicInfo:recovery">
			<input id="btnStatus3" class="btn btn-primary" type="button" value="回收状态为3的卡" />&nbsp;
			<input id="btnStatus6" class="btn btn-primary" type="button" value="回收状态为6的卡" />
			</shiro:hasPermission>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}" />
	<div id="tableDiv">
		<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width:100%">
			<thead>
				<tr>
					<th style="display: none">唯一标识</th>
					<th>卡号</th>
					<th>卡状态</th>
					<th>卡类型</th>
					<th>所属渠道</th>
					<th>激活时间</th>
					<th>卡槽编号</th>
					<th>卡槽位置</th>
					<th>卡总流量(M)</th>
					<th>卡已使用流量(M)</th>
					<th>allowedmcc</th>
					<th>imsi</th>
					<th>iccid</th>
					<th>pin</th>
					<th>puk</th>
					<th>批次号</th>
					<th>供应商</th>
					<th>入库时间</th>
					<th>入库用户</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${page.list}" var="a">
					<tr>
						<td style="display: none">${a.id}</td>
						<td>${a.sn}</td>
						<td><c:if test="${!empty a.USIMSTATUS }">${a.USIMSTATUS}|${fns:getDictLabel(a.USIMSTATUS, 'usimstatus', '未配置状态')}</c:if></td>
						<td><c:if test="${!empty a.type }">${a.type}|${fns:getLabelByTable('sim_card_type', 'card_type','card_type_name', a.type)}</c:if></td>
						<td>${a.source_type}|${fns:getLabelByTableAndWhere('om_channel', 'channel_name_en', 'channel_name', ' and del_flag = 0 ', a.source_type)}</td>
						<td><fmt:formatDate value="${a.stamp_firstactive}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td>${a.sim_bank_id}</td>
						<td>${a.sim_id}</td>
						<td>${a.DATACAP}</td>
						<td>${a.DATAUSED}</td>
						<td>${a.allowedmcc}</td>
						<td>${a.imsi}</td>
						<td>${a.iccid}</td>
						<td>${a.pin}</td>
						<td>${a.puk}</td>
						<td>${a.bath}</td>
						<td>${a.supplier}</td>
						<td><fmt:formatDate value="${a.create_time}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td>${a.create_user}</td>
						<td>
							<shiro:hasPermission name="mifi:cardBasicInfo:edit">
							<a href="${ctx}/mifi/cardBasicInfo/form?id=${a.id}">修改</a>
							</shiro:hasPermission>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>