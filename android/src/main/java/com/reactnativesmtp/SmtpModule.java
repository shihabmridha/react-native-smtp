package com.reactnativesmtp;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.io.File;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SmtpModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    private String username;
    private String password;
    private String host;
    private String port;
    private String authEnabled;
    private String tls;

    // Errors
    private static final String E_CREDENTIAL_NOT_SET_ERROR = "E_CREDENTIAL_NOT_SET_ERROR";
    private static final String E_REQUIRED_MAIL_FIELD_ERROR = "E_REQUIRED_MAIL_FIELD_ERROR";
    private static final String E_SENDING_FAILED_ERROR = "E_SENDING_FAILED_ERROR";
    private static final String E_FILE_NOT_FOUND_ERROR = "E_FILE_NOT_FOUND_ERROR";

    public SmtpModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        this.host = null;
        this.port = "465";
        this.username = null;
        this.password = null;

        this.authEnabled = "true";
        this.tls = "true";
    }

    @Override
    public String getName() {
        return "Smtp";
    }

    @ReactMethod
    public void config (ReadableMap config) {
        if (config.hasKey("host")) {
            this.host = config.getString("host");
        }

        if (config.hasKey("port")) {
            this.port = config.getString("port");
        }

        if (config.hasKey("username")) {
            this.username = config.getString("username");
        }

        if (config.hasKey("password")) {
            this.password = config.getString("password");
        }

        if (config.hasKey("authEnabled")) {
            this.authEnabled = config.getString("authEnabled");
        }

        if (config.hasKey("tls")) {
            this.tls = config.getString("tls");
        }
    }

    @ReactMethod
    public void send(final ReadableMap mailData, final ReadableArray attachments, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (username == null || password == null || host == null) {
                    promise.reject(E_CREDENTIAL_NOT_SET_ERROR, "Credential not set.");
                    return;
                }

                if (!mailData.hasKey("from")) {
                    promise.reject(E_REQUIRED_MAIL_FIELD_ERROR, "Missing required field (from).");
                    return;
                }

                if (!mailData.hasKey("to")) {
                    promise.reject(E_REQUIRED_MAIL_FIELD_ERROR, "Missing required field (to).");
                    return;
                }

                if (!mailData.hasKey("subject")) {
                    promise.reject(E_REQUIRED_MAIL_FIELD_ERROR, "Missing required field (subject).");
                    return;
                }

                if (!mailData.hasKey("body")) {
                    promise.reject(E_REQUIRED_MAIL_FIELD_ERROR, "Missing required field (body).");
                    return;
                }

                String from, to, subject, body;

                from = mailData.getString("from");
                to = mailData.getString("to");
                subject = mailData.getString("subject");
                body = mailData.getString("body");

                Properties props = new Properties();
                props.put("mail.smtp.auth", authEnabled);
                props.put("mail.smtp.starttls.enable", tls);
                props.put("mail.smtp.host", host);
                props.put("mail.smtp.port", port);

                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(from));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                    message.setSubject(subject);

                    Multipart multipart = new MimeMultipart();
                    MimeBodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setContent(body, "text/html");
                    multipart.addBodyPart(messageBodyPart);

                    int totalAttachmentCount = attachments.size();
                    if (totalAttachmentCount > 0) {
                        // Process attachments
                        for (int i = 0; i < totalAttachmentCount; i++) {
                            String filePath = attachments.getString(i);
                            File file = new File(filePath);
                            if (!file.exists()) {
                                promise.reject(E_FILE_NOT_FOUND_ERROR, "File not found.");
                                return;
                            }

                            String fileName = file.getName();
                            messageBodyPart = new MimeBodyPart();
                            DataSource source = new FileDataSource(filePath);
                            messageBodyPart.setDataHandler(new DataHandler(source));
                            messageBodyPart.setFileName(fileName);
                            multipart.addBodyPart(messageBodyPart);
                        }
                    }

                    message.setContent(multipart);

                    Transport.send(message);

                    promise.resolve(true);

                } catch (MessagingException e) {
                    promise.reject(E_SENDING_FAILED_ERROR, e.getMessage());
                }
            }
        }).start();
    }
}
