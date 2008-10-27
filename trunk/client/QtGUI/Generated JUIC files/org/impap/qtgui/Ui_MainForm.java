/********************************************************************************
** Form generated from reading ui file 'MainForm.jui'
**
** Created: ѕн 27. окт 04:08:41 2008
**      by: Qt User Interface Compiler version 4.4.2
**
** WARNING! All changes made in this file will be lost when recompiling ui file!
********************************************************************************/

package org.impap.qtgui;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class Ui_MainForm
{
    public QAction actionConnect;
    public QWidget centralwidget;
    public QTreeWidget messageTree;
    public QGroupBox groupBox;
    public QLineEdit addressEdit;
    public QLabel label;
    public QLabel label_2;
    public QSpinBox portSpin;
    public QLabel label_3;
    public QLineEdit usernameEdit;
    public QLabel label_4;
    public QLineEdit passEdit;
    public QLabel label_5;
    public QLineEdit fromEdit;
    public QLabel label_6;
    public QLineEdit toEdit;
    public QLabel label_7;
    public QLineEdit subjectEdit;
    public QTextBrowser bodyText;
    public QPushButton connectButton;
    public QStatusBar statusbar;

    public Ui_MainForm() { super(); }

    public void setupUi(QMainWindow MainForm)
    {
        MainForm.setObjectName("MainForm");
        MainForm.resize(new QSize(785, 526).expandedTo(MainForm.minimumSizeHint()));
        actionConnect = new QAction(MainForm);
        actionConnect.setObjectName("actionConnect");
        centralwidget = new QWidget(MainForm);
        centralwidget.setObjectName("centralwidget");
        messageTree = new QTreeWidget(centralwidget);
        messageTree.setObjectName("messageTree");
        messageTree.setGeometry(new QRect(0, 0, 311, 511));
        messageTree.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.WheelFocus);
        groupBox = new QGroupBox(centralwidget);
        groupBox.setObjectName("groupBox");
        groupBox.setGeometry(new QRect(310, 0, 471, 511));
        addressEdit = new QLineEdit(groupBox);
        addressEdit.setObjectName("addressEdit");
        addressEdit.setGeometry(new QRect(10, 20, 113, 20));
        addressEdit.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);
        label = new QLabel(groupBox);
        label.setObjectName("label");
        label.setGeometry(new QRect(10, 0, 46, 14));
        label_2 = new QLabel(groupBox);
        label_2.setObjectName("label_2");
        label_2.setGeometry(new QRect(140, 0, 46, 14));
        portSpin = new QSpinBox(groupBox);
        portSpin.setObjectName("portSpin");
        portSpin.setGeometry(new QRect(140, 20, 61, 22));
        portSpin.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.WheelFocus);
        portSpin.setMaximum(32768);
        portSpin.setValue(143);
        label_3 = new QLabel(groupBox);
        label_3.setObjectName("label_3");
        label_3.setGeometry(new QRect(10, 50, 61, 16));
        usernameEdit = new QLineEdit(groupBox);
        usernameEdit.setObjectName("usernameEdit");
        usernameEdit.setGeometry(new QRect(10, 70, 113, 20));
        usernameEdit.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);
        label_4 = new QLabel(groupBox);
        label_4.setObjectName("label_4");
        label_4.setGeometry(new QRect(140, 50, 46, 14));
        passEdit = new QLineEdit(groupBox);
        passEdit.setObjectName("passEdit");
        passEdit.setGeometry(new QRect(140, 70, 113, 20));
        passEdit.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);
        passEdit.setEchoMode(com.trolltech.qt.gui.QLineEdit.EchoMode.Password);
        label_5 = new QLabel(groupBox);
        label_5.setObjectName("label_5");
        label_5.setGeometry(new QRect(10, 100, 46, 14));
        fromEdit = new QLineEdit(groupBox);
        fromEdit.setObjectName("fromEdit");
        fromEdit.setGeometry(new QRect(10, 120, 451, 20));
        fromEdit.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);
        label_6 = new QLabel(groupBox);
        label_6.setObjectName("label_6");
        label_6.setGeometry(new QRect(10, 150, 46, 14));
        toEdit = new QLineEdit(groupBox);
        toEdit.setObjectName("toEdit");
        toEdit.setGeometry(new QRect(10, 170, 451, 20));
        toEdit.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);
        toEdit.setReadOnly(true);
        label_7 = new QLabel(groupBox);
        label_7.setObjectName("label_7");
        label_7.setGeometry(new QRect(10, 200, 46, 14));
        subjectEdit = new QLineEdit(groupBox);
        subjectEdit.setObjectName("subjectEdit");
        subjectEdit.setGeometry(new QRect(10, 220, 451, 20));
        subjectEdit.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);
        subjectEdit.setReadOnly(true);
        bodyText = new QTextBrowser(groupBox);
        bodyText.setObjectName("bodyText");
        bodyText.setGeometry(new QRect(10, 250, 451, 261));
        bodyText.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.WheelFocus);
        connectButton = new QPushButton(groupBox);
        connectButton.setObjectName("connectButton");
        connectButton.setGeometry(new QRect(220, 20, 51, 23));
        connectButton.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);
        MainForm.setCentralWidget(centralwidget);
        statusbar = new QStatusBar(MainForm);
        statusbar.setObjectName("statusbar");
        MainForm.setStatusBar(statusbar);
        retranslateUi(MainForm);
        connectButton.pressed.connect(actionConnect, "toggle()");

        MainForm.connectSlotsByName();
    } // setupUi

    void retranslateUi(QMainWindow MainForm)
    {
        MainForm.setWindowTitle(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "MainWindow"));
        actionConnect.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "connect"));
        messageTree.headerItem().setText(0, com.trolltech.qt.core.QCoreApplication.translate("MainForm", "mailbox"));
        groupBox.setTitle(com.trolltech.qt.core.QCoreApplication.translate("MainForm", ""));
        addressEdit.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "imap.aol.com"));
        label.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "address"));
        label_2.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "port"));
        label_3.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "username"));
        usernameEdit.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "alertmytest"));
        label_4.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "password"));
        passEdit.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "Qqqqqqqq"));
        label_5.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "from"));
        label_6.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "to"));
        label_7.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "subject"));
        connectButton.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "connect"));
    } // retranslateUi

}

