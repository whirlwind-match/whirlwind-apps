package com.wwm.proto.email.integration;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.mail.MailHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Test inner parts of system.
 * 
 * We inject an inbound email into the emailIn channel, and check that correct messages appear
 * on emailOut channel, or potentially an error channel.
 * 
 * @author Neale Upstone
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/spring/common/*-context.xml")
public class ProcessEmailMessageTest {

	private static Logger log = LoggerFactory.getLogger(ProcessEmailMessageTest.class);
	
	@Autowired
	@Qualifier("emailIn")
	private DirectChannel emailInChannel;
	
	
	@Autowired
	@Qualifier("emailOut")
	private DirectChannel emailOutChannel;

	@Autowired
	private ConversationService conversationService; // mock

	
	protected MimeMessage outboundMessage;
	
	
	@Before
	public void setupConversationServiceMocks() {
		when(conversationService.getAccountsForConversation(Matchers.eq("convId")))
			.thenReturn(Arrays.asList("11111abc","11111def"));
	}
	
	
	@Test
	public void receiveOneEmailWithinAGivenTimePeriod() throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		emailOutChannel.subscribe(new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				log.info("Message: " + message);
				MimeMessage mail = (MimeMessage) message.getPayload();
				try {
					log.info(" from " + mail.getSender() + 
							" : " + mail.getSubject());
					outboundMessage = mail;
					latch.countDown();
				}
				catch (javax.mail.MessagingException e) {
					throw new MessagingException("Unexpected javamail exception", e);
				}
			}
		});

		log.info("=== Injecting inbound mail message ===" );
		injectMessage();
		
		log.info("=== Mail message injected ===" );

		latch.await(30, TimeUnit.SECONDS);
		assertThat(outboundMessage.getSubject(), is("message subject"));
		assertThat((String)outboundMessage.getContent(), is("message body\r\n"));
		InternetAddress sender = (InternetAddress) outboundMessage.getFrom()[0];
		assertThat(sender.getAddress(), is("fridgemountain.router@gmail.com"));
	}


	public static Message<String> createMailMessage() {
		return MessageBuilder.withPayload("[message body")
				.setHeader(MailHeaders.SUBJECT, "message subject")
				.setHeader(MailHeaders.TO, "fridgemountain.router@gmail.com")
//				.setHeader(MailHeaders.CC, "neale.upstone@opencredo.com")
//				.setHeader(MailHeaders.BCC, "neale@nealeupstone.com")
				.setHeader(MailHeaders.FROM, "some.user@fridgemountain.com")
				.setHeader(MailHeaders.REPLY_TO, "fridgemountain.router@gmail.com")
				.build();
	}
		
	private void injectMessage() {
		emailInChannel.send(createMailMessage());
		
	}
	
}
