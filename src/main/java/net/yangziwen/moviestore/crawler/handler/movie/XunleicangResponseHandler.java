package net.yangziwen.moviestore.crawler.handler.movie;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.yangziwen.moviestore.pojo.MovieInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class XunleicangResponseHandler implements ResponseHandler<MovieInfo> {
	
	@Override
	public MovieInfo handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		try {
			if(response.getStatusLine().getStatusCode() >= 400) {
				return null;
			}
			String pageContent = EntityUtils.toString(response.getEntity(), "UTF-8");
			
			Document doc = Jsoup.parse(pageContent);
			
			Element cateWrapper = doc.select("div.pleft h2 b").first();
			Elements cates = cateWrapper.children();
			String category = cates.get(1).text();
			String subcategory = cates.size() >= 4? cates.get(2).text(): null;
			
			Element infoWrapper = doc.select("div.movieCont").first();
			Element img = infoWrapper.select("img").first();
			Element title = infoWrapper.select("h1").first();
			Elements spans = infoWrapper.select("span[class!=bg]");
			Map<String, String> rawInfo = new HashMap<String, String>();
			for(Element span: spans) {
				String[] arr = span.text().split("：");
				if(arr.length < 2) {
					continue;
				}
				rawInfo.put(arr[0], arr[1]);
			}
			
			MovieInfo movieInfo = new MovieInfo();
			movieInfo.setTitle(title.text());
			movieInfo.setPhotoUrl(img.attr("src"));
			movieInfo.setActors(rawInfo.get("主演"));
			movieInfo.setArea(rawInfo.get("地区"));
			movieInfo.setYear(rawInfo.get("年份"));
			movieInfo.setCategory(category);
			movieInfo.setSubcategory(subcategory);
			return movieInfo;
		} catch (ClientProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (RuntimeException e) {
			return MovieInfo.INVALID_INFO;
		}
	}
	
}
