<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%--@elvariable id="mapModel" type="cz.upce.diplomovaprace.model.MapModel"--%>
<%--@elvariable id="mapModels" type="java.util.List<cz.upce.diplomovaprace.model.MapModel>"--%>

<jsp:include page="../common/map/header.jsp"/>

<script>
    let challenges = [
        <c:forEach items="${mapModels}" var="mapModel" varStatus="status">
        [
            '${mapModel.latCoords}', // latCoords - [i][0]
            '${mapModel.lngCoords}', // lngCoords - [i][1]
            '<b><spring:message code="map.challenge.game"/></b> <spring:message code="global.game.${mapModel.gameName}"/>', // gameName - [i][2]
            '<b><spring:message code="map.challenge.challengerName"/></b>  ${mapModel.hostName}', // challengerName - [i][3]
            '<b><spring:message code="map.challenge.rating"/></b> ${mapModel.rating}', // rating - [i][4]
            '<b><spring:message code="map.challenge.start"/></b> ${mapModel.start}', // start - [i][5]
            '<b><spring:message code="map.challenge.end"/></b> ${mapModel.end}', // end - [i][6]
            '<b><spring:message code="map.challenge.players"/></b> ${mapModel.numberOfPlayers} / ${mapModel.maxPlayers}', // numberOfPlayers - [i][7]
            '<b><spring:message code="map.challenge.challengeDetail"/></b>', // linkToDetail - [i][8]
            '${mapModel.challengeId}', // challengeId - [i][9]
            '${mapModel.gameName}', // gameName - [i][10]
        ]
        <c:if test="${!status.last}">
        ,
        </c:if>
        </c:forEach>
    ];
    $(document).ready(function () {
        // Javascript method's body can be found in assets/js/demos.js
        demo.initGoogleMaps();
    });
</script>
<%@ include file="../common/map/footer.jsp" %>