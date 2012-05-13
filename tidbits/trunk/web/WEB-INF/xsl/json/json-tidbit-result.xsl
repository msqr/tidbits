<?xml version="1.0"?>
<!--
	Convert search results into JSON data.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:t="http://msqr.us/xsd/TidBits" xmlns:x="http://msqr.us/xsd/jaxb-web"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="t x date">

	<xsl:import href="../tmpl/json.xsl"/>

	<xsl:output method="text" media-type="application/json"/>

	<xsl:template match="x:x-data">
		<xsl:text>{"tidbits": [&#xa;</xsl:text>
		<xsl:apply-templates select="x:x-model[1]/t:model[1]/t:tidbit"/>
		<xsl:text>&#xa;]}</xsl:text>
	</xsl:template>

	<xsl:template match="t:tidbit">
		<xsl:if test="position() &gt; 1">
			<xsl:text>,</xsl:text>
		</xsl:if>
		<xsl:text>&#xa;{</xsl:text>
		<xsl:text>"id":</xsl:text><xsl:value-of select="@tidbit-id"/>

		<!-- name -->
		<xsl:text>, "name":</xsl:text>
		<xsl:call-template name="json-string">
			<xsl:with-param name="str" select="@name"/>
		</xsl:call-template>

		<!-- kind -->
		<xsl:text>, "kind":</xsl:text>
		<xsl:call-template name="json-string">
			<xsl:with-param name="str" select="t:kind/@name"/>
		</xsl:call-template>

		<!-- value -->
		<xsl:text>, "value":</xsl:text>
		<xsl:call-template name="json-string">
			<xsl:with-param name="str" select="t:data"/>
		</xsl:call-template>

		<!-- comment -->
		<xsl:if test="string-length(t:comment) &gt; 0">
			<xsl:text>, "comment":</xsl:text>
			<xsl:call-template name="json-string">
				<xsl:with-param name="str" select="t:comment"/>
			</xsl:call-template>
		</xsl:if>
		
		<xsl:if test="@created-by">
			<xsl:text>, "createdBy":</xsl:text>
			<xsl:call-template name="json-string">
				<xsl:with-param name="str" select="@created-by"/>
			</xsl:call-template>
		</xsl:if>

		<!-- creation / mod dates -->
		<xsl:text>, "creationDate":</xsl:text>
		<xsl:call-template name="json-string">
			<xsl:with-param name="str" select="date:format-date(string(@creation-date),'d MMM yyyy')"/>
		</xsl:call-template>

		<xsl:if test="@modify-date">
			<xsl:text>, "modifyDate":</xsl:text>
			<xsl:call-template name="json-string">
				<xsl:with-param name="str" select="date:format-date(string(@modify-date),'d MMM yyyy')"/>
			</xsl:call-template>
		</xsl:if>

		<xsl:text>}</xsl:text>
	</xsl:template>

</xsl:stylesheet>
