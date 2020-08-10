<!DOCTYPE html>
<html>
   <head>
      <meta charset="UTF-8">
      <title>Login</title>
   </head>
   <body>

      <jsp:include page="_menu.jsp"></jsp:include>

      <h3>Register Page</h3>

      <p style="color: red;">${errorMessage}</p>

      <form method="POST" action="${pageContext.request.contextPath}/register">
         <table border="0">
            <tr>
               <td>User Login</td>
               <td><input type="text" name="userLogin"/> </td>
            </tr>
            <tr>
               <td>Password</td>
               <td><input type="password" name="userPassword"/> </td>
            </tr>

            <tr>
               <td colspan ="2">
                  <input type="submit" value= "Submit" />
                  <a href="${pageContext.request.contextPath}/">Cancel</a>
               </td>
            </tr>
         </table>
      </form>
   </body>

</html>