<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Vet Schedule</title>
</head>
<body>
    <h2>Vet Schedule</h2>
    <table>
        <tr>
            <th>Name</th>
            <th>Schedule</th>
        </tr>
        <c:forEach items="${vets}" var="vet">
            <tr>
                <td>${vet.firstName} ${vet.lastName}</td>
                <td>
                    <!-- Display schedule details here -->
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>