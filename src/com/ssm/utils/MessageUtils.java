package com.ssm.utils;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ssm.po.News;
import com.ssm.po.Notice;

public class MessageUtils {

	private static String IP = "120.27.33.180:8080";
	
	private static int 	PHARMACY = 27;//华西药学院
	private static String PHARMACY_COLLEGE = "pharmacy";
	private static int RECONSTRUCTION = 31;// 灾后重建管理学院
	private static String RECONSTRUCTION_COLLEGE = "postDisaster";

	private static int MANUFUCTURE = 14;// 制造科学与工程学院
	private static String MANUFUCTURE_COLLEGE = "manufucture";
	/**
	 * 获取第一页的信息
	 * 
	 * @param url
	 * @param pattenUrl
	 * @param headUrl
	 * @param format
	 * @return
	 */
	public static List<String> getUrls(String url, String pattenUrl,
			String headUrl, String format) {

		List<String> urls = new ArrayList<>();

		// 通知页面
		String htmlStr = HttpUtil.sendGet(url, format);
		// 使用正则表达式匹配详细地址的url
		Pattern pattern = Pattern.compile(pattenUrl);
		Matcher matcher = pattern.matcher(htmlStr);
		System.out.println(htmlStr);

		// <A href=/cs/xytz/webinfo/2016/09/1474334197265427.htm target=_blank>
		boolean isFind = matcher.find();
		// http://bs.scu.edu.cn/index.php?m=fcontent&a=show&catid=42&cid=3527
		// boolean isFind = true;
		while (isFind) {// 如果有匹配的通知详细地址，继续循环
			urls.add(headUrl + matcher.group(1));

			isFind = matcher.find();
		}

		return urls;
	}

	/**
	 * 获取所有的url地址
	 * 
	 * @param url
	 *            首页url
	 * @param pattenUrl
	 *            匹配url的正则表达式
	 * @param nextPageHead
	 *            下一页新闻或者通知的前段地址
	 * @param nextPageEnd
	 *            下一页末端地址
	 * @param headUrl
	 *            通知和新闻的地址头部
	 * @param format
	 *            编码方式
	 * @return
	 */
	public static List<String> getUrls(String url, String pattenUrl,
			String nextPageHead, String nextPageEnd, String headUrl,
			String format) {

		List<String> urls = new ArrayList<>();

		// 记录通知地址所在页数
		int count = 1;
		// 通知页面
		String htmlStr = HttpUtil.sendGet(url, format);
		// 使用正则表达式匹配详细地址的url
		Pattern pattern = Pattern.compile(pattenUrl);
		Matcher matcher = pattern.matcher(htmlStr);
		System.out.println(htmlStr);

		// <A href=/cs/xytz/webinfo/2016/09/1474334197265427.htm target=_blank>
		boolean isFind = matcher.find();
		// http://bs.scu.edu.cn/index.php?m=fcontent&a=show&catid=42&cid=3527
		// boolean isFind = true;
		while (isFind) {// 如果有匹配的通知详细地址，继续循环
			while (isFind) {
				urls.add(headUrl + matcher.group(1));
				//System.out.println("text" + matcher.group(1));
				isFind = matcher.find();
			}
			count++;
			htmlStr = HttpUtil.sendGet(nextPageHead + count + nextPageEnd,
					format);
			// htmlStr = EncoderUtil.getUTF8StringFromGBKString(htmlStr);
			matcher = pattern.matcher(htmlStr);
			isFind = matcher.find();
		}

		return urls;
	}

	/**
	 * 获取通知多条信息
	 * 
	 * @param urList
	 * @param patTitle
	 * @param patTime
	 * @param picHeadUrl
	 * @param patContent
	 * @param format
	 * @param count
	 * @param academyId
	 * @return
	 */
	public static List<Notice> getNotices(List<String> urList, String patTitle,
			String patTime, String picHeadUrl, String patContent,
			String format, int count, int academyId,String academy) {

		List<Notice> list = new ArrayList<Notice>();

		for (String url : urList) {

			list.add(getNotice(url,patTitle, patTime, patContent, format,
					count++, academyId,academy));
		}
		return list;
	}

	/**
	 * 获取多条新闻信息
	 * @param urllList
	 * @param patTitle
	 * @param patTime
	 * @param patPic
	 * @param picHeadUrl
	 * @param patContent
	 * @param format
	 * @param count
	 * @return
	 */
	public static List<News> getNews(List<String> urllList, String patTitle,
			String patTime, String patPic, String picHeadUrl,
			String patContent, String format, int count, int academyId,String academy) {
		List<News> list = new ArrayList<News>();

		for (String url : urllList) {

			list.add(getNew(url, patTitle, patTime, patPic, picHeadUrl,
					patContent, format, count++, academyId,academy));
		}

		return list;

	}

	/**
	 * 获取一条通知信息
	 * 
	 * @param url
	 * @param patTitle
	 * @param patTime
	 * @param patContent
	 * @param format
	 * @param fileCount
	 * @param academyId
	 * @return
	 */
	public static Notice getNotice(String url, String patTitle, String patTime,
			String patContent, String format, int fileCount, int academyId,String academy) {
		Notice notice = new Notice();

		String htmlStr = HttpUtil.sendGet(url, format);
		String title = getTitle(htmlStr, patTitle);
		Date time;
		if(academyId == PHARMACY){
			 time= getPharmacyTime(htmlStr, patTime);
		}else{
			 time = getTime(htmlStr, patTime);
		}
		

		String content = getContent(htmlStr, patContent);
		
		
		// 设置图片完整的url
		/*
		 * content = content.replaceAll("src=\"", "src=\"http://cs.scu.edu.cn");
		 */
		content = content
				.replace(
						content,
						"<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" "
							+ "pageEncoding=\"UTF-8\"%><html><head> <base target=\"_blank\" /> <meta"
								+ " http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>"
								+ content + "</html>");
		

		// 获取工程的相对路径
		// 当项目在tomcat目录下相对路径为D:\developmentUtils\apache-tomcat-8.0.35\webapps\SCU_News_Notice\
		String relativelyPath = System.getProperty("b2cweb.root");
		// 修改路径格式
		relativelyPath = relativelyPath.replace("\\", "/");

		String filePaths = relativelyPath + "WEB-INF/jsp/notice/"+academy+"/" + fileCount
				+ ".jsp";
		// 将通知页面内容写入到文件中
		FileUtil.writeIntoFile(content, filePaths, false);

		// 设置获取通知页面内容的url
		notice.setContent("http://" + IP
				+ "/SCU_News_Notice/notice/"+academy+"/" + fileCount
				);

		notice.setTitle(title);
		notice.setAddress(url);
		notice.setTime(time);
		notice.setAcademyId(academyId);
		notice.setAccessNum(0);

		return notice;
	}

	/**
	 * 获取其中的一条新闻，返回News对象
	 * 
	 * @param url
	 * @param patTitle
	 * @param patTime
	 * @param patPic
	 * @param picHeadUrl
	 * @param patContent
	 * @param format
	 * @return
	 */
	public static News getNew(String url, String patTitle, String patTime,
			String patPic, String picHeadUrl, String patContent, String format,
			int fileCount, int academyId,String academy) {
		News news = new News();

		String htmlStr = HttpUtil.doGet(url, format);
		String title = getTitle(htmlStr, patTitle);
		
		Date time;
		if(academyId == PHARMACY){
			 time= getPharmacyTime(htmlStr, patTime);
		}else{
			 time = getTime(htmlStr, patTime);
		}
		String content;
		if(academyId == RECONSTRUCTION){
			content = getPostDisasterContent(htmlStr, patContent);
		}else {
			content = getContent(htmlStr, patContent);
		}
		 
		
		String pic = getPic(content, patPic, picHeadUrl);
		

		// 处理图片大小和url
		content = content.replace(".JPG", ".jpg");
		content = content.replaceAll("<img",
				"<IMG");
		content = content.replaceAll("<IMG.+?src=\"",
				"<IMG align=center src=\"" + picHeadUrl);
		
		content = content.replaceAll(".jpg\".+?>", ".jpg\" width=90%");
		
		content = content
				.replace(
						content,
						"<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" "
							+ "pageEncoding=\"UTF-8\"%><html><head> <base target=\"_blank\" /> <meta"
								+ " http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head>"
								+ content + "</html>");

		// 获取工程的相对路径
		// 当项目在tomcat目录下相对路径为D:\developmentUtils\apache-tomcat-8.0.35\webapps\SCU_News_Notice\
		String relativelyPath = System.getProperty("b2cweb.root");
		// 修改路径格式
		relativelyPath = relativelyPath.replace("\\", "/");

		String filePaths = relativelyPath + "WEB-INF/jsp/news/"+academy+"/" + fileCount
				+ ".jsp";
		// 将通知页面内容写入到文件中
		FileUtil.writeIntoFile(content, filePaths, false);

		// 设置获取通知页面内容的url
		news.setContent("http://" + IP
				+ "/SCU_News_Notice/news/"+academy+"/" + fileCount
				);
		news.setTitle(title);
		news.setAddress(url);
		news.setPic(pic);
		news.setTime(time);
		news.setAcademyId(academyId);
		news.setAccessNum(0);
		return news;
	}

	/**
	 * 获取标题
	 * 
	 * @param htmlStr
	 * @param patTitle
	 * @return
	 */
	public static String getTitle(String htmlStr, String patTitle) {
		Pattern pattern = Pattern.compile(patTitle);
		Matcher matcher = pattern.matcher(htmlStr);
		String title = "";
		if (matcher.find()) {
			title = matcher.group(1);
		}

		return title;

	}

	/**
	 * 获取日期
	 * @return
	 */
	public static Date getPharmacyTime(String aspxStr, String patTime) {
		// 获取日期
		Pattern pattern = Pattern.compile(patTime);
		Matcher matcher = pattern.matcher(aspxStr);
		Date date = new Date();
		if (matcher.find()) {// 设置日期
			SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");// 设置日期格式
			try {
				// 将字符串转换成date类型
				date = format.parse(matcher.group(1));

			} catch (ParseException e) {
				System.out.println("日期格式不正确");
				e.printStackTrace();
			}
		}

		return date;
	}
	/**
	 * 获取日期
	 * 
	 * @param htmlStr
	 * @param patTime
	 * @return
	 */
	public static Date getTime(String htmlStr, String patTime) {
		// 获取日期
		Pattern pattern = Pattern.compile(patTime);
		Matcher matcher = pattern.matcher(htmlStr);
		Date date = new Date();
		if (matcher.find()) {// 设置日期
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");// 设置日期格式
			
			try {
				// 将字符串转换成date类型
				date = format.parse(matcher.group(1));

			} catch (ParseException e) {
				System.out.println("日期格式不正确");
				e.printStackTrace();
			}
		}

		return date;
	}

	public static String getPic(String htmlStr, String patPic, String picHeadUrl) {
		Pattern pattern = Pattern.compile(patPic);
		Matcher matcher = pattern.matcher(htmlStr);
		String pic = "";
		if (matcher.find()) {// 设置图片

			pic = picHeadUrl + matcher.group(1);
		}
		return pic;
	}

	public static String getContent(String html, String patContent) {

		Pattern pattern = Pattern.compile(patContent);
		Matcher matcher = pattern.matcher(html);
		String content = "";
		if (matcher.find()) {
			content = matcher.group(0);
		}

		return content;
	}
	/* **************************************下面是药学院的方法****************************************** */
	 /**药学院调用的方法
     * 获取所欲URL地址
     * @param url 首页
     * @param patternUrl  正则表达式
     * @param nextPageHead  下页新闻或通知前段地址
     * @param headUrl 通知和新闻地址头部
     * @param format 编码方式
     * @return 
     */
	public static List<String> getUrls(String url,String patternUrl,String nextPageHead,String headUrl,String format){
		List<String> urls = new ArrayList<>();
		//记录新闻地址多扎页数
		int count = 1;
		//新闻页面
		String aspxStr=HttpUtil.sendGet(url, format);
		//使用正则表达式匹配详细地址Url
		Pattern pattern = Pattern.compile(patternUrl);
		 // 定义一个matcher用来做匹配
		Matcher matcher = pattern.matcher(aspxStr);
		System.out.println(aspxStr);
		//判断是否找到匹配地址
		boolean isFind = matcher.find();
		while(isFind&&!CrawlerQueue.containsUrl(matcher.group(0))){
			//如果有匹配的地址继续循环
			while (isFind){
				String s = matcher.group(0);
				System.out.println(s);
				urls.add(headUrl+matcher.group(1));
				CrawlerQueue.addVistedUrl(s);
				
				//System.out.println("text"+matcher.group(1));
				isFind = matcher.find();
			}
			count++;
			aspxStr = HttpUtil.sendGet(nextPageHead+count, format);
			matcher = pattern.matcher(aspxStr);
			isFind = matcher.find();
		}
		return urls;
    }
	/* *********************************下面是灾后重建的方法*************************************************** */
	/**
	 * 灾后重建学院
	 * 获取所有的url地址
	 * 
	 * @param url 首页url
	 * @param pattenUrl 匹配url的正则表达式
	 * @param nextPageHead 下一页新闻或者通知的前段地址
	 * @param nextPageEnd 下一页末端地址
	 * @param headUrl 通知和新闻的地址头部
	 * @param format 编码方式
	 * @return
	 */
	public static List<String> getPostDisasterUrls(String url, String pattenUrl,
			String nextPageHead, String nextPageEnd, String headUrl,
			String format,String mlastPage) {

		List<String> urls = new ArrayList<>();

		// 记录通知地址所在页数
		int count = 1;
		// 通知页面
		String htmlStr = HttpUtil.doGet(url, format);
		// 使用正则表达式匹配详细地址的url
		Pattern pattern = Pattern.compile(pattenUrl);
		Matcher matcher = pattern.matcher(htmlStr);
		System.out.println(htmlStr);

		// <A href=/cs/xytz/webinfo/2016/09/1474334197265427.htm target=_blank>
		boolean isFind = matcher.find();
		String lastPage = getPage(htmlStr, mlastPage);

		int i = Integer.parseInt(lastPage); 
		
		while (isFind&&count<=i) {// 如果有匹配的通知详细地址，继续循环
			while (isFind) {
				urls.add(headUrl + matcher.group(1));
				System.out.println("text" + matcher.group(1));
				isFind = matcher.find();
			}
			count++;
			//获取下一页的新闻内容
			htmlStr = HttpUtil.doGet(nextPageHead + count + nextPageEnd,
					format);
			//匹配下一页的新闻url
			matcher = pattern.matcher(htmlStr);
			isFind = matcher.find();
		}

		return urls;
	}
	
	public static String getPage(String html, String mlastPage) {

		Pattern pattern = Pattern.compile(mlastPage);
		Matcher matcher = pattern.matcher(html);
		String lastPage = "";
		if (matcher.find()) {
			lastPage = matcher.group(1);
		}

		return lastPage;
	}
	
	/**
	 * 获取内容
	 * @param html
	 * @param patContent
	 * @return
	 */
	public static String getPostDisasterContent(String html, String patContent) {

		Pattern pattern = Pattern.compile(patContent, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(html);
		String content = "";
		if (matcher.find()) {
			content = matcher.group(1);
		}

		return content;
	}
	
	/* *****************************制造与工程学院****************************************** */
	
	
}
