<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:t="http://msqr.us/xsd/TidBits"
	xmlns:x="http://msqr.us/xsd/jaxb-web"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="t x date">
	
	<!-- imports -->
	<xsl:import href="tmpl/tidbit-form.xsl"/>
	<xsl:import href="tmpl/default-layout.xsl"/>
	
	<xsl:template match="x:x-data" mode="page-main-nav">
		<xsl:call-template name="main-nav">
			<xsl:with-param name="page">
				<xsl:choose>
					<xsl:when test="string($edit-tidbit/@tidbit-id)">
						<xsl:text>edit-tidbit</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>add-tidbit</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>	
	
	<xsl:template match="x:x-data" mode="page-body">
		<div id="content-pane">
			<xsl:if test="$err">
				<xsl:apply-templates select="x:x-errors" mode="error-intro"/>
			</xsl:if>
			<xsl:call-template name="tidbit.form">
				<xsl:with-param name="include.title" select="'false'"/>
				<xsl:with-param name="new">
					<xsl:choose>
						<xsl:when test="string($edit-tidbit/@tidbit-id)">
							<xsl:text>false</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>true</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
		</div>
	</xsl:template>
	
</xsl:stylesheet>
