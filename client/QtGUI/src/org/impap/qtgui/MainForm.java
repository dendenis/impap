package org.impap.qtgui;

import org.imap.common.ApplicationClient;
import org.imap.message.Item;
import org.imap.message.Message;

import com.trolltech.qt.core.QByteArray;
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
	private Message currentMessage = null;

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

	public void updateMessageTree() {
		if (client.isChanged()) {
			ui.messageTree.clear();
			QTreeWidgetItem root = new QTreeWidgetItem();
			root.setText(0, "mailbox");
			root.setData(1, 0, null);

			ui.messageTree.addTopLevelItem(root);

			for (Item child : client.getItemTree().getChildren().values()) {
				updateMessageTree(root, child);
			}

			client.setChanged(false);
		}
		else if((currentMessage != null)&&(ui.messageTree.currentItem() != null)){
			Item folder = (Item) ui.messageTree.currentItem().data(1, 0);
			if (!currentMessage.isDownloaded() && folder.getId().equals(currentMessage.getId())){
				currentMessage = client.getMessage(folder, currentMessage.getId());
				ui.fromEdit.setText(currentMessage.from());
				ui.toEdit.setText(currentMessage.to());
				ui.subjectEdit.setText(currentMessage.subject());
				QByteArray byteArray = new QByteArray();
				byteArray.append(currentMessage.content());
				ui.bodyText.setContent(byteArray, "text/html");
			}
		}
	}

	private void updateMessageTree(QTreeWidgetItem parent, Item imapItem) {
		QTreeWidgetItem item = new QTreeWidgetItem(parent);
		item.setText(0, imapItem.getText());
		item.setData(1, 0, imapItem);

		for (Item child : imapItem.getChildren().values()) {
			updateMessageTree(item, child);
		}
	}

	public void connect(boolean value) {
		// System.out.println("Connect clicked");

		connected = !connected;

		if (connected) {
			client.connect(ui.addressEdit.text(), Integer.valueOf(ui.portSpin
					.value()), ui.usernameEdit.text(), ui.passEdit.text());
		} else {
			client.disconnect();
		}
	}

	public void itemSelected() {
		if (connected) {
			if (ui.messageTree.currentItem() != null) {
				Item imapItem = (Item) ui.messageTree.currentItem().data(1, 0);

				if (imapItem != null) {
					if (imapItem.isFolder()) {
						currentMessage = null;
						ui.fromEdit.setText("");
						ui.toEdit.setText("");
						ui.subjectEdit.setText("");
						ui.bodyText.setContent(new QByteArray(), "text/html");
						client.listFolder(imapItem);
						System.out.println("list folder called");
					} else {
						currentMessage = client.getMessage(imapItem, imapItem.getId());
						ui.fromEdit.setText(currentMessage.from());
						ui.toEdit.setText(currentMessage.to());
						ui.subjectEdit.setText(currentMessage.subject());
						QByteArray byteArray = new QByteArray();
						byteArray.append(currentMessage.content());
						ui.bodyText.setContent(byteArray, "text/html");
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

}
