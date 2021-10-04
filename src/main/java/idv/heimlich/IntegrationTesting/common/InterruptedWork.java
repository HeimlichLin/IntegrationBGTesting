package idv.heimlich.IntegrationTesting.common;

public class InterruptedWork implements IWork {
	
	private Thread thread;

	public InterruptedWork(Runnable runnable) {
		this.thread = new Thread(runnable);
	}

	@Override
	public void start() {
		this.thread.start();
		try {
			this.thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void callInterrupted() {
		this.thread.interrupt();
	}

}
