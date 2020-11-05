<!DOCTYPE html>
<html>
   <head>
      <meta charset="UTF-8">
      <title>Login</title>
   </head>
   <body>

      <jsp:include page="_menu.jsp"></jsp:include>

      <form method="POST" action="${pageContext.request.contextPath}/editUserProfile">
         <table border="0">
            <tr>
               <td>User Name</td>
               <td><input type="text" name="userName" value = "${userAccount.getName()}"/> </td>

            </tr>
            <tr>
               <td>Second Name</td>
               <td><input type="text" name="secondName" value = "${userAccount.getSecondName()}"/> </td>
            </tr>
            <tr>
               <td>Date of Birth</td>
               <td><input type="datetime-local" name="dateOfBirth"/> </td>
            </tr>

            <tr>
               <td colspan ="2">
                  <input type="submit" value= "Submit" />
               </td>
            </tr>
         </table>
      </form>
   </body>

</html>