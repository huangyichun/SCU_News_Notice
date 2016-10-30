package com.ssm.mapper;

import java.util.List;

import com.ssm.po.Notice;

public interface NoticeMapper {

	//根据id查询notice
	public Notice findNoticeById(int id) ;
	
	//删除notice表中的所有数据
	public void deleteAllNotice() ;
	
	//添加notice表中的数据
	public void insertNotice(Notice notice);
	
	//批量插入
	public void insertNoticesList(List<Notice> list);
	
	//查询所通知信息
	public List<Notice> findAllNotices();
	
	//根据学院id查找通知,从大到小排序
	public List<Notice> findNoticeByAcademyId(int academyId);
	
	//根据学院id查找最后一条通知
	public Notice findOneNoticeByAcademyId(int academyId);
	
	//根据学院id查找该学院通知条数
	public int countNoticeByAcademy(int academyId);
	
	public int findNoticeId(String content);
	
	public void updateNotice(Notice notice);
}
