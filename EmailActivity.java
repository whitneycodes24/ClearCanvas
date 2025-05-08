package com.example.fyp_clearcanvas;


import java.io.File;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
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

public class EmailActivity {

    private final String senderEmail;
    private final String senderPassword;
    private final Session session;

    public EmailActivity(String email, String password) {  //uses clear canvas gmail account
        this.senderEmail = email;
        this.senderPassword = password;

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");  //protects data

        session = Session.getInstance(props, new Authenticator() {  //creates mail session
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

    }

    public void sendEmail(String recipientEmail, String subject, String body, File pdfFile) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(subject);

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body);  //my message

        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataSource source = new FileDataSource(pdfFile);
        attachmentPart.setDataHandler(new DataHandler(source));
        attachmentPart.setFileName(pdfFile.getName());

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}
