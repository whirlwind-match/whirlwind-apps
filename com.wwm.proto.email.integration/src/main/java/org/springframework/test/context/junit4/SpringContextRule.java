package org.springframework.test.context.junit4;

import java.lang.reflect.Method;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.TestExecutionListeners;

/**
 * WARNING: PROOF OF CONCEPT.  DO NOT ASSUME PRODUCTION CODE ;)
 * 
 * A {@Rule} alternative to {@link SpringJUnit4ClassRunner}.
 * <p>
 * Currently used as follows:
 * <pre>
 *     &#064;Rule &#064;ClassRule
 *     public static ComplexRule rule = new SpringContextRule();
 * </pre> 
 * What's missing is a way to provide class level test annotation data such as {@link ContextConfiguration}, 
 * {@link TestExecutionListeners}.  Ideally if JUnit were to detect rules on annotations and to instantiate 
 * this rule, we could create a meta-annotation with:<br>
 * <pre>
 * &#064;Rule(SpringContextRule.class)
 * &#064;ContextConfiguration("classpath:/integrationTestContext.xml")
 * &#064;TestExcutionListeners(...)
 * </pre> 
 * 
 * @author Neale Upstone (skype: neale87)
 */
public class SpringContextRule implements TestRule {

//	private static final Log logger = LogFactory.getLog(SpringContextRule.class);

	private TestContextManager testContextManager;

	/**
	 * Currently the only way to create a SpringContextRule.
	 * The rule will look for the usual Spring annotations etc on the test classes that this 
	 * rule is applied to.
	 */
	public SpringContextRule() {
	}
	
	/**
	 * Possible alternative way to provide class level test annotation data is to subclass
	 * SpringContextRule and provide annotation 
	 * <pre>
	 * &#064;ContextConfiguration("...")
	 * &#064;TestExcutionListeners(...)
	 * public class SpringIntegrationTestRule extends SpringContextRule {
	 *     public SpringIntegrationtestRule() {
	 *         super(true);
	 *     }
	 * }
	 * </pre>  
	 */
	protected SpringContextRule(boolean useRuleClassAnnotations) {
		// e.g. useRuleClassAnnotations = true;
		throw new UnsupportedOperationException("Wishful thinking...?");
	}

	public Statement apply(Statement base, Description description, Object target) {
		if (target == null) {
			return applyClassRule(base, description, description.getTestClass());
		}
		else {
			return applyMethodRule(base, description, target);
		}
	}
	
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

	protected Statement applyMethodRule(final Statement base, final Description description, final Object target) {
		
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				getTestContextManager().prepareTestInstance(target);
				Method testMethod = description.getMethod();
				getTestContextManager().beforeTestMethod(target, testMethod);
				Exception thrown = null;
				try {
					base.evaluate();
				} 
				catch (Exception e) {
					thrown = e;
				}
				finally {
					getTestContextManager().afterTestMethod(target, testMethod, thrown);
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
