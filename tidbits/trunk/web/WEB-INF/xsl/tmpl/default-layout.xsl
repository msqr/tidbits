<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:t="http://msqr.us/xsd/TidBits"
	xmlns:x="http://msqr.us/xsd/jaxb-web"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="t x date">

	<!-- imports -->
	<xsl:import href="global.xsl"/>
	
	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" media-type="application/xhtml+xml"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" 
		doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/>
    
	<!-- 
		Layout Stylesheet
		
		This stylesheet is not designed to be used directly, rather it should be 
		imported or included into another stylesheet. That stylesheet must define
		the following variables:
		
		layout.global.nav.page: the current global nav page
		
		The layout of this template is as roughly as follows:
		
		+============================================================+
		| PAGE-HEAD-CONTENT, PAGE-BODY-CLASS                         |
		+============================================================+
		| PAGE-TITLE                                 PAGE-GLOBAL-NAV |
		| - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  |
		| PAGE-BODY                                                  |
		|                                                            |
		| PAGE-FOOTER                                                |
		+============================================================+
		
		Thus implementing stylesheets should define templates that match 
		the x:x-data element for the mode of the elements outlined 
		above (the modes should be lower-case). This stylesheet does 
		provide defaults for some of these elements, so to override 
		those defaults you must import this stylesheet rather than 
		include it.
	-->
	<xsl:template match="x:x-data">
		<xsl:variable name="layout.page.title">
			<xsl:apply-templates select="." mode="page-title"/>
		</xsl:variable>
		<xsl:variable name="layout.body.class">
			<xsl:apply-templates select="." mode="page-body-class"/>
		</xsl:variable>
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
				<title><xsl:value-of select="$layout.page.title"/></title>
				<xsl:apply-templates select="." mode="page-head-basecontent"/>
				<xsl:apply-templates select="." mode="page-head-content"/>
			</head>
			<body>
				<xsl:if test="string($layout.body.class)">
					<xsl:attribute name="class">
						<xsl:value-of select="$layout.body.class"/>
					</xsl:attribute>
				</xsl:if>
				
				<h1 class="title"><xsl:value-of select="$layout.page.title"/></h1>
                
				<xsl:apply-templates select="." mode="page-main-nav"/>
				
				<xsl:apply-templates select="." mode="page-body"/>
                
				<!--div id="system-working" style="display: none;">
					<xsl:value-of select="key('i18n','working.displayName')"/>
				</div-->
				<div id="top-hide" style="display: none;">
					<xsl:text> </xsl:text>
				</div>
				<div id="message-pane" style="display: none;">
					<div class="close-x">
						<span class="alt-hide"><xsl:value-of select="key('i18n','close')"/></span>
					</div>
					<div id="message-content-pane" class="message-box">
						<xsl:text> </xsl:text>
					</div>
				</div>
				<div id="dialog-pane" style="display: none;">
					<div class="close-x">
						<span class="alt-hide"><xsl:value-of select="key('i18n','close')"/></span>
					</div>
					<div id="dialog-content-pane" class="dialog-box">
						<xsl:text> </xsl:text>
					</div>
				</div>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="x:x-data" mode="page-head-basecontent">
		<link rel="stylesheet" type="text/css" href="{$web-context}/css/global-tidbits.css" media="screen,print"/>
		<xsl:choose>
			<xsl:when test="$handheld = 'true'">
				<link rel="stylesheet" type="text/css" href="{$web-context}/css/global-tidbits-mini.css" media="screen,print"/>
				<meta name="viewport" content="width=320"/>
			</xsl:when>
			<xsl:otherwise>
				<link rel="stylesheet" type="text/css" href="{$web-context}/css/datatable.css" media="screen,print"/>
				<script type="text/javascript" src="{$web-context}/js-lib/jquery-1.3.2.js" xml:space="preserve"> </script>
				<script type="text/javascript" src="{$web-context}/js-lib/jquery.center.js" xml:space="preserve"> </script>
				<script type="text/javascript" src="{$web-context}/js-lib/jquery.dataTables.js" xml:space="preserve"> </script>
				<script type="text/javascript" src="{$web-context}/js-lib/jquery.form.js" xml:space="preserve"> </script>
				<script type="text/javascript" src="{$web-context}/js/xweb-locale.js" xml:space="preserve"> </script>
				<script id="appstate-js" type="text/javascript" src="{$web-context}/js/appstate.js?context={$web-context}&amp;lang={$ctx/x:user-locale}">
					<xsl:text> </xsl:text>
				</script>
				<script type="text/javascript" src="{$web-context}/js/tidbits.js" xml:space="preserve"> </script>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="x:x-errors" mode="error-intro">
		<div class="error-intro">
			<xsl:for-each select="x:error[not(@field)]">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</div>
	</xsl:template>

	<!-- 
		PAGE-TITLE
		
		Render the browser page title as well as the main heading. This should 
		return a simple string, without any markup.
	-->
	<xsl:template match="x:x-data" mode="page-title">
		<xsl:value-of select="key('i18n','title')"/>
	</xsl:template>
	
	<!--
		PAGE-MAIN-NAV
		
		Default implementation: calls "global-nav" template with $page = 'home'.
	-->
	<xsl:template match="x:x-data" mode="page-main-nav">
		<xsl:call-template name="main-nav">
			<xsl:with-param name="page" select="'home'"/>
		</xsl:call-template>
	</xsl:template>
	
	<!--
		MAIN-NAV
		
		Global vars:
		acting-user: the logged in user, if logged in
		web-context: the web context
	-->
	<xsl:template name="main-nav">
		<xsl:param name="page"/>
		<div id="main-nav">
			<xsl:choose>
				<xsl:when test="$page = 'kinds'">
					<a class="link-home" href="{$web-context}/home.do" 
						title="{key('i18n','link.home.title')}">
						<xsl:value-of select="key('i18n','link.home')"/>
					</a>
					<xsl:text> - </xsl:text>
					<a class="link-add-kind" href="{$web-context}/newTidbitKind.do" 
						title="{key('i18n','link.add.tidbitkind.title')}">
						<xsl:value-of select="key('i18n','link.add.tidbitkind')"/>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="$page = 'add-tidbit'">
							<xsl:value-of select="key('i18n','link.add.tidbit')"/>
						</xsl:when>
						<xsl:otherwise>
							<a class="link-add-tidbit" href="{$web-context}/newTidbit.do" 
								title="{key('i18n','link.add.tidbit.title')}">
								<xsl:value-of select="key('i18n','link.add.tidbit')"/>
							</a>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:text> - </xsl:text>
					<a class="link-manage-kinds" href="{$web-context}/manageKinds.do" 
						title="{key('i18n','link.edit.kinds.title')}">
						<xsl:value-of select="key('i18n','link.edit.kinds')"/>
					</a>
					<xsl:if test="$handheld = 'false'">
						<xsl:text> - </xsl:text>
						<xsl:choose>
							<xsl:when test="$page = 'import'">
								<xsl:value-of select="key('i18n','link.import.csv')"/>
							</xsl:when>
							<xsl:otherwise>
								<a class="link-import-csv" href="{$web-context}/importCsv.do" 
									title="{key('i18n','link.import.csv.title')}">
									<xsl:value-of select="key('i18n','link.import.csv')"/>
								</a>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text> - </xsl:text>
			<a class="link-logoff" href="{$web-context}/logoff.do" 
				title="{key('i18n','link.logoff.title')}">
				<xsl:value-of select="key('i18n','link.logoff')"/>
			</a>
		</div>
	</xsl:template>
	
	<xsl:template name="render.page.option">
		<xsl:param name="page" select="0"/>
		<xsl:param name="total-pages" select="ceiling(/x:x-data/x:x-model[1]/t:model[1]/t:search-results/@total-results 
			div /x:x-data/x:x-model[1]/t:model[1]/t:search-results/t:pagination/@page-size)"/>
		<xsl:if test="$page &lt; $total-pages">
			<option value="{$page}">
				<xsl:if test="$page = /x:x-data/x:x-model[1]/t:model[1]/t:search-results/t:pagination/@page-offset">
					<xsl:attribute name="selected">selected</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="$page + 1"/>
			</option>
			<xsl:call-template name="render.page.option">
				<xsl:with-param name="total-pages" select="$total-pages"/>
				<xsl:with-param name="page" select="$page + 1"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!--
		PAGE-HEAD-CONTENT (empty implementation)
		
		Can be used to insert more links (CSS, JavaScript) into <head> section.
	-->
	<xsl:template match="x:x-data" mode="page-head-content"/>
	
	<!-- 
		PAGE-BODY-CLASS
		
		Add a "class" attribute to the <body> tag. Default implementation 
		does not specify any value, so no class attribute added.
	-->
	<xsl:template match="x:x-data" mode="page-body-class"/>
	
	<!--
		PAGE-BODY (empty implementation)
		
		Main page content.
	-->
	<xsl:template match="x:x-data" mode="page-body"/>
	
</xsl:stylesheet>