<!DOCTYPE html>
<html>
   <head>
      <meta charset="UTF-8">
      <title>EditBill</title>
   </head>
   <body>

      <jsp:include page="_menu.jsp"></jsp:include>

      <form method="POST" action="${pageContext.request.contextPath}/editBill">
      <table>
            <tr>
            <td>Select bill status</td>
            <td>
            <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
            <select name="statusItems">
                <c:forEach items="${status}" var="item" varStatus="loop">
                    <option value="${status.get(loop.index)}">
                        ${item}
                    </option>
                </c:forEach>
            </select>
            </td>
            </tr>

            <tr>
            <td><input name="inputStatus" type="submit" value="Change" /></td>
            </tr>
      </table>
      </form>
   </body>

</html>