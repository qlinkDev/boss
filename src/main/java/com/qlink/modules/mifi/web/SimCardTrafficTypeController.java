package main.java.com.qlink.modules.mifi.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.shingle.ShingleMatrixFilter.Matrix.Column.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.entity.MifiBasicInfo;
import com.uu.modules.mifi.entity.SimCardType;
import com.uu.modules.mifi.service.MifiTrafficService;
/**
 * 卡类型流量统计查询
 * @Description 
 * @author wangsai
 * @date 2016年12月9日 下午4:37:09
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/simCardTrafficType")
public class SimCardTrafficTypeController extends BaseController{
	public static Logger logger = LoggerFactory.getLogger(SimCardTrafficTypeController.class);

	@Autowired
	MifiTrafficService mifiTrafficService;
/**
 * 查询页
 * @Description 
 * @param paramMap
 * @param request
 * @param response
 * @param model
 * @return 
 * @return String  
 * @author wangsai
 * @date 2016年12月9日 下午4:37:46
 */
	@RequestMapping(value = { "list", "" })
	public String list(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		if (paramMap.containsKey("initTag")) {// 无奈啊,否则默认执行此方法
			Page<HashMap> reqPage = new Page<HashMap>(request, response);
			Page<HashMap> page = mifiTrafficService.findSimCardTrafficType(reqPage, paramMap);
			model.addAttribute("page", page);
		} else {
			paramMap.put("beginDate", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
			paramMap.put("endDate", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
		}
		model.addAllAttributes(paramMap);
		return "modules/mifi/simCardTrafficTypeList";
	}
	/**
	 * 导出
	 * @Description 
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return 
	 * @return String  
	 * @author wangsai
	 * @date 2016年12月9日 下午4:38:01
	 */
	@RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String type = ObjectUtils.toString(paramMap.get("type"));
			String fileName = type+"卡类型流量统计数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx"; 
			List<SimCardType> list = mifiTrafficService.findMifiDeviceListForExport(paramMap);
			if(list!=null && list.size()>0){
				new ExportExcel(type+"卡类型流量统计数据", SimCardType.class).setDataList(list).write(response, fileName).dispose();
				return null;
			}else{
				addMessage(redirectAttributes, "导出卡类型流量统计数据失败！失败信息：没有数据");
			}
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出卡类型流量统计数据失败！失败信息："+e.getMessage());
			e.printStackTrace();
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/simCardTrafficType";
    }
	
}
