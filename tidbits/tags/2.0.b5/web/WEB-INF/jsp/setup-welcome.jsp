<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
	<head>
		<title><fmt:message key="title"/></title>
		<%@ include file="setup-head.jspf" %>
	</head>
	<body>
		<form method="post" class="hero-unit">
			<input type="hidden" name="page" value="welcome" />
			<h1><fmt:message key="title"/></h1>
			<p><fmt:message key="setup.welcome.intro"/></p>
			<div>
				<button type="submit" name="_to" class="btn btn-primary btn-large" value="db">
					<fmt:message key="continue.displayName"/>
				</button>
			</div>
		</form>
	</body>
</html>
