<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:t="http://msqr.us/xsd/TidBits"
	xmlns:x="http://msqr.us/xsd/jaxb-web"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="t x date">
	
	<!-- imports -->
	<xsl:import href="tmpl/default-layout.xsl"/>
	
	<xsl:template match="x:x-data" mode="page-head-content">
		<script type="text/javascript" src="{$web-context}/js/tidbits-behaviours.js">
			<xsl:text> </xsl:text>
		</script>
	</xsl:template>
	
	<xsl:template match="x:x-data" mode="page-main-nav">
		<xsl:call-template name="main-nav">
			<xsl:with-param name="page" select="'import'"/>
		</xsl:call-template>
	</xsl:template>	
	
	<xsl:template match="x:x-data" mode="page-body">
		<div id="content-pane">
			<p>
				<xsl:value-of select="key('i18n','import.verify.intro')" disable-output-escaping="yes"/>
			</p>
			<form action="{$web-context}{$ctx/x:path}" method="post">
				<table class="tidbits">
					<tr class="header">
						<th><xsl:text> </xsl:text></th>
						<th>
							<xsl:value-of select="key('i18n','tidbit.kind.displayName')"/>
						</th>
						<th>
							<xsl:value-of select="key('i18n','tidbit.name.displayName')"/>
						</th>
						<th>
							<xsl:value-of select="key('i18n','tidbit.data.displayName')"/>
						</th>
						<th>
							<xsl:value-of select="key('i18n','tidbit.comment.displayName')"/>
						</th>
						<th>
							<xsl:value-of select="key('i18n','tidbit.created.displayName')"/>
						</th>
						<th>
							<xsl:value-of select="key('i18n','tidbit.modified.displayName')"/>
						</th>
					</tr>
					<xsl:apply-templates select="$aux/t:model/t:tidbit"/>
				</table>
				<div>
					<input type="submit" name="_cancel" value="{key('i18n','cancel.displayName')}" />
					<input type="submit" name="_finish" value="{key('i18n','save.displayName')}" />
				</div>
			</form>
		</div>
	</xsl:template>
	
	<xsl:template match="t:tidbit">
		<tr>
			<xsl:if test="(position() mod 2) = 0">
				<xsl:attribute name="class">even</xsl:attribute>
			</xsl:if>
			<th>
				<xsl:value-of select="position()"/>
			</th>
			<td>
				<xsl:value-of select="t:kind/@name"/>
			</td>
			<td>
				<xsl:value-of select="@name"/>
			</td>
			<td>
				<xsl:value-of select="t:data" disable-output-escaping="yes"/>
			</td>
			<td>
				<xsl:value-of select="t:comment" disable-output-escaping="yes"/>
			</td>
			<td>
				<xsl:value-of select="date:format-date(string(@creation-date),'d MMM yyyy')"/>
			</td>
			<td>
				<xsl:choose>
					<xsl:when test="@modify-date">
						<xsl:value-of select="date:format-date(string(@modify-date),'d MMM yyyy')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text> </xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>
	
</xsl:stylesheet>
