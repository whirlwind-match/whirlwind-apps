package com.wwm.proto.email.integration;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHandlingException;
import org.springframework.integration.MessageHeaders;
import org.springframework.integration.mail.MailHeaders;
import org.springframework.integration.support.MessageBuilder;

/**
 * TODO RENAME TO AnonymousDistributionListEnricher
 * 
 * What it does:
 * - validate that sender is on this conversation
 * - To field becomes from perhaps with senders name
 * - ConvId in subject is used to provide a list of recipients for the bcc field
 * @author Neale
 *
 */
public class RecipientListEnricher {

	private static final String CONVERSATION_ID = "conversationId";

	private static final String ACCOUNTS_HEADER_NAME = "accounts";

	@Autowired
	private ConversationService conversationService;

	public Message<String> enrichWithRecipientList(
			Message<String> inboundMessage) {

		// TODO: Validate inbound user has active account and is a member of the list (could be an exception
		
		String conversationId = getConversationId(inboundMessage);
		Collection<String> accountIds = conversationService.getAccountsForConversation(conversationId);

		String emailAddresses = "cautious.hardwired@gmail.com"; // TODO
		
		Message<String> enrichedMessage = MessageBuilder
				.fromMessage(inboundMessage)
				.setHeader(MailHeaders.BCC, emailAddresses)
				.setHeader(MailHeaders.FROM, inboundMessage.getHeaders().get(MailHeaders.TO))
				.build();
		return enrichedMessage;
	}

	/**
	 * Extract conversationId as string text between [ and ] in message subject header.
	 */
	private String getConversationId(Message<String> message) {
		MessageHeaders headers = message.getHeaders();
		// TODO: Do we get this from the subject or even a reply header (should
				// try replying and find out)
				String subject = (String) headers.get(MailHeaders.SUBJECT);
				int start = subject.indexOf('[');
				int end = subject.indexOf(']');
				if (start < 0 || end < 0 || end < start) {
					throw new MessageHandlingException(message, "Didn't find conversationId - expected subject containing [convId] and got: " + subject);
				}
		return subject.substring(start + 1, end);
	}
}
