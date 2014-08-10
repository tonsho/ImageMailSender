package com.tonton.imagemailsender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@SuppressWarnings("serial")
public class ImageMailSenderServlet extends HttpServlet {
	static final String SENDER = "kazutaka.tonsho@gmail.com";
	static final protected Logger log = Logger.getLogger(ImageMailSenderServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.fine("doPost(req=" + req + ")");
		StringBuilder sb = new StringBuilder();
		try {
			InternetAddress address = null;
			ServletFileUpload upload = new ServletFileUpload(); // ServletFileUploadのインスタンスを取得.
			FileItemIterator iterator = upload.getItemIterator(req); // イテレータにぶっ込む.
			// イテレータが空になるまでwhileループ.
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				String fieldName = item.getFieldName();
				if ("address".equals(fieldName)) {
					String mailAddress = converToString(item.openStream());
					address = new InternetAddress(mailAddress);
				}
				if ("files".equals(fieldName)) {
					sb.append("name : " + item.getName()).append("<br/>\n");
					sendMail(address, item);
				}
			}
		} catch (Exception e) {
			log.severe(e.toString());
			e.printStackTrace();
			sb.append(e.toString());
		}
		req.setAttribute("upload_files", sb.toString());
		req.getRequestDispatcher("/upload.jsp").forward(req, resp);
	}

	private InternetAddress createInternetAddress(HttpServletRequest req) throws FileUploadException, IOException, AddressException {
		ServletFileUpload upload = new ServletFileUpload(); // ServletFileUploadのインスタンスを取得.
		FileItemIterator iterator = upload.getItemIterator(req); // イテレータにぶっ込む.
		while (iterator.hasNext()) {
			FileItemStream item = iterator.next();
			if ("address".equals(item.getFieldName())) {
				String address = converToString(item.openStream());
				return new InternetAddress(address);
			}
		}
		return null;
	}

	private String converToString(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		return sb.toString();
	}

	public void sendMail(InternetAddress address, FileItemStream item) throws MessagingException, IOException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

//		Message message = createMessage(session, address, item);
		Message message = createMessageWithAttache(session, address, item);
		Transport.send(message);
	}

	private Message createMessageWithAttache(Session session, InternetAddress address, FileItemStream item) throws MessagingException, IOException {
		InternetAddress fromAddress = new InternetAddress(SENDER);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(fromAddress); // 送信元アドレス
		message.addRecipient(Message.RecipientType.TO, address); // あて先アドレス

		message.setSubject(item.getName(), "ISO-2022-JP"); // タイトル

		MimeBodyPart body = new MimeBodyPart();
		body.setText(""); // メール本文

		Multipart mp = new MimeMultipart();
		mp.addBodyPart(body);

		ByteArrayDataSource dataSouce = new ByteArrayDataSource(item.openStream(), "image/*");
		MimeBodyPart attache = new MimeBodyPart();
		attache.setDataHandler(new DataHandler(dataSouce));
		attache.setFileName(item.getName()); // 添付ファイル名
		mp.addBodyPart(attache);
		message.setContent(mp); // 添付ファイルデータ

		return message;
	}

	private Message createMessage(Session session, InternetAddress address, FileItemStream item) throws MessagingException {
		MimeMessage message = new MimeMessage(session);
		message.setFrom(address); // 送信元アドレス
		message.addRecipient(Message.RecipientType.TO, address); // あて先アドレス

		message.setSubject(item.getName(), "ISO-2022-JP"); // タイトル
		message.setText(item.getName()); // メール本文
		return message;
	}
}
