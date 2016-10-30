package com.ssm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.ssm.mapper.AcademyMapper;
import com.ssm.po.Academy;
import com.ssm.service.AcademyService;

@Service("academyService")
public class AcademyServiceImpl implements AcademyService {

	@Autowired
	private AcademyMapper academyMapper;
	
	/**
	 * 查找所有的学院信息，返回json数据格式
	 */
	@Override
	public String findAllAcademy() {
		List<Academy> list = academyMapper.findAllAcademy(); 
		Gson gson = new Gson();
		
		return gson.toJson(list);
	}

}
