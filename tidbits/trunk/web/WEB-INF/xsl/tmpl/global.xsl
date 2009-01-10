<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ma="http://msqr.us/xsd/MediaAlbum"
	xmlns:t="http://msqr.us/xsd/TidBits"
	xmlns:x="http://msqr.us/xsd/jaxb-web"
	exclude-result-prefixes="x">
	
	<xsl:import href="util.xsl"/>
	
	<!-- standard data vars -->
	<xsl:variable name="ctx" select="x:x-data/x:x-context"/>
	<xsl:variable name="req" select="x:x-data/x:x-request/x:param"/>
	<xsl:variable name="aux" select="x:x-data/x:x-auxillary"/>
	<xsl:variable name="err" select="x:x-data/x:x-errors/x:error[not(@field)]"/>
	
	<!-- message resource bundle defined as key for quick lookup -->
	<xsl:key name="i18n" match="x:x-data/x:x-msg/x:msg" use="@key"/>
	
	<!-- helper vars -->
	<xsl:variable name="web-context" select="string($ctx/x:web-context)"/>
	<xsl:variable name="user-agent" select="string($ctx/x:user-agent)"/>
	<xsl:variable name="acting-user" select="x:x-data/x:x-session/t:session/t:acting-user"/>
	
	<!-- set to 'true' for hand held devices -->
	<xsl:variable name="handheld">
		<xsl:choose>
			<xsl:when test="contains($user-agent,'Blazer')">
				<xsl:text>true</xsl:text>
			</xsl:when>
			<xsl:when test="contains($user-agent, 'Windows CE')">
				<xsl:text>true</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>false</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
		
	<xsl:template name="error-intro">
		<xsl:param name="errors-node"/>
		<xsl:if test="$errors-node/x:error">
			<div class="error-intro">
				<xsl:if test="$errors-node/x:error[not(@field)]">
					<!--<xsl:value-of select="$messages[@key='global.error.intro']"/>
					<xsl:text> </xsl:text>-->
					<xsl:apply-templates select="$errors-node/x:error[not(@field)]"/>
				</xsl:if>
				<xsl:if test="$errors-node/x:error[@field]">
					<xsl:value-of select="key('i18n','field.error.intro')"/>
					<ul>
						<xsl:for-each select="$errors-node/x:error[@field]">
							<li><xsl:value-of select="."/></li>
						</xsl:for-each>
					</ul>
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
