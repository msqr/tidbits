<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:t="http://msqr.us/xsd/TidBits"
	xmlns:x="http://msqr.us/xsd/jaxb-web"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="t x date">
	
	<xsl:import href="global.xsl"/>

	<xsl:variable name="edit-tidbit" select="x:x-data/x:x-model/t:model/t:tidbit[1]"/>
	
	<xsl:template name="tidbit.form">
		<xsl:param name="include.title" select="'true'"/>
		<xsl:param name="new" select="'false'"/>
		<form method="post" class="simple-form">
			<xsl:attribute name="id">
				<xsl:choose>
					<xsl:when test="$new = 'true'">
						<xsl:text>new-tidbit-form</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>edit-tidbit-form</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="action">
				<xsl:value-of select="$web-context"/>
				<xsl:choose>
					<xsl:when test="$new = 'true'">
						<xsl:text>/newTidbit.do</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>/saveTidbit.do</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="$include.title = 'true'">
				<h3>
					<xsl:choose>
						<xsl:when test="$new = 'true'">
							<xsl:value-of select="key('i18n','new.tidbit.title')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="key('i18n','edit.tidbit.title')"/>
						</xsl:otherwise>
					</xsl:choose>
				</h3>
			</xsl:if>
			<div>
				<label>
					<xsl:attribute name="for">
						<xsl:choose>
							<xsl:when test="$new = 'true'">
								<xsl:text>new-tidbit-kind</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>edit-tidbit-kind</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>					
					<xsl:value-of select="key('i18n','tidbit.kind.displayName')"/>
				</label>
				<div>
					<select name="tidbit.kind.kindId">
						<xsl:attribute name="id">
							<xsl:choose>
								<xsl:when test="$new = 'true'">
									<xsl:text>new-tidbit-kind</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>edit-tidbit-kind</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:text> </xsl:text>
						<xsl:if test="//t:model/t:kind">
							<xsl:for-each select="//t:model/t:kind">
								<option value="{@kind-id}">
									<xsl:if test="@kind-id = $edit-tidbit/t:kind/@kind-id">
										<xsl:attribute name="selected">
											<xsl:text>selected</xsl:text>
										</xsl:attribute>
									</xsl:if>
									<xsl:value-of select="@name"/>
								</option>
							</xsl:for-each>
						</xsl:if>
					</select>
				</div>
			</div>
			<div>
				<label>
					<xsl:attribute name="for">
						<xsl:choose>
							<xsl:when test="$new = 'true'">
								<xsl:text>new-tidbit-name</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>edit-tidbit-name</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:value-of select="key('i18n','tidbit.name.displayName')"/>
				</label>
				<div>
					<input type="text" name="tidbit.name" value="{$edit-tidbit/@name}">
						<xsl:attribute name="id">
							<xsl:choose>
								<xsl:when test="$new = 'true'">
									<xsl:text>new-tidbit-name</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>edit-tidbit-name</xsl:text>
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
								<xsl:text>new-tidbit-data</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>edit-tidbit-data</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:value-of select="key('i18n','tidbit.data.displayName')"/>
				</label>
				<div>
					<textarea name="tidbit.data" class="large">
						<xsl:attribute name="id">
							<xsl:choose>
								<xsl:when test="$new = 'true'">
									<xsl:text>new-tidbit-data</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>edit-tidbit-data</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:choose>
							<xsl:when test="string($edit-tidbit/t:data)">
								<xsl:value-of select="$edit-tidbit/t:data"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text> </xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</textarea>
				</div>
			</div>
			<div>
				<label>
					<xsl:attribute name="for">
						<xsl:choose>
							<xsl:when test="$new = 'true'">
								<xsl:text>new-tidbit-comment</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>edit-tidbit-comment</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:value-of select="key('i18n','tidbit.comment.displayName')"/>
				</label>
				<div>
					<textarea name="tidbit.comment" class="large">
						<xsl:attribute name="id">
							<xsl:choose>
								<xsl:when test="$new = 'true'">
									<xsl:text>new-tidbit-comment</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>edit-tidbit-comment</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:choose>
							<xsl:when test="string($edit-tidbit/t:comment)">
								<xsl:value-of select="$edit-tidbit/t:comment"/>
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
						<input type="hidden" id="edit-tidbit-id" name="tidbit.tidbitId" 
							value="{$edit-tidbit/@tidbit-id}" />
						<input type="hidden" id="edit-tidbit-delete" name="delete" value="false" />
						<input value="{key('i18n','save.displayName')}" id="edit-tidbit-submit" type="submit" />
						<xsl:if test="$handheld != 'true'">
							<input value="{key('i18n','delete.displayName')}" id="edit-tidbit-submit-delete" type="submit" />
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<div><xsl:comment>This is here to "clear" the floats.</xsl:comment></div>
		</form>		
	</xsl:template>

</xsl:stylesheet>
	