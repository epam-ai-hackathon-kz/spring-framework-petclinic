<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="petList">
    <h2 id="pets">Pets</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert">
            <strong>Error:</strong>
            <c:out value="${error}"/>
        </div>
    </c:if>

    <c:forEach var="pet" items="${selections}">
        <c:out value="${pet.name} "/>
    </c:forEach>

</petclinic:layout>