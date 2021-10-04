package idv.heimlich.IntegrationTesting.common.tester.ssh;

public interface ISSHCommandListener {

	void update(String arg0, ISSHCommandContent arg1) throws InterruptedException;
	
}
