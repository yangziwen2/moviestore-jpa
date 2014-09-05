-- website

INSERT INTO `website` (id, name, display_name, home_page_url, movie_url_template, test_proxy_url, rank) 
VALUES (1, 'xunleicang', '迅雷仓',
'http://www.xunleicang.com',
'http://www.xunleicang.com/vod-read-id-${movieId}.html', 
'http://www.xunleicang.com/vod-read-id-5.html', 0);

INSERT INTO `website` (id, name, display_name, home_page_url, movie_url_template, test_proxy_url, rank) 
VALUES (2, 'bttiantang', 'BT天堂',
'http://www.bttiantang.com/',
'http://www.bttiantang.com/subject/${movieId}.html', 
'http://www.bttiantang.com/subject/25612.html', 2);

INSERT INTO `website` (id, name, display_name, home_page_url, movie_url_template, test_proxy_url, rank) 
VALUES (3, 'xunleipu', '迅雷铺',
'http://www.xlpu.cc/',
'http://www.xlpu.cc/html/${movieId}.html', 
'http://www.xlpu.cc/html/1.html', 1);

INSERT INTO `website` (id, name, display_name, home_page_url, movie_url_template, test_proxy_url, mock_photo_referer, rank) 
VALUES (4, 'zerodongman', 'Zero动漫',
'http://dmxz.zerodm.net/',
'http://dmxz.zerodm.net/xiazai/${movieId}.html', 
'http://dmxz.zerodm.net/xiazai/2005.html', true, 3);

-- movie_info
insert into `movie_info` (id, website_id, movie_id, title, actors, area, year, photo_url, category, subcategory)
values 
(1, 1, 1, '这个杀手不太冷', '未录入', '欧美', '1994', 'http://tu.yayakan.com/newpic/94.jpg', '电影', '动作片'),
(3, 1, 3, '高楼大劫案[1080p]', '未录入', '欧美', '2011', 'http://apollo.s.dpool.sina.com.cn/nd/dataent/moviepic/pics/66/moviepic_bb1c2e014d4c395f30c06b907b2b728f.jpg', '1080P', ''),
(3975, 1, 3982, '生化危机3:灭绝', '伊恩·格雷 米拉·乔沃维奇 奥德·菲尔', '欧美', '2008', 'http://tu.yayakan.com/newpic/330.jpg', '电影', '科幻片'),
(4181, 1, 4189, '十月围城', '甄子丹 黎明 谢霆锋 梁家辉 胡军 王学圻 范冰冰 李宇春', '香港', '2009', 'http://tu.yayakan.com/newpic/5810.jpg', '电影', '剧情片'),
(4462, 1, 4471, '大兵小将', '成龙 王力宏 于荣光 刘承俊', '大陆', '2010', 'http://tu.yayakan.com/newpic/6462.jpg', '电影', '动作片'),

(78554, 4, 1, '火影忍者', 'Naruto,火影忍者疾风传,狐忍,火影', '日本', '2000', 'http://tp.hellodm.cn/xzpic/uploadimg/2010-4/2010430452347476.jpg', 'TV', '热血'),
(78555, 4, 2, '海贼王', 'One Piece,航海王,海盗路飞,海盗王', '日本', '2014', 'http://tp.hellodm.cn/xzpic/uploadimg/2010-4/2010431243822392.jpg', 'TV', '热血'),
(78560, 4, 7, '名侦探柯南', 'Detective Conan,名侦探柯南日语版', '日本', '2014', 'http://tp.hellodm.cn/xzpic/uploadimg/2010-4/20104318191787207.jpg', 'TV', '推理');
