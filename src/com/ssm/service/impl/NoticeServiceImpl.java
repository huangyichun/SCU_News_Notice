package com.ssm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import com.ssm.mapper.NoticeMapper;
import com.ssm.po.Notice;
import com.ssm.service.NoticeService;
@Service("noticeService")
public class NoticeServiceImpl implements NoticeService{

	//定义每页显示的数量
	private static final int PAGESIZE = 10;
	
	@Autowired
	private NoticeMapper noticeMapper;
	
	@Override
	public Notice findNoticeById(int id) throws Exception {
		
		return noticeMapper.findNoticeById(id);
	}

	@Override
	public void deleteAllNotice() throws Exception {
		
		noticeMapper.deleteAllNotice();
	}

	@Override
	public void insertNotice(Notice notice) throws Exception {
		noticeMapper.insertNotice(notice);
	}

	@Override
	public void insertNoticesList(List<Notice> list) throws Exception {
		noticeMapper.insertNoticesList(list);
	}

	@Override
	public List<Notice> findAllNotices() throws Exception {
		return noticeMapper.findAllNotices();
	}
	
	/**
	 * 分页查询，并且返回Json数据格式
	 */
	@Override
	public String findPageNotices(int pageNum) throws Exception {
		PageHelper.startPage(pageNum,PAGESIZE);
		List<Notice> list = noticeMapper.findAllNotices();
		Gson gson = new Gson();
		return gson.toJson(list);
	}

	/**
	 * 根据学院id分页查询。并返回json数据
	 */
	@Override
	public String findNoticeByAcademyId(int academyId, int pageNum) throws Exception {
		PageHelper.startPage(pageNum,PAGESIZE);
		List<Notice> list = noticeMapper.findNoticeByAcademyId(academyId);
		Gson gson = new Gson();
		return gson.toJson(list);
	}

	//根据内容获取学院id
	@Override
	public int findNoticeId(String content) throws Exception {
		// TODO Auto-generated method stub
		return noticeMapper.findNoticeId(content.trim());
	}

	@Override
	public void updateNotice(Notice notice) throws Exception {
		// TODO Auto-generated method stub
		noticeMapper.updateNotice(notice);
	}
	
	

}
