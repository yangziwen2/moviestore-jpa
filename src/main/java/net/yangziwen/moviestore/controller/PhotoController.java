package net.yangziwen.moviestore.controller;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.yangziwen.moviestore.crawler.Crawler;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.LastModified;

@Controller
@RequestMapping("/photo")
public class PhotoController implements LastModified {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String HEADER_LAST_MODIFIED = "Last-Modified";
	
	/**
	 * 目前使用web服务器启动后此controller的getLastModified方法第一次被访问的时间作为lastModifiedTime
	 */
	private long lastModifiedTime = 0L;

	/**
	 * 解决图片防盗链的问题
	 */
	@RequestMapping(value="/mockReferer")
	public void mockPhotoReferer(
			@RequestParam
			String photoUrl,
			@RequestParam
			String referer,
			NativeWebRequest webRequest,
			HttpServletResponse response
			) {

		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		// 手动实现了下缓存的控制，不知道有没有更好的方法
		if(webRequest.checkNotModified(getLastModified(request))) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}
		
		response.setDateHeader(HEADER_LAST_MODIFIED, getLastModified(request));
		
		HttpClient httpClient = Crawler.newHttpClientInstance();
		HttpGet photoRequest = new HttpGet(photoUrl);
		photoRequest.addHeader("Referer", referer);
		InputStream  photoIn = null;
		OutputStream photoOut = null;
		try {
			HttpResponse photoResponse = httpClient.execute(photoRequest);
			response.setContentType(photoResponse.getFirstHeader("Content-Type").getValue());
			HttpEntity photoEntity = photoResponse.getEntity();
			photoIn = photoEntity.getContent();
			photoOut = response.getOutputStream();
			IOUtils.copy(photoEntity.getContent(), response.getOutputStream());
		} catch (Exception e) {
			logger.warn("failed to load photo [{}]", photoUrl);
		} finally {
			IOUtils.closeQuietly(photoIn);
			IOUtils.closeQuietly(photoOut);
		}
	}

	@Override
	public long getLastModified(HttpServletRequest request) {
		if(lastModifiedTime == 0) {
			lastModifiedTime = System.currentTimeMillis();
		}
		return lastModifiedTime;
	}
	
}
