package org.impap.qtgui;

import org.imap.common.ApplicationClient;
import org.imap.common.Logger;
import org.imap.message.Item;
import org.imap.message.Message;

import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.gui.QApplication;
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
		ui.newMessageButton.clicked.connect(this, "editMode()");
		ui.saveMessageButton.clicked.connect(this, "saveMessage()");
		ui.messageTree.currentItemChanged.connect(this, "itemSelected()");
		ui.checkBoxSeen.clicked.connect(this, "seenFlagChanged()");
		ui.checkBoxStarred.clicked.connect(this, "starredFlagChanged()");
		ui.createFolderButton.clicked.connect(this, "createFolder()");
		ui.renameFolderButton.clicked.connect(this, "renameFolder()");
		ui.deleteFolderButton.clicked.connect(this, "deleteFolder()");

		ui.checkBoxSeen.setEnabled(false);
		ui.checkBoxStarred.setEnabled(false);
		ui.checkBoxSeen.setChecked(false);
		ui.checkBoxStarred.setChecked(false);
		ui.createFolderButton.setEnabled(false);
		ui.renameFolderButton.setEnabled(false);
		ui.deleteFolderButton.setEnabled(false);

		client.addLogger(new GuiLogger(ui));

		// menubar.triggered.connect(MainForm, "close()");
		// connect(timer, SIGNAL())
	}

	public MainForm(QWidget parent) {
		super(parent);
		ui.setupUi(this);
	}

	public static class GuiLogger implements Logger {
		private Ui_MainForm ui;

		public GuiLogger(Ui_MainForm ui) {
			this.ui = ui;
		}

		public void log(final String str) {
			QApplication.invokeLater(new Runnable() {
				public void run() {
					ui.consoleText.setText(ui.consoleText.toPlainText() + str
							+ "\n");
				}
			});
		}
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
		} else if ((currentMessage != null)
				&& (ui.messageTree.currentItem() != null)) {
			Item folder = (Item) ui.messageTree.currentItem().data(1, 0);
			if (!currentMessage.isDownloaded()
					&& folder.getId().equals(currentMessage.getId())) {
				currentMessage = client.getMessage(folder, currentMessage
						.getId());
				ui.fromEdit.setText(currentMessage.from());
				ui.toEdit.setText(currentMessage.to());
				ui.subjectEdit.setText(currentMessage.subject());
				ui.bodyText.setHtml(currentMessage.content());
				ui.checkBoxSeen.setChecked(currentMessage.getFlag("\\Seen"));
				ui.checkBoxStarred.setChecked(currentMessage
						.getFlag("\\Flagged"));
			}
		}
	}

	private void updateMessageTree(QTreeWidgetItem parent, Item imapItem) {
		System.out.println("update begin");
		QTreeWidgetItem item = new QTreeWidgetItem(parent);
		item.setText(0, imapItem.getText());
		item.setData(1, 0, imapItem);

		for (Item child : imapItem.getChildren().values()) {
			updateMessageTree(item, child);
		}
		System.out.println("update end");
	}

	public void connect(boolean value) {
		// System.out.println("Connect clicked");

		connected = !connected;

		if (connected) {
			ui.messageTree.collapseAll();
			client.connect(ui.addressEdit.text(), Integer.valueOf(ui.portSpin
					.value()), ui.usernameEdit.text(), ui.passEdit.text());
			ui.createFolderButton.setEnabled(true);
			ui.renameFolderButton.setEnabled(true);
			ui.deleteFolderButton.setEnabled(true);

		} else {
			client.disconnect();
			ui.createFolderButton.setEnabled(false);
			ui.renameFolderButton.setEnabled(false);
			ui.deleteFolderButton.setEnabled(false);
		}
	}

	public void saveMessage() {
		if (ui.messageTree.topLevelItem(0) != (ui.messageTree.currentItem())
				&& ui.messageTree.currentItem() != null) {
			Item imapItem = (Item) ui.messageTree.currentItem().data(1, 0);
			client.saveMessage(ui.toEdit.text(), ui.fromEdit.text(),
					ui.subjectEdit.text(), ui.bodyText.toPlainText(), imapItem);
		}
	}

	public void createFolder() {
		if (ui.messageTree.currentItem() != null) {
 		  client.createFolder(ui.newFolderText.text());
		}
	}

	public void renameFolder() {
		if (ui.messageTree.currentItem() != null
				&& ui.messageTree.topLevelItem(0) != (ui.messageTree
						.currentItem())) {
			Item imapItem = (Item) ui.messageTree.currentItem().data(1, 0);
			if (imapItem.isFolder()) {
				client.renameFolder(imapItem, ui.newFolderText.text());
			}
		}
	}

	public void deleteFolder() {
		if (ui.messageTree.currentItem() != null
				&& ui.messageTree.topLevelItem(0) != (ui.messageTree
						.currentItem())) {
			Item imapItem = (Item) ui.messageTree.currentItem().data(1, 0);
			if (imapItem.isFolder()) {
				client.deleteFolder(imapItem);
				// ui.messageTree.removeItemWidget(ui.messageTree.currentItem(),
				// 0);
				ui.messageTree.clear();
				ui.messageTree.collapseAll();
			}
		}
	}

	public void editMode() {
		if (ui.messageTree.topLevelItem(0) != (ui.messageTree.currentItem())
				&& ui.messageTree.currentItem() != null) {
			setViewMode(false);
			ui.fromEdit.setText(ui.usernameEdit.text());
			ui.toEdit.setText("");
			ui.subjectEdit.setText("");
			ui.bodyText.setText("");
			ui.checkBoxSeen.setChecked(false);
			ui.checkBoxStarred.setChecked(false);
		}
	}

	public void viewMode() {
		setViewMode(true);
	}

	public void seenFlagChanged() {
		Item imapItem = (Item) ui.messageTree.currentItem().data(1, 0);

		if (imapItem != null) {
			boolean value = ui.checkBoxSeen.isChecked();
			client.setFlag(imapItem, currentMessage, "\\Seen", value);
		}
	}

	public void starredFlagChanged() {
		Item imapItem = (Item) ui.messageTree.currentItem().data(1, 0);

		if (imapItem != null) {
			boolean value = ui.checkBoxStarred.isChecked();
			client.setFlag(imapItem, currentMessage, "\\Flagged", value);
		}
	}

	public void setViewMode(boolean value) {
		ui.fromEdit.setReadOnly(value);
		ui.toEdit.setReadOnly(value);
		ui.subjectEdit.setReadOnly(value);
		ui.bodyText.setReadOnly(value);
	}

	public void itemSelected() {
		ui.checkBoxSeen.setEnabled(false);
		ui.checkBoxStarred.setEnabled(false);
		ui.checkBoxSeen.setChecked(false);
		ui.checkBoxStarred.setChecked(false);

		viewMode();
		// if (connected) {
		if (ui.messageTree.currentItem() != null) {
			Item imapItem = (Item) ui.messageTree.currentItem().data(1, 0);

			if (imapItem != null) {

				if (imapItem.isFolder()) {
					currentMessage = null;
					ui.fromEdit.setText("");
					ui.toEdit.setText("");
					ui.subjectEdit.setText("");
					ui.bodyText.setText("");
					client.listFolder(imapItem);
					System.out.println("list folder called");
				} else {
					currentMessage = client.getMessage(imapItem, imapItem
							.getId());
					ui.fromEdit.setText(currentMessage.from());
					ui.toEdit.setText(currentMessage.to());
					ui.subjectEdit.setText(currentMessage.subject());
					// QByteArray byteArray = new QByteArray();
					// byteArray.append(currentMessage.content());
					String content = currentMessage.content();
					System.out.println("GUI got message content");
					ui.bodyText.setHtml(content);
					System.out.println("content is set");
					ui.checkBoxSeen.setEnabled(true);
					ui.checkBoxStarred.setEnabled(true);
					ui.checkBoxSeen
							.setChecked(currentMessage.getFlag("\\Seen"));
					ui.checkBoxStarred.setChecked(currentMessage
							.getFlag("\\Flagged"));
				}
			}
		}
		// }
	}

	@Override
	protected void closeEvent(QCloseEvent event) {
		client.close();
		super.closeEvent(event);
	}

}
