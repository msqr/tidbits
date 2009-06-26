<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:t="http://msqr.us/xsd/TidBits"
	xmlns:x="http://msqr.us/xsd/jaxb-web"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="t x date">
	
	<!-- imports -->
	<xsl:import href="tmpl/search-form.xsl"/>
	<xsl:import href="tmpl/default-layout.xsl"/>
	
	<xsl:template match="x:x-data" mode="page-main-nav">
		<xsl:call-template name="main-nav">
			<xsl:with-param name="page" select="'search'"/>
		</xsl:call-template>
	</xsl:template>	
	
	<xsl:template match="x:x-data" mode="page-title">
		<xsl:value-of select="key('i18n','search.tidbit.title')"/>
	</xsl:template>

	<xsl:template match="x:x-data" mode="page-body">
		<div id="content-pane">
			<xsl:if test="$err">
				<xsl:apply-templates select="x:x-errors" mode="error-intro"/>
			</xsl:if>
			<xsl:call-template name="search.form">
				<xsl:with-param name="include.title" select="'false'"/>
			</xsl:call-template>
		</div>
	</xsl:template>
	
</xsl:stylesheet>
