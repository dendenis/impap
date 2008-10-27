package org.impap.qtgui;

import org.imap.common.ApplicationClient;

import com.trolltech.qt.gui.QApplication;

public class ClientGui {
	
	private ApplicationClient client;

	public ClientGui(ApplicationClient client){
		this.client = client;
	}
	
	public static void main(String[] args) {
		new ClientGui(null).start();
	}
	
	public void start() {
		new Thread(new Runnable() {

			public void run() {

				QApplication.initialize(new String[] {});

				MainForm testMainForm = new MainForm(client);
				testMainForm.show();

				QApplication.exec();
			}

		}).start();
	}
}
