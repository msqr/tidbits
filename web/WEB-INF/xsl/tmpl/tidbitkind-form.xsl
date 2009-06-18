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
						<xsl:text>new-kind-form</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>edit-kind-form</xsl:text>
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
								<xsl:text>new-kind-name</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>edit-kind-name</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>					
					<xsl:value-of select="key('i18n','tidbitkind.name.displayName')"/>
				</label>
				<div>
					<input type="text" name="kind.name">
						<xsl:attribute name="id">
							<xsl:choose>
								<xsl:when test="$new = 'true'">
									<xsl:text>new-kind-name</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>edit-kind-name</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="value">
							<xsl:if test="not($new = 'true')">
								<xsl:value-of select="$edit-tidbit-kind/@name"/>
							</xsl:if>
						</xsl:attribute>
					</input>
				</div>
			</div>
			<div>
				<label>
					<xsl:attribute name="for">
						<xsl:choose>
							<xsl:when test="$new = 'true'">
								<xsl:text>new-kind-comment</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>edit-kind-comment</xsl:text>
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
									<xsl:text>new-kind-comment</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>edit-kind-comment</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>					
						<xsl:choose>
							<xsl:when test="not($new = 'true') and string($edit-tidbit-kind/t:comment)">
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
			<xsl:if test="not($new = 'true')">
				<div>
					<label for="edit-kind-reassign">
						<xsl:value-of select="key('i18n','tidbitkind.reassign.displayName')"/>
					</label>
					<div>
						<select name="reassign" id="edit-kind-reassign">
							<xsl:for-each select="//t:model/t:kind">
								<option value="{@kind-id}">
									<xsl:value-of select="@name"/>
								</option>
							</xsl:for-each>
						</select>
						<div class="caption"><xsl:value-of select="key('i18n','reassign.caption')"/></div>
					</div>
				</div>
			</xsl:if>
			<div class="submit">
				<xsl:choose>
					<xsl:when test="$new = 'true'">
						<input value="{key('i18n','add.displayName')}" type="submit" />
					</xsl:when>
					<xsl:otherwise>
						<input type="hidden" id="edit-kind-id" name="kind.kindId" 
							value="{$edit-tidbit-kind/@kind-id}" />
						<input type="hidden" id="edit-kind-delete" name="delete" value="false" />
						<input value="{key('i18n','save.displayName')}" id="edit-kind-submit" type="submit" />
						<xsl:if test="$handheld != 'true'">
							<input value="{key('i18n','delete.displayName')}" id="edit-kind-submit-delete" type="submit" />
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<div><xsl:comment>This is here to "clear" the floats.</xsl:comment></div>
		</form>		
	</xsl:template>

</xsl:stylesheet>
	