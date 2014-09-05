package net.yangziwen.moviestore.crawler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.BackoffManager;
import org.apache.http.client.ConnectionBackoffStrategy;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiProxiedHttpClientBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(MultiProxiedHttpClientBuilder.class);

	private List<HttpHost> proxyList = new ArrayList<HttpHost>();
	private List<HttpClientBuilder> builderList = new ArrayList<HttpClientBuilder>();

	public static MultiProxiedHttpClientBuilder create(List<HttpHost> proxyList) {
		return new MultiProxiedHttpClientBuilder(proxyList);
	}

	/**
	 * @param proxyList list中的某个元素为空，代表所对应的httpClient不使用代理
	 */
	protected MultiProxiedHttpClientBuilder(List<HttpHost> proxyList) {
		if (CollectionUtils.isEmpty(proxyList)) {
			throw new IllegalArgumentException("Proxy list should not be empty!");
		}
		for (HttpHost proxy : proxyList) {
			this.proxyList.add(proxy);
			HttpClientBuilder builder = HttpClients.custom();
			if (proxy != null) {	// 如果proxy为空，则不使用代理
				builder.setRoutePlanner(new DefaultProxyRoutePlanner(proxy));
			}
			this.builderList.add(builder);
		}
		if (CollectionUtils.isEmpty(this.proxyList)) {
			throw new IllegalArgumentException("Proxy list should not be empty!");
		}
	}

	public List<HttpHost> getProxyList() {
		return Collections.unmodifiableList(proxyList);
	}

	public final MultiProxiedHttpClientBuilder setRequestExecutor(final HttpRequestExecutor requestExec) {
		for (HttpClientBuilder builder : builderList) {
			builder.setRequestExecutor(requestExec);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setHostnameVerifier(final X509HostnameVerifier hostnameVerifier) {
		for (HttpClientBuilder builder : builderList) {
			builder.setHostnameVerifier(hostnameVerifier);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setSslcontext(final SSLContext sslcontext) {
		for (HttpClientBuilder builder : builderList) {
			builder.setSslcontext(sslcontext);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setSSLSocketFactory(final LayeredConnectionSocketFactory sslSocketFactory) {
		for (HttpClientBuilder builder : builderList) {
			builder.setSSLSocketFactory(sslSocketFactory);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setMaxConnPerProxy(final int maxConnTotal) {
		for (HttpClientBuilder builder : builderList) {
			builder.setMaxConnTotal(maxConnTotal);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setMaxConnPerRoutePerProxy(final int maxConnPerRoute) {
		for (HttpClientBuilder builder : builderList) {
			builder.setMaxConnPerRoute(maxConnPerRoute);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setDefaultSocketConfig(final SocketConfig config) {
		for (HttpClientBuilder builder : builderList) {
			builder.setDefaultSocketConfig(config);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setDefaultConnectionConfig(final ConnectionConfig config) {
		for (HttpClientBuilder builder : builderList) {
			builder.setDefaultConnectionConfig(config);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setConnectionManager(final HttpClientConnectionManager connManager) {
		for (HttpClientBuilder builder : builderList) {
			builder.setConnectionManager(connManager);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setConnectionReuseStrategy(final ConnectionReuseStrategy reuseStrategy) {
		for (HttpClientBuilder builder : builderList) {
			builder.setConnectionReuseStrategy(reuseStrategy);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setKeepAliveStrategy(final ConnectionKeepAliveStrategy keepAliveStrategy) {
		for (HttpClientBuilder builder : builderList) {
			builder.setKeepAliveStrategy(keepAliveStrategy);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setTargetAuthenticationStrategy(final AuthenticationStrategy targetAuthStrategy) {
		for (HttpClientBuilder builder : builderList) {
			builder.setTargetAuthenticationStrategy(targetAuthStrategy);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setProxyAuthenticationStrategy(final AuthenticationStrategy proxyAuthStrategy) {
		for (HttpClientBuilder builder : builderList) {
			builder.setProxyAuthenticationStrategy(proxyAuthStrategy);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setUserTokenHandler(final UserTokenHandler userTokenHandler) {
		for (HttpClientBuilder builder : builderList) {
			builder.setUserTokenHandler(userTokenHandler);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder disableConnectionState() {
		for (HttpClientBuilder builder : builderList) {
			builder.disableConnectionState();
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setSchemePortResolver(final SchemePortResolver schemePortResolver) {
		for (HttpClientBuilder builder : builderList) {
			builder.setSchemePortResolver(schemePortResolver);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setUserAgent(final String userAgent) {
		for (HttpClientBuilder builder : builderList) {
			builder.setUserAgent(userAgent);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setDefaultHeaders(final Collection<? extends Header> defaultHeaders) {
		for (HttpClientBuilder builder : builderList) {
			builder.setDefaultHeaders(defaultHeaders);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder addInterceptorFirst(final HttpResponseInterceptor itcp) {
		for (HttpClientBuilder builder : builderList) {
			builder.addInterceptorFirst(itcp);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder addInterceptorLast(final HttpResponseInterceptor itcp) {
		for (HttpClientBuilder builder : builderList) {
			builder.addInterceptorLast(itcp);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder disableCookieManagement() {
		for (HttpClientBuilder builder : builderList) {
			builder.disableCookieManagement();
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder disableContentCompression() {
		for (HttpClientBuilder builder : builderList) {
			builder.disableContentCompression();
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder disableAuthCaching() {
		for (HttpClientBuilder builder : builderList) {
			builder.disableAuthCaching();
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setHttpProcessor(final HttpProcessor httpprocessor) {
		for (HttpClientBuilder builder : builderList) {
			builder.setHttpProcessor(httpprocessor);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setRetryHandler(final HttpRequestRetryHandler retryHandler) {
		for (HttpClientBuilder builder : builderList) {
			builder.setRetryHandler(retryHandler);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder disableAutomaticRetries() {
		for (HttpClientBuilder builder : builderList) {
			builder.disableAutomaticRetries();
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setRedirectStrategy(final RedirectStrategy redirectStrategy) {
		for (HttpClientBuilder builder : builderList) {
			builder.setRedirectStrategy(redirectStrategy);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder disableRedirectHandling() {
		for (HttpClientBuilder builder : builderList) {
			builder.disableRedirectHandling();
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setConnectionBackoffStrategy(final ConnectionBackoffStrategy connectionBackoffStrategy) {
		for (HttpClientBuilder builder : builderList) {
			builder.setConnectionBackoffStrategy(connectionBackoffStrategy);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setBackoffManager(final BackoffManager backoffManager) {
		for (HttpClientBuilder builder : builderList) {
			builder.setBackoffManager(backoffManager);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setServiceUnavailableRetryStrategy(final ServiceUnavailableRetryStrategy serviceUnavailStrategy) {
		for (HttpClientBuilder builder : builderList) {
			builder.setServiceUnavailableRetryStrategy(serviceUnavailStrategy);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setDefaultCookieStore(final CookieStore cookieStore) {
		for (HttpClientBuilder builder : builderList) {
			builder.setDefaultCookieStore(cookieStore);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setDefaultCredentialsProvider(final CredentialsProvider credentialsProvider) {
		for (HttpClientBuilder builder : builderList) {
			builder.setDefaultCredentialsProvider(credentialsProvider);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setDefaultAuthSchemeRegistry(final Lookup<AuthSchemeProvider> authSchemeRegistry) {
		for (HttpClientBuilder builder : builderList) {
			builder.setDefaultAuthSchemeRegistry(authSchemeRegistry);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setDefaultCookieSpecRegistry(final Lookup<CookieSpecProvider> cookieSpecRegistry) {
		for (HttpClientBuilder builder : builderList) {
			builder.setDefaultCookieSpecRegistry(cookieSpecRegistry);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder setDefaultRequestConfig(final RequestConfig config) {
		for (HttpClientBuilder builder : builderList) {
			builder.setDefaultRequestConfig(config);
		}
		return this;
	}

	public final MultiProxiedHttpClientBuilder useSystemProperties() {
		for (HttpClientBuilder builder : builderList) {
			builder.useSystemProperties();
		}
		return this;
	}

	public MultiProxiedHttpClient build() {
		return build(null);
	}

	public MultiProxiedHttpClient build(final HttpRequestBase testRequest) {
		final ThreadPool threadPool = new ThreadPool("proxyTest", 10);
		final List<HttpClientBuilder> builderList = this.builderList;
		final List<HttpHost> proxyList = this.proxyList;
		final BlockingQueue<CloseableHttpClient> httpClientBlockingQueue = new ArrayBlockingQueue<CloseableHttpClient>(builderList.size());
		for(int i = 0, l = builderList.size(); i < l; i++) {
			final int idx = i;
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					CloseableHttpClient delegatedHttpClient = builderList.get(idx).build();
					if(testProxy(testRequest, delegatedHttpClient, proxyList.get(idx))) {
						try {
							httpClientBlockingQueue.put(delegatedHttpClient);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(1000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new MultiProxiedHttpClient(new ArrayList<CloseableHttpClient>(httpClientBlockingQueue));
	}

	private static boolean testProxy(HttpRequestBase testRequest, HttpClient delegatedHttpClient, HttpHost proxy) {
		if (delegatedHttpClient == null) {
			logger.warn("delegatedHttpClient should not be null!");
			return false;
		}
		if (testRequest == null) {
			return true;
		}
		HttpResponse response = null;
		try {
			response = delegatedHttpClient.execute(testRequest);
			if(response.getStatusLine().getStatusCode() >= 300) {
				logger.warn("failed to get valid response from [{}] and response status is [{}]", proxy, response.getStatusLine());
				return false;
			}
			EntityUtils.consume(response.getEntity());
			logger.info("proxy[{}] is valid!", proxy);
			return true;
		} catch (Exception e) {
			logger.warn(e.getMessage() + " [{}]", proxy);
			return false;
		} finally {
			if (response != null) {
				EntityUtils.consumeQuietly(response.getEntity());
			}
		}
	}

}
