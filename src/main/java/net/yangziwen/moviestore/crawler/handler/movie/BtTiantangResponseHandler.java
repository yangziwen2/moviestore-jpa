package net.yangziwen.moviestore.crawler.handler.movie;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.yangziwen.moviestore.pojo.MovieInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BtTiantangResponseHandler  implements ResponseHandler<MovieInfo> {
	
	private static final String HOST = "http://www.bttiantang.com/";

	@Override
	public MovieInfo handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		try {
			if(response.getStatusLine().getStatusCode() >= 400) {
				return null;
			}
			String pageContent = EntityUtils.toString(response.getEntity(), "UTF-8");
			
			Document doc = Jsoup.parse(pageContent);
			
			Element title = doc.select("div.moviedteail_tt h1").first();
			Element img = doc.select("div.moviedteail_img a.pic img").first();
			Element infoUl = doc.select("ul.moviedteail_list").first();
			Elements infoLis = infoUl.children();
			
			Map<String, String> rawInfo = new HashMap<String, String>();
			for(Element infoLi: infoLis) {
				String key = infoLi.childNode(0).toString().split(":")[0];
				Elements aList = infoLi.children();
				StringBuilder buff = new StringBuilder();
				for(Element a: aList) {
					buff.append(a.text()).append(" ");
				}
				rawInfo.put(key, buff.toString().trim());
			}
			
			MovieInfo movieInfo = new MovieInfo();
			movieInfo.setTitle(title.text());
			movieInfo.setPhotoUrl(HOST + img.attr("src"));
			movieInfo.setActors(rawInfo.get("主演"));
			String[] areas = rawInfo.get("地区").split(" ");
			movieInfo.setArea(areas.length > 0 && StringUtils.isNotBlank(areas[0])? areas[0]: null);
			movieInfo.setYear(rawInfo.get("年份"));
			
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
