<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
	<head>
		<title><fmt:message key="setup.complete.title"/></title>
		<%@ include file="setup-head.jspf" %>
	</head>
	<body>
		<h1><fmt:message key="setup.complete.title"/></h1>
		<p><fmt:message key="setup.complete.intro"/></p>
	</body>
</html>
