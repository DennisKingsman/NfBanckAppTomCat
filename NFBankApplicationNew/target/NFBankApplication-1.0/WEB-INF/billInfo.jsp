<!DOCTYPE html>
<html>
    <head>
        <meta charset = "UTF-8">
        <title>Bill info</title>
    </head>
    <body>
      <jsp:include page="_menu.jsp"></jsp:include>
    <table border="1">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>OpenDate</th>
                    <th>Balance</th>
                    <th>Status</th>
                    <th>CloseDate</th>
                    <th>Actions</th>
                </tr>
            <thead>
            <tbody>

                 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
                 <c:forEach items="${userBills}" var="item" varStatus="loop">
                 <tr>
                    <td>${userBills.get(loop.index).getId()}</td>
                    <td>${userBills.get(loop.index).getOpenDate()}</td>
                    <td>${userBills.get(loop.index).getBalance()}</td>
                    <td>${userBills.get(loop.index).getStatus()}</td>
                    <td>${userBills.get(loop.index).getCloseDate()}</td>

                    <c:if test="${(nm == 'deepak') && (psw == 'deepak')}">
                    <p>Welcome, <c:out value="${nm }"/></p>
                    </c:if>

                    <c:if test = "${userBills.get(loop.index).getStatus() eq 'OPEN' }">
                        <td><a href = "${pageContext.request.contextPath}/transferBtoB/${userBills.get(loop.index).getId()}">Transfer money</a></td>
                    </c:if>

                    <c:if test = "${userBills.get(loop.index).getStatus() ne 'CLOSED' }">
                        <td><a href = "${pageContext.request.contextPath}/closeBill/${userBills.get(loop.index).getId()}">Close bill</a></td>
                    </c:if>
                    <td><a href="${pageContext.request.contextPath}/editBill/${userBills.get(loop.index).getId()}">EditStatus</a></td>
                    </tr>
                 </c:forEach>



            </tbody>
    </table>

    <input value="Create New Bill" type="button" onclick="location.href='${pageContext.request.contextPath}/createBill'" />
    </body>
</html>