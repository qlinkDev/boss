package main.java.com.qlink.modules.utils;

/**
 * 全局返回码
 * @see http://mp.weixin.qq.com/wiki/17/fa4e1434e57290788bde25603fa2fcbd.html
 * @author liaowu
 *
 */
public class ReturnCode {
	public final static String ERR__1    = "系统繁忙，此时请开发者稍候再试";
	public final static String ERR_0     = "请求成功";
	public final static String ERR_2     = "请求失败";
	public final static String ERR_40001 = "获取token时key错误，或者token无效。";
	public final static String ERR_40002 = "不合法的凭证类型";
	public final static String ERR_40003 = "不合法的userId";
	public final static String ERR_40004 = "不合法的媒体文件类型";
	public final static String ERR_40005 = "不合法的文件类型";
	public final static String ERR_40006 = "不合法的文件大小";
	public final static String ERR_40007 = "不合法的媒体文件id";
	public final static String ERR_40008 = "不合法的消息类型";
	public final static String ERR_40009 = "不合法的图片文件大小";
	public final static String ERR_40010 = "不合法的语音文件大小";
	public final static String ERR_40011 = "不合法的视频文件大小";
	public final static String ERR_40012 = "不合法的缩略图文件大小";
	public final static String ERR_40013 = "不合法的userId，请开发者检查userId的正确性，避免异常字符，注意大小写";
	public final static String ERR_40014 = "不合法的token，请开发者认真比对token的有效性（如是否过期）";
	public final static String ERR_40029 = "不合法的oauth_code";
	public final static String ERR_40030 = "不合法的refresh_token";
	public final static String ERR_40031 = "不合法的userId列表";
	public final static String ERR_40032 = "不合法的userId列表长度";
	public final static String ERR_40033 = "不合法的请求字符，不能包含\\uxxxx格式的字符";
	public final static String ERR_40035 = "不合法的参数";
	public final static String ERR_40038 = "不合法的请求格式";
	public final static String ERR_40039 = "不合法的URL长度";
	public final static String ERR_40137 = "不支持的图片格式";
	public final static String ERR_40036 = "不合法的请求，sign校验失败";
	public final static String ERR_41001 = "缺少token参数";
	public final static String ERR_41002 = "缺少appid参数";
	public final static String ERR_41003 = "缺少refresh_token参数";
	public final static String ERR_41004 = "缺少secret参数";
	public final static String ERR_41005 = "缺少多媒体文件数据";
	public final static String ERR_41008 = "缺少oauth code";
	public final static String ERR_41009 = "缺少userId";
	public final static String ERR_41010 = "缺少timestamp参数";
	public final static String ERR_41011 = "缺少sign参数";
	public final static String ERR_42001 = "token超时，请检查token的有效期";
	public final static String ERR_42002 = "refresh_token超时";
	public final static String ERR_42003 = "oauth_code超时";
	public final static String ERR_43001 = "需要GET请求";
	public final static String ERR_43002 = "需要POST请求";
	public final static String ERR_43003 = "需要HTTPS请求";
	public final static String ERR_44001 = "多媒体文件为空";
	public final static String ERR_44002 = "POST的数据包为空";
	public final static String ERR_44003 = "图文消息内容为空";
	public final static String ERR_44004 = "文本消息内容为空";
	public final static String ERR_45001 = "多媒体文件大小超过限制";
	public final static String ERR_45002 = "消息内容超过限制";
	public final static String ERR_45003 = "标题字段超过限制";
	public final static String ERR_45004 = "描述字段超过限制";
	public final static String ERR_45005 = "链接字段超过限制";
	public final static String ERR_45006 = "图片链接字段超过限制";
	public final static String ERR_45007 = "语音播放时间超过限制";
	public final static String ERR_45008 = "图文消息超过限制";
	public final static String ERR_45009 = "接口调用超过限制";
	public final static String ERR_45015 = "回复时间超过限制";
	public final static String ERR_46001 = "不存在媒体数据";
	public final static String ERR_46004 = "不存在的用户";
	public final static String ERR_46005 = "已存在的用户";
	public final static String ERR_46006 = "登录密码校验失败";
	public final static String ERR_46007 = "支付密码校验失败";
	public final static String ERR_46008 = "已存在绑定关系";
	public final static String ERR_47001 = "解析JSON/XML内容错误";
	public final static String ERR_48001 = "api功能未授权";
	public final static String ERR_49001 = "不合法的请求，内容签名校验失败";
	public final static String ERR_49002 = "交易数据不存在";
	public final static String ERR_50001 = "用户未授权该api";
	public final static String ERR_50002 = "用户受限，可能是违规后接口被封禁";
	public final static String ERR_61451 = "缺少参数或者参数错误(invalid parameter)";
	public final static String ERR_61457 = "无效头像文件类型(invalid file type)";
	public final static String ERR_61450 = "系统错误(system error)";
	public final static String ERR_61500 = "日期格式错误";
	public final static String ERR_61501 = "日期范围错误";
	public final static String ERR_9001001 = "POST数据参数不合法";
	public final static String ERR_9001002 = "远端服务不可用";
	public final static String ERR_9001003 = "Ticket不合法";
	public final static String ERR_9001005 = "获取商户信息失败";
	public final static String ERR_9001006 = "获取userId失败";
	public final static String ERR_9001007 = "上传文件缺失";
	public final static String ERR_9001008 = "上传文件类型不合法";
	public final static String ERR_9001009 = "上传文件尺寸不合法";
	public final static String ERR_9001010 = "上传失败";
	public final static String ERR_9001020 = "帐号不合法";
}
