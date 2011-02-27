package com.wwm.proto.email.integration;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration("classpath*:/spring/common/*-context.xml")
public class DoSomethingUsingComplexRulesTest {

	
//	@Rule @ClassRule
//	public static TestRule rule = new SpringContextRule();
	
	
	@Autowired
	@Qualifier("emailIn")
	private DirectChannel emailInChannel;

	@Ignore
	@Test
	public void channelsShouldBeAutowired() {
		Assert.assertNotNull(emailInChannel);
	}
}
