package com.wwm.proto.email.integration;

import java.util.Collection;

public interface ConversationService {

	/**
	 * Return a collection of the user accounts (or do we call them profiles) that are part of this conversation
	 * @param conversationId
	 * @return Collection of ids
	 */
	Collection<String> getAccountsForConversation(String conversationId);

}
