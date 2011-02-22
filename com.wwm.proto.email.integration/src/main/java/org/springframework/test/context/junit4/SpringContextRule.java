package org.springframework.test.context.junit4;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.rules.ComplexRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.test.context.TestContextManager;

public class SpringContextRule extends ComplexRule {

	private static final Log logger = LogFactory.getLog(SpringContextRule.class);

	private TestContextManager testContextManager;

	public SpringContextRule(String ...locations) {
//		blah
	}

	@Override
	protected Statement applyClassRule(final Statement base, Description description, Class<?> clazz) {

		// TODO: clean this up 
		if (testContextManager == null) {
			testContextManager = createTestContextManager(clazz);
		}

		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				getTestContextManager().beforeTestClass();
				try {
					base.evaluate();
				} 
				finally {
					getTestContextManager().afterTestClass();
				}
			}
		};
	}

	@Override
	protected Statement applyMethodRule(final Statement base, Description description, final Object target) {
		
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				getTestContextManager().prepareTestInstance(target);
//			TODO	Method testMethod = description.getMethod();
//				getTestContextManager().beforeTestMethod(target, testMethod);
				Exception thrown = null;
				try {
					base.evaluate();
				} 
				catch (Exception e) {
					thrown = e;
				}
				finally {
//					getTestContextManager().afterTestMethod(target, testMethod, thrown);
				}
			}
		};
	}
	
	private TestContextManager getTestContextManager() {
		return testContextManager;
	}
	
	/**
	 * Creates a new {@link TestContextManager} for the supplied test class and
	 * the configured <em>default <code>ContextLoader</code> class name</em>.
	 * Can be overridden by subclasses.
	 * 
	 * @param clazz the test class to be managed
	 * @see #getDefaultContextLoaderClassName(Class)
	 */
	protected TestContextManager createTestContextManager(Class<?> clazz) {
		return new TestContextManager(clazz, getDefaultContextLoaderClassName(clazz));
	}

	/**
	 * Get the name of the default <code>ContextLoader</code> class to use for
	 * the supplied test class. The named class will be used if the test class
	 * does not explicitly declare a <code>ContextLoader</code> class via the
	 * <code>&#064;ContextConfiguration</code> annotation.
	 * <p>
	 * The default implementation returns <code>null</code>, thus implying use
	 * of the <em>standard</em> default <code>ContextLoader</code> class name.
	 * Can be overridden by subclasses.
	 * </p>
	 * 
	 * @param clazz the test class
	 * @return <code>null</code>
	 */
	protected String getDefaultContextLoaderClassName(Class<?> clazz) {
		return null;
	}

}
