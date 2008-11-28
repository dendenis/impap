package org.imap.client;

import java.net.URL;
import java.net.URLClassLoader;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;

import org.imap.common.CompositeLogger;
import org.impap.qtgui.ClientGui;

public class Main {
	public static void main(String[] args) {
		
		// add handlers for main MIME types
		MailcapCommandMap mc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);
		
		CompositeLogger logger = new CompositeLogger();
		
		ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();

        //Get the URLs
        URL[] urls = ((URLClassLoader)sysClassLoader).getURLs();

        for(int i=0; i< urls.length; i++)
        {
            System.out.println(urls[i].getFile());
        }       
		
        
		IMAPClient client = new IMAPClient(logger);
		NagaNetworkService networkService = new NagaNetworkService(client, logger);
		MessagePersistenceManager persistenceManager = new MessagePersistenceManager(logger, client);
		persistenceManager.start();
		client.start(networkService, persistenceManager);
		networkService.start();
		ClientGui gui = new ClientGui(client);
		gui.start();
	}
}
