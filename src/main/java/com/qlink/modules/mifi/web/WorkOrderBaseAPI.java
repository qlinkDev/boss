package main.java.com.qlink.modules.mifi.web;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.uu.common.utils.DateUtils;
import com.uu.common.utils.Md5Utils;
import com.uu.common.utils.StringUtils;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.condition.WorkOrderCondition;
import com.uu.modules.mifi.entity.WorkOrder;
import com.uu.modules.mifi.entity.WorkOrderMessage;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

/**
 * 工单api接口类
 * 
 * @author shuxin
 * @date 2016年6月7日
 */
public abstract class WorkOrderBaseAPI extends BaseController {
	/** 工单创建接口URL */
	private static final String WORK_ORDER_CREATE_URL = "http://native.youyoumob.com/work/create.json?apiKey=wifi20";
	/** 获取工单详情接口ＵＲＬ */
	private static final String WORK_ORDER_DETAIL_URL = "http://native.youyoumob.com/work/detail.json?apiKey=wifi20";
	/** 添加工单会话消息接口URL */
	private static final String WORK_ORDER_ADD_MESSAGE_URL = "http://native.youyoumob.com/work/addMessage.json?apiKey=wifi20";
	/** 获取工单会话消息接口URL */
	private static final String WORK_ORDER_GET_MESSAGE_URL = "http://native.youyoumob.com/work/getMessage.json?apiKey=wifi20";
	/** 筛选工单接口URL */
	private static final String WORK_ORDER_LIST_URL = "http://native.youyoumob.com/work/list.json?apiKey=wifi20";
	/** 工单关闭接口URL */
	private static final String WORK_ORDER_COLSE_URL = "http://native.youyoumob.com/work/close.json?apiKey=wifi20";

	private static final String SECRE = "a265812ef66f2decd0eecca019";

	/**
	 * 设置httpRequest 头部信息
	 * 
	 * @athor shuxin
	 * @date 2016年6月7日上午11:08:27
	 * @param url
	 * @return HttpRequest
	 */
	public static HttpRequest setHttpRequestHeader(String url) {
		HttpRequest request = HttpRequest.get(url);
		// 安全验证参数
		String nonce = randomStr((int) (Math.random() * 128 + 1));
		String curTime = DateUtils.timeStamp();
		StringBuffer buffer = new StringBuffer(SECRE);
		buffer.append(nonce);
		buffer.append(curTime);
		String checkSum = buffer.toString();
		// request header绑定的参数
		request.method("post");
		request.queryEncoding("UTF-8");
		request.header("User-Agent", "Mozilla/5.0s (Windows NT 6.1; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0");
		request.header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		request.header("Nonce", nonce);
		request.header("CurTime", curTime);
		request.header("CheckSum", Md5Utils.toMD5(checkSum).toLowerCase());
		return request;
	}

	/**
	 * 工单创建APi接口
	 * 
	 * @athor shuxin
	 * @date 2016年6月6日上午9:39:43
	 * @param workOrder
	 * @param map
	 * @return String
	 */
	protected String createWorkOrderAPI(WorkOrder workOrder, Map<String, Object> map) {
		String temp = "";
		if (workOrder != null) {
			HttpRequest request = setHttpRequestHeader(WORK_ORDER_CREATE_URL);
			// API接口请求参数
			request.form("device_sn", workOrder.getDeviceSn());
			if(map != null && map.size() > 0){
				try {
					request.form("problem_desc",  new String(workOrder.getProblemDesc().getBytes(),"iso-8859-1"));
					request.form("files", map.get("fileArray"));
					request.form("attach_name", map.get("fileNameArray"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else {
				request.form("problem_desc", workOrder.getProblemDesc());
			}
			request.form("problem_type", workOrder.getProblemType());
			request.form("channel_sn", workOrder.getChannelSn());
			if (workOrder.getLevel() != null) {
				request.form("level", workOrder.getLevel());
			}
			HttpResponse response = request.send();
			temp = response.bodyText();
		}
		return temp;
	}

	/**
	 * 获取工单详情API接口
	 * 
	 * @athor shuxin
	 * @date 2016年6月6日上午10:53:13
	 * @param wid
	 * @param type
	 * @return String
	 */
	protected String getWorkOrderAPI(Integer wid, Integer type) {
		String temp = "";
		if (wid != null && type != null) {
			HttpRequest request = setHttpRequestHeader(WORK_ORDER_DETAIL_URL);
			// API接口请求参数
			request.form("wid", wid);
			request.form("type", type);
			HttpResponse response = request.send();
			temp = response.bodyText();
		}
		return temp;

	}

	/**
	 * 创建工单会话信息接口API
	 * 
	 * @athor shuxin
	 * @date 2016年6月7日下午4:26:11
	 * @param message
	 * @param map
	 * @return String
	 */
	protected String createWorkOrderMessageAPI(WorkOrderMessage message, Map<String, Object> map) {
		String temp = "";
		if (message != null) {
			HttpRequest request = setHttpRequestHeader(WORK_ORDER_ADD_MESSAGE_URL);
			// API接口请求参数
			request.form("wid", message.getWid());
			request.form("message_type", message.getMessageType());
			if(map != null && map.size() > 0){
				try {
					request.form("content",  new String(message.getContent().getBytes(),"iso-8859-1"));
					request.form("files", map.get("fileArray"));
					request.form("attach_name", map.get("fileNameArray"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else {
				request.form("content", message.getContent());
			}
			HttpResponse response = request.send();
			temp = response.bodyText();
		}
		return temp;
	}

	/**
	 * 获取工单会话消息接口API
	 * 
	 * @athor shuxin
	 * @date 2016年6月7日下午4:27:53
	 * @param wid
	 * @return String
	 */
	protected String getWorkOrderMessageAPI(Integer wid) {
		String temp = "";
		if (wid != null) {
			HttpRequest request = setHttpRequestHeader(WORK_ORDER_GET_MESSAGE_URL);
			// API接口请求参数
			request.form("wid", wid);
			HttpResponse response = request.send();
			temp = response.bodyText();
		}
		return temp;
	}
	
	protected String getWorkOrderListAPI(WorkOrderCondition condition){
		String temp = "";
		if(condition != null){
			HttpRequest request = setHttpRequestHeader(WORK_ORDER_LIST_URL);
			// API接口请求参数
			request.form("wids", condition.getWids());
			if(StringUtils.isNotBlank(condition.getChannelSn())){
				request.form("channel_sn",condition.getChannelSn());
			}
			if(condition.getStatus() != null){
				request.form("status",condition.getStatus());
			}
			if(StringUtils.isNotBlank(condition.getStartDate())){
				request.form("create_date_start",condition.getStartDate());
			}
			if(StringUtils.isNotBlank(condition.getEndDate())){
				request.form("create_date_end",condition.getEndDate());
			}
			HttpResponse response = request.send();
			temp = response.bodyText();
		}
		return temp;
	}

	/**
	 * 关闭工单接口API
	 * 
	 * @athor shuxin
	 * @date 2016年6月8日上午10:35:21
	 * @param order
	 * @return String
	 */
	protected String closeWorkOrderAPI(WorkOrder order) {
		String temp = "";
		if (order != null) {
			HttpRequest request = setHttpRequestHeader(WORK_ORDER_COLSE_URL);
			// API接口请求参数
			request.form("wid", order.getWid());
			request.form("close_desc", order.getCloseDesc());
			if (order.getIsResove() != null) {
				request.form("is_resolve", order.getCloseDesc());
			}
			if (order.getIsRefund() != null) {
				request.form("is_refund", order.getIsResove());
			}
			if (order.getIsRefund() != null && order.getIsRefund() == 1) {
				request.form("refund_desc", order.getRefundDesc());
			}
			if (order.getIsLoss() != null) {
				request.form("is_loss", order.getIsLoss());
			}
			if (order.getLossPlug() != null) {
				request.form("loss_plug", order.getLossPlug());
			}
			if (order.getLossDataLine() != null) {
				request.form("loss_data_line", order.getLossDataLine());
			}
			if (order.getLossPassword() != null) {
				request.form("loss_password", order.getLossPassword());
			}
			HttpResponse response = request.send();
			temp = response.bodyText();
		}
		return temp;
	}

	/**
	 * 随机数生成（由数字、大小写字母组成）
	 * 
	 * @athor shuxin
	 * @date 2016年6月3日下午5:07:02
	 * @param len
	 * @return String
	 */
	private static String randomStr(int len) {
		if (len == 0) {
			return "";
		}
		int a = (int) (Math.random() * 3);
		if (a == 0) {
			return ((int) (Math.random() * 10)) + randomStr(len - 1);
		} else if (a == 1) {
			return ((char) ((int) (Math.random() * 26) + 65)) + randomStr(len - 1);
		} else {
			return ((char) ((int) (Math.random() * 26) + 97)) + randomStr(len - 1);
		}
	}

}
