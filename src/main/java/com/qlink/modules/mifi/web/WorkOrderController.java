package main.java.com.qlink.modules.mifi.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.StringUtils;
import com.uu.modules.mifi.condition.WorkOrderCondition;
import com.uu.modules.mifi.entity.WorkOrder;
import com.uu.modules.mifi.entity.WorkOrderMessage;
import com.uu.modules.mifi.service.WorkOrderMessageService;
import com.uu.modules.mifi.service.WorkOrderService;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.utils.UserUtils;

/**
 * 工单控制器
 * 
 * @author shuxin
 * @date 2016年5月31日
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/wOrder")
public class WorkOrderController extends WorkOrderBaseAPI {
	public static Logger logger = LoggerFactory.getLogger(WorkOrderController.class);
	/** 文件类型格式 */
	private static final String FILE_TYPE = "jpg,png,doc,docx,xls,xlsx,ppt,pptx,txt";

	@Autowired
	private WorkOrderService orderService;

	@Autowired
	private WorkOrderMessageService messageService;
	
	/**
	 * 列表页面
	 * 
	 * @athor shuxin
	 * @date 2016年6月2日下午2:20:57
	 * @param condition
	 * @param request
	 * @param response
	 * @param model
	 * @return String
	 */
	@RequiresPermissions("mifi:workOrder:view")
	@RequestMapping(value = { "list", "" })
	public String list(WorkOrderCondition condition, HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		User user = UserUtils.getUser();
		if ("YOUYOUMOB".equals(user.getChannelNameEn()) || "".equals(user.getChannelNameEn())) {
			if (condition.getInitTag() != null) {
				this.getDate(condition, request, response, model);
			}
			return "modules/mifi/workOrderAllList";
		} else {
			if (condition.getInitTag() != null) {
				condition.setChannelSn(user.getChannelNameEn());
				this.getDate(condition, request, response, model);
			}
			return "channel/mifi/workOrderList";
		}
	}

	/**
	 * 获取列表数据
	 * 
	 * @athor shuxin
	 * @date 2016年6月14日下午2:39:48
	 * @param condition
	 * @param request
	 * @param response
	 * @param model
	 *            void
	 */
	private void getDate(WorkOrderCondition condition, HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		Page<WorkOrder> page = orderService.findWorkOrdersByPage(new Page<WorkOrder>(request, response), condition);
		List<WorkOrder> workOrders = page.getList();
		if(!workOrders.isEmpty()){ //没有数据无需更新
			getList(workOrders, condition);
		}
		model.put("page", page);
		model.put("condition", condition);
	}

	/**
	 * 同步接口数据（最新）
	 * 
	 * @athor shuxin
	 * @date 2016年6月12日下午4:58:38
	 * @param workOrders
	 * @param condition
	 */
	private void getList(List<WorkOrder> workOrders, WorkOrderCondition condition) {
		net.sf.json.JSONArray jsonArray = new net.sf.json.JSONArray();
		for (WorkOrder workOrder : workOrders) {
			jsonArray.add(workOrder.getWid());
		}
		condition.setWids(jsonArray.toString());
		// 同步筛选接口数据
		String jsonStr = getWorkOrderListAPI(condition);
		if (StringUtils.isNotBlank(jsonStr)) { // 进行数据同步
			net.sf.json.JSONObject obj = net.sf.json.JSONObject.fromObject(jsonStr);
			int code = obj.getInt("code");
			if (code == 200) {
				net.sf.json.JSONArray arry = obj.getJSONObject("data").getJSONArray("lists");
				Object[] list = arry.toArray();
				for (int i = 0; i < list.length; i++) {
					net.sf.json.JSONObject temp = net.sf.json.JSONObject.fromObject(list[i]);
					if (!temp.get("status").equals(null) && temp.getInt("status") == 3) { // 关闭状态的工单不需要处理
						continue;
					}
					for (WorkOrder workOrder : workOrders) {
						if (!temp.get("id").equals(null) && (temp.getInt("id") == workOrder.getWid())) {
							workOrder.setStatus(temp.get("status").equals(null) ? null : temp.getInt("status"));
							workOrder.setpDiagnosisType(temp.get("problem_diagnosis_type").equals(null) ? null
									: temp.getInt("problem_diagnosis_type"));
						}
					}
				}
			}
		}
	}

	/**
	 * 工单添加页面
	 * 
	 * @athor shuxin
	 * @date 2016年6月3日上午11:54:26
	 * @param model
	 * @return String
	 */
	@RequiresPermissions("mifi:workOrder:view")
	@RequestMapping("formWo")
	public String formWorkOrder(WorkOrder workOrder, Model model) {
		User user = UserUtils.getUser();
		model.addAttribute("user", user);
		return "modules/mifi/workOrderForm";
	}

	/**
	 * 保存
	 * 
	 * @athor shuxin
	 * @date 2016年6月3日下午5:13:33
	 * @param workOrder
	 * @param binderResult
	 * @param request
	 * @param multipartFile
	 * @param redirectAttributes
	 * @return String
	 */
	@RequiresPermissions("mifi:workOrder:edit")
	@RequestMapping("saveWo")
	public String saveWorkOrder(@Valid WorkOrder workOrder, BindingResult binderResult, HttpServletRequest request,
			MultipartFile multipartFile, RedirectAttributes redirectAttributes, Model model) {
		// 必填参数验证
		if (binderResult.hasErrors()) {
			return "modules/mifi/workOrderForm";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if (multipartFile != null && multipartFile.getSize() > 0) {
			try {
				// 上传文件验证
				if (!isRightFileType(multipartFile.getOriginalFilename())) {
					addMessage(model, "上传文件的格式只支持：jpg,png,doc,docx,xls,xlsx,ppt,pptx,pdf,txt");
					return formWorkOrder(workOrder, model);
				}
				if (!validateFileMaxSize(multipartFile)) {
					addMessage(model, "上传文件的大小不能超过10M");
					return formWorkOrder(workOrder, model);
				}
				byte[] fileArray = multipartFile.getBytes();
				map.put("fileArray", fileArray);
				map.put("fileNameArray", multipartFile.getOriginalFilename());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		User user = UserUtils.getUser();
		workOrder.setChannelSn(user.getChannelNameEn());
		// 创建工单请求验证
		String jsonStr = createWorkOrderAPI(workOrder, map);
		if (!StringUtils.isNotBlank(jsonStr)) {
			addMessage(model, "接口通讯失败，请稍后再试......");
			return formWorkOrder(workOrder, model);
		}
		JSONObject json = new JSONObject(jsonStr);
		int key = json.getInt("code");
		if (key == 200) { // 请求成功后，保存工单数据
			JSONObject dataJson = json.getJSONObject("data"); // 获取data json数据
			workOrder.setWid(dataJson.getInt("wid"));
			// 保存文件
			String attachPath = saveFile(multipartFile, Global.getConfig("workOrder.basedir"), request);
			workOrder.setAttachPath(attachPath);
			// 保存工单
			workOrder.setCreateBy(user);
			workOrder.setCreateTime(new Date());
			orderService.saveWorkOrder(workOrder);
		} else { // 接口请求失败
			// 绑定接口调用返回信息给客服展示
			addMessage(model, json.get("info").toString());
			return formWorkOrder(workOrder, model);
		}
		addMessage(redirectAttributes, "创建工单成功");
		return "redirect:" + Global.getAdminPath() + "/mifi/wOrder/list?repage";
	}

	/**
	 * 附件保存
	 * 
	 * @athor shuxin
	 * @date 2016年6月3日下午5:03:15
	 * @param file
	 * @param request
	 * @return String
	 */
	private String saveFile(MultipartFile file, String configPath, HttpServletRequest request) {
		if (file != null && file.getSize() <= 0) {
			return "";
		}
		String contextPath = request.getSession().getServletContext().getRealPath("//");
		String fileName = new Date().getTime() + ".jpg";
		String savePath = configPath + "/" + fileName;
		File targetFile = new File(contextPath + Global.getConfig("workOrder.basedir"), fileName);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		// 保存文件
		try {
			file.transferTo(targetFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return savePath;
	}

	/**
	 * 工单查看
	 * 
	 * @athor shuxin
	 * @date 2016年6月6日下午3:22:18
	 * @param wid
	 * @param model
	 * @return String
	 */
	@RequiresPermissions("mifi:workOrder:view")
	@RequestMapping("view")
	public String view(Integer wid, Model model) {
		if (wid != null) {
			WorkOrder localWOrder = orderService.getWorkOrderById(wid);
			if (localWOrder.getStatus() != null && localWOrder.getStatus() == 3) { // 工单关闭状态，无需调用获取工单详情接口
				model.addAttribute("workOrder", localWOrder);
			    List<WorkOrderMessage> messages =	messageService.findWorkOrderMessagesByWid(wid);
			    model.addAttribute("messages", messages);
			} else {
				String result = getWorkOrderAPI(wid, 1);
				JSONObject workOrderDetail = new JSONObject(result);
				int code = workOrderDetail.getInt("code");
				if (code == 200) {
					JSONObject detail = workOrderDetail.getJSONObject("data").getJSONObject("detail");
					localWOrder.setStatus(detail.get("status").equals(null) ? null : detail.getInt("status"));
					localWOrder.setpDiagnosisType(detail.get("problem_diagnosis_type").equals(null) ? null
							: detail.getInt("problem_diagnosis_type"));
					// 更新本地工单
					orderService.saveWorkOrder(localWOrder);
					List<WorkOrderMessage> messages = getMessages(wid, model);
					model.addAttribute("workOrder", localWOrder);
					model.addAttribute("messages", messages);
				} else {
					addMessage(model, workOrderDetail.getString("info"));
				}
			}
		} else {
			addMessage(model, "工单不存在");
		}
		return "modules/mifi/workOrderView";
	}

	/**
	 * 工单消息添加页面
	 * 
	 * @athor shuxin
	 * @date 2016年6月7日下午2:39:04
	 * @param message
	 * @param model
	 * @return String
	 */
	@RequiresPermissions("mifi:workOrder:view")
	@RequestMapping("formMe")
	public String formWorkOrderMessage(WorkOrderMessage message, Model model) {

		return "modules/mifi/workOrderMessageForm";
	}

	/**
	 * 保存工单会话消息
	 * 
	 * @athor shuxin
	 * @date 2016年6月7日下午4:10:03
	 * @param message
	 * @param binderResult
	 * @param request
	 * @param multipartFile
	 * @param redirectAttributes
	 * @param model
	 * @return String
	 */
	@RequiresPermissions("mifi:workOrder:edit")
	@RequestMapping("saveMe")
	public String saveWorkOrderMessage(@Valid WorkOrderMessage message, BindingResult binderResult,
			HttpServletRequest request, MultipartFile multipartFile, RedirectAttributes redirectAttributes,
			Model model) {
		// 必填参数验证
		if (binderResult.hasErrors()) {
			return "modules/mifi/workOrderMessageForm";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if (multipartFile != null && multipartFile.getSize() > 0) {
			try {
				// 上传文件验证
				if (!isRightFileType(multipartFile.getOriginalFilename())) {
					addMessage(model, "上传文件的格式只支持：jpg,png,doc,docx,xls,xlsx,ppt,pptx,pdf,txt");
					return formWorkOrderMessage(message, model);
				}
				if (!validateFileMaxSize(multipartFile)) {
					addMessage(model, "上传文件的大小不能超过10M");
					return formWorkOrderMessage(message, model);
				}
				byte[] fileArray = multipartFile.getBytes();
				map.put("fileArray", fileArray);
				map.put("fileNameArray", multipartFile.getOriginalFilename());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 创建工单请求验证
		String jsonStr = createWorkOrderMessageAPI(message, map);
		if (!StringUtils.isNotBlank(jsonStr)) {
			addMessage(model, "接口通讯失败，请稍后再试......");
			return formWorkOrderMessage(message, model);
		}
		JSONObject json = new JSONObject(jsonStr);
		int key = json.getInt("code");
		if (key == 200) { // 请求成功后，保存工单数据
			// 保存文件
			String attachPath = saveFile(multipartFile, Global.getConfig("workOrderMessage.basedir"), request);
			message.setAttachPath(attachPath);
			// 保存工单
			messageService.saveWorkOrderMessage(message);
		} else { // 接口请求失败
			// 绑定接口调用返回信息给客服展示
			addMessage(model, json.getString("info"));
			return formWorkOrderMessage(message, model);
		}
		addMessage(redirectAttributes, "创建工单会话消息成功");
		return "redirect:" + Global.getAdminPath() + "/mifi/wOrder/list?repage";
	}

	/**
	 * @athor shuxin
	 * @date 2016年6月7日下午4:19:01
	 * @param wid
	 * @param model
	 * @return String
	 */
	@RequiresPermissions("mifi:workOrder:view")
	@RequestMapping("viewMsg")
	public String viewMessage(Integer wid, Model model) {
		if (wid == null) {
			addMessage(model, "会话消息不存在");
			return "modules/mifi/workOrderMessageView";
		}
		List<WorkOrderMessage> messages = new ArrayList<WorkOrderMessage>();
		WorkOrder wOrder = orderService.getWorkOrderById(wid);
		if (wOrder != null && wOrder.getStatus() != 3) {
			messages = getMessages(wid, model);
		} else {
			messages = messageService.findWorkOrderMessagesByWid(wid);
		}
		model.addAttribute("messages", messages);
		return "modules/mifi/workOrderMessageView";
	}

	/**
	 * 关闭工单页面
	 * 
	 * @athor shuxin
	 * @date 2016年6月8日上午10:06:30
	 * @param workOrder
	 * @return String
	 */
	@RequiresPermissions("mifi:workOrder:view")
	@RequestMapping("closeForm")
	public String colseForm(WorkOrder workOrder) {
		return "modules/mifi/workOrderCloseForm";
	}

	/**
	 * 关闭工单
	 * 
	 * @athor shuxin
	 * @date 2016年6月8日上午10:49:10
	 * @param workOrder
	 * @param model
	 * @return String
	 */
	@RequiresPermissions("mifi:workOrder:edit")
	@RequestMapping("close")
	public String closeWorkOrder(WorkOrder workOrder, Model model, RedirectAttributes redirectAttributes) {
		WorkOrder old = orderService.getWorkOrderById(workOrder.getWid());
		if (old == null) {
			addMessage(model, "关闭的工单不存在");
			return "modules/mifi/workOrderCloseForm";
		}
		String jsonStr = closeWorkOrderAPI(workOrder);
		if (!StringUtils.isNotBlank(jsonStr)) {
			addMessage(model, "接口通讯失败，请稍后再试......");
			return "modules/mifi/workOrderCloseForm";
		}
		JSONObject json = new JSONObject(jsonStr);
		int code = json.getInt("code");
		if (code == 200) {
			addMessage(redirectAttributes, json.get("data").toString());
			old.setStatus(3); // 设置工单状态
			old.setCloseDesc(workOrder.getCloseDesc()); // 设置工单关闭原因
			orderService.saveWorkOrder(old);
		} else {
			addMessage(model, json.get("info").toString());
			return "modules/mifi/workOrderCloseForm";
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/wOrder/list?repage";
	}

	/**
	 * 验证符合要求的上传文件格式
	 * 
	 * @athor shuxin
	 * @date 2016年6月13日上午9:20:26
	 * @param fileName
	 * @return boolean
	 */
	private boolean isRightFileType(String fileName) {
		String[] fileTypes = FILE_TYPE.split(",");
		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
		for (int i = 0; i < fileTypes.length; i++) {
			if (fileTypes[i].equalsIgnoreCase(fileType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 文件大小验证
	 * 
	 * @athor shuxin
	 * @date 2016年6月13日上午9:20:51
	 * @param imgFile
	 * @return boolean
	 */
	private boolean validateFileMaxSize(MultipartFile imgFile) {
		// 最大文件大小10M
		Long maxSize = Long.parseLong(Global.getConfig("web.maxUploadSize"));
		if (imgFile.getSize() > maxSize) {
			return false;
		}
		return true;
	}

	/**
	 * 验证设备编号
	 * 
	 * @athor shuxin
	 * @date 2016年6月16日上午10:15:14
	 * @param deviceSn
	 * @return Boolean
	 */
	@RequestMapping("checkDeviceSn")
	public @ResponseBody Boolean checkDeviceSn(String deviceSn) {
		return orderService.isBelongToChannel(deviceSn);
	}

	/**
	 * 获取会话消息集合
	 * 
	 * @athor shuxin
	 * @date 2016年6月16日下午3:43:09
	 * @param wid
	 * @param model
	 * @return List<WorkOrderMessage>
	 */
	private List<WorkOrderMessage> getMessages(Integer wid, Model model) {
		List<WorkOrderMessage> messages = new ArrayList<WorkOrderMessage>();
		String jsonStr = getWorkOrderMessageAPI(wid); // 获取工单会话接口数据
		JSONObject json = new JSONObject(jsonStr);
		if (json.getInt("code") == 200) {
			JSONArray arry = json.getJSONObject("data").getJSONArray("message");
			int length = arry.length();
			for (int i = 0; i < length; i++) {
				JSONObject obj = (JSONObject) arry.get(i);
				Integer workId = obj.getInt("work_id");
				String content = obj.getString("content");
				String userName = obj.getString("username");
				String createTime = obj.getString("create_time");
				Integer uid = obj.getInt("uid");
				Integer messageType = obj.getInt("message_type");
				Integer isRead = obj.getInt("is_read");
				String attachPath = obj.get("attach_path").equals(null) ? "" : obj.getString("attach_path");
				WorkOrderMessage message = new WorkOrderMessage(workId, messageType, content,
						DateUtils.parseDate(createTime), isRead, uid, userName, attachPath);
				messages.add(message);
			}
			messageService.saveWorkOrderMessageByList(messages); // 批量修改工单会话信息
		} else {
			addMessage(model, json.getString("info"));
		}
		return messages;

	}
}
