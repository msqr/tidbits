<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:t="http://msqr.us/xsd/TidBits"
	xmlns:x="http://msqr.us/xsd/jaxb-web"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="t x date xml">

	<!-- imports -->
	<xsl:import href="global-variables.xsl"/>
	
	<xsl:output method="xml" omit-xml-declaration="yes" indent="yes" media-type="text/html"/>
    
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
		<xsl:variable name="layout.body.id">
			<xsl:apply-templates select="." mode="page-body-id"/>
		</xsl:variable>
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
		<html>
			<head>
				<meta charset="utf-8" />
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
				<xsl:if test="string($layout.body.id)">
					<xsl:attribute name="id">
						<xsl:value-of select="$layout.body.id"/>
					</xsl:attribute>
				</xsl:if>
				
				<xsl:apply-templates select="." mode="page-main-nav"/>
				<xsl:apply-templates select="." mode="page-body"/>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="x:x-data" mode="page-head-basecontent">
		<link rel="stylesheet" type="text/css" href="{$web-context}/css/bootstrap.css" />
		<link rel="stylesheet" type="text/css" href="{$web-context}/css/bootstrap-responsive.css"/>
		<link rel="stylesheet" type="text/css" href="{$web-context}/css/font-awesome.css" />
		<link rel="stylesheet" type="text/css" href="{$web-context}/css/tidbits.css" />
		<!-- The following exposes a bug in iOS9:
		<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no" />
		
		so replacing with this work-around: -->
		<meta name="viewport" content="initial-scale=1.0001, minimum-scale=1.0001, maximum-scale=1.0001, user-scalable=no" />
		<meta name="apple-mobile-web-app-capable" content="yes" />
		<script type="text/javascript" src="{$web-context}/js-lib/jquery-1.8.3.js"><xsl:text> </xsl:text></script>
		<script type="text/javascript" src="{$web-context}/js-lib/jquery.form.js"><xsl:text> </xsl:text></script>
		<script type="text/javascript" src="{$web-context}/js-lib/jquery.transform2d.js"><xsl:text> </xsl:text></script>
		<script type="text/javascript" src="{$web-context}/js-lib/bootstrap.js"><xsl:text> </xsl:text></script>
		<script type="text/javascript" src="{$web-context}/js/xweb-locale.js"><xsl:text> </xsl:text></script>
		<script type="text/javascript" src="{$web-context}/js/tidbits-cards.js?lang={$ctx/x:user-locale}"><xsl:text> </xsl:text></script>
		<script type="text/javascript" src="{$web-context}/js/tidbits-editor.js"><xsl:text> </xsl:text></script>
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

	    <div id="navbar" class="navbar navbar-inverse">
	        <div class="navbar-inner">
	            <div class="container">
	
	                <a class="brand" href="{$web-context}/home.do">
	                	<xsl:value-of select="key('i18n','title')"/>
	               	</a> 
					
					<xsl:if test="$page = 'home'">
						<form id="nav-search-tidbit-form" action="search.json" class="navbar-search" method="GET">
							<input name="query" type="text" 
								placeholder="{key('i18n', 'search.displayName')}" class="search-query span4" 
								autocapitalize="off" autocomplete="off" autocorrect="off" spellcheck="false"/>
						</form>
					</xsl:if>
					
					<ul class="nav hidden-phone" id="nav-other">
						<li class="dropdown">
							<a href="#" class="dropdown-toggle" data-toggle="dropdown">
								<xsl:value-of select="key('i18n', 'go.displayName')"/>
								<b class="caret"><xmltext> </xmltext></b>
							</a>
							<ul class="dropdown-menu">
								<li>
									<a id="import-tidbits-btn" href="{$web-context}/import.do" title="{key('i18n','link.import.csv.title')}">
										<xsl:value-of select="key('i18n','link.import.csv')"/>
									</a>
								</li>
								<li>
									<a id="export-tidbits-btn" href="{$web-context}/export.do" title="{key('i18n','link.export.csv.title')}">
										<xsl:value-of select="key('i18n','link.export.csv')"/>
									</a>
								</li>
								<li>
									<a class="link-logoff" href="{$web-context}/logoff.do">
										<xsl:value-of select="key('i18n','link.logoff')"/>
									</a>
								</li>
							</ul>
						</li>
					</ul>
					
					<ul class="nav pull-right" id="nav-add">
						<li><button class="btn btn-primary" id="add-new-tidbit-btn">
							<i class="icon-plus"><xsl:text> </xsl:text></i>
						</button></li>
					</ul>
						
	            </div>
	        </div>    
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
		PAGE-BODY-ID
		
		Add a "id" attribute to the <body> tag. Default implementation 
		does not specify any value, so no ID attribute added.
	-->
	<xsl:template match="x:x-data" mode="page-body-id"/>
	
	<!--
		PAGE-BODY (empty implementation)
		
		Main page content.
	-->
	<xsl:template match="x:x-data" mode="page-body"/>
	
</xsl:stylesheet>