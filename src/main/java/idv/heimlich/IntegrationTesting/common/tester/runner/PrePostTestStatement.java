package idv.heimlich.IntegrationTesting.common.tester.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.runners.model.Statement;

public class PrePostTestStatement extends Statement {

	private Statement invoker;
	private Object target;
	private List<Method> preMethods = new ArrayList<Method>(); // 前置
	private List<Method> postMethods = new ArrayList<Method>(); // 結束

	public PrePostTestStatement(Statement invoker, Object target) {
		this.invoker = invoker;
		this.target = target;
	}

	public void addPre(Method method) {
		this.preMethods.add(method);
	}

	public void addPost(Method method) {
		this.postMethods.add(method);
	}
	
	/**
	 * 實作執行的順序
	 */
	@Override
	public void evaluate() throws Throwable {
		this.excuteOtherMethod(this.preMethods); // @PreTest
		this.invoker.evaluate(); // @Test
		this.excuteOtherMethod(this.postMethods); // @PostTest
	}
	
	private void excuteOtherMethod(List<Method> methods) throws Throwable {
		Iterator<Method> arg1 = methods.iterator();
		Method method;
		while (arg1.hasNext()) {
			method = (Method) arg1.next();
			method.invoke(this.target, (Object[]) null);
		}
	}

}
