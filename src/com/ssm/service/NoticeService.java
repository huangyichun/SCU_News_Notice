package com.ssm.service;

import java.util.List;

import com.ssm.po.Notice;

public interface NoticeService {

	// 根据id查询notice
	public Notice findNoticeById(int id) throws Exception;

	// 删除所有Notice表中的数据
	public void deleteAllNotice() throws Exception;

	// 添加notice表中的数据
	public void insertNotice(Notice notice) throws Exception;

	// 批量插入
	public void insertNoticesList(List<Notice> list)throws Exception;

	// 查询所通知信息
	public List<Notice> findAllNotices()throws Exception;
	
	//分页查询通知信息，每页10条
	public String findPageNotices(int pageNum)throws Exception;
	
	//根据学院id分页查询通知信息，每页10条
	public String findNoticeByAcademyId(int academyId, int pageNum)throws Exception;
	
	public int findNoticeId(String content)throws Exception;
	//public Notice findOneNoticeByAcademyId(int acadmyId); 
	
	public void updateNotice(Notice notice)throws Exception;
	
}
