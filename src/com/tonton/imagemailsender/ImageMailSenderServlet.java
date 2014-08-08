package com.tonton.imagemailsender;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@SuppressWarnings("serial")
public class ImageMailSenderServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		StringBuilder sb = new StringBuilder();
		try {
			ServletFileUpload upload = new ServletFileUpload(); // ServletFileUploadのインスタンスを取得.
			FileItemIterator iterator = upload.getItemIterator(req); // イテレータにぶっ込む.
			// イテレータが空になるまでwhileループ.
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
//				InputStream stream = item.openStream();
				sb.append("name : " + item.getName()).append("<br/>\n");
				sb.append("field name : " + item.getFieldName()).append("<br/>\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		req.setAttribute("upload_files", sb.toString());
		req.getRequestDispatcher("/upload.jsp").forward(req, resp);
	}
}
