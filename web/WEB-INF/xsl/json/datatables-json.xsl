<?xml version="1.0"?>
<!--
	Convert search results into jQuery Data Tables-compatible JSON data.
	
	TODO: This does not successfully escape all characters required for valid JSON.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:t="http://msqr.us/xsd/TidBits" xmlns:x="http://msqr.us/xsd/jaxb-web"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="t x date">

	<xsl:import href="../tmpl/global-variables.xsl"/>
	<xsl:import href="../tmpl/json.xsl"/>

	<xsl:output method="text" media-type="application/json"/>

	<xsl:template match="x:x-data">
		<xsl:text>{&#xa;</xsl:text>
		<xsl:apply-templates select="x:x-model[1]/t:model[1]/t:search-results"/>
		<xsl:text>&#xa;}</xsl:text>
	</xsl:template>

	<xsl:template match="t:search-results">
		<xsl:text>iTotalRecords: </xsl:text>
		<xsl:value-of select="@total-results"/>
		<xsl:text>,&#xa;iTotalDisplayRecords: </xsl:text>
		<xsl:value-of select="@returned-results"/>
		<xsl:text>,&#xa;aaData: [</xsl:text>
		<xsl:apply-templates select="t:tidbit"/>
		<xsl:text>&#xa;]</xsl:text>
	</xsl:template>

	<xsl:template match="t:tidbit">
		<xsl:if test="position() &gt; 1">
			<xsl:text>,</xsl:text>
		</xsl:if>
		<xsl:text>&#xa;[</xsl:text>

		<!-- result num -->
		<xsl:variable name="page.offset"
			select="/x:x-data/x:x-model[1]/t:model[1]/t:search-results/t:pagination/@page-offset"/>
		<xsl:choose>
			<xsl:when test="$page.offset &gt; 0">
				<xsl:value-of
					select="position() + ($page.offset * /x:x-data/x:x-model[1]/t:model[1]/t:search-results/t:pagination/@page-size)"
				/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="position()"/>
			</xsl:otherwise>
		</xsl:choose>

		<!-- edit button -->
		<xsl:if test="$handheld != 'true'">
			<xsl:text>,</xsl:text>
			<xsl:value-of select="@tidbit-id"/>
			<!--td class="edit-button">
				<div class="edit-btn" id="tidbit-{@tidbit-id}-edit" style="display: none;">
					<span class="alt-hide"><xsl:value-of select="key('i18n','edit')"/></span>
				</div>				
			</td-->
		</xsl:if>

		<!-- name -->
		<xsl:text>,</xsl:text>
		<xsl:call-template name="json-string">
			<xsl:with-param name="str" select="@name"/>
		</xsl:call-template>

		<!-- kind -->
		<xsl:text>,</xsl:text>
		<xsl:call-template name="json-string">
			<xsl:with-param name="str" select="t:kind/@name"/>
		</xsl:call-template>

		<!-- value -->
		<xsl:text>,</xsl:text>
		<xsl:call-template name="json-string">
			<xsl:with-param name="str" select="t:data"/>
		</xsl:call-template>

		<!-- comment -->
		<xsl:text>,</xsl:text>
		<xsl:call-template name="json-string">
			<xsl:with-param name="str" select="t:comment"/>
		</xsl:call-template>

		<!-- creation / mod dates -->
		<xsl:if test="$handheld != 'true'">
			<xsl:text>,"</xsl:text>
			<xsl:value-of
				select="date:format-date(string(@creation-date),'d MMM yyyy')"/>
			<xsl:if test="@created-by">
				<xsl:text> </xsl:text>
				<xsl:value-of select="key('i18n','by')"/>
				<xsl:text> </xsl:text>
				<xsl:value-of select="@created-by"/>
			</xsl:if>
			<xsl:text>","</xsl:text>
			<xsl:if test="@modify-date">
				<xsl:value-of
					select="date:format-date(string(@modify-date),'d MMM yyyy')"
				/>
			</xsl:if>
			<xsl:text>"</xsl:text>
		</xsl:if>

		<xsl:text>]</xsl:text>
	</xsl:template>

</xsl:stylesheet>
