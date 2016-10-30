package com.ssm.utils.college;

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
import com.ssm.utils.FileUtil;
import com.ssm.utils.HttpUtil;

public class MessageManufuctureUtils {

	private static String IP = "120.27.33.180:8080";

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

		List<String> urls = new ArrayList<String>();
		int count = 1;

		// 通知页面
		String htmlStr = HttpUtil.sendGet(url, format);// sendGet获取要抓取的网页内容
		// 使用正则表达式匹配详细地址的url
		Pattern pattern = Pattern.compile(pattenUrl);
		Matcher matcher = pattern.matcher(htmlStr);

		System.out.println(htmlStr);

		boolean isFind = matcher.find();
		while (isFind) {// 濡傛灉鏈夊尮閰嶇殑閫氱煡璇︾粏鍦板潃锛岀户缁惊鐜�
			while (isFind) {
				urls.add(matcher.group(1));

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

	public static List<News> getNews(List<String> urllList, String patTitle,
			String patTime, String patPic, String picHeadUrl,
			String patContent, String format, int count,int academyId,String academy) {
		List<News> list = new ArrayList<News>();

		for (String url : urllList) {

			list.add(getNew(url, patTitle, patTime, patPic, picHeadUrl,
					patContent, format, count++,academyId, academy));
		}

		return list;

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
			int fileCount,int academyId, String academy) {
		News news = new News();

		String htmlStr = HttpUtil.sendGet(url, format);
		String title = getTitle(htmlStr, patTitle);
		// String timestr=getTimeString(htmlStr, patTime);
		Date time = getTime(htmlStr, patTime);
		String pic = getPic(htmlStr, patPic, picHeadUrl);
		String content = getContent(htmlStr, patContent);

		// 处理图片大小和url
		content = content.replace(".JPG", ".jpg");
		content = content.replaceAll("<img", "<IMG");
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

		String filePaths = relativelyPath + "WEB-INF/jsp/news/" + academy + "/"
				+ fileCount + ".jsp";
		// 将通知页面内容写入到文件中
		FileUtil.writeIntoFile(content, filePaths, false);

		// 设置获取通知页面内容的url
		news.setContent("http://" + IP + "/SCU_News_Notice/news/" + academy
				+ "/" + fileCount);
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

	public static String getTimeString(String htmlStr, String patTime) {
		// 获取日期
		String timestr;
		Pattern pattern = Pattern.compile(patTime);
		Matcher matcher = pattern.matcher(htmlStr);
		timestr = matcher.group(1);
		return timestr;
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
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");// 设置日期格式
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

			pic = "http://msec.scu.edu.cn" + matcher.group(1);
		}
		return pic;
	}

	public static String getContent(String html, String patContent) {

		Pattern pattern = Pattern.compile(patContent);
		Matcher matcher = pattern.matcher(html);
		String content = "";
		if (matcher.find()) {
			content = matcher.group(1);
		}

		return content;
	}

	public static List<Notice> getNotices(List<String> urllList,
			String patTitle, String patTime, String picHeadUrl,
			String patContent, String format, int count, int academyId,
			String academy) {
		List<Notice> list = new ArrayList<Notice>();

		for (String url : urllList) {

			list.add(getNotice(url, patTitle, patTime, picHeadUrl, patContent,
					format, count++, academyId, academy));
		}

		return list;

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
	public static Notice getNotice(String url, String patTitle, String patTime,
			String picHeadUrl, String patContent, String format, int fileCount,
			int academyId, String academy) {
		Notice notice = new Notice();

		String htmlStr = HttpUtil.sendGet(url, format);
		String title = getTitle(htmlStr, patTitle);
		// String timestr=getTimeString(htmlStr, patTime);
		Date time = getTime(htmlStr, patTime);

		String content = getContent(htmlStr, patContent);

		content = content.replace("/uploadfile",
				"http://msec.scu.edu.cn/uploadfile");
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

		String filePaths = relativelyPath + "WEB-INF/jsp/notice/" + academy
				+ "/" + fileCount + ".jsp";
		// 将通知页面内容写入到文件中
		FileUtil.writeIntoFile(content, filePaths, false);

		// 设置获取通知页面内容的url
		notice.setContent("http://" + IP + "/SCU_News_Notice/notice/" + academy
				+ "/" + fileCount);

		notice.setTitle(title);
		notice.setAddress(url);
		notice.setTime(time);
		notice.setAcademyId(academyId);
		notice.setAccessNum(0);
		return notice;
	}
}
