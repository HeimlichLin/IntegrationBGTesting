package idv.heimlich.IntegrationTesting.common;

import java.util.Timer;
import java.util.TimerTask;

public class TaskTimeout extends TimerTask {
	
	private Timer timer = new Timer();
	final InterruptedWork work;
	final int timeout;
	
	/**
	 * @param timeout 最大處理時間
	 * @param thread 執行thread
	 */
	public TaskTimeout(int timeout, InterruptedWork work) {
		this.work = work;
		this.timeout = timeout;
	}

	public void execute() {
		this.timer.schedule(this, this.timeout);
		this.work.start();
	}

	@Override
	public void run() {
		this.work.callInterrupted();

	}

}
