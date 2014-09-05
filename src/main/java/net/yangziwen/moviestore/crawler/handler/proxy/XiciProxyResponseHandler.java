package net.yangziwen.moviestore.crawler.handler.proxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class XiciProxyResponseHandler implements ResponseHandler<List<HttpHost>> {

	@Override
	public List<HttpHost> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		List<HttpHost> proxyList = new ArrayList<HttpHost>();
		Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
		Element tblEl = doc.getElementById("ip_list");
		Elements trEls = tblEl.select("tr");
		for(int i=1, l=trEls.size(); i<l; i++) {
			Elements tdEls = trEls.get(i).children();
			String hostname = tdEls.get(1).text();
			int port = NumberUtils.toInt(tdEls.get(2).text());
			String scheme = tdEls.get(5).text().trim().toLowerCase();
			if(!"http".equals(scheme)) {
				continue;
			}
			proxyList.add(new HttpHost(hostname, port, scheme));
		}
		return proxyList;
	}

}
