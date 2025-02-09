<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
	<head>
		<title><fmt:message key="setup.fs.title"/></title>
		<%@ include file="setup-head.jspf" %>
	</head>
	<body>
		<h1><fmt:message key="setup.fs.title"/></h1>
		<p><fmt:message key="setup.fs.intro"/></p>

		<spring:hasBindErrors name="setupForm">
			<div class="alert alert-error">
				<spring:bind path="setupForm.settings">
					<c:choose>
						<c:when test="${status.errors.errorCount > 1}">
							<ul>
								<c:forEach items="${status.errors.allErrors}" var="error">
									<li>
										<spring:message code="${error.code}" arguments="${error.arguments}"/><br />
									</li>
								</c:forEach>
							</ul>
						</c:when>
						<c:when test="${status.errors.errorCount == 1}">
							<c:set var="error" value="${status.errors.allErrors[0]}"/>
							<spring:message code="${error.code}" arguments="${error.arguments}"/><br />
						</c:when>
					</c:choose>
				</spring:bind>
			</div>
		</spring:hasBindErrors>

		<form:form method="post" modelAttribute="setupForm" cssClass="form-horizontal">
			<input type="hidden" name="page" value="filesystem" />
			<fieldset class="control-group">
				<div class="control-group">
					<label class="control-label" for="base-path">
						<fmt:message key="setup.fs.index.dir.displayName"/>
					</label>
					<div class="controls">
						<form:input path="settings['lucene.index.base.path']" cssClass="span6"/>
						<p class="help-block">
							<fmt:message key="setup.fs.index.dir.caption"/>
						</p>
					</div>
				</div>
			</fieldset>

			<div class="form-actions">
				<button type="submit" name="_to" class="btn btn-primary" value="confirm">
					<fmt:message key="continue.displayName"/>
				</button>
				<button type="submit" name="_to" class="btn" value="db">
					<fmt:message key="back.displayName"/>
				</button>
			</div>

		</form:form>

	</body>
</html>
