package test.dao;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.ehcache.search.expression.Not;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ssm.mapper.AcademyMapper;
import com.ssm.mapper.NewsMapper;
import com.ssm.mapper.NoticeMapper;
import com.ssm.po.Academy;
import com.ssm.po.News;
import com.ssm.po.Notice;
import com.ssm.utils.MessageUtils;



public class MybatisDaoTest {

	private ApplicationContext applicationContext;
	private NoticeMapper noticeMapper;
	private AcademyMapper academyMapper;
	private NewsMapper newsMapper;
	//在setUp这个方法得到Spring容器
	@Before
	public void setUp()throws Exception{
		applicationContext = new ClassPathXmlApplicationContext(
				"classpath:spring/applicationContext-dao.xml");
		noticeMapper = (NoticeMapper) applicationContext.getBean(NoticeMapper.class);
		newsMapper = applicationContext.getBean(NewsMapper.class);
		academyMapper = applicationContext.getBean(AcademyMapper.class);
	}
	
	
	
	@Test
	public void updateNews() throws Exception{
		Notice news = noticeMapper.findNoticeById(1);
		
		news.setAccessNum(3);
		noticeMapper.updateNotice(news);
	}
	
	
	
	/**
	 * 获取最新更新的通知url
	 * @param academyId
	 * @param url
	 * @param headUrl
	 * @param format
	 * @param pattenUrl
	 * @return
	 */
	public List<String> getUpdateNoticeUrl(int academyId, String url, String headUrl, String format, String pattenUrl){
		List<String> list = new ArrayList<String>();
		Notice notice = noticeMapper.findOneNoticeByAcademyId(academyId);
		//数据库中最新通知的url
		String firstUrl = notice.getAddress();
		List<String> urlsList = MessageUtils.getUrls(url, pattenUrl, headUrl, format);
		for(String string  : urlsList){
			if(string.compareTo(firstUrl) != 0){
				list.add(string);
			}else{
				return list;
			}
		}
		return list;
	}
	
	
	
	@Test
	public void testFindOneNoticeByAcademyId() {
		
		/*int id =noticeMapper.findNoticeId("http://120.27.33.180:8080/SCU_News_Notice/html/computer/notice/389.html");
		System.out.println("id:"+id);*/
		int newId = newsMapper.findNewsId("http://localhost:8080/SCU_News_Notice/news/computer/399");
		System.out.println("newsId="+newId);
	}
	
	
	@Test
	public void testFindNoticeByAcademyId() {
		//PageHelper.startPage(1, 5);
		// TODO Auto-generated method stub
		List<Notice> list = noticeMapper.findNoticeByAcademyId(1);
		//PageInfo<Notice> pageInfo = new PageInfo<Notice>(list);
		//System.out.println(pageInfo.getTotal());
		for(Notice notice : list){
			System.out.println(notice.getTitle());
		}
		
	}
	
	@Test
	public void testFindAll(){
		PageHelper.startPage(1, 5);
		List<Notice> list = noticeMapper.findAllNotices();
		PageInfo<Notice> pageInfo = new PageInfo<Notice>(list);
		System.out.println(pageInfo.getTotal());
		System.out.println(list.size());
	}
	
	//插入
	@Test
	public void testInsert() throws Exception{
		List<Notice> list = new ArrayList<Notice>();
		Notice notice = new Notice();
		notice.setAcademyId(1);
		Date date = new Date();
		notice.setTime(date);
		notice.setAddress("huang");
		notice.setTitle("huangyichun");
		notice.setContent("huagiewyeuf");
		notice.setAccessNum(0);
		
		list.add(notice);
		
		noticeMapper.insertNoticesList(list);
	}
	
	@Test
	public void findNoticeTest() throws Exception{
		
		Notice notice = noticeMapper.findNoticeById(1);
		System.out.println(notice);
	}
	
	@Test
	public void deleteTest(){
		noticeMapper.deleteAllNotice();
	}
	
	
	@Test
	public void findAcademyTest(){
		List<Academy> list = academyMapper.findAllAcademy();
		for(Academy  academy : list){
			System.out.println(academy.toString());
		}
	}
	
}
