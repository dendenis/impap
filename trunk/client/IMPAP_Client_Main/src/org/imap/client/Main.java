package org.imap.client;

import java.net.URL;
import java.net.URLClassLoader;

import org.impap.qtgui.ClientGui;

public class Main {
	public static void main(String[] args) {
		ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();

        //Get the URLs
        URL[] urls = ((URLClassLoader)sysClassLoader).getURLs();

        for(int i=0; i< urls.length; i++)
        {
            System.out.println(urls[i].getFile());
        }       
		
		IMAPClient client = new IMAPClient();
		NagaNetworkService networkService = new NagaNetworkService(client);
		client.start(networkService);
		networkService.start();
		ClientGui gui = new ClientGui(client);
		gui.start();
	}
}
