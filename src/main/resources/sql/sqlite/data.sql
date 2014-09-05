INSERT INTO website (id, name, display_name, home_page_url, movie_url_template, test_proxy_url) 
VALUES (1, 'xunleicang', '迅雷仓',
'http://www.xunleicang.com',
'http://www.xunleicang.com/vod-read-id-${movieId}.html', 
'http://www.xunleicang.com/vod-read-id-5.html');

INSERT INTO website (id, name, display_name, home_page_url, movie_url_template, test_proxy_url) 
VALUES (2, 'bttiantang', 'BT天堂',
'http://www.bttiantang.com/',
'http://www.bttiantang.com/subject/${movieId}.html', 
'http://www.bttiantang.com/subject/25612.html');

INSERT INTO website (id, name, display_name, home_page_url, movie_url_template, test_proxy_url) 
VALUES (3, 'xunleipu', '迅雷铺',
'http://www.xlpu.cc/',
'http://www.xlpu.cc/html/${movieId}.html', 
'http://www.xlpu.cc/html/1.html');

INSERT INTO website (id, name, display_name, home_page_url, movie_url_template, test_proxy_url, mock_photo_referer) 
VALUES (4, 'zerodongman', 'Zero动漫',
'http://dmxz.zerodm.net/',
'http://dmxz.zerodm.net/xiazai/${movieId}.html', 
'http://dmxz.zerodm.net/xiazai/2005.html', 1);
