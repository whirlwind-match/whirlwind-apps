package com.wwm.proto.email.integration;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import org.junit.ClassRule;
import org.junit.rules.ComplexRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringContextRule;

@ContextConfiguration("classpath*:/spring/common/*-context.xml")
public class DoSomethingUsingComplexRulesTest {

	
	@Rule @ClassRule
	public static ComplexRule rule = new SpringContextRule("classpath*:/spring/common/*-context.xml");
	
	
	@Autowired
	@Qualifier("emailIn")
	private DirectChannel emailInChannel;
	
	@Test
	public void channelsShouldBeAutowired() {
		Assert.assertNotNull(emailInChannel);
	}
}
