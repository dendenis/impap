package org.impap.qtgui;

import org.imap.common.ApplicationClient;
import org.imap.common.Item;

import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.gui.QCloseEvent;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QTreeWidgetItem;
import com.trolltech.qt.gui.QWidget;

public class MainForm extends QMainWindow {

	Ui_MainForm ui = new Ui_MainForm();
	private ApplicationClient client;
	private QTimer timer;
	private boolean connected = false;

	public MainForm(ApplicationClient client) {
		this.client = client;
		timer = new QTimer(this);
		timer.setInterval(100);
		timer.timeout.connect(this, "updateMessageTree()");

		ui.setupUi(this);
		timer.start();
		ui.connectButton.clicked.connect(this, "connect(boolean)");
		ui.messageTree.currentItemChanged.connect(this, "itemSelected()");
		// menubar.triggered.connect(MainForm, "close()");
		// connect(timer, SIGNAL())
	}

	public MainForm(QWidget parent) {
		super(parent);
		ui.setupUi(this);
	}

	private void updateMessageTree() {
		if (client.isChanged()) {
			ui.messageTree.clear();
			QTreeWidgetItem root = new QTreeWidgetItem();
			root.setText(0, "mailbox");
			root.setData(1, 0, null);

			ui.messageTree.addTopLevelItem(root);

			for (Item child : client.getRoot().getChildren().values()) {
				updateMessageTree(root, child);
			}

			client.setChanged(false);
		}
	}

	private void updateMessageTree(QTreeWidgetItem parent, Item imapItem) {
		QTreeWidgetItem item = new QTreeWidgetItem(parent);
		item.setText(0, imapItem.getName());
		item.setData(1, 0, imapItem);
		
		for (Item child : imapItem.getChildren().values()) {
			updateMessageTree(item, child);
		}
	}

	public void connect(boolean value) {
		// System.out.println("Connect clicked");

		connected = !connected;
	
		if (connected) {
			client.connect(ui.addressEdit.text(), Integer.valueOf(ui.portSpin.value()),
					ui.usernameEdit.text(), ui.passEdit.text());
		} else {
			client.disconnect();
		}
	}

	public void itemSelected() {
		if (connected) {
			if (ui.messageTree.currentItem() != null) {
				Item imapItem = (Item) ui.messageTree.currentItem().data(1, 0);
					
				if (imapItem != null) {
					ui.fromEdit.setText(imapItem.from());
					ui.toEdit.setText(imapItem.to());
 				    ui.subjectEdit.setText(imapItem.subject());
 				    ui.bodyText.setPlainText(imapItem.text());
					
					if(imapItem.isFolder()){
					  client.listFolder(imapItem);
					  System.out.println("list folder called");
					}
				}
			}
		}
	}

	@Override
	protected void closeEvent(QCloseEvent event) {
		client.close();
		super.closeEvent(event);
	}

	// public boolean onClose(){
	// client.close();
	// th
	// }
}
