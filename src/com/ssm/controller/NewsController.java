package com.ssm.controller;

import org.aspectj.asm.IProgramElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ssm.po.News;
import com.ssm.service.NewsService;
import com.ssm.utils.FileUtil;

@Controller
public class NewsController {

	@Autowired
	private NewsService newsService;
	//120.27.33.180:8080
	private static String IP = "120.27.33.180:8080";
	

/*	@RequestMapping(value = "/findPageNewss/{pageId}", method = RequestMethod.GET)
	public ModelAndView findPageNewss(@PathVariable("pageId") int pageId)
			throws Exception {
		String news = newsService.findNewsByAcademyId(1, pageId);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("news", news);
		modelAndView.setViewName("home");
		System.out.println(news);
		return modelAndView;
	}*/
	
	
	@RequestMapping(value="/news/{academy}/{newsNum}",method = RequestMethod.GET)
	public String setNewsAccessNum(@PathVariable("academy") String academy,@PathVariable("newsNum") int newsNum)
			throws Exception{
		//				 http://localhost:8080/SCU_News_Notice/news/computer/399
		String content = "http://"+IP+"/SCU_News_Notice/news/" + academy + "/" + newsNum;
		int newsId = newsService.findNewsId(content.trim());
		News news = newsService.getNewsById(newsId);
		int accessNum = news.getAccessNum();
		news.setAccessNum(accessNum + 1);
		newsService.updateNew(news);
		
	
		return "/news"+"/"+academy+"/"+newsNum;
	}
	
	/**
	 * 获取计算机学院的新闻
	 * @param pageId
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/findPageNews/{academyId}/{pageId}", method = RequestMethod.GET)
	public String findPageNews(@PathVariable("academyId") int academyId,@PathVariable("pageId") int pageId)
			throws Exception {
		String news = newsService.findNewsByAcademyId(academyId, pageId);
//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.addObject("news", news);
//		modelAndView.setViewName("home");
//		System.out.println(news);
		return news;
	}
}
