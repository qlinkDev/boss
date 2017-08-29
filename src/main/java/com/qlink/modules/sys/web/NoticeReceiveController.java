/** 
 * @Package com.uu.modules.sys.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月18日 上午9:16:37 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.sys.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.web.BaseController;
import com.uu.modules.sys.condition.NoticeReceiveCondition;
import com.uu.modules.sys.entity.Dict;
import com.uu.modules.sys.entity.NoticeReceive;
import com.uu.modules.sys.service.NoticeReceiveService;
import com.uu.modules.sys.utils.DictUtils;
import com.uu.modules.utils.Constants;

/** 
 * @Description 通知接收 对外服务包实现
 * @author yifang.huang
 * @date 2016年4月18日 上午9:16:37 
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/noticeReceive")
public class NoticeReceiveController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(NoticeReceiveController.class);
	
	@Autowired
	private NoticeReceiveService noticeReceiveService;
	
	@ModelAttribute
	public NoticeReceive get(@RequestParam(required=false) String id) {
		
		if (StringUtils.isNotBlank(id))
			return noticeReceiveService.get(id);
		
		return new NoticeReceive();
		
	}
	
	@RequiresPermissions("sys:noticeReceive:view")
	@RequestMapping(value = {"list", ""})
	public String list(NoticeReceiveCondition condtion, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<NoticeReceive> page = noticeReceiveService.find(new Page<NoticeReceive>(request, response), condtion); 
		model.addAttribute("page", page);
		
		return "modules/sys/noticeReceiveList";
		
	}
	
	@RequiresPermissions("sys:noticeReceive:view")
	@RequestMapping(value = "form")
	public String form(NoticeReceive bean, Model model) {
		
		model.addAttribute("noticeReceive", bean);
		
		// 卡监控故障编码
		List<Dict> dictList = DictUtils.getDictList(Constants.DICT_CARD_MONITOR_TYPE);
		
		// 如果是update并且已选择故障编码
		String faultCodes = bean.getFaultCodes();
		
		List<Object[]> result = new ArrayList<Object[]>();
		Object[] tempArr = null;
		for (Dict dict : dictList) {
			
			tempArr = new Object[3];
			tempArr[0] = dict.getValue();
			tempArr[1] = dict.getLabel();
		
			// 回显已选择的渠道
			if (include(faultCodes, dict.getValue()))
				tempArr[2] = "1";
			else 
				tempArr[2] = "0";
			
			result.add(tempArr);
			tempArr = null;
		}
		
		model.addAttribute("dictList", result);
		
		return "modules/sys/noticeReceiveForm";
		
	}

	@RequiresPermissions("sys:noticeReceive:edit")
	@RequestMapping(value = "save")
	public String save(NoticeReceive bean, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/sys/noticeReceive/?repage";
		}
		
		// 配置参数验证(手机号码和邮箱不能同时为空)
		if (!beanValidator(model, bean)){
			return form(bean, model);
		}
		
		// 类别不能重复(20161209版本升级,类别可以重复)
		/*NoticeReceiveCondition condition = new NoticeReceiveCondition();
		condition.setNeId(bean.getId());
		condition.setEqType(bean.getType());
		List<NoticeReceive> nrList = noticeReceiveService.findListByCondition(condition);
		if (nrList!=null && nrList.size()>0) {
			addMessage(model, "类别  [" + bean.getType() +"] 的通知接收配置信息已存在");
			return form(bean, model);
		}*/
		
		// 保存数据
		noticeReceiveService.save(bean);
		
		addMessage(redirectAttributes, "保存通知接收'" + bean.getName() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/sys/noticeReceive/?repage";
		
	}

	@RequiresPermissions("sys:noticeReceive:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/sys/noticeReceive/?repage";
		}
		
		noticeReceiveService.delete(id);
		
		addMessage(redirectAttributes, "删除通知接收成功");
		return "redirect:"+Global.getAdminPath()+"/sys/noticeReceive/?repage";
	}
	
	// 判断故障编码是否被选择
	private boolean include(String faultCodes, String value) {
		
		if (StringUtils.isBlank(faultCodes))
			return false;
		
		if ("ALL".equals(faultCodes))
			return false;
		
		String[] faultCodeArr = faultCodes.split(",");
		for (String code : faultCodeArr) {
			if (code.equals(value)) {
				return true;
			}
		}
		
		return false;
	}

}
