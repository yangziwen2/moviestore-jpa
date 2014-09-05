package net.yangziwen.moviestore.crawler;

import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class Crawler {

	private static final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
	static {
		connManager.setMaxTotal(200);
		connManager.setDefaultMaxPerRoute(10);
	}
	
	public static CloseableHttpClient newHttpClientInstance() {
		return newHttpClientInstance((HttpHost) null);
	}
	
	public static CloseableHttpClient newHttpClientInstance(HttpHost proxy) {
		HttpClientBuilder builder = HttpClients.custom();
		builder.setConnectionManager(connManager);
		if(proxy != null) {
			builder.setRoutePlanner(new DefaultProxyRoutePlanner(proxy));
		}
		return builder.build();
	}
	
	public static MultiProxiedHttpClient newHttpClientInstance(List<HttpHost> proxyList) {
		return newHttpClientInstance(proxyList, (HttpGet) null);
	}
	
	public static MultiProxiedHttpClient newHttpClientInstance(List<HttpHost> proxyList, HttpGet testRequest) {
		MultiProxiedHttpClientBuilder builder = MultiProxiedHttpClientBuilder.create(proxyList);
		builder.setConnectionManager(connManager);
		return builder.build(testRequest);
	}
	
	public static String crawlPage(HttpUriRequest request, HttpClient httpClient) {
		return crawlPage(request, HttpClientContext.create(), httpClient);
	}
	
	public static String crawlPage(HttpUriRequest request, HttpClientContext context, HttpClient httpClient) {
		try {
			return EntityUtils.toString(httpClient.execute(request, context).getEntity());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static <T> T crawlPage(HttpUriRequest request, ResponseHandler<T> handler, HttpClient httpClient) {
		return crawlPage(request, handler, HttpClientContext.create(), httpClient);
	}
	
	public static <T> T crawlPage(HttpUriRequest request, ResponseHandler<T> handler, HttpClientContext context, HttpClient httpClient) {
		try {
			return httpClient.execute(request, handler, context);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
