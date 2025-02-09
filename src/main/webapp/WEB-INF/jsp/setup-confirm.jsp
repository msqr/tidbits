<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
	<head>
		<title><fmt:message key="setup.confirm.title"/></title>
		<%@ include file="setup-head.jspf" %>
	</head>
	<body>
		<h1><fmt:message key="setup.confirm.title"/></h1>
		<p><fmt:message key="setup.confirm.intro"/></p>
		
		<form:form modelAttribute="setupForm" cssClass="form-horizontal">
			<input type="hidden" name="page" value="confirm" />
			<fieldset class="control-group">
				<div class="control-group">
					<label class="control-label" for="jpa-platform">
						<fmt:message key="setup.db.jpa.platform.displayName"/>
					</label>
					<div class="controls">
						<div class="uneditable-input span2">
							${jpaDrivers[setupForm.settings['jpa.platform']]}
						</div>
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" for="base-path">
						<fmt:message key="setup.fs.index.dir.displayName"/>
					</label>
					<div class="controls">
						<div class="uneditable-input span6">
							${setupForm.settings['lucene.index.base.path']}
						</div>
					</div>
				</div>
			</fieldset>
			<div class="form-actions">
				<button type="submit" name="_to" class="btn btn-primary" value="complete">
					<fmt:message key="save.displayName"/>
				</button>
				<button type="submit" name="_to" class="btn" value="filesystem">
					<fmt:message key="back.displayName"/>
				</button>
			</div>
		</form:form>
	</body>
</html>
