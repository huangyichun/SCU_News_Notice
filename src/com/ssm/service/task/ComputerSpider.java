package com.ssm.service.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator.IsFirstChild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ssm.mapper.NewsMapper;
import com.ssm.mapper.NoticeMapper;
import com.ssm.po.News;
import com.ssm.po.Notice;
import com.ssm.utils.*;
import com.ssm.utils.college.MessageManufuctureUtils;

@Service("spider")
public class ComputerSpider {

	private static int TOURISM = 8;// 历史学院 没有问题
	private static String TOURISM_COLLEGE = "tourism";

	private static int COMPUTER = 1; // 计算机学院
	private static String COMPUTER_COLLEGE = "computer";

	private static int LABORUNION = 34; // 四川大学工会
	private static String SCU_LABORUNION = "labor";

	private static int MANUFUCTURE = 14;// 制造科学与工程学院
	private static String MANUFUCTURE_COLLEGE = "manufucture";

	private static int PHARMACY = 27;// 华西药学院 没有问题
	private static String PHARMACY_COLLEGE = "pharmacy";

	private static int RECONSTRUCTION = 31;// 灾后重建管理学院 新闻部分页面无法匹配
	private static String RECONSTRUCTION_COLLEGE = "postDisaster";

	// 120.27.33.180:8080
	private static String IP = "120.27.33.180:8080";
	@Autowired
	NoticeMapper noticeMapper;
	@Autowired
	NewsMapper newsMapper;

	boolean IsFirst = true;

	// 1000 * 60 * 60 * 6
	@Scheduled(fixedRate = 1000 * 60 * 60)
	public void getNotices() throws Exception {

		if (IsFirst) {// 第一次启动遍历所有的网站
			List<Notice> notices = getComputerNotice();
			// 将List反转
			Collections.reverse(notices);
			noticeMapper.deleteAllNotice();

			noticeMapper.insertNoticesList(notices);

			List<News> newsList = getComputerNews();
			// 将newsList的内容进行反转
			Collections.reverse(newsList);
			News news = getDragonNews();
			newsMapper.deleteAllNews();

			newsMapper.insertNewsList(newsList);
			if (news.getTitle() != null) {
				newsMapper.insertNews(news);
			}

			// 第一次启动获取所有旅游学院信息
			firstHistoryNewsAndNotice();
			// 第一次启动获取所有四川大学工会信息
			//firstStartLaborUnion();
			firstStartManufacturingEngineer();
			firstStartPharmacy();
			firstStartPostDisaster();
			IsFirst = false;
		} else {// 不是第一次启动，只需更新部分网站信息
			/*
			 * 更新思路：将所有学院第一次爬虫的信息倒序存入，更新时，获取该学院信息的数目，然后+1,
			 * 并且取数据库中该学院最后一条信息，也就是最新信息，然后获取该学院网站上第一页的新闻url与
			 * 该学院的最新信息的url比对。截取不相同的上面部分，然后倒序插入
			 */

			// 更新计算机学院的通知
			updateComputerNotice();
			// 更新计算机学院的新闻
			updateComputerNews();
			updateTourism();
			//updateLabor(); //四川大学工会，问题太多无法使用
			updatePharmacy();
			updateManufucture();
			updatePostDisaster();

		}

	}

	public void updatePostDisaster() {
		updatePostDisasterNews();
		updatePostDisasterNotice();
	}

	public void updatePostDisasterNews() {
		// 学院新闻url
		String url = "http://idmr.scu.edu.cn/xyxw/index.jhtml";
		// 新闻匹配正则表达式
		String pattenUrl = "(/zhuzhan/xyxw/.+?.html)";
		// 新闻完整地址的前部分
		String headUrl = "http://idmr.scu.edu.cn";
		// 尾页页号的正则表达式
		String format = "utf8";

		// 标题的正则表达式
		String patTitle = "<p style=\"text-align: center;font-size:16px;font-weight:bold\">(.+?)</p></br></br>";
		// 时间的正则表达式
		String patTime = "([0-9]{4}[-][0-9]{2}[-][0-9]{2})";
		// 图片的正则表达式
		String patPic = "<img alt=\"\" src=\"(.+?.jpg)\" style=\"width";
		// 内容的正则表达式
		String patContent = "<p style=\"text-align: center;font-size:16px;font-weight:bold\">.+?</p></br></br>(.+?)</div>";

		List<String> urls = getNewsUpdateUrl(RECONSTRUCTION, url, headUrl,
				"utf8", pattenUrl);
		if (urls.size() != 0) {
			int count = newsMapper.countNewsByAcademy(RECONSTRUCTION) + 1;
			List<News> news = MessageUtils.getNews(urls, patTitle, patTime,
					patPic, "http://idmr.scu.edu.cn", patContent, format,
					count, RECONSTRUCTION, RECONSTRUCTION_COLLEGE);
			if (news.size() != 0) {
				try {
					Collections.reverse(news);
					newsMapper.insertNewsList(news);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void updatePostDisasterNotice() {
		// 学院新闻url
		String url = "http://idmr.scu.edu.cn/tzgg/index.jhtml";
		// 新闻匹配正则表达式
		String pattenUrl = "(/zhuzhan/tzgg/.+?.html)";
		// 下一页地址的前半部分
		String nextPageHead = "http://idmr.scu.edu.cn/tzgg/index_";
		// 下一页地址的尾部分
		String nextPageEnd = ".jhtml";
		// 新闻完整地址的前部分
		String headUrl = "http://idmr.scu.edu.cn";
		// 尾页页号的正则表达式
		String mlastPage = "</a> <a href=\"index_(.+?).jhtml\">尾页</a>";

		// 标题的正则表达式
		String patTitle = "<p style=\"text-align: center;font-size:16px;font-weight:bold\">(.+?)</p></br></br>";
		// 时间的正则表达式
		String patTime = "([0-9]{4}[-][0-9]{2}[-][0-9]{2}[-])";
		// 图片的正则表达式
		String patPic = "<img alt=\"\" src=\"(.+?.jpg)\" style=\"width";
		// 内容的正则表达式
		String patContent = "<p style=\"text-align: center;font-size:16px;font-weight:bold\">.+?</p></br></br>(.+?)</div>";

		List<String> urls = getNoticeUpdateUrl(RECONSTRUCTION, url, headUrl,
				"utf8", pattenUrl);
		if (urls.size() != 0) {
			int count = noticeMapper.countNoticeByAcademy(RECONSTRUCTION) + 1;
			String format = "utf-8";
			List<Notice> notices = MessageUtils.getNotices(urls, patTitle,
					patTime, "http://idmr.scu.edu.cn", patContent, format,
					count, RECONSTRUCTION, RECONSTRUCTION_COLLEGE);
			Collections.reverse(notices);
			if (notices.size() != 0) {
				noticeMapper.insertNoticesList(notices);
			}
		}
	}

	public void updateManufucture() {
		updateManufuctureNews();
		updateManufuctureNotice();
	}

	public void updateManufuctureNews() {
		// 获取newslist
		String url = "http://msec.scu.edu.cn/list-113-1.html";
		String nextPageEnd = ".html";
		String nextPageHead = "http://msec.scu.edu.cn/list-113-";
		// <h4><a href="http://msec.scu.edu.cn/content-113-1273-1.html">
		String pattenUrl = "<h4><a href=\"(.+?)\">";
		String headUrl = "";
		String patTitle = " <h5>(.+?)</h5>";

		String patPic = "<img src=\"(.+?)\" />";

		String patTime = "<span>发布时间：(.+?)&nbsp;&nbsp;&nbsp;&nbsp; 点击：<font id=\"hits\">0</font></span>";

		String patContent = "<div class=\"detailed_zt\">(.+?)</div>";

		List<String> urls = getNewsUpdateUrl(MANUFUCTURE, url, headUrl,
				"gb2312", pattenUrl);
		if (urls.size() != 0) {
			int count = newsMapper.countNewsByAcademy(MANUFUCTURE) + 1;
			List<News> news = MessageManufuctureUtils.getNews(urls, patTitle,
					patTime, patPic, "http://msec.scu.edu.cn/", patContent,
					"gb2312", count, MANUFUCTURE, MANUFUCTURE_COLLEGE);
			if (news.size() != 0) {
				try {
					Collections.reverse(news);
					newsMapper.insertNewsList(news);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void updateManufuctureNotice() {
		String url_notice = "http://msec.scu.edu.cn/list-114-1.html";
		String nextPageHead_notice = "http://msec.scu.edu.cn/list-114-";

		String pattenUrl_notice = "<li><a href=\"(http://msec.scu.edu.cn/content.+?html)\">";

		String patTitle_notice = " <h5>(.+?)</h5>";
		String patPic_notice = "<img src=\"(.+?)\" />";
		String patTime_notice = "<span>发布时间：(.+?)&nbsp;&nbsp;&nbsp;&nbsp; 点击：<font id=\"hits\">0</font></span>";
		String patContent_notice = "<div class=\"detailed_zt\">(.+?)<script>";

		List<String> urls = getNoticeUpdateUrl(MANUFUCTURE, url_notice, "",
				"gb2312", pattenUrl_notice);
		if (urls.size() != 0) {
			int count = noticeMapper.countNoticeByAcademy(MANUFUCTURE) + 1;

			List<Notice> notices = MessageManufuctureUtils.getNotices(urls,
					patTitle_notice, patTime_notice, "http://msec.scu.edu.cn/",
					patContent_notice, "gb2312", count, MANUFUCTURE,
					MANUFUCTURE_COLLEGE);
			Collections.reverse(notices);
			if (notices.size() != 0) {
				noticeMapper.insertNoticesList(notices);
			}
		}
	}

	public void updatePharmacy() {
		String newsurl = "http://pharmacy.scu.edu.cn/newslist.aspx?id=14&page=1";
		String patternUrl = "<a href='(/news.aspx\\?id=\\d+?)' target='_blank'";
		String nextPageHead1 = "http://pharmacy.scu.edu.cn/newslist.aspx?id=14&page=";
		String headUrl = "http://pharmacy.scu.edu.cn";
		List<String> urls1 = MessageUtils.getUrls(newsurl, patternUrl,
				nextPageHead1, headUrl, "GBK");

		System.out.println("新闻网页地址：");
		for (String s : urls1) {
			System.out.println(s);
		}
		int count1 = 1;
		String patTitle = "<div class=\"xjxs_tit01\"><span style=\"font-size:22;font-weight:bold;font-family:;color:;\">(.+?)</span>";
		String patTime = "发布时间：(.+?)&nbsp";
		String patPic = "src=\"(.+?.jpg)\"";
		String patContent = "<div class=\"xjxs_nr01[^\t]+?</div>";
		List<String> urls = getNewsUpdateUrl(PHARMACY, newsurl, headUrl, "GBK",
				patternUrl);
		if (urls.size() != 0) {
			String format = "GBK";
			int count = newsMapper.countNewsByAcademy(PHARMACY) + 1;
			List<News> news = MessageUtils.getNews(urls, patTitle, patTime,
					patPic, "http://pharmacy.scu.edu.cn/", patContent, format,
					count, PHARMACY, PHARMACY_COLLEGE);
			if (news.size() != 0) {
				try {
					Collections.reverse(news);
					newsMapper.insertNewsList(news);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		/********************************* 通知 ********************************************/

		String noticeurl = "http://pharmacy.scu.edu.cn/newslist.aspx?id=13&page=1";
		String nextPageHead2 = "http://pharmacy.scu.edu.cn/newslist.aspx?id=13&page=";
		List<String> urls2 = MessageUtils.getUrls(noticeurl, patternUrl,
				nextPageHead2, headUrl, "GBK");

		// 通知页面
		List<String> noticeList = getNoticeUpdateUrl(PHARMACY, noticeurl,
				headUrl, "GBK", patternUrl);
		if (urls.size() != 0) {
			String format = "GBK";
			int count = noticeMapper.countNoticeByAcademy(PHARMACY) + 1;

			List<Notice> notices = MessageUtils.getNotices(noticeList,
					patTitle, patTime, "http://pharmacy.scu.edu.cn/",
					patContent, format, count, PHARMACY, PHARMACY_COLLEGE);
			Collections.reverse(notices);
			if (notices.size() != 0) {
				noticeMapper.insertNoticesList(notices);
			}
		}

	}

	public void updateLabor() {
		updateLaborNews();
		updateLaborNotice();
	}

	public void updateLaborNews() {

		String url = null;
		String pattenUrl = null;
		String nextPageHead = null;
		String nextPageEnd = null;
		String headUrl = null;
		String patTitle = "<TD align=middle width=721 height=27 a><DIV align=center>(.+?)</DIV>";

		String patTime = "<SPAN class=hangjc style=\"LINE-HEIGHT: 30px\" valign=\"bottom\">时间： </SPAN>([0-9].+?)<SPAN class=hangjc style=\"LINE-HEIGHT: 30px\" valign=\"bottom\">";

		String patPic = "align=center src=\"(.+?.jpg)\"";

		String patContent = "<DIV id=BodyLabel>.+?</DIV></DIV>";

		url = "http://xgh.scu.edu.cn/xgh/xwdt/H9602index_1.htm";
		pattenUrl = "<A title=.+? href=(/xgh/.+?/webinfo/.+?) target=_blank>";
		headUrl = "http://xgh.scu.edu.cn/";

		List<String> urls = getNewsUpdateUrl(LABORUNION, url, headUrl, "GBK",
				pattenUrl);
		if (urls.size() != 0) {
			String format = "GBK";
			String picHeadUrl = "http://cs.scu.edu.cn/";
			int count = newsMapper.countNewsByAcademy(LABORUNION) + 1;
			List<News> news = MessageUtils.getNews(urls, patTitle, patTime,
					patPic, picHeadUrl, patContent, format, count, LABORUNION,
					SCU_LABORUNION);
			if (news.size() != 0) {
				try {
					Collections.reverse(news);
					newsMapper.insertNewsList(news);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void updateLaborNotice() {
		String url = null;
		String pattenUrl = null;
		String nextPageHead = null;
		String nextPageEnd = null;
		String headUrl = null;
		// List<String> urls = null;

		url = "http://xgh.scu.edu.cn/xgh/gztz/H9603index_1.htm";
		pattenUrl = "<A title=.+? href=(/xgh/gztz/webinfo/.+?) target=_blank>";
		nextPageHead = "http://xgh.scu.edu.cn/xgh/gztz/H9603index_";
		nextPageEnd = ".htm";
		headUrl = "http://xgh.scu.edu.cn/";

		String patTitle = "<TD align=middle width=721 height=27 a><DIV align=center>(.+?)</DIV>";

		String patTime = "<SPAN class=hangjc style=\"LINE-HEIGHT: 30px\" valign=\"bottom\">时间： </SPAN>([0-9].+?)<SPAN class=hangjc style=\"LINE-HEIGHT: 30px\" valign=\"bottom\">";

		String patPic = "align=center src=\"(.+?.jpg)\"";

		String patContent = "<DIV id=BodyLabel>.+?</DIV></DIV>";

		List<String> urls = getNoticeUpdateUrl(LABORUNION, url, headUrl, "GBK",
				pattenUrl);
		if (urls.size() != 0) {

			String format = "GBK";
			int count = noticeMapper.countNoticeByAcademy(LABORUNION) + 1;

			List<Notice> notices = MessageUtils.getNotices(urls, patTitle,
					patTime, "", patContent, format, count, LABORUNION,
					SCU_LABORUNION);
			Collections.reverse(notices);
			if (notices.size() != 0) {
				noticeMapper.insertNoticesList(notices);
			}
		}
	}

	// 更新旅游学院新闻和通知
	public void updateTourism() {
		// 新闻页面的url地址
		String newsUrl = "http://historytourism.scu.edu.cn/history/news/xueyuandongtai/";
		// 通知页面url地址
		String noticeUrl = "http://historytourism.scu.edu.cn/history/news/gonggaotongzhi/";
		// 匹配每条新闻和通知的url的正则表达式
		String patternUrl = "<li><a href=\"(.+?)\".+?> <span";
		String headUrl = "";
		String format = "GBK";

		// Html正文正则表达式
		String patContent = "(<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">[\\s\\S]*?)<table border=\"0\" cellspacing=\"8\" cellpadding=\"0\" align=\"center\">";
		// 匹配标题的正则表达式
		String patTitle = "<h1>(.+?)</h1>";
		// 匹配时间的正则表达式
		// 匹配完成后再进行字符串处理的方法
		// String patTime =
		// "<span id=\"ctl00_ContentPlaceHolder1_shijian\".+?>(.+?)</span>";

		// 直接匹配Unicode
		String patTime = "\u65f6\u95f4\uff1a(.+?)&nbsp;&nbsp;";

		// 匹配图片
		String patPic = "<img alt=.+?src=\"(.+?)\" />";

		// 图片文件在html源码中被隐藏的部分路径名
		String picHeadUrl = "http://historytourism.scu.edu.cn";
		// http://historytourism.scu.edu.cn/history/d/file/news/xueyuandongtai/2016-10-21/178db6c545aea550c6ea6f815f80b82b.png

		// String url = "http://cs.scu.edu.cn/cs/xyxw/H9501index_1.htm";
		// String pattenUrl = "<A href=(/cs/xyxw/webinfo.+?) target=_blank>";
		// String headUrl = "http://cs.scu.edu.cn";
		// String academy = "computer";
		List<String> urls = getNewsUpdateUrl(TOURISM, newsUrl, headUrl, format,
				patternUrl);
		if (urls.size() != 0) {

			int count = newsMapper.countNewsByAcademy(TOURISM) + 1;
			List<News> news = MessageUtils.getNews(urls, patTitle, patTime,
					patPic, picHeadUrl, patContent, format, count, TOURISM,
					TOURISM_COLLEGE);
			if (news.size() != 0) {
				try {
					Collections.reverse(news);
					newsMapper.insertNewsList(news);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		List<String> noticeUrls = getNoticeUpdateUrl(TOURISM, noticeUrl,
				headUrl, "GBK", patternUrl);
		if (urls.size() != 0) {
			int count = noticeMapper.countNoticeByAcademy(TOURISM) + 1;

			List<Notice> notices = MessageUtils.getNotices(urls, patTitle,
					patTime, picHeadUrl, patContent, format, count, TOURISM,
					TOURISM_COLLEGE);
			Collections.reverse(notices);
			if (notices.size() != 0) {
				noticeMapper.insertNoticesList(notices);
			}
		}

	}

	public void updateComputerNews() {
		int academyId = 1;
		String url = "http://cs.scu.edu.cn/cs/xyxw/H9501index_1.htm";
		String pattenUrl = "<A href=(/cs/xyxw/webinfo.+?) target=_blank>";
		String headUrl = "http://cs.scu.edu.cn";
		String academy = "computer";
		List<String> urls = getNewsUpdateUrl(academyId, url, headUrl, "GBK",
				pattenUrl);
		if (urls.size() != 0) {
			String patTitle = "<DIV align=center> (.+?)</DIV>";
			String patTime = "</SPAN> ([0-9].+?)<SPAN class=hangjc "
					+ "style=\"LINE-HEIGHT: 30px\" valign=\"bottom\">";
			String patPic = "src=\"(.+?.jpg)\"";
			String patContent = "<DIV id=BodyLabel>.+?</DIV>";
			String format = "GBK";
			String picHeadUrl = "http://cs.scu.edu.cn/";
			int count = newsMapper.countNewsByAcademy(academyId) + 1;
			List<News> news = MessageUtils.getNews(urls, patTitle, patTime,
					patPic, picHeadUrl, patContent, format, count, academyId,
					academy);
			if (news.size() != 0) {
				try {
					Collections.reverse(news);
					newsMapper.insertNewsList(news);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 增量更新计算机学院的通知
	 */
	public void updateComputerNotice() {
		// 通知页面
		int academyId = 1;
		String url = "http://cs.scu.edu.cn/cs/xytz/H9502index_1.htm";
		// <A href=(/cs/xytz/webinfo.+?) target=_blank>
		String pattenUrl = "<A href=(/cs/xytz/webinfo.+?) target=_blank>";
		String headUrl = "http://cs.scu.edu.cn";
		String academy = "computer";
		List<String> urls = getNoticeUpdateUrl(academyId, url, headUrl, "GBK",
				pattenUrl);
		if (urls.size() != 0) {
			String patTitle = "<DIV align=center> (.+?)</DIV>";
			String patTime = "</SPAN> ([0-9].+?)<SPAN class=hangjc "
					+ "style=\"LINE-HEIGHT: 30px\" valign=\"bottom\">";
			String picHeadUrl = "http://cs.scu.edu.cn/";
			String patContent = "<DIV id=BodyLabel>.+?</DIV>";
			String format = "GBK";
			int count = noticeMapper.countNoticeByAcademy(academyId) + 1;

			List<Notice> notices = MessageUtils.getNotices(urls, patTitle,
					patTime, picHeadUrl, patContent, format, count, academyId,
					academy);
			Collections.reverse(notices);
			if (notices.size() != 0) {
				noticeMapper.insertNoticesList(notices);
			}
		}

	}

	/**
	 * 获取最新更新的通知url
	 * 
	 * @param academyId
	 * @param url
	 * @param headUrl
	 * @param format
	 * @param pattenUrl
	 * @return
	 */
	public List<String> getNewsUpdateUrl(int academyId, String url,
			String headUrl, String format, String pattenUrl) {
		// 存储更新的url
		List<String> list = new ArrayList<String>();
		News news = newsMapper.findOneNewsByAcademyId(academyId);
		String firstUrl = "";
		// 数据库中最新通知的url
		if (news != null) {
			firstUrl = news.getAddress();
		}
		List<String> urlsList = MessageUtils.getUrls(url, pattenUrl, headUrl,
				format);
		for (String string : urlsList) {
			if (string.compareTo(firstUrl) != 0) {
				list.add(string);
			} else {
				return list;
			}
		}
		return list;
	}

	/**
	 * 获取最新更新的通知url
	 * 
	 * @param academyId
	 * @param url
	 * @param headUrl
	 * @param format
	 * @param pattenUrl
	 * @return
	 */
	public List<String> getNoticeUpdateUrl(int academyId, String url,
			String headUrl, String format, String pattenUrl) {
		// 存储更新的url
		List<String> list = new ArrayList<String>();
		Notice notice = noticeMapper.findOneNoticeByAcademyId(academyId);
		String firstUrl = "";
		if (notice != null) {
			// 数据库中最新通知的url
			firstUrl = notice.getAddress();
		}

		List<String> urlsList = MessageUtils.getUrls(url, pattenUrl, headUrl,
				format);
		for (String string : urlsList) {
			if (string.compareTo(firstUrl) != 0) {
				list.add(string);
			} else {
				return list;
			}
		}
		return list;
	}

	// 灾后重建学院
	public void firstStartPostDisaster() throws Exception {
		// 学院新闻url
		String url = "http://idmr.scu.edu.cn/xyxw/index.jhtml";
		// 新闻匹配正则表达式
		String pattenUrl = "(/zhuzhan/xyxw/.+?.html)";
		// 下一页地址的前半部分
		String nextPageHead = "http://idmr.scu.edu.cn/xyxw/index_";
		// 下一页地址的尾部分
		String nextPageEnd = ".jhtml";
		// 新闻完整地址的前部分
		String headUrl = "http://idmr.scu.edu.cn";
		// 尾页页号的正则表达式
		String mlastPage = "</a> <a href=\"index_(.+?).jhtml\">尾页</a>";

		List<String> urls = MessageUtils.getPostDisasterUrls(url, pattenUrl,
				nextPageHead, nextPageEnd, headUrl, "utf8", mlastPage);

		System.out.println("网页地址:");
		for (String s : urls) {
			System.out.println(s);
		}

		int count = 1;

		// 标题的正则表达式
		String patTitle = "<p style=\"text-align: center;font-size:16px;font-weight:bold\">(.+?)</p></br></br>";
		// 时间的正则表达式
		String patTime = "([0-9]{4}[-][0-9]{2}[-][0-9]{2})";
		// 图片的正则表达式
		String patPic = "<img alt=\"\" src=\"(.+?.jpg)\" style=\"width";
		// 内容的正则表达式
		String patContent = "<p style=\"text-align: center;font-size:16px;font-weight:bold\">.+?</p></br></br>(.+?)</div>";

		/*
		 * getNews(List<String> urllList, String patTitle, String patTime,
		 * String patPic, String picHeadUrl, String patContent, String format,
		 * int count, int academyId,String academy)
		 */
		List<News> news = MessageUtils.getNews(urls, patTitle, patTime, patPic,
				"http://idmr.scu.edu.cn", patContent, "utf8", count,
				RECONSTRUCTION, RECONSTRUCTION_COLLEGE);

		// 学院新闻url
		url = "http://idmr.scu.edu.cn/tzgg/index.jhtml";
		// 新闻匹配正则表达式
		pattenUrl = "(/zhuzhan/tzgg/.+?.html)";
		// 下一页地址的前半部分
		nextPageHead = "http://idmr.scu.edu.cn/tzgg/index_";
		// 下一页地址的尾部分
		nextPageEnd = ".jhtml";
		// 新闻完整地址的前部分
		headUrl = "http://idmr.scu.edu.cn";
		// 尾页页号的正则表达式
		mlastPage = "</a> <a href=\"index_(.+?).jhtml\">尾页</a>";

		List<String> urlStrings = MessageUtils.getPostDisasterUrls(url,
				pattenUrl, nextPageHead, nextPageEnd, headUrl, "utf8",
				mlastPage);
		count = 1;
		// 标题的正则表达式
		patTitle = "<p style=\"text-align: center;font-size:16px;font-weight:bold\">(.+?)</p></br></br>";
		// 时间的正则表达式
		patTime = "([0-9]{4}[-][0-9]{2}[-][0-9]{2}[-])";
		// 图片的正则表达式
		patPic = "<img alt=\"\" src=\"(.+?.jpg)\" style=\"width";
		// 内容的正则表达式
		patContent = "<p style=\"text-align: center;font-size:16px;font-weight:bold\">.+?</p></br></br>(.+?)</div>";

		/*
		 * getNotices(List<String> urList, String patTitle, String patTime,
		 * String picHeadUrl, String patContent, String format, int count, int
		 * academyId,String academy)
		 */
		List<Notice> notices = MessageUtils.getNotices(urlStrings, patTitle,
				patTime, "http://idmr.scu.edu.cn", patContent, "utf8", count,
				RECONSTRUCTION, RECONSTRUCTION_COLLEGE);
		Collections.reverse(news);
		Collections.reverse(notices);
		noticeMapper.insertNoticesList(notices);
		newsMapper.insertNewsList(news);
	}

	// 华西药学院
	public void firstStartPharmacy() throws Exception {
		String newsurl = "http://pharmacy.scu.edu.cn/newslist.aspx?id=14&page=1";
		String patternUrl = "<a href='(/news.aspx\\?id=\\d+?)' target='_blank'";
		String nextPageHead1 = "http://pharmacy.scu.edu.cn/newslist.aspx?id=14&page=";
		String headUrl = "http://pharmacy.scu.edu.cn";
		List<String> urls1 = MessageUtils.getUrls(newsurl, patternUrl,
				nextPageHead1, headUrl, "GBK");

		System.out.println("新闻网页地址：");
		for (String s : urls1) {
			System.out.println(s);
		}
		int count1 = 1;
		String patTitle = "<div class=\"xjxs_tit01\"><span style=\"font-size:22;font-weight:bold;font-family:;color:;\">(.+?)</span>";
		String patTime = "发布时间：(.+?)&nbsp";
		String patPic = "src=\"(.+?.jpg)\"";
		String patContent = "<div class=\"xjxs_nr01[^\t]+?</div>";

		List<News> news = MessageUtils.getNews(urls1, patTitle, patTime,
				patPic, "http://pharmacy.scu.edu.cn/", patContent, "GBK",
				count1, PHARMACY, PHARMACY_COLLEGE);

		String noticeurl = "http://pharmacy.scu.edu.cn/newslist.aspx?id=13&page=1";
		String nextPageHead2 = "http://pharmacy.scu.edu.cn/newslist.aspx?id=13&page=";
		List<String> urls2 = MessageUtils.getUrls(noticeurl, patternUrl,
				nextPageHead2, headUrl, "GBK");

		System.out.println("通知网页地址：");
		for (String s : urls2) {
			System.out.println(s);
		}
		int count2 = 1;
		/*
		 * getNotices(List<String> urList, String patTitle, String patTime,
		 * String picHeadUrl, String patContent, String format, int count, int
		 * academyId,String academy)
		 */
		List<Notice> notices = MessageUtils.getNotices(urls2, patTitle,
				patTime, "http://pharmacy.scu.edu.cn/", patContent, "GBK",
				count2, PHARMACY, PHARMACY_COLLEGE);
		Collections.reverse(news);
		Collections.reverse(notices);
		noticeMapper.insertNoticesList(notices);
		newsMapper.insertNewsList(news);

	}

	// 第一次启动制造科学与工程学院Manufacturing science and engineering college.
	public void firstStartManufacturingEngineer() throws Exception {
		List<Notice> notices = new ArrayList<Notice>();
		List<News> news = new ArrayList<News>();
		// 获取newslist
		String url = "http://msec.scu.edu.cn/list-113-1.html";
		String nextPageEnd = ".html";
		String nextPageHead = "http://msec.scu.edu.cn/list-113-";

		// <h4><a href="http://msec.scu.edu.cn/content-113-1273-1.html">
		String pattenUrl = "<h4><a href=\"(.+?)\">";

		String headUrl = "http://msec.scu.edu.cn/";
		/*
		 * 获取新闻链接
		 */
		List<String> urls = MessageManufuctureUtils.getUrls(url, pattenUrl,
				nextPageHead, nextPageEnd, "", "gb2312");

		int count = 1;

		/*
		 * 新闻标题的正则表达式
		 */
		String patTitle = " <h5>(.+?)</h5>";

		/*
		 * 新闻图片的正则表达式
		 */
		String patPic = "<img src=\"(.+?)\" />";
		/*
		 * 新闻 时间的正则表达式
		 */
		String patTime = "<span>发布时间：(.+?)&nbsp;&nbsp;&nbsp;&nbsp; 点击：<font id=\"hits\">0</font></span>";
		/*
		 * 新闻内容的正则表达式
		 */
		String patContent = "<div class=\"detailed_zt\">(.+?)</div>";

		news = MessageManufuctureUtils.getNews(urls, patTitle, patTime, patPic,
				"http://msec.scu.edu.cn/", patContent, "gb2312", count,
				MANUFUCTURE, MANUFUCTURE_COLLEGE);

		// 获取noticelist
		String url_notice = "http://msec.scu.edu.cn/list-114-1.html";
		String nextPageHead_notice = "http://msec.scu.edu.cn/list-114-";

		// String pattenUrl_notice =
		// "</p></a></li>[\\s\\S]*?<a href=\"(.+?)\"><span>";
		String pattenUrl_notice = "<li><a href=\"(http://msec.scu.edu.cn/content.+?html)\">";

		List<String> urls_notice = MessageManufuctureUtils.getUrls(url_notice,
				pattenUrl_notice, nextPageHead_notice, nextPageEnd, headUrl,
				"gb2312");

		String patTitle_notice = " <h5>(.+?)</h5>";
		String patPic_notice = "<img src=\"(.+?)\" />";
		String patTime_notice = "<span>发布时间：(.+?)&nbsp;&nbsp;&nbsp;&nbsp; 点击：<font id=\"hits\">0</font></span>";
		String patContent_notice = "<div class=\"detailed_zt\">(.+?)<script>";

		notices = MessageManufuctureUtils.getNotices(urls_notice,
				patTitle_notice, patTime_notice, "http://msec.scu.edu.cn/",
				patContent_notice, "gb2312", count, MANUFUCTURE,
				MANUFUCTURE_COLLEGE);

		Collections.reverse(news);
		Collections.reverse(notices);
		noticeMapper.insertNoticesList(notices);
		newsMapper.insertNewsList(news);

	}

	// 第一次启动四川大学工会
	public void firstStartLaborUnion() throws Exception {
		List<Notice> notices = new ArrayList<Notice>();
		List<News> news = new ArrayList<News>();
		String url = null;
		String pattenUrl = null;
		String nextPageHead = null;
		String nextPageEnd = null;
		String headUrl = null;
		List<String> urls = null;
		int count;
		String patTitle = "<TD align=middle width=721 height=27 a><DIV align=center>(.+?)</DIV>";

		String patTime = "<SPAN class=hangjc style=\"LINE-HEIGHT: 30px\" valign=\"bottom\">时间： </SPAN>([0-9].+?)<SPAN class=hangjc style=\"LINE-HEIGHT: 30px\" valign=\"bottom\">";

		String patPic = "align=center src=\"(.+?.jpg)\"";

		String patContent = "<DIV id=BodyLabel>.+?</DIV></DIV>";

		int a = 1;
		if (a == 1) {

			url = "http://xgh.scu.edu.cn/xgh/xwdt/H9602index_1.htm";
			pattenUrl = "<A title=.+? href=(/xgh/.+?/webinfo/.+?) target=_blank>";
			nextPageHead = "http://xgh.scu.edu.cn/xgh/xwdt/H9602index_";
			nextPageEnd = ".htm";
			headUrl = "http://xgh.scu.edu.cn/";
			urls = MessageUtils.getUrls(url, pattenUrl, nextPageHead,
					nextPageEnd, headUrl, "GBK");

			System.out.println("网页地址:");
			for (String s : urls) {
				System.out.println(s);
			}
			count = 1;
			news = MessageUtils.getNews(urls, patTitle, patTime, patPic,
					headUrl, patContent, "GBK", count, LABORUNION,
					SCU_LABORUNION);
			a++;
		}

		if (a == 2) {
			url = "http://xgh.scu.edu.cn/xgh/gztz/H9603index_1.htm";
			pattenUrl = "<A title=.+? href=(/xgh/gztz/webinfo/.+?) target=_blank>";
			nextPageHead = "http://xgh.scu.edu.cn/xgh/gztz/H9603index_";
			nextPageEnd = ".htm";
			headUrl = "http://xgh.scu.edu.cn/";
			urls.clear();
			urls = MessageUtils.getUrls(url, pattenUrl, nextPageHead,
					nextPageEnd, headUrl, "GBK");

			System.out.println("网页地址:");
			for (String s : urls) {
				System.out.println(s);
			}
			count = 1;
			notices = MessageUtils.getNotices(urls, patTitle, patTime, "",
					patContent, "GBK", count, LABORUNION, SCU_LABORUNION);

		}
		Collections.reverse(news);
		Collections.reverse(notices);
		noticeMapper.insertNoticesList(notices);
		newsMapper.insertNewsList(news);

	}

	// 第一次启动获取四川大学历史文化学院(旅游学院)的所有通知和新闻
	public void firstHistoryNewsAndNotice() throws Exception {

		// 新闻页面的url地址
		String newsUrl = "http://historytourism.scu.edu.cn/history/news/xueyuandongtai/";
		// 通知页面url地址
		String noticeUrl = "http://historytourism.scu.edu.cn/history/news/gonggaotongzhi/";
		// 匹配每条新闻和通知的url的正则表达式
		String patternUrl = "<li><a href=\"(.+?)\".+?> <span";
		// 新闻每一页的url固定不变的前一部分
		String newsNextPageHead = "http://historytourism.scu.edu.cn/history/news/xueyuandongtai/index_";
		// 新闻每一页的url固定不变的后一部分
		String newsNextPageEnd = ".html";
		// 通知每一页的url固定不变的前一部分
		String noticeNextPageHead = "http://historytourism.scu.edu.cn/history/news/gonggaotongzhi/index_";
		// 通知每一页的url固定不变的后一部分
		String noticeNextPageEnd = ".html";
		String headUrl = "";
		String format = "GBK";
		// 通知页面html源码
		String noticeHtmlStr = HttpUtil.doGet(noticeUrl, format);
		// 新闻页面的Html源码
		String newsHtmlStr = HttpUtil.doGet(newsUrl, format);
		// 获取每条新闻的url
		List<String> newsUrls = MessageUtils.getUrls(newsUrl, patternUrl,
				newsNextPageHead, newsNextPageEnd, headUrl, format);
		// 获取每条通知的url
		List<String> noticeUrls = MessageUtils.getUrls(noticeUrl, patternUrl,
				noticeNextPageHead, noticeNextPageEnd, headUrl, format);
		// 打印url的数量
		System.out.println("获取到的新闻url,总数为：" + newsUrls.size());
		for (String s : newsUrls) {
			System.out.println(s);
		}
		System.out.println("获取到的通知url总数为：" + noticeUrls.size());
		for (String s : noticeUrls) {
			System.out.println(s);
		}
		// Html正文正则表达式
		String patContent = "(<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">[\\s\\S]*?)<table border=\"0\" cellspacing=\"8\" cellpadding=\"0\" align=\"center\">";
		// 匹配标题的正则表达式
		String patTitle = "<h1>(.+?)</h1>";
		// 匹配时间的正则表达式
		// 匹配完成后再进行字符串处理的方法
		// String patTime =
		// "<span id=\"ctl00_ContentPlaceHolder1_shijian\".+?>(.+?)</span>";

		// 直接匹配Unicode
		String patTime = "\u65f6\u95f4\uff1a(.+?)&nbsp;&nbsp;";

		// 匹配图片
		String patPic = "<img alt=.+?src=\"(.+?)\" />";

		// 图片文件在html源码中被隐藏的部分路径名
		String picHeadUrl = "http://historytourism.scu.edu.cn";
		// http://historytourism.scu.edu.cn/history/d/file/news/xueyuandongtai/2016-10-21/178db6c545aea550c6ea6f815f80b82b.png
		int count = 1;

		// 获取通知列表noticeList
		List<Notice> noticeList = MessageUtils.getNotices(noticeUrls, patTitle,
				patTime, picHeadUrl, patContent, format, count, TOURISM,
				TOURISM_COLLEGE);
		// 获取新闻列表newsList
		List<News> newsList = MessageUtils.getNews(newsUrls, patTitle, patTime,
				patPic, picHeadUrl, patContent, format, count, TOURISM,
				TOURISM_COLLEGE);
		/*
		 * String s = newsList.get(0).getTitle(); Date date =
		 * newsList.get(0).getTime(); System.out.println("第一条标题：" + s + date +
		 * newsList.size());
		 */
		Collections.reverse(newsList);
		Collections.reverse(noticeList);
		noticeMapper.insertNoticesList(noticeList);
		newsMapper.insertNewsList(newsList);
	}

	// 第一次启动获取所有的通知
	public List<Notice> getComputerNotice() {

		// 存放通知详细页的url
		List<String> links = new ArrayList<String>();
		// 记录通知地址所在页数
		int count = 1;
		// 通知页面
		String htmlStr = HttpUtil.sendGet(
				"http://cs.scu.edu.cn/cs/xytz/H9502index_" + count + ".htm",
				"GBK");
		// 使用正则表达式匹配详细地址的url
		Pattern pattern = Pattern
				.compile("<A href=/cs/xytz/webinfo(.+?) target=_blank>");
		Matcher matcher = pattern.matcher(htmlStr);
		System.out.println(htmlStr);

		boolean isFind = matcher.find();
		// boolean isFind = true;
		while (isFind) {// 如果有匹配的通知详细地址，继续循环
			while (isFind) {
				links.add("http://cs.scu.edu.cn/cs/xytz/webinfo"
						+ matcher.group(1));
				System.out.println("text" + matcher.group(1));
				isFind = matcher.find();
			}
			count++;
			htmlStr = HttpUtil
					.sendGet("http://cs.scu.edu.cn/cs/xytz/H9502index_" + count
							+ ".htm", "GBK");
			// htmlStr = EncoderUtil.getUTF8StringFromGBKString(htmlStr);
			matcher = pattern.matcher(htmlStr);
			isFind = matcher.find();
		}

		// 存放通知具体信息
		List<Notice> notices = new ArrayList<Notice>();
		int fileCount = 1;

		for (String link : links) {
			Notice notice = new Notice();
			htmlStr = HttpUtil.sendGet(link, "GBK");
			htmlStr = EncoderUtil.getUTF8StringFromGBKString(htmlStr);

			// 获取标题
			pattern = Pattern.compile("<DIV align=center> (.+?)</DIV>");
			matcher = pattern.matcher(htmlStr);
			if (matcher.find()) {// 设置标题
				notice.setTitle(matcher.group(1));
			}
			// 获取日期
			pattern = Pattern.compile("</SPAN> ([0-9].+?)<SPAN class=hangjc "
					+ "style=\"LINE-HEIGHT: 30px\" valign=\"bottom\">");
			matcher = pattern.matcher(htmlStr);
			if (matcher.find()) {// 设置日期
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm");// 设置日期格式
				try {
					// 将字符串转换成date类型
					Date date = format.parse(matcher.group(1));
					notice.setTime(date);
					System.out.println(date);

				} catch (ParseException e) {
					System.out.println("日期格式不正确");
					e.printStackTrace();
				}
			}

			// 获取通知的内容
			Document doc = Jsoup.parse(htmlStr);
			Element contentEle = doc.getElementById("BodyLabel");
			String contentStr = contentEle.toString();
			// 添加图片完整路径
			contentStr = contentStr.replaceAll("<IMG.+?src=\"",
					"<IMG align=center src=\"http://cs.scu.edu.cn");
			contentStr = contentStr.replace(".JPG", ".jpg");
			contentStr = contentStr
					.replaceAll(".jpg\".+?>", ".jpg\" width=90%");

			/*
			 * <html> <head> <base target="_blank" /> <meta
			 * http-equiv="Content-Type" content="text/html; charset=utf-8" />
			 * 添加页面头
			 */
			contentStr = contentStr
					.replace(
							contentStr,
							"<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" "
									+ "pageEncoding=\"UTF-8\"%><html><head> <base target=\"_blank\" /> <meta"
									+ " http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head>"
									+ contentStr + "</html>");

			// 获取工程的相对路径
			// 当项目在tomcat目录下相对路径为D:\developmentUtils\apache-tomcat-8.0.35\webapps\SCU_News_Notice\
			String relativelyPath = System.getProperty("b2cweb.root");
			System.out.println(relativelyPath);
			// 修改路径格式
			relativelyPath = relativelyPath.replace("\\", "/");

			// int index = relativelyPath.indexOf(".");
			// relativelyPath = relativelyPath.substring(0, index);

			/*
			 * String filePaths = relativelyPath +
			 * "WebContent/html/computer/notice/" + fileCount + ".html";
			 */

			String filePaths = relativelyPath + "WEB-INF/jsp/notice/computer/"
					+ fileCount + ".jsp";
			// System.out.println(contentStr);

			FileUtil.writeIntoFile(contentStr, filePaths, false);
			notice.setContent("http://" + IP
					+ "/SCU_News_Notice/notice/computer/" + fileCount);

			notice.setAcademyId(1);
			notice.setAddress(link);
			notice.setAccessNum(0);

			notices.add(notice);

			fileCount++;

		}
		return notices;
	}

	public News getDragonNews() {
		News news = new News();

		int fileCount = 0;

		// 获取标题
		String dsUrl = "http://cs.scu.edu.cn/cs/xyxw/H9501index_1.htm";
		dsUrl = HttpUtil.sendGet(dsUrl, "GBK");
		Pattern pattern = Pattern
				.compile("<A href=http://cbd2016.scu.edu.cn/ds2016 target=_blank>(.+?)</A>");
		Matcher matcher = pattern.matcher(dsUrl);
		if (matcher.find()) {
			news.setTitle(matcher.group(1));
		}

		// 获取时间
		pattern = Pattern
				.compile("<DIV align=right><FONT size=2>.(.+?).</FONT>");
		matcher = pattern.matcher(dsUrl);
		if (matcher.find()) {// 设置日期

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
			try {
				// 将字符串转换成date类型
				Date date = format.parse(matcher.group(1));
				news.setTime(date);
				System.out.println(date);

			} catch (ParseException e) {
				System.out.println("日期格式不正确");
				e.printStackTrace();
			}
		}

		// 获取内容
		String dsUrl2 = "http://cbd2016.scu.edu.cn/ds2016/";
		dsUrl2 = HttpUtil.sendGet(dsUrl2, "UTF-8");
		pattern = Pattern
				.compile("<span style=\"font-size: 18px;\"><span style=\"font-weight: bold;\">.+?</span><br>"
						+ "</p><p style=\"margin-top: 0px; margin-bottom: 0px; word-spacing:"
						+ " 0px; text-align: justify;\">.+?<span style=\"font-size: 18px; font-family:.+?</span>"
						+ "</p><p style=\"margin-top: 0px; margin-bottom: 0px; word-spacing: 0px; text-align: justify;\">");
		matcher = pattern.matcher(dsUrl2);
		if (matcher.find()) {
			System.out.println(matcher.group(0));
			String contentStr = matcher.group(0).replaceAll(
					"images/slide10.jpg", "http://cbd2016.scu.edu.cn/ds2016/");
			// 添加页面头
			contentStr = contentStr
					.replace(
							contentStr,
							"<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" "
									+ "pageEncoding=\"UTF-8\"%><html><head> <base target=\"_blank\" /> <meta"
									+ " http-equiv=\"Content-Type\" content=\"textml; charset=utf-8\"/>"
									+ contentStr + "<ml>");

			// 获取图片
			pattern = Pattern.compile("images/slide.+?.jpg");
			matcher = pattern.matcher(dsUrl2);
			if (matcher.find()) {
				news.setPic("http://cbd2016.scu.edu.cn/ds2016/"
						+ matcher.group(0));
			}

			// 获取相对路径
			String relativelyPath = System.getProperty("b2cweb.root");

			relativelyPath = relativelyPath.replace("\\", "/");

			/*
			 * int index = relativelyPath.indexOf("."); relativelyPath =
			 * relativelyPath.substring(0, index);
			 */

			String filePaths = relativelyPath + "WEB-INF/jsp/news/computer/"
					+ fileCount + ".jsp";
			// System.out.println(contentStr);

			FileUtil.writeIntoFile(contentStr, filePaths, false);
			news.setContent("http://" + IP + "/SCU_News_Notice/news/computer/"
					+ fileCount);

			// System.out.println(contentStr);

		}
		// news.setContent("龙星计划");
		news.setAddress("http://cbd2016.scu.edu.cn/ds2016/");
		news.setAcademyId(1);
		news.setAccessNum(0);

		return news;
	}

	public List<News> getComputerNews() {

		// 存放新闻详细信息页地址
		List<String> links = new ArrayList<String>();
		int count = 1;
		// http://cs.scu.edu.cn/cs/xytz/H9502index_1.htm

		String htmlStr = HttpUtil.sendGet(
				"http://cs.scu.edu.cn/cs/xyxw/H9501index_" + count + ".htm",
				"GBK");
		// 转换成utf-8编码格式
		// htmlStr = EncoderUtil.getUTF8StringFromGBKString(htmlStr);
		// 使用正则表达式匹配详细地址的url
		Pattern pattern = Pattern
				.compile("<A href=/cs/xyxw/webinfo(.+?) target=_blank>");
		Matcher matcher = pattern.matcher(htmlStr);

		boolean isFind = matcher.find();
		while (isFind) {// 如果有匹配的通知详细地址，继续循环
			while (isFind) {
				links.add("http://cs.scu.edu.cn/cs/xyxw/webinfo"
						+ matcher.group(1));
				System.out.println("text" + matcher.group(1));
				isFind = matcher.find();
			}
			count++;
			htmlStr = HttpUtil
					.sendGet("http://cs.scu.edu.cn/cs/xyxw/H9501index_" + count
							+ ".htm", "GBK");
			// htmlStr = EncoderUtil.getUTF8StringFromGBKString(htmlStr);
			matcher = pattern.matcher(htmlStr);
			isFind = matcher.find();
		}

		// 存放通知具体信息
		List<News> newsList = new ArrayList<News>();
		int fileCount = 1;

		for (String link : links) {
			News news = new News();
			htmlStr = HttpUtil.sendGet(link, "GBK");
			htmlStr = EncoderUtil.getUTF8StringFromGBKString(htmlStr);

			// 获取标题
			pattern = Pattern.compile("<DIV align=center> (.+?)</DIV>");
			matcher = pattern.matcher(htmlStr);
			if (matcher.find()) {// 设置标题
				news.setTitle(matcher.group(1));
			}
			// 获取日期
			pattern = Pattern.compile("</SPAN> ([0-9].+?)<SPAN class=hangjc "
					+ "style=\"LINE-HEIGHT: 30px\" valign=\"bottom\">");
			matcher = pattern.matcher(htmlStr);
			if (matcher.find()) {// 设置日期
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm");// 设置日期格式
				try {
					// 将字符串转换成date类型
					Date date = format.parse(matcher.group(1));
					news.setTime(date);
					System.out.println(date);

				} catch (ParseException e) {
					System.out.println("日期格式不正确");
					e.printStackTrace();
				}
			}

			// 获取内容
			Document doc = Jsoup.parse(htmlStr);
			Element contentEle = doc.getElementById("BodyLabel");
			String contentStr = contentEle.toString();
			// 添加页面头
			contentStr = contentStr
					.replace(
							contentStr,
							"<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" "
									+ "pageEncoding=\"UTF-8\"%><html><head> <base target=\"_blank\" /> <meta"
									+ " http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>"
									+ contentStr + "<html>");

			// 添加图片的完整路径
			contentStr = contentStr.replace(".JPG", ".jpg");
			contentStr = contentStr.replace("<img", "<IMG");
			contentStr = contentStr.replaceAll("<IMG.+?src=\"",
					"<IMG align=center src=\"http://cs.scu.edu.cn");

			contentStr = contentStr
					.replaceAll(".jpg\".+?>", ".jpg\" width=90%");

			// 获取相对路径
			String relativelyPath = System.getProperty("b2cweb.root");

			relativelyPath = relativelyPath.replace("\\", "/");

			// int index = relativelyPath.indexOf(".");
			// relativelyPath = relativelyPath.substring(0, index);

			String filePaths = relativelyPath + "WEB-INF/jsp/news/computer/"
					+ fileCount + ".jsp";
			// System.out.println(contentStr);

			FileUtil.writeIntoFile(contentStr, filePaths, false);
			news.setContent("http://" + IP + "/SCU_News_Notice/news/computer/"
					+ fileCount);

			// 获取图片
			Elements images = contentEle.getElementsByTag("img");
			String[] imageUrls = new String[images.size()];
			for (int i = 0; i < imageUrls.length; i++) {
				imageUrls[i] = images.get(i).attr("src");
				news.setPic("http://cs.scu.edu.cn/" + imageUrls[i]);
			}

			news.setAcademyId(1);
			news.setAddress(link);
			news.setAccessNum(0);

			newsList.add(news);

			fileCount++;

		}
		System.out.println(links.size());
		System.out.println(newsList.size());
		return newsList;
	}

}
