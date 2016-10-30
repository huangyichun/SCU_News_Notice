package com.ssm.service;

import java.util.List;

import com.ssm.po.News;

public interface NewsService {

	// 通过id获取新闻信息
	public News getNewsById(Integer id) throws Exception;

	// 列出所有的新闻信息
	public List<News> listAllNews() throws Exception;

	// 插入新闻信息
	public void insertNews(News news) throws Exception;
	// 批量插入新闻信息
		public void insertNewsList(List<News> newsList)throws Exception;

	// 删除所有新闻信息
	public void deleteAllNews() throws Exception;

	// 通过页码查询新闻，一页10条
	public String findPageNews(Integer pageNum) throws Exception;
	
	//根据学院id分页查询新闻信息，每页10条
	public String findNewsByAcademyId(int academyId, int pageNum)throws Exception;

	public int findNewsId(String content)throws Exception;
	
	public void updateNew(News news)throws Exception;
}
