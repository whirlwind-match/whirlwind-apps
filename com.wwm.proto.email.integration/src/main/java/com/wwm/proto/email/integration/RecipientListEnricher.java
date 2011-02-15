package com.wwm.proto.email.integration;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHeaders;
import org.springframework.integration.support.MessageBuilder;


public class RecipientListEnricher {

	
	private static final String CONVERSATION_ID = "conversationId";

	private static final String ACCOUNTS_HEADER_NAME = "accounts";

	@Autowired
	private ConversationService conversationService;

	public Message<String> enrichWithRecipientList(Message<String> inboundMessage) {
		
		MessageHeaders headers = inboundMessage.getHeaders();
		// TODO: Do we get this from the subject or even a reply header (should try replying and find out)
		String conversationId = (String) headers.get(CONVERSATION_ID);
		Collection<String> accountIds = conversationService.getAccountsForConversation(conversationId);
		
		Message<String> enrichedMessage = MessageBuilder.fromMessage(inboundMessage).setHeader(ACCOUNTS_HEADER_NAME, accountIds).build();
		return enrichedMessage;
	}
	
}
