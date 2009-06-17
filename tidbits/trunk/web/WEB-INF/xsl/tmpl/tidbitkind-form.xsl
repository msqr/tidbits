<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:t="http://msqr.us/xsd/TidBits"
	xmlns:x="http://msqr.us/xsd/jaxb-web"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="t x date">
	
	<xsl:import href="global.xsl"/>

	<xsl:variable name="edit-tidbit-kind" select="x:x-data/x:x-model/t:model/t:kind[1]"/>
	
	<xsl:template name="tidbitkind.form">
		<xsl:param name="include.title" select="'true'"/>
		<xsl:param name="new" select="'false'"/>
		<form method="post" class="simple-form">
			<xsl:attribute name="id">
				<xsl:choose>
					<xsl:when test="$new = 'true'">
						<xsl:text>new-tidbitkind-form</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>edit-tidbitkind-form</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="action">
				<xsl:value-of select="$web-context"/>
				<xsl:choose>
					<xsl:when test="$new = 'true'">
						<xsl:text>/newTidbitKind.do</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>/saveTidbitKind.do</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="$include.title = 'true'">
				<h3>
					<xsl:choose>
						<xsl:when test="$new = 'true'">
							<xsl:value-of select="key('i18n','new.tidbitkind.title')"/>							
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="key('i18n','edit.tidbitkind.title')"/>
						</xsl:otherwise>
					</xsl:choose>
				</h3>
			</xsl:if>

			<div>
				<label>
					<xsl:attribute name="for">
						<xsl:choose>
							<xsl:when test="$new = 'true'">
								<xsl:text>new-tidbitkind-name</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>edit-tidbitkind-name</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>					
					<xsl:value-of select="key('i18n','tidbitkind.name.displayName')"/>
				</label>
				<div>
					<input type="text" name="kind.name" value="{$edit-tidbit-kind/@name}">
						<xsl:attribute name="id">
							<xsl:choose>
								<xsl:when test="$new = 'true'">
									<xsl:text>new-tidbitkind-name</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>edit-tidbitkind-name</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>					
					</input>
				</div>
			</div>
			<div>
				<label>
					<xsl:attribute name="for">
						<xsl:choose>
							<xsl:when test="$new = 'true'">
								<xsl:text>new-tidbitkind-comment</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>edit-tidbitkind-comment</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>					
					<xsl:value-of select="key('i18n','tidbitkind.comment.displayName')"/>
				</label>
				<div>
					<textarea name="kind.comment" cols="4" rows="20">
						<xsl:attribute name="id">
							<xsl:choose>
								<xsl:when test="$new = 'true'">
									<xsl:text>new-tidbitkind-comment</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>edit-tidbitkind-comment</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>					
						<xsl:choose>
							<xsl:when test="string($edit-tidbit-kind/t:comment)">
								<xsl:value-of select="$edit-tidbit-kind/t:comment"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text> </xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</textarea>
					<div class="caption"><xsl:value-of 
						select="key('i18n','optional.caption')"/></div>
				</div>
			</div>
			<div class="submit">
				<xsl:choose>
					<xsl:when test="$new = 'true'">
						<input value="{key('i18n','add.displayName')}" type="submit" />
					</xsl:when>
					<xsl:otherwise>
						<input type="hidden" id="edit-tidbitkind-id" name="tidbit.tidbitId" 
							value="{$edit-tidbit-kind/@kind-id}" />
						<input type="hidden" id="edit-tidbitkind-delete" name="delete" value="false" />
						<input value="{key('i18n','save.displayName')}" id="edit-tidbitkind-submit" type="submit" />
						<xsl:if test="$handheld != 'true'">
							<input value="{key('i18n','delete.displayName')}" id="edit-tidbitkind-submit-delete" type="submit" />
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<div><xsl:comment>This is here to "clear" the floats.</xsl:comment></div>
		</form>		
	</xsl:template>

</xsl:stylesheet>
	