<?xml version="1.0"?>
<!--
	Convert search results, or just tidbit results, into JSON data.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:t="http://msqr.us/xsd/TidBits" xmlns:x="http://msqr.us/xsd/jaxb-web"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="t x date">

	<xsl:import href="../tmpl/json.xsl"/>

	<xsl:output method="text" media-type="application/json"/>

	<xsl:template match="x:x-data">
		<xsl:text>{&#xa;</xsl:text>
		<xsl:apply-templates select="x:x-model[1]/t:model[1]/t:search-results"/>
		<xsl:text>"tidbits": [</xsl:text>
		<xsl:apply-templates select="//t:tidbit"/>
		<xsl:text>&#xa;]</xsl:text>
		<xsl:text>&#xa;}</xsl:text>
	</xsl:template>

	<xsl:template match="t:search-results">
		<xsl:text>"totalResults": </xsl:text>
		<xsl:value-of select="@total-results"/>
		<xsl:text>,&#xa;"returnedResults": </xsl:text>
		<xsl:value-of select="@returned-results"/>
		<xsl:text>,&#xa;"isPartialResult": </xsl:text>
		<xsl:value-of select="@is-partial-result"/>
		<xsl:text>,&#xa;"pageStart": </xsl:text>
		<xsl:choose>
			<xsl:when test="t:pagination">
				<xsl:value-of select="t:pagination/@page-offset"/>
				<xsl:text>,&#xa;"pageSize": </xsl:text>
				<xsl:value-of select="t:pagination/@page-size"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>0,&#xa;"pageSize": 0</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>,&#xa;</xsl:text>
	</xsl:template>

	<xsl:template match="t:tidbit">
		<xsl:if test="position() &gt; 1">
			<xsl:text>,</xsl:text>
		</xsl:if>
		<xsl:text>&#xa;{</xsl:text>
		<xsl:text>"id":</xsl:text>
		
		<!--  The ID attribute might be null, in the case of importing tidbits -->
		<xsl:choose>
			<xsl:when test="@tidbit-id">
					<xsl:value-of select="@tidbit-id"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>null</xsl:text>
			</xsl:otherwise>
		</xsl:choose>

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
		<xsl:if test="string-length(t:kind/@kind-id) &gt; 0">
			<xsl:text>, "kindId":</xsl:text>
			<xsl:value-of select="t:kind/@kind-id"/>
		</xsl:if>

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
