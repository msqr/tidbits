<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:t="http://msqr.us/xsd/TidBits"
    xmlns:x="http://msqr.us/xsd/jaxb-web"
    version="1.0">
    
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
            <xsl:when test="contains($user-agent, 'iPhone')">
                <xsl:text>true</xsl:text>
            </xsl:when>
            <xsl:when test="contains(translate($user-agent, 'MOBILE', 'mobile'), 'mobile')">
                <xsl:text>true</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>false</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
	
	<xsl:variable name="search-input-support">
		<xsl:choose>
			<xsl:when test="contains(translate($user-agent, 'WEBKIT', 'webkit'), 'webkit')">
				<xsl:text>true</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>false</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
        
</xsl:stylesheet>