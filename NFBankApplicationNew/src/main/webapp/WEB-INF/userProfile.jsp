<!DOCTYPE html>
<html>
   <head>
      <meta charset="UTF-8">
      <title>User Info</title>
   </head>
   <body>

     <jsp:include page="_menu.jsp"></jsp:include>

      <h3>Hello: ${loginedUser.getLogin()}</h3>

      User Name: <b>${loginedUser.getName()}</b>
      <br/>
      <a href="${pageContext.request.contextPath}/editUserParam/name">Edit user Name</a>
      <br />

      User dateOfBirth: <b>${loginedUser.getDate()}</b>
      <br/>
      <a href="${pageContext.request.contextPath}/dateParam/dateOfBirth">Edit user date of birth</a>

      <br/>

      User Second Name: <b>${loginedUser.getSecondName()}</b>
      <br/>
      <a href="${pageContext.request.contextPath}/editUserParam/secondName">Edit user second Name</a>
      <br/>

      <a href = "${pageContext.request.contextPath}/editUserProfile">Edit</a>

   </body>
</html>