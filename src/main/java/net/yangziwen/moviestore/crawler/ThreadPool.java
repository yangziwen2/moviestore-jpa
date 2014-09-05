package net.yangziwen.moviestore.crawler;

import java.sql.Timestamp;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 可伸缩可阻塞的线程池
 * @author zyang
 */
public class ThreadPool {
	
	protected static final int DEFAULT_MIN_POOL_SIZE = 0;
	protected static final int DEFAULT_MAX_POOL_SIZE = 10;

	private String poolName;
	private ThreadPoolExecutor executor;
	private Timestamp createTime;

	public ThreadPool(String poolName) {
		init(poolName, DEFAULT_MIN_POOL_SIZE, DEFAULT_MAX_POOL_SIZE);
	}

	public ThreadPool(String poolName, int maxPoolSize) {
		init(poolName, DEFAULT_MIN_POOL_SIZE, maxPoolSize);
	}

	public ThreadPool(String poolName, int minPoolSize, int maxPoolSize) {
		init(poolName, minPoolSize, maxPoolSize);
	}

	protected void init(final String poolName, int minPoolSize, int maxPoolSize) {
		this.poolName = poolName;
		if (minPoolSize < 0) {
			minPoolSize = 0;
		}
		if (maxPoolSize <= minPoolSize) {
			maxPoolSize = Math.max(minPoolSize, 1);
		}
		executor = new ThreadPoolExecutor(
				minPoolSize, 
				maxPoolSize, 
				10, TimeUnit.SECONDS, 
				new SynchronousQueue<Runnable>(), 
				new BlockingPolicy());
		executor.setThreadFactory(new ThreadFactory() {
			private final AtomicInteger threadNum = new AtomicInteger(0);
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, poolName + "-thread-" + threadNum.getAndIncrement());
			}
		});
		createTime = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * 获取线程池名称
	 */
	public String getPoolName() {
		return poolName;
	}

	/**
	 * 获取当前正在运行的任务数
	 */
	public int getRunningTaskCount() {
		return executor.getActiveCount();
	}

	/**
	 * 获取已完成的任务数
	 */
	public long getCompletedTaskCount() {
		return executor.getCompletedTaskCount();
	}
	
	/**
	 * 获取当前线程池大小
	 */
	public int getCurrentPoolSize() {
		return executor.getPoolSize();
	}

	/**
	 * 获取线程池的线程数下限
	 */
	public int getMinPoolSize() {
		return executor.getCorePoolSize();
	}

	/**
	 * 获取线程池的线程数上限
	 */
	public int getMaxPoolSize() {
		return executor.getMaximumPoolSize();
	}

	/**
	 * 设置线程池的线程数上限
	 */
	public void setMaxPoolSize(int maxPoolSize) {
		if (maxPoolSize < getMinPoolSize()) {
			throw new IllegalStateException("maxPoolSize should not be less than minPoolSize!");
		}
		executor.setMaximumPoolSize(maxPoolSize);
	}
	
	public long getKeepRunningMillis() {
		return System.currentTimeMillis() - createTime.getTime();
	}

	public void execute(Runnable task) {
		executor.execute(task);
	}
	
	/**
	 * 关闭线程池(不打断正在执行的任务)
	 */
	public void shutdown() {
		shutdown(false);
	}

	/**
	 * 关闭线程池
	 * @param force 是否强行打断正在执行的任务
	 */
	public void shutdown(boolean force) {
		if (force) {
			executor.shutdownNow();
		} else {
			executor.shutdown();
		}
	}
	
	public boolean isShutdown() {
		return executor.isShutdown();
	}
	
	public boolean isTerminating() {
		return executor.isTerminating();
	}
	
	public boolean isTerminated() {
		return executor.isTerminated();
	}
	
	public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		executor.awaitTermination(timeout, unit);
	}

	/**
	 * 当线程池已满时，此策略用于阻塞住提交新任务的线程
	 */
	protected final static class BlockingPolicy implements RejectedExecutionHandler {
		private Logger logger = LoggerFactory.getLogger(this.getClass());
		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			try {
				executor.getQueue().put(r);
			} catch (InterruptedException e) {
				// ignore the interruptted exception, and output the log;
				// 一般都是关submitter线程的时候，会进这个异常
				logger.warn("[{}] The blocked thread is interrupted when submiting task to the thread pool!", this.getClass().getName() + ".class");
			}
		}
	}

}