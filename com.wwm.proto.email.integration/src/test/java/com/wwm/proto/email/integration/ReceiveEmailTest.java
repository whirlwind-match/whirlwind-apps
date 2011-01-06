package com.wwm.proto.email.integration;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/spring/common/*-context.xml")
public class ReceiveEmailTest {

	private static Logger log = LoggerFactory.getLogger(ReceiveEmailTest.class);
	
	@Autowired
	private DirectChannel imapChannel;
	
	
	@Test(timeout=10000)
	public void receiveOneEmailWithin10Seconds() {
		
		imapChannel.subscribe(new MessageHandler() {
			public void handleMessage(Message<?> message) throws MessagingException {
				log.info("Message: " + message);
			}
		});

	}
	
}
