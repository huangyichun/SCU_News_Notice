package com.ssm.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ssm.po.News;
import com.ssm.po.Notice;
import com.ssm.service.NoticeService;

@Controller
public class NoticeController {

	@Autowired
	private NoticeService noticeService;
	private static String IP = "120.27.33.180:8080";
	/**
	 * 测试环境是否集成成功
	 * @return
	 */
	
	//@RequestMapping(value="/news/{academy}/{newsNum}")
	public String home(@PathVariable("academy") String academy,@PathVariable("newsNum") int newsNum){
		return academy+"/"+newsNum;
	}
	
	@RequestMapping(value="/notice/{academy}/{noticeNum}",method = RequestMethod.GET)
	public String setNewsAccessNum(@PathVariable("academy") String academy,@PathVariable("noticeNum") int noticeNum)
			throws Exception{
		//				 http://localhost:8080/SCU_News_Notice/news/computer/399
		String content = "http://"+IP+"/SCU_News_Notice/notice/" + academy + "/" + noticeNum;
		int noticeId = noticeService.findNoticeId(content.trim());
		Notice notice = noticeService.findNoticeById(noticeId);
		int accessNum = notice.getAccessNum();
		notice.setAccessNum(accessNum + 1);
		noticeService.updateNotice(notice);
		
	
		return "/notice"+"/"+academy+"/"+noticeNum;
	}
	/**
	 * 测试环境是否集成成功
	 * @return
	 */
	@RequestMapping("/index")
	public String index(){
		return "index";
	}
	
	
	/**
	 * 测试分页查询是否成功
	 * @param pageId
	 * @return
	 * @throws Exception
	 */
/*	@RequestMapping(value="/findPageNotices/{academyId}/{pageId}", method=RequestMethod.GET)
	public ModelAndView findPageNotices(@PathVariable("pageId")int pageId) throws Exception{
		String notices = noticeService.findPageNotices(pageId);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("notices", notices);
		modelAndView.setViewName("home");
		return modelAndView;
	}
	*/
	/**
	 * 获取学院的通知
	 * @param pageId
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/findPageNotice/{academyId}/{pageId}", method=RequestMethod.GET)
	public String findPageNotice(@PathVariable("academyId")int academyId,@PathVariable("pageId")int pageId) throws Exception{
		String notices = noticeService.findNoticeByAcademyId(academyId, pageId);
		/*ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("notices", notices);
		modelAndView.setViewName("home");*/
		return notices;
	}
	
	
}
