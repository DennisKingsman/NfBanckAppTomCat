<!DOCTYPE html>
<html>
   <head>
      <meta charset="UTF-8">
      <title>User Info</title>
   </head>
   <body>

     <jsp:include page="_menu.jsp"></jsp:include>

      <h3>Hello: ${loginedUser.getLogin()}</h3>

      User Password: <b>${loginedUser.getPassword()}</b>
      <br />


   </body>
</html>