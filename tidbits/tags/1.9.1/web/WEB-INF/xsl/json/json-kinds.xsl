<?xml version="1.0"?>
<!--
	Convert search results into JSON data.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:t="http://msqr.us/xsd/TidBits" xmlns:x="http://msqr.us/xsd/jaxb-web"
	exclude-result-prefixes="t x">

	<xsl:import href="../tmpl/json.xsl"/>

	<xsl:output method="text" media-type="application/json"/>

	<xsl:template match="x:x-data">
		<xsl:text>[</xsl:text>
		<xsl:apply-templates select="x:x-model[1]/t:model[1]/t:kind"/>
		<xsl:text>&#xa;]</xsl:text>
	</xsl:template>

	<xsl:template match="t:kind">
		<xsl:if test="position() &gt; 1">
			<xsl:text>,</xsl:text>
		</xsl:if>
		<xsl:text>&#xa;{</xsl:text>
		<xsl:text>"id": </xsl:text>
		<xsl:value-of select="@kind-id"/>
		<xsl:text>, "name": </xsl:text>
		<xsl:call-template name="json-string">
			<xsl:with-param name="str" select="@name"/>
		</xsl:call-template>
		<xsl:if test="string-length(t:comment) &gt; 0">
			<xsl:text>, "comment":</xsl:text>
			<xsl:call-template name="json-string">
				<xsl:with-param name="str" select="t:comment"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:text>}</xsl:text>
	</xsl:template>

</xsl:stylesheet>
