<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/global-tidbits.css"/>" />
		<title><fmt:message key="logon.title"/></title>
		<script type="text/javascript">
			window.onload = function() {
				document.getElementById('j_username').focus();
			};
		</script>
		<meta name="viewport" content="width=320"/>
	</head>
	<body>
		<c:choose>
			<c:when test="${not empty param.login_error}">
				<p class="error">
					<fmt:message key="logon.error"/>
				</p>
			</c:when>
			<c:otherwise>
				<p>
					<fmt:message key="logon.intro"/>
				</p>
			</c:otherwise>
		</c:choose>
		<form id="loginForm" method="post" action="<c:url value='/j_spring_security_check'/>">
			<table class="form">
				<tr>
					<th><label for="j_username"><fmt:message key="logon.username.displayName"/></label></th>
					<td>
						<input type="text" name="j_username" id="j_username"/><br />
					</td>
				</tr>
				<tr>
					<th><label for="j_password"><fmt:message key="logon.password.displayName"/></label></th>
					<td>
						<input type="password" name="j_password" id="j_password"/><br />
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<input type="submit" value="<fmt:message key="logon.displayName"/>"/>
					</td>
				</tr>
			</table>
		</form> 
    	</body>
</html> 
