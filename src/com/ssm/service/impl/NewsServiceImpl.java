package com.ssm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import com.ssm.mapper.NewsMapper;
import com.ssm.po.News;
import com.ssm.service.NewsService;
@Service("newsServcie")
public class NewsServiceImpl implements NewsService {

	@Autowired
	private NewsMapper newsMapper;
	
	private static final int PAGESIZE = 10;

	@Override
	public News getNewsById(Integer id) throws Exception {
		return newsMapper.getNewsById(id);
	}

	@Override
	public List<News> listAllNews() throws Exception {
		// TODO Auto-generated method stub
		return newsMapper.listAllNews();
	}

	@Override
	public String findPageNews(Integer pageNum) throws Exception {
		PageHelper.startPage(pageNum,PAGESIZE);
		List<News> newsList = newsMapper.listAllNews();
		Gson gson = new Gson();		
		return gson.toJson(newsList);
	}

	@Override
	public void insertNews(News news) throws Exception {
		newsMapper.insertNews(news);
	}

	@Override
	public void insertNewsList(List<News> newsList) throws Exception {
		newsMapper.insertNewsList(newsList);
	}

	@Override
	public void deleteAllNews() throws Exception {
		newsMapper.deleteAllNews();
	}

	@Override
	public String findNewsByAcademyId(int academyId, int pageNum)
			throws Exception {
		PageHelper.startPage(pageNum,PAGESIZE);
		List<News> newsList = newsMapper.findNewsByAcademyId(academyId);
		Gson gson = new Gson();		
		return gson.toJson(newsList);
	}

	@Override
	public int findNewsId(String content) throws Exception {
		// TODO Auto-generated method stub
		return newsMapper.findNewsId(content);
	}

	@Override
	public void updateNew(News news) throws Exception {
		
		newsMapper.updateNew(news);
	}

}
