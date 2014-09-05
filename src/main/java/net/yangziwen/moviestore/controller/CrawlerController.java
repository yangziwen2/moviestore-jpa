package net.yangziwen.moviestore.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.yangziwen.moviestore.crawler.Crawler;
import net.yangziwen.moviestore.crawler.MultiProxiedHttpClient;
import net.yangziwen.moviestore.crawler.ResponseHandlerRegistry;
import net.yangziwen.moviestore.crawler.ThreadPool;
import net.yangziwen.moviestore.crawler.handler.proxy.XiciProxyResponseHandler;
import net.yangziwen.moviestore.pojo.MovieInfo;
import net.yangziwen.moviestore.pojo.Website;
import net.yangziwen.moviestore.service.IMovieInfoService;
import net.yangziwen.moviestore.service.IWebsiteService;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/crawl")
public class CrawlerController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IWebsiteService websiteService;
	@Autowired
	private IMovieInfoService movieInfoService;
	
	/**
	 * 保存能反应页面抓取进度的countDownLatch
	 * key为website.name
	 */
	private static final ConcurrentHashMap<String, CountDownLatch> countDownLatchMap = new ConcurrentHashMap<String, CountDownLatch>();
	
	@ResponseBody
	@RequestMapping("/{websiteName}/showCountDownLatch")
	public Map<String, Object> showCountDownLatch(@PathVariable("websiteName") String latchKey) {
		CountDownLatch latch = countDownLatchMap.get(latchKey);
		return new ModelMap().addAttribute("latchCount", latch == null? null: latch.getCount());
	}

	/**
	 * 同一网站在同一时间内只能运行一个爬取任务，
	 * 通过countDownLatchMap.get(website.getName())可查看剩余工作量
	 */
	@ResponseBody
	@RequestMapping("/movie/{websiteName}")
	public Map<String, Object> crawlMovie (
			@PathVariable("websiteName") String websiteName,
			@RequestParam(defaultValue="0") int from,
			@RequestParam(required = true) int to,
			@RequestParam(defaultValue = "5") int threadNum) {
		Website website = websiteService.getWebsiteByName(websiteName);
		if(website == null) {
			return new ModelMap().addAttribute("success", false).addAttribute("message", "网站信息不存在!");
		}
		if(from == 0) {
			MovieInfo movieInfoMax = movieInfoService.getMovieInfoWithMaxMovieId(website.getId());
			if(movieInfoMax != null) {
				from = movieInfoMax.getMovieId().intValue() + 1;
			} else {
				from = 1;
			}
		}
		if(to == 0 || from > to) {
			return new ModelMap().addAttribute("success", false).addAttribute("message", "请正确设置开始页码和结束页码!");
		}
		
		List<HttpHost> proxyList = getProxyList(1, 10);
		HttpGet testRequest = null;
		if(StringUtils.isNotBlank(website.getTestProxyUrl())) {
			RequestConfig testConfig = RequestConfig.custom()
					.setConnectTimeout(150)
					.setSocketTimeout(150)
					.build();
			testRequest = new HttpGet(website.getTestProxyUrl());
			testRequest.setConfig(testConfig);
			logger.info("Use following request to test proxy [ {} ]", testRequest);
		}
		MultiProxiedHttpClient httpClient = Crawler.newHttpClientInstance(proxyList, testRequest);
		if(!httpClient.hasValidProxy()) {
			return new ModelMap().addAttribute("success", false).addAttribute("message", "Failed to find valid proxy!");
		}
		
		MovieInfo[] infos = null;
		int retryTimes = 5;
		CountDownLatch latch = new CountDownLatch(to - from + 1);
		try {
			if(null != countDownLatchMap.putIfAbsent(website.getName(), latch)) {
				return new ModelMap().addAttribute("success", false).addAttribute("message", "对此网站的爬取工作正在进行中!");
			}
			infos = doCrawl(website, from, to, threadNum, retryTimes, httpClient);
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelMap().addAttribute("success", false).addAttribute("message", e.getMessage());
		} finally {
			countDownLatchMap.remove(website.getName());
		}
		
		// 剔除infos中为null的元素
		List<MovieInfo> infoList = new ArrayList<MovieInfo>(infos.length);
		for(MovieInfo info: infos) {
			if(info == null) {
				continue;
			}
			infoList.add(info);
		}
		logger.info("Crawled [{}] pages successfully!", infoList.size());
		
		long persistenceBeginTime = System.currentTimeMillis();
		movieInfoService.batchSaveMovieInfo(infoList.toArray(new MovieInfo[]{}), 200);
		long persistenceEndTime = System.currentTimeMillis();
		logger.info("{} ms to save [{}] records!", persistenceEndTime - persistenceBeginTime, infoList.size());
		
		return new ModelMap().addAttribute("success", true);
	}
	
	public MovieInfo[] doCrawl(final Website website, int from, int to, int threadNum, final int retryTimes, final HttpClient httpClient) {
		final ResponseHandler<MovieInfo> responseHandler = ResponseHandlerRegistry.getMovieResponseHandlerByWebsite(website);
		if(responseHandler == null) {
			throw new IllegalStateException("Failed to find responseHandler corresponding to [" + website.getName() + "]");
		}
		ThreadPool threadPool  = new ThreadPool(website.getName(), threadNum);
		final RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(2000)
				.setConnectionRequestTimeout(5000)
				.setSocketTimeout(5000)
				.build();
		final CountDownLatch latch = countDownLatchMap.get(website.getName()); // new CountDownLatch(infos.length);
		final MovieInfo[] infos = new MovieInfo[Integer.valueOf(String.valueOf(latch.getCount()))];
		for(int i = 0; i < infos.length; i++) {
			final int idx = i;
			final int movieId = i + from;
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						infos[idx] = doCrawlMovie(website, movieId, httpClient, requestConfig, responseHandler, retryTimes);
					} finally {
						latch.countDown();
					}
				}
			});
		}
		try {
			threadPool.shutdown();
			// 要求最后一个任务被分配后，最多120秒后必须返回结果。
			// 最后一个任务被分配时，线程池内剩余的任务数最多不会超过threadNum个
			threadPool.awaitTermination(120, TimeUnit.SECONDS);	
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			threadPool.shutdown(true);	// 用awaitTermination好像有时候就会被一直卡住
		}
		return infos;
	}
	
	private MovieInfo doCrawlMovie(Website website, int movieId, HttpClient httpClient, RequestConfig requestConfig, ResponseHandler<MovieInfo> responseHandler, int retryTimes) {
		int tryTimes = 0;
		String url = website.getMovieUrl(movieId);
		HttpGet request = new HttpGet(url);
		request.setConfig(requestConfig);
		
		MovieInfo info = Crawler.crawlPage(new HttpGet(url), responseHandler, httpClient);
		while(info == null && tryTimes < retryTimes) {
			logger.warn(appendProxyInfoSuffix("failed to crawl [{}] [{}]", request, httpClient)
					+ " and [ {} ] retry times left", movieId, url, retryTimes - tryTimes);
			String retryUrl = url + "?try=" + (++tryTimes);
			request = new HttpGet(retryUrl);
			request.setConfig(requestConfig);
			info = Crawler.crawlPage(request, responseHandler, httpClient);
		}
		if(info == null) {
			logger.error(appendProxyInfoSuffix("failed to crawl [{}] [{}]", request, httpClient), movieId, url);
			return null;
		} else if (info == MovieInfo.INVALID_INFO) {
			logger.error(appendProxyInfoSuffix("page [{}] [{}] are not valid ", request, httpClient), movieId, url);
			return null;
		}
		info.setMovieId(Long.valueOf(movieId));
		info.setWebsiteId(website.getId());
		logger.info(appendProxyInfoSuffix("finished to crawl [{}] [{}]", request, httpClient), movieId, info.getTitle());
		return info;
	}
	
	private String appendProxyInfoSuffix(String message, HttpRequest request, HttpClient httpClient) {
		if(! (httpClient instanceof MultiProxiedHttpClient)) {
			return message;
		}
		MultiProxiedHttpClient proxiedHttpClient = (MultiProxiedHttpClient) httpClient;
		return message + " by proxy [" + proxiedHttpClient.calProxyIndex(request) + "]";
	}
	
	private List<HttpHost> getProxyList(int crawlStartPageNum, int crawlEndPageNum) {
		if(crawlStartPageNum < 1 || crawlStartPageNum > crawlEndPageNum) {
			throw new IllegalArgumentException("Please input correct crawlStartPageNum and crawlEndPageNum!");
		}
		List<HttpHost> proxyList = new ArrayList<HttpHost>();
		HttpClient httpClient = Crawler.newHttpClientInstance();
		for(int i=crawlStartPageNum; i<=crawlEndPageNum; i++) {
			HttpGet request = new HttpGet("http://www.xici.net.co/nn/" + (i>1? i: ""));
			try {
				proxyList.addAll(httpClient.execute(request, new XiciProxyResponseHandler()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ListUtils.emptyIfNull(proxyList);
	}
	
	
} 
