/********************************************************************************
** Form generated from reading ui file 'MainForm.jui'
**
** Created: �� 11. ��� 10:46:58 2008
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
    public QHBoxLayout horizontalLayout;
    public QGroupBox groupBox_2;
    public QTreeWidget messageTree;
    public QGroupBox groupBox;
    public QVBoxLayout verticalLayout;
    public QGroupBox groupBox_4;
    public QLabel label_7;
    public QLineEdit subjectEdit;
    public QLabel label_5;
    public QLineEdit fromEdit;
    public QLineEdit toEdit;
    public QLabel label_6;
    public QPushButton connectButton;
    public QLabel label_4;
    public QLabel label;
    public QLabel label_2;
    public QLineEdit usernameEdit;
    public QLineEdit addressEdit;
    public QSpinBox portSpin;
    public QLineEdit passEdit;
    public QLabel label_3;
    public QPushButton newMessageButton;
    public QPushButton saveMessageButton;
    public QGroupBox groupBox_3;
    public QTextBrowser bodyText;
    public QGroupBox groupBox_5;
    public QTextBrowser consoleText;
    public QStatusBar statusbar;

    public Ui_MainForm() { super(); }

    public void setupUi(QMainWindow MainForm)
    {
        MainForm.setObjectName("MainForm");
        MainForm.resize(new QSize(897, 941).expandedTo(MainForm.minimumSizeHint()));
        actionConnect = new QAction(MainForm);
        actionConnect.setObjectName("actionConnect");
        centralwidget = new QWidget(MainForm);
        centralwidget.setObjectName("centralwidget");
        horizontalLayout = new QHBoxLayout(centralwidget);
        horizontalLayout.setObjectName("horizontalLayout");
        groupBox_2 = new QGroupBox(centralwidget);
        groupBox_2.setObjectName("groupBox_2");
        messageTree = new QTreeWidget(groupBox_2);
        messageTree.setObjectName("messageTree");
        messageTree.setGeometry(new QRect(3, 8, 430, 890));
        messageTree.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.WheelFocus);
        messageTree.setAutoFillBackground(false);

        horizontalLayout.addWidget(groupBox_2);

        groupBox = new QGroupBox(centralwidget);
        groupBox.setObjectName("groupBox");
        verticalLayout = new QVBoxLayout(groupBox);
        verticalLayout.setObjectName("verticalLayout");
        groupBox_4 = new QGroupBox(groupBox);
        groupBox_4.setObjectName("groupBox_4");
        label_7 = new QLabel(groupBox_4);
        label_7.setObjectName("label_7");
        label_7.setGeometry(new QRect(10, 240, 46, 14));
        subjectEdit = new QLineEdit(groupBox_4);
        subjectEdit.setObjectName("subjectEdit");
        subjectEdit.setGeometry(new QRect(10, 260, 451, 20));
        subjectEdit.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);
        subjectEdit.setReadOnly(true);
        label_5 = new QLabel(groupBox_4);
        label_5.setObjectName("label_5");
        label_5.setGeometry(new QRect(10, 150, 46, 14));
        fromEdit = new QLineEdit(groupBox_4);
        fromEdit.setObjectName("fromEdit");
        fromEdit.setGeometry(new QRect(10, 170, 451, 20));
        fromEdit.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);
        fromEdit.setReadOnly(true);
        toEdit = new QLineEdit(groupBox_4);
        toEdit.setObjectName("toEdit");
        toEdit.setGeometry(new QRect(10, 210, 451, 20));
        toEdit.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);
        toEdit.setReadOnly(true);
        label_6 = new QLabel(groupBox_4);
        label_6.setObjectName("label_6");
        label_6.setGeometry(new QRect(10, 190, 46, 14));
        connectButton = new QPushButton(groupBox_4);
        connectButton.setObjectName("connectButton");
        connectButton.setGeometry(new QRect(220, 34, 51, 23));
        connectButton.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);
        label_4 = new QLabel(groupBox_4);
        label_4.setObjectName("label_4");
        label_4.setGeometry(new QRect(140, 64, 46, 14));
        label = new QLabel(groupBox_4);
        label.setObjectName("label");
        label.setGeometry(new QRect(10, 14, 46, 14));
        label_2 = new QLabel(groupBox_4);
        label_2.setObjectName("label_2");
        label_2.setGeometry(new QRect(140, 14, 46, 14));
        usernameEdit = new QLineEdit(groupBox_4);
        usernameEdit.setObjectName("usernameEdit");
        usernameEdit.setGeometry(new QRect(10, 84, 113, 20));
        usernameEdit.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);
        addressEdit = new QLineEdit(groupBox_4);
        addressEdit.setObjectName("addressEdit");
        addressEdit.setGeometry(new QRect(10, 34, 113, 20));
        addressEdit.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);
        portSpin = new QSpinBox(groupBox_4);
        portSpin.setObjectName("portSpin");
        portSpin.setGeometry(new QRect(140, 34, 61, 22));
        portSpin.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.WheelFocus);
        portSpin.setMaximum(32768);
        portSpin.setValue(143);
        passEdit = new QLineEdit(groupBox_4);
        passEdit.setObjectName("passEdit");
        passEdit.setGeometry(new QRect(140, 84, 113, 20));
        passEdit.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);
        passEdit.setEchoMode(com.trolltech.qt.gui.QLineEdit.EchoMode.Password);
        label_3 = new QLabel(groupBox_4);
        label_3.setObjectName("label_3");
        label_3.setGeometry(new QRect(10, 64, 61, 16));
        newMessageButton = new QPushButton(groupBox_4);
        newMessageButton.setObjectName("newMessageButton");
        newMessageButton.setGeometry(new QRect(10, 120, 81, 23));
        newMessageButton.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);
        saveMessageButton = new QPushButton(groupBox_4);
        saveMessageButton.setObjectName("saveMessageButton");
        saveMessageButton.setGeometry(new QRect(100, 120, 81, 23));
        saveMessageButton.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.StrongFocus);

        verticalLayout.addWidget(groupBox_4);

        groupBox_3 = new QGroupBox(groupBox);
        groupBox_3.setObjectName("groupBox_3");
        bodyText = new QTextBrowser(groupBox_3);
        bodyText.setObjectName("bodyText");
        bodyText.setGeometry(new QRect(5, 5, 401, 281));
        bodyText.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.WheelFocus);

        verticalLayout.addWidget(groupBox_3);

        groupBox_5 = new QGroupBox(groupBox);
        groupBox_5.setObjectName("groupBox_5");
        consoleText = new QTextBrowser(groupBox_5);
        consoleText.setObjectName("consoleText");
        consoleText.setGeometry(new QRect(5, 21, 401, 261));
        consoleText.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.WheelFocus);

        verticalLayout.addWidget(groupBox_5);


        horizontalLayout.addWidget(groupBox);

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
        groupBox_2.setTitle(com.trolltech.qt.core.QCoreApplication.translate("MainForm", ""));
        messageTree.headerItem().setText(0, com.trolltech.qt.core.QCoreApplication.translate("MainForm", "mailbox"));
        groupBox.setTitle(com.trolltech.qt.core.QCoreApplication.translate("MainForm", ""));
        groupBox_4.setTitle(com.trolltech.qt.core.QCoreApplication.translate("MainForm", ""));
        label_7.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "subject"));
        label_5.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "from"));
        label_6.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "to"));
        connectButton.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "connect"));
        label_4.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "password"));
        label.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "address"));
        label_2.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "port"));
        usernameEdit.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "alertmytest"));
        addressEdit.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "imap.aol.com"));
        passEdit.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "Int&Fun"));
        label_3.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "username"));
        newMessageButton.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "new message"));
        saveMessageButton.setText(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "save"));
        groupBox_3.setTitle(com.trolltech.qt.core.QCoreApplication.translate("MainForm", ""));
        groupBox_5.setTitle(com.trolltech.qt.core.QCoreApplication.translate("MainForm", "Console"));
    } // retranslateUi

}

