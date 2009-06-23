<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:t="http://msqr.us/xsd/TidBits"
	xmlns:x="http://msqr.us/xsd/jaxb-web"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="t x date">
	
	<xsl:import href="global.xsl"/>

	<xsl:template name="search.form">
		<xsl:param name="include.title" select="'true'"/>
		<form id="search-tidbit-form" action="{$web-context}/search.do" method="post" class="simple-form">
			<xsl:if test="$include.title = 'true'">
				<h3>
					<xsl:value-of select="key('i18n','search.tidbit.title')" disable-output-escaping="yes"/>
				</h3>
			</xsl:if>
			<div>
				<label for="search-tidbit-query">
					<xsl:value-of select="key('i18n','search.tidbit.query.displayName')"/>
				</label>
				<div>
					<input type="text" name="query" id="search-tidbit-query" value=""/>
					<xsl:if test="$handheld != 'true'">
						<div class="caption" id="search-help-caption">
							<xsl:value-of select="key('i18n','search.tidbit.help')" 
								disable-output-escaping="yes"/>
						</div>
					</xsl:if>
				</div>
			</div>
			<div class="submit">
				<input type="hidden" name="page" value="0"/>
				<input value="{key('i18n','search.displayName')}" type="submit" />
			</div>
			<div><xsl:comment>This is here to "clear" the floats.</xsl:comment></div>
		</form>
	</xsl:template>

</xsl:stylesheet>
	