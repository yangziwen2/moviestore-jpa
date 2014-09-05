package net.yangziwen.moviestore.crawler.handler.movie;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.yangziwen.moviestore.pojo.MovieInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class XunleipuResponseHandler implements ResponseHandler<MovieInfo> {

	/**
	 * 迅雷铺页面上的电影信息组织的也太乱了!
	 */
	@Override
	public MovieInfo handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		if(response.getStatusLine().getStatusCode() >= 400) {
			return null;
		}
		try{
			return processPageContent(response);
		} catch (ClientProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (RuntimeException e) {
			return MovieInfo.INVALID_INFO;
		}
	}
	
	private static MovieInfo processPageContent(HttpResponse response) throws ParseException, IOException {
		String pageContent = EntityUtils.toString(response.getEntity(), "GBK");
		
		Document doc = Jsoup.parse(pageContent);
		
		Element category = doc.select("table td span.font_green").get(1);
		
		Elements classpages = doc.select("div[id=classpage6]");
		Element title = classpages.get(0).select("h1").first();
		Element img = classpages.get(2).select("img").get(0);
		
		Element firstBr = null;
		Elements brEls = classpages.get(2).select("br");
		for(Element br: brEls) {
			if(br.nextSibling() != null && "#text".equals(br.nextSibling().nodeName())) {
				firstBr = br;
				break;
			}
		}
		Map<String, String> infoMap = new HashMap<String, String>();
		for(Node node = firstBr; node != null; node = node.nextSibling()) {
			if("font".equals(node.nodeName())) {
				node = node.childNode(0);
			}
			if(!"#text".equals(node.nodeName())) {
				continue;
			}
			try {
				parseInfo(node, infoMap);
			} catch (RuntimeException e) {
				System.err.println("Failed to parse more details [" + e.getMessage() + "]");
			}
			if(infoMap.containsKey("area") && infoMap.containsKey("year") && infoMap.containsKey("actors")) {
				break;
			}
		}
		MovieInfo movieInfo = new MovieInfo();
		movieInfo.setTitle(title.text().replace("下载", ""));
		movieInfo.setPhotoUrl(img.attr("src"));
		movieInfo.setArea(infoMap.get("area"));
		movieInfo.setYear(infoMap.get("year"));
		movieInfo.setActors(infoMap.get("actors").replaceAll("\\s*/\\s*", " "));
		movieInfo.setCategory(category.text());
		movieInfo.setSubcategory(infoMap.get("subcategory"));
		return movieInfo;
	}
	
	private static void parseInfo(Node node, Map<String, String> infoMap) {
		String line = node.toString();
		if(StringUtils.isBlank(line)) {
			return;
		}
		line = line.replaceAll("　", " ").replaceAll("&nbsp;", " ").trim();
		int splitPos = -1;
		if(splitPos == -1) {
			splitPos = line.indexOf(":");
		}
		if(splitPos == -1) {
			splitPos = line.indexOf("：");
		}
		if(splitPos == -1 && (line.indexOf("◎") == 0 || line.indexOf("◆") == 0)) {
			line = line.substring(1);
			splitPos = 4;
		}
		if(splitPos == -1 || line.length() <= splitPos) {
			return;
		}
		
		String key = line.substring(0, splitPos).replace(" ", "");
		String value = line.substring(splitPos + 1).trim();
		
		if(key.contains("国家") || key.contains("地区")) {
			key = "area";
			String[] areaArr = value.replaceAll("/", " ").replaceAll(",", "/").trim().split("\\s");
			value = areaArr[0].trim();
			if(value.length() < 2 && areaArr.length >= 2) {
				value += areaArr[1];
			}
			if(value.length() > 10) {
				value = "";
			}
		} else if (key.contains("年")) {
			key = "year";
			value = value.trim();
			if(value.indexOf("1") != 0 && value.indexOf("2") != 0) {
				return;
			}
			if(value.length() > 4) {
				value = value.substring(0, 4);
			}
		} else if (key.contains("演员") || key.contains("主演")) {
			key = "actors";
			StringBuilder actorsBuff = new StringBuilder();
			String[] firstLineActors = value.split("\\s"); // 此处还不清楚演员到底是写成一行还是多行 
			actorsBuff.append(value.split("\\s")[0]);
			Node textNode = node;
			boolean isMultiLineActors = false;
			while((textNode = getNextTextNode(textNode)) != null && !hasKey(textNode.toString())) {
				String nextLine = textNode.toString().replaceAll("　", " ").replaceAll("&nbsp;", " ").trim();
				String actor = nextLine.split("\\s")[0];
				if(actorsBuff.length() + actor.length() > MovieInfo.MAX_ACTORS_LENGTH) {
					break;
				}
				actorsBuff.append(" ").append(actor);
				isMultiLineActors = true;
			}
			if(!isMultiLineActors) {
				for(int i = 1; i < firstLineActors.length; i++ ) {
					actorsBuff.append(" ").append(firstLineActors[i]);
				}
			}
			value = actorsBuff.toString();
			
		} else if (key.contains("类")) {
			key = "subcategory";
			value = value.split("/")[0];
		} else {
			return;
		}
		if(!infoMap.containsKey(key)) {
			infoMap.put(key, value.trim());
		}
	}
	
	private static Node getNextTextNode(Node node) {
		for(
			node = node.nextSibling(); 
			node != null && (!"#text".equals(node.nodeName()) || StringUtils.isBlank(node.toString()));
			node = node.nextSibling()
		);
		return node;
	}
	
	private static boolean hasKey(String line) {
		return StringUtils.isNotBlank(line) && (line.contains("◆") || line.contains("◎") || line.contains(":") || line.contains("："));
	}
	
}
