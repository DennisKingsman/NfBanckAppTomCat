<!DOCTYPE html>
<html>
    <head>
        <meta charset = "UTF-8">
        <title>Transfer Page</title>
    </head>
    <body>
      <jsp:include page="_menu.jsp"></jsp:include>
      </br>
      <strong>
        Select an account to transfer money to </br>
        At your disposal: ${FromBillBalance} .
      </strong>

    <table border="1">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Balance</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            <thead>
            <tbody>

                 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
                 <c:forEach items="${otherBills}" var="item" varStatus="loop">
                 <tr>
                    <td>${otherBills.get(loop.index).getId()}</td>
                    <td>${otherBills.get(loop.index).getBalance()}</td>
                    <td>${otherBills.get(loop.index).getStatus()}</td>

                    <td>
                     <form method="POST" action="${pageContext.request.contextPath}/transferBtoB/${FromBillBalance}">
                           <input type="text" name="amount"/>
                          <input type = "hidden" name = "BillFromAmount" value = "${FromBillBalance}">
                           <input type = "hidden" name = "BillToId" value = "${otherBills.get(loop.index).getId()}">
                          <input type="hidden"  name="BillFromId" value="${billFromId}">
                           <input type="submit" name="transfer" value="Transfer"></td>
                          </form>
                    </td>
<!--
                    <td><a href = "${pageContext.request.contextPath}/transferBtoB/${userBills.get(loop.index).getId()}">Transfer money</a></td>
    -->
                    </tr>
                 </c:forEach>



            </tbody>
    </table>

    </body>
</html>