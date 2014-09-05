package net.yangziwen.moviestore.crawler;

import java.util.Map;

import net.yangziwen.moviestore.crawler.handler.movie.BtTiantangResponseHandler;
import net.yangziwen.moviestore.crawler.handler.movie.XunleicangResponseHandler;
import net.yangziwen.moviestore.crawler.handler.movie.XunleipuResponseHandler;
import net.yangziwen.moviestore.crawler.handler.movie.ZeroDongmanResponseHandler;
import net.yangziwen.moviestore.pojo.MovieInfo;
import net.yangziwen.moviestore.pojo.Website;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.http.client.ResponseHandler;

public class ResponseHandlerRegistry {
	
	@SuppressWarnings("serial")
	private static final Map<String, ResponseHandler<MovieInfo>> movieResponseHandlerMap = new HashedMap<String, ResponseHandler<MovieInfo>>(){{
		put("xunleicang", new XunleicangResponseHandler());
		put("bttiantang", new BtTiantangResponseHandler());
		put("xunleipu", new XunleipuResponseHandler());
		put("zerodongman", new ZeroDongmanResponseHandler());
	}};

	public static ResponseHandler<MovieInfo> getMovieResponseHandlerByWebsite(Website website) {
		return movieResponseHandlerMap.get(website.getName());
	}
	
}
