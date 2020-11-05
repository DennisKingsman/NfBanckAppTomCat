<!DOCTYPE html>
<html>
   <head>
      <meta charset="UTF-8">
      <title>Edit</title>
   </head>
   <body>

      <jsp:include page="_menu.jsp"></jsp:include>

      <form method="POST" action="${pageContext.request.contextPath}/editUserParam/*">
         <table border="0">
            <tr>
               <td>Enter new value</td>
               <td><input type="text" name="paramString"/> </td>
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