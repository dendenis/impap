package org.imap.client;

import java.net.URL;
import java.net.URLClassLoader;

import org.imap.common.CompositeLogger;
import org.impap.qtgui.ClientGui;

public class Main {
	public static void main(String[] args) {
		
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
		client.start(networkService);
		networkService.start();
		ClientGui gui = new ClientGui(client);
		gui.start();
	}
}
