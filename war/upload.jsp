<%@page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html>
 <head>
  <meta charset="utf-8">
  <title>ファイルアップロード</title>
 </head>
 <body>
  <form action="/imagemailsender" enctype="multipart/form-data" method="post">
   <input type="file" name="files" multiple><br>
   <input type="submit" value="送信">
  </form>
<%
    String upload_files = (String) request.getAttribute("upload_files");
    if (upload_files != null) {
%>
<br>
<span style="color: #ff0000;">以下のファイルがアップロードされました。</span><br>
<%= upload_files %>
<%
    }
%>
 </body>
</html>