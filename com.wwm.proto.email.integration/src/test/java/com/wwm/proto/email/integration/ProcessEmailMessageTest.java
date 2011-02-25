package com.wwm.proto.email.integration;


import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.Message;
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
@ContextConfiguration({
	"classpath*:/spring/common/*-context.xml", 
	"classpath:/spring/test/mocks-context.xml",
	"classpath:/spring/test/property-substitution-context.xml",
	})
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

	
	protected Message<String> outboundMessage;
	
	
	@Before
	public void setupConversationServiceMocks() {
		when(conversationService.getAccountsForConversation(Matchers.eq("convId")))
			.thenReturn(Arrays.asList("11111abc","11111def"));
	}
	
	
	@Test
	public void outboundMessageShouldHaveMultipleBccRecipients() throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		emailOutChannel.subscribe(new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) {
				log.info("Message: " + message);
				outboundMessage = (Message<String>) message;
				latch.countDown();
			}
		});

		log.info("=== Injecting inbound mail message ===" );
		injectMessage();
		
		log.info("=== Mail message injected ===" );

		latch.await(30, TimeUnit.SECONDS);
		assertSubject("[convId] message subject");
		assertFrom("fridgemountain.router@gmail.com");
		assertBcc("cautious.user@gmail.com");
		assertBody("message body");
	}

	private void assertSubject(String expected) {
		assertEquals(outboundMessage.getHeaders().get(MailHeaders.SUBJECT), expected);
	}

	private void assertFrom(String expected) {
		String sender = (String) outboundMessage.getHeaders().get(MailHeaders.FROM);
		assertThat(sender, is(expected));
	}

	private void assertBcc(String expected) {
		String sender = (String) outboundMessage.getHeaders().get(MailHeaders.BCC);
		assertThat(sender, is(expected));
	}

	private void assertBody(String string) {
		assertThat(outboundMessage.getPayload(), is(string));
	}
	

	/**
	 * Original message to be made anonymous
	 * @return
	 */
	public static Message<String> createMailMessage() {
		return MessageBuilder.withPayload("message body")
				.setHeader(MailHeaders.SUBJECT, "[convId] message subject")
				.setHeader(MailHeaders.TO, "fridgemountain.router@gmail.com")	// should be removed or become anon.recipients@fridgerouter...
//				.setHeader(MailHeaders.CC, "neale.upstone@opencredo.com") // should never get set
//				.setHeader(MailHeaders.BCC, "neale@nealeupstone.com")	// Should become multiple outbound 
				.setHeader(MailHeaders.FROM, "some.user@fridgemountain.com")  // Should become fridgemountain.router@ or even "Sender Name" <fridgerouter...>
//				.setHeader(MailHeaders.REPLY_TO, "fridgemountain.router@gmail.com") // should become fridgemountain.router@ ...
				.build();
	}
		
	private void injectMessage() {
		emailInChannel.send(createMailMessage());
		
	}
}
