package net.yangziwen.moviestore.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

@SuppressWarnings("deprecation")
public class MultiProxiedHttpClient extends CloseableHttpClient {
	
	private static final int DEFAULT_MAX_FAILED_TIMES = 10;

	private Map<CloseableHttpClient, AtomicInteger> delegatedHttpClientFailedTimesMap = new HashMap<CloseableHttpClient, AtomicInteger>();
	private List<CloseableHttpClient> delegatedHttpClientList = new ArrayList<CloseableHttpClient>();
	
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	private Lock readLock = lock.readLock();
	private Lock writeLock = lock.writeLock();
	
	private int maxFailedTimes = DEFAULT_MAX_FAILED_TIMES;
	
	MultiProxiedHttpClient(List<CloseableHttpClient> httpClientList) {
		if(CollectionUtils.isEmpty(httpClientList)) {
			return;
		}
		for(CloseableHttpClient httpClient: httpClientList) {
			this.delegatedHttpClientList.add(httpClient);
			this.delegatedHttpClientFailedTimesMap.put(httpClient, new AtomicInteger(0));
		}
	}
	
	public int getMaxFailedTimes() {
		return maxFailedTimes;
	}

	public void setMaxFailedTimes(int maxFailedTimes) {
		this.maxFailedTimes = maxFailedTimes;
	}
	
	public boolean hasValidProxy() {
		return getDelegatedHttpClientList().size() > 0;
	}

	public List<CloseableHttpClient> getDelegatedHttpClientList() {
		return Collections.unmodifiableList(delegatedHttpClientList);
	}
	
	public int calProxyIndex(HttpRequest request) {
		try {
			readLock.lock();
			int hashCode = request.toString().hashCode();
			return Math.abs(hashCode) % delegatedHttpClientList.size();
		} finally {
			readLock.unlock();
		}
	}
	
	private CloseableHttpClient fetchHttpClient(HttpRequest request) {
		try {
			readLock.lock();
			return delegatedHttpClientList.get(calProxyIndex(request));
		} finally {
			readLock.unlock();
		}
	}
	
	@Override
	public HttpParams getParams() {
		throw new UnsupportedOperationException("This method is deprecated!");
	}

	@Override
	public ClientConnectionManager getConnectionManager() {
		throw new UnsupportedOperationException("This method is deprecated!");
	}
	
	/**
	 * 发生IOException时，评估下对应的httpClient，
	 * 如果累计失败次数已经超过maxFailedTimes，
	 * 则从httpClient列表中剔除这个httpClient
	 * @param httpClient
	 */
	private void evaluateHttpClientWhenFailed(CloseableHttpClient httpClient) {
		AtomicInteger failedTimesHolder = delegatedHttpClientFailedTimesMap.get(httpClient);
		if(failedTimesHolder != null && failedTimesHolder.incrementAndGet() <= maxFailedTimes) {
			return;
		}
		try {
			writeLock.lock();
			if(delegatedHttpClientList.size() <= 1) {	// 如果只剩下最后一个httpClient了，那么就不剔除了
				return;
			}
			delegatedHttpClientList.remove(httpClient);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public CloseableHttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
		return execute(request, (HttpContext) null);
	}

	@Override
	public CloseableHttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
		CloseableHttpClient httpClient = null;
		try {
			httpClient = fetchHttpClient(request);
			return fetchHttpClient(request).execute(request, context);
		} catch (IOException e) {
			evaluateHttpClientWhenFailed(httpClient);
			throw e;
		}
	}

	@Override
	public CloseableHttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
		return execute(target, request, (HttpContext) null);
	}

	@Override
	public CloseableHttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
		CloseableHttpClient httpClient = null;
		try {
			httpClient = fetchHttpClient(request);
			return fetchHttpClient(request).execute(target, request, context);
		} catch (IOException e) {
			evaluateHttpClientWhenFailed(httpClient);
			throw e;
		}
	}

	@Override
	public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
		return execute(request, responseHandler, (HttpContext) null);
	}

	@Override
	public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
		CloseableHttpClient httpClient = null;
		try {
			httpClient = fetchHttpClient(request);
			return fetchHttpClient(request).execute(request, responseHandler, context);
		} catch (IOException e) {
			evaluateHttpClientWhenFailed(httpClient);
			throw e;
		}
	}

	@Override
	public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
		return execute(target, request, responseHandler, (HttpContext) null);
	}

	@Override
	public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
		CloseableHttpClient httpClient = null;
		try {
			httpClient = fetchHttpClient(request);
			return fetchHttpClient(request).execute(target, request, responseHandler, context);
		} catch (IOException e) {
			evaluateHttpClientWhenFailed(httpClient);
			throw e;
		}
	}

	@Override
	public void close() throws IOException {
		for(CloseableHttpClient httpClient: delegatedHttpClientFailedTimesMap.keySet()) {
			IOUtils.closeQuietly(httpClient);
		}
	}

	@Override
	protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
		throw new UnsupportedOperationException("This method is not supported");
	}

}
