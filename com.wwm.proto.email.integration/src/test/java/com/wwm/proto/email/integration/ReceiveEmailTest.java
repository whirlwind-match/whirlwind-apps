package com.wwm.proto.email.integration;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
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
 * External test to validate we're talking to servers sensibly.
 * @author Neale
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath:/spring/common/channels-context.xml", 
	"classpath:/spring/runtime/external-context.xml",
	"classpath:/spring/test/property-substitution-context.xml",
	})
public class ReceiveEmailTest {

	private static Logger log = LoggerFactory.getLogger(ReceiveEmailTest.class);
	
	@Autowired
	@Qualifier("emailIn")
	private DirectChannel imapChannel;
	
	
	@Autowired
	@Qualifier("emailOut")
	private DirectChannel sendSmtpChannel;

	
	protected MimeMessage receivedMessage;
	
	
	@Test
	public void receiveOneEmailWithinAGivenTimePeriod() throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		imapChannel.subscribe(new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				log.info("Message: " + message);
				MimeMessage mail = (MimeMessage) message.getPayload();
				try {
					log.info(" from " + mail.getSender() + 
							" : " + mail.getSubject());
					receivedMessage = mail;
					latch.countDown();
				}
				catch (javax.mail.MessagingException e) {
					throw new MessagingException("Unexpected javamail exception", e);
				}
			}
		});

		log.info("=== Sending mail message ===" );
		sendSmtpMessage();
		
		log.info("=== Mail message sent ===" );

		latch.await(30, TimeUnit.SECONDS);
		assertThat(receivedMessage.getSubject(), is("[convId] message subject"));
		assertThat((String)receivedMessage.getContent(), is("message body\r\n"));
		InternetAddress sender = (InternetAddress) receivedMessage.getFrom()[0];
		assertThat(sender.getAddress(), is("fridgemountain.router@gmail.com"));
	}


	public static Message<String> createMailMessage() {
		return MessageBuilder.withPayload("message body")
				.setHeader(MailHeaders.SUBJECT, "[convId] message subject")
				.setHeader(MailHeaders.TO, "fridgemountain.router@gmail.com")
//				.setHeader(MailHeaders.CC, "neale.upstone@opencredo.com")
//				.setHeader(MailHeaders.BCC, "neale@nealeupstone.com")
				.setHeader(MailHeaders.FROM, "no-reply@fridgemountain.com")
				.setHeader(MailHeaders.REPLY_TO, "fridgemountain.router@gmail.com")
				.build();
	}
		
	private void sendSmtpMessage() {
		sendSmtpChannel.send(createMailMessage());
		
	}
	
}
