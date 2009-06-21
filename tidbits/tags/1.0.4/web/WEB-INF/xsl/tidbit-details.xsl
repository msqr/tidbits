<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:t="http://msqr.us/xsd/TidBits">
	
	<xsl:output method="xml" omit-xml-declaration="no"/>
	
	<xsl:template match="/">
		<xsl:apply-templates select="." mode="copy"/>
	</xsl:template>
	
	<xsl:template match="t:data">
		<t:data>
			<xsl:value-of select="." disable-output-escaping="no"/>
		</t:data>
	</xsl:template>
	
	<xsl:template match="@*|node()" mode="copy">
		<xsl:copy>
			<xsl:apply-templates select="@*" mode="copy"/>
			<xsl:apply-templates mode="copy"/>
		</xsl:copy>
	</xsl:template>
	
</xsl:stylesheet>
