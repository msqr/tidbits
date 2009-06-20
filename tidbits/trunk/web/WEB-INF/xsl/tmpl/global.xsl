<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ma="http://msqr.us/xsd/MediaAlbum"
	xmlns:t="http://msqr.us/xsd/TidBits"
	xmlns:x="http://msqr.us/xsd/jaxb-web"
	exclude-result-prefixes="x">
	
	<xsl:import href="global-variables.xsl"/>
	<xsl:import href="util.xsl"/>
	
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
