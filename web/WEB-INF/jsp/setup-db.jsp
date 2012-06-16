<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
	<head>
		<title><fmt:message key="setup.db.title"/></title>
		<%@ include file="setup-head.jspf" %>
	</head>
	<body>
		<h1><fmt:message key="setup.db.title"/></h1>
		<p><fmt:message key="setup.db.intro"/></p>
    
		<form:form method="post" modelAttribute="setupForm" cssClass="form-horizontal">
			<input type="hidden" name="page" value="db" />
			<fieldset class="control-group">
				<div class="control-group">
					<label class="control-label" for="jpa-platform">
						<fmt:message key="setup.db.hibernate.dialect.displayName"/>
					</label>
					<div class="controls">
						<form:select path="settings['jpa.platform']" id="jpa-platform">
							<form:options items="${jpaDrivers}"/>
						</form:select>
						<p class="help-block">
							<fmt:message key="setup.db.hibernate.dialect.caption"/>
						</p>
					</div>
				</div>
			</fieldset>
			<div class="form-actions">
				<button type="submit" name="_to" class="btn btn-primary" value="filesystem">
					<fmt:message key="continue.displayName"/>
				</button>
				<button type="submit" name="_to" class="btn" value="welcome">
					<fmt:message key="back.displayName"/>
				</button>
			</div>
		</form:form>

	</body>
</html>
