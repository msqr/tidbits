<?xml version="1.0" encoding="UTF-8"?>
<!--
	Generates JSON encoded i18n message bundle from Xweb messages.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:x="http://msqr.us/xsd/jaxb-web">
	
	<xsl:import href="../tmpl/json.xsl"/>

	<xsl:output method="text" media-type="application/json"/>
	
	<xsl:template match="x:x-data">
		<xsl:text>{</xsl:text>
		<xsl:apply-templates select="x:x-msg/x:msg"/>
		<xsl:text>}</xsl:text>
	</xsl:template>
	
	<xsl:template match="x:msg">
		<xsl:if test="position() &gt; 1">
			<xsl:text>,&#xA;</xsl:text>
		</xsl:if>
		<xsl:call-template name="json-string">
			<xsl:with-param name="str" select="@key"/>
		</xsl:call-template>
		<xsl:text>:</xsl:text>
		<xsl:call-template name="json-string">
			<xsl:with-param name="str" select="string(.)"/>
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>
