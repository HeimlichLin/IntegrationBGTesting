package idv.heimlich.IntegrationTesting.common.tester.runner;

import java.lang.reflect.Method;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * 執行器
 */
public class PrePostRunner extends BlockJUnit4ClassRunner {

	public PrePostRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}
	
	/**
	 * 改寫，加入@test外，前後欲執行的method
	 */
	@Override
	protected Statement methodInvoker(FrameworkMethod method, Object test) {
		PrePostTestStatement statement = new PrePostTestStatement(
				super.methodInvoker(method, test), test);
		PreTest preTest = (PreTest) method.getAnnotation(PreTest.class);
		if (preTest != null) {
			this.addMethod(test, statement, preTest.value(), true);
		}

		PostTest postTest = (PostTest) method.getAnnotation(PostTest.class);
		if (postTest != null) {
			this.addMethod(test, statement, postTest.value(), false);
		}

		return statement;
	}

	private void addMethod(Object test, PrePostTestStatement statement,
			String[] methodNames, boolean isPre) {
		for(String methodName : methodNames) {
            Method[] methods = test.getClass().getMethods();
            for(Method method : methods) {
                if(method.getName().equals(methodName)) {
                    if(isPre) {
                        statement.addPre(method);
                    }
                    else {
                        statement.addPost(method);
                    }
                }
            }
        }
	}

}
