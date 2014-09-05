package net.yangziwen.moviestore.crawler.handler.movie;

import java.io.IOException;

import net.yangziwen.moviestore.pojo.MovieInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ZeroDongmanResponseHandler implements ResponseHandler<MovieInfo> {

	@Override
	public MovieInfo handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		if(response.getStatusLine().getStatusCode() >= 400) {
			return null;
		}
		try {
			String pageContent = EntityUtils.toString(response.getEntity(), "UTF-8");
			Document doc = Jsoup.parse(pageContent);
			
			Element infoWrapper = doc.select("div.info.item").first();
			Element title = infoWrapper.select("div.title h1").first();
			Element img = infoWrapper.select("div.pic img").first();
			
			String actorsInfo = doc.select("div.detail2 p").first().text();
			String actors = actorsInfo.substring(actorsInfo.indexOf("：") + 1);
			actors = actors.replaceAll("\\s*/\\s*", " ");
	
			Element detailWrapper = doc.select("div.detail").first();
			
			Elements cateInfos = detailWrapper.child(2).select("a");
			String category = cateInfos.get(0).text();
			String subcategory = cateInfos.size() > 1? cateInfos.get(1).text(): null;
			String year = detailWrapper.child(3).text();
			year = year.substring(year.indexOf("：") + 1);
			String area = detailWrapper.child(5).select("a").first().text();
			
			MovieInfo info = new MovieInfo();
			info.setTitle(title.text());
			info.setPhotoUrl(img.attr("src"));
			info.setYear(year.substring(0, 4));
			info.setArea(area);
			info.setCategory(category);
			info.setSubcategory(subcategory);
			info.setActors(actors);
			return info;
		} catch (ClientProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (RuntimeException e) {
			return MovieInfo.INVALID_INFO;
		}
	}
	
}
