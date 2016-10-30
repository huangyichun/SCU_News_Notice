package com.ssm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ssm.service.AcademyService;

@Controller
public class AcademyController {

	@Autowired
	private AcademyService academyService;
	
	
	
	@RequestMapping("/academys")
	public ModelAndView getAllAcademys(){
		
		ModelAndView modelAndView = new ModelAndView();
		String academy = academyService.findAllAcademy();
		
		modelAndView.addObject("academy", academy);
		modelAndView.setViewName("home");
		
		return modelAndView;
	}
	
	@ResponseBody
	@RequestMapping("/academy")
	
	public String getAllAcademy(){
		
		//ModelAndView modelAndView = new ModelAndView();
		String academy = academyService.findAllAcademy();
		
//		modelAndView.addObject("academy", academy);
//		modelAndView.setViewName("home");
		
		return academy;
	}
}
