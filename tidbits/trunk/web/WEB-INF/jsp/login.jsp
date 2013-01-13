<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/bootstrap.css"/>" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/bootstrap-responsive.css"/>" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/tidbits.css"/>" />
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
		<form id="loginForm" method="post" action="<c:url value='/j_spring_security_check'/>" class="form-horizontal">
			<fieldset>
				<div class="control-group">
					<label for="j_username" class="control-label"><fmt:message key="logon.username.displayName"/></label>
					<div class="controls">
						<input type="text" name="j_username" id="j_username" autocapitalize="off" autocorrect="off" autocomplete="off"/>
					</div>
				</div>
				<div class="control-group">
					<label for="j_password" class="control-label"><fmt:message key="logon.password.displayName"/></label>
					<div class="controls">
						<input type="password" name="j_password" id="j_password" />
					</div>
				</div>
				<div class="form-actions">
					<button type="submit" class="btn btn-primary"><fmt:message key="logon.displayName"/></button>
				</div>
			</fieldset>
		</form> 
    </body>
</html> 
