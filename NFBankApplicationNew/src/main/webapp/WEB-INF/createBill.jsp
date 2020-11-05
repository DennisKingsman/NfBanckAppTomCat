<!DOCTYPE html>
<html>
<head>
      <meta charset="UTF-8">
      <title>CreateBill</title>
</head>
<body>
      <jsp:include page="_menu.jsp"></jsp:include>
      <h3>Create Bill Page</h3> </br>
      Confirm the bill creation
      <form method="POST" action="${pageContext.request.contextPath}/createBill">
     <!--
      <input type="radio" name="answer" value="valOne"/>First package<br>
      -->
      <input type="hidden"  name="hiddenCreate" value="receivedCreate">
       <input name="create" type="submit" value="Create"></td>
      </form>
</body>
</html>