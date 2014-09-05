DROP TABLE IF EXISTS `website`;
CREATE TABLE `website` (
	id bigint not null primary key auto_increment,
	name varchar(40) NOT NULL UNIQUE,
	display_name varchar(40) NOT NULL,
	home_page_url varchar(256),
	movie_url_template varchar(256),
	test_proxy_url varchar(128),
	mock_photo_referer boolean DEFAULT false,
	rank int DEFAULT 0,
	primary key (id)
);

DROP TABLE IF EXISTS `movie_info`;
CREATE TABLE `movie_info` (
	id bigint not null primary key auto_increment,
	website_id bigint NOT NULL,
	movie_id int NOT NULL,
	title varchar(100),
	actors varchar(200),
	area varchar(20),
	year varchar(20),
	photo_url varchar(256),
	category varchar(10),
	subcategory varchar(10)
);