<?xml version="1.0"?>
<!--
	JSON service OK response.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:t="http://msqr.us/xsd/TidBits" xmlns:x="http://msqr.us/xsd/jaxb-web"
	exclude-result-prefixes="t x">

	<xsl:import href="../tmpl/json.xsl"/>

	<xsl:output method="text" media-type="application/json"/>

	<xsl:template match="x:x-data">
		<xsl:text>{"success":true}</xsl:text>
	</xsl:template>

</xsl:stylesheet>
