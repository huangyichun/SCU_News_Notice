package com.ssm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ssm.po.News;
import com.ssm.po.Notice;

public interface NewsMapper {

	// 通过id获取新闻信息
	public News getNewsById(Integer id) throws Exception;

	// 列出所有的新闻信息
	public List<News> listAllNews() throws Exception;

	// 插入新闻信息
	public void insertNews(News news) throws Exception;

	// 批量插入新闻信息
	public void insertNewsList(List<News> newsList) throws Exception;

	// 删除所有新闻信息
	public void deleteAllNews() throws Exception;

	// 通过页码查询新闻，一页10条
	public String findPageNews(Integer pageNum) throws Exception;
	
	//根据学院id查找新闻根据id从到小排列
	public List<News> findNewsByAcademyId(int academyId);
	
	
	public News findOneNewsByAcademyId(int academyId);
	//根据学院id查找该学院的新闻数
	public int countNewsByAcademy(int academyId);
	
	public int findNewsId(String content);
	
	public void updateNew(News news);

}
