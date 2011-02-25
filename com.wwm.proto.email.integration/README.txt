
Consuming this component:
=========================
Import the following spring contexts:  classpath*:/spring/common/*-context.xml, classpath*:/spring/runtime/*-context.xml

The following properties will need to be available via a property placeholder configurer (or Environment as per Spring 3.1 new features)
	inbound.imap.userid=blah%40gmail.com
	inbound.imap.password=password

	outbound.smtp.userid=blah@gmail.com
	outbound.smtp.password=password




Remember to unlock Captcha: 



Notes on IMAP IDLE on Gmail (and probably all that implement it)
============================
- If there are unread messages in your inbox, these will not be seen until the next inbound message is received.


 