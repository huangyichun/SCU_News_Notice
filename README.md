# SCU_News_Notice
四川大学部分学院网站新闻和通知的获取和更新
目前支持 历史学院 计算机学院   制造科学与工程学院通知
		 华西药学院 灾后重建管理学院 

http://120.27.33.180:8080/SCU_News_Notice/academy 返回所有学院的信息
返回一个json字符串:
[{"id":1,"name":"计算机学院","address":"http://cs.scu.edu.cn/"},
{"id":2,"name":"工商学院"},{"id":3,"name":"经济学院"},
{"id":4,"name":"法学院"},{"id":5,"name":"文学与新闻学院"},
{"id":6,"name":"外国语学院"},{"id":7,"name":"艺术学院"},
{"id":8,"name":"旅游学院"},{"id":9,"name":"数学学院"},
{"id":10,"name":"物理科学与技术学院"},
{"id":11,"name":"化学学院"},
其中被使用的学院id有：8 历史学院 
					1 计算机学院  14 制造科学与工程学院通知
					27 华西药学院 31 灾后重建管理学院 
http://120.27.33.180:8080/SCU_News_Notice/findPageNotice/14/1 
访问制造科学与工程学院通知的第1页的内容，一页10条数据。(第一个1是代表学院id，第二个1代表页数)
返回是一个json字符串

http://120.27.33.180:8080/SCU_News_Notice/findPageNews/1/1 访问新闻的第5页的内容，一页为10数据。
返回一个json字符串
pic：为新闻第一张图片,其他的与通知相同 accessNum为被访问的次数
