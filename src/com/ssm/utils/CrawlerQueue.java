package com.ssm.utils;
import java.util.HashSet;
import java.util.Set;
//存储已访问过的URL队列
public class CrawlerQueue {
   private static Set<Object>visitedUrl = new HashSet<>();
   //添加队列到访问过的URL中
   public static void addVistedUrl(String url){
	   visitedUrl.add(url);
   }
   //移除访问过URL
   public static void removeVisitedUrl(String url){
	   visitedUrl.remove(url);
   }
   public static boolean containsUrl(String url){
	  return  visitedUrl.contains(url);
   }
}
