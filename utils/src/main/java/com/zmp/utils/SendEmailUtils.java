package com.zmp.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by zmp on 2017/10/19.
 */

public class SendEmailUtils {

        private String TAG = "SendEmailUtils";

        private List<String> toUserNames;

        private ISendResult resultListener;


        private final Handler handler;

        private SendEmailUtils() {
                HandlerThread handlerThread = new HandlerThread(TAG);
                handlerThread.start();
                handler = new Handler(handlerThread.getLooper());
        }

        private static SendEmailUtils instance;

        public static SendEmailUtils getInstance() {
                if (instance == null) {
                        instance = new SendEmailUtils();
                }
                return instance;
        }

        private String username;

        private String password;

        public void sendEmail(final String title, final String content, final String filePath, final String emailFileName) {
                handler.post(new Runnable() {
                        @Override
                        public void run() {
                                String sendhost = "smtp.exmail.qq.com";
                                Properties props = new Properties();
                                props.put("mail.smtp.host", sendhost);// 存储发送邮件服务器的信息
                                props.put("mail.smtp.auth", "true");// 同时通过验证
                                // 基本的邮件会话
                                Session session = Session.getInstance(props);
                                session.setDebug(false);// 设置调试标志
                                // 构造信息体
                                MimeMessage message = new MimeMessage(session);
                                Address fromAddress = null;
                                try {
                                        fromAddress = new InternetAddress(username);
                                        message.setFrom(fromAddress);
                                        for (String toUserName : toUserNames) {
                                                Address toAddress = new InternetAddress(toUserName);
                                                message.addRecipient(Message.RecipientType.TO, toAddress);
                                        }

                                        message.setSubject(title);// 设置信件的标题
                                        // 邮件文本/HTML内容
                                        Multipart multipart = new MimeMultipart();
                                        MimeBodyPart messageBodyPart = new MimeBodyPart();
                                        byte[] bytes = content.getBytes();
                                        String s = new String(bytes, "ISO-8859-1");
                                        messageBodyPart.setContent(s, "text/html");
                                        multipart.addBodyPart(messageBodyPart);
                                        if (!TextUtils.isEmpty(filePath)) {
                                                File file = new File(filePath);
                                                if (file.exists() && file.isFile()) {
                                                        // 添加邮件附件
                                                        MimeBodyPart attachPart = new MimeBodyPart();
                                                        DataSource source = new FileDataSource(filePath);
                                                        attachPart.setDataHandler(new DataHandler(source));
                                                        String emailName = emailFileName;
                                                        if (TextUtils.isEmpty(emailName)) {
                                                                emailName = file.getName();
                                                        }
                                                        attachPart.setFileName(emailName);
                                                        multipart.addBodyPart(attachPart);
                                                }
                                        }
                                        message.setContent(multipart);
                                        message.saveChanges(); // implicit with send()//存储有信息
                                        // send e-mail message
                                        Transport transport = null;
                                        transport = session.getTransport("smtp");
                                        transport.connect(sendhost, username, password);
                                        transport.sendMessage(message, message.getAllRecipients());
                                        transport.close();
                                }
                                catch (Exception e) {
                                        e.printStackTrace();
                                        if (resultListener != null) {
                                                try {
                                                        resultListener.onFail(new String(e.getMessage().getBytes("ISO_8859-1"), "GBK"));
                                                }
                                                catch (UnsupportedEncodingException e1) {
                                                        e1.printStackTrace();
                                                        resultListener.onFail(e.getMessage());
                                                }
                                        }
                                        return;
                                }
                                if (resultListener != null) {
                                        resultListener.onSuccess();
                                }
                        }
                });
        }


        public void sendEmail(final String title, final String content) {
                handler.post(new Runnable() {
                        @Override
                        public void run() {
                                String sendHost = "smtp.exmail.qq.com";
                                Properties props = new Properties();
                                props.put("mail.smtp.host", sendHost);// 存储发送邮件服务器的信息
                                props.put("mail.smtp.auth", "true");// 同时通过验证
                                // 基本的邮件会话
                                Session session = Session.getInstance(props);
                                session.setDebug(false);// 设置调试标志
                                // 构造信息体
                                MimeMessage message = new MimeMessage(session);
                                Address fromAddress = null;
                                try {
                                        fromAddress = new InternetAddress(username);
                                        message.setFrom(fromAddress);
                                        for (String toUserName : toUserNames) {
                                                Address toAddress = new InternetAddress(toUserName);
                                                message.addRecipient(Message.RecipientType.TO, toAddress);
                                        }
                                        message.setSubject(title);// 设置信件的标题
                                        message.setText(content);// 设置信件内容
                                        Transport transport = session.getTransport("smtp");
                                        transport.connect(sendHost, username, password);
                                        transport.sendMessage(message, message.getAllRecipients());
                                        transport.close();
                                }
                                catch (Exception e) {
                                        e.printStackTrace();
                                        if (resultListener != null) {
                                                try {
                                                        resultListener.onFail(new String(e.getMessage().getBytes("ISO_8859-1"), "GBK"));
                                                }
                                                catch (UnsupportedEncodingException e1) {
                                                        e1.printStackTrace();
                                                        resultListener.onFail(e.getMessage());
                                                }
                                        }
                                        return;
                                }
                                if (resultListener != null) {
                                        resultListener.onSuccess();
                                }
                        }
                });
        }

        public void init(String username, String password, ISendResult resultListener) {
                this.username = username;
                this.password = password;
                this.resultListener = resultListener;
                toUserNames = Collections.synchronizedList(new ArrayList<String>());
        }

        public void addToUser(String toName) {
                if (TextUtils.isEmpty(toName) || toUserNames.contains(toName)) {
                        return;
                }
                toUserNames.add(toName);
        }

        public interface ISendResult {

                void onSuccess();

                void onFail(String ex);
        }
}
