<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:t="http://msqr.us/xsd/TidBits"
	xmlns:x="http://msqr.us/xsd/jaxb-web"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="t x date">
	
	<!-- imports -->
	<xsl:import href="tmpl/default-layout.xsl"/>
	
	<xsl:template match="x:x-data" mode="page-title">
		<xsl:value-of select="key('i18n','edit.kinds.title')"/>
	</xsl:template>
	
	<xsl:template match="x:x-data" mode="page-head-content">
		<xsl:choose>
			<xsl:when test="not(contains($user-agent, 'MSIE'))">
				<script type="text/javascript" src="{$web-context}/js/tidbits-behaviours.js">
					<xsl:text> </xsl:text>
				</script>
			</xsl:when>
			<xsl:otherwise>
				<xsl:comment>
					Not all features available when using Internet Explorer!
					<xsl:value-of select="$user-agent"/>
				</xsl:comment>
				<script type="text/javascript" src="{$web-context}/js/tidbits-behaviours-ie.js">
					<xsl:text> </xsl:text>
				</script>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
		
	<xsl:template match="x:x-data" mode="page-main-nav">
		<xsl:call-template name="main-nav">
			<xsl:with-param name="page" select="'kinds'"/>
		</xsl:call-template>
	</xsl:template>	
	
	<xsl:template match="x:x-data" mode="page-body">
		<div id="content-pane">
			<xsl:choose>
				<xsl:when test="x:x-model[1]/t:model[1]/t:kind[1]">
					<table class="tidbits">
						<tr class="header">
							<th><xsl:text> </xsl:text></th>
							<xsl:if test="$handheld != 'true'">
								<th><xsl:text> </xsl:text></th>
							</xsl:if>
							<th>
								<xsl:value-of select="key('i18n','tidbitkind.name.displayName')"/>
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
						<xsl:apply-templates select="x:x-model[1]/t:model[1]/t:kind"/>
					</table>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="key('i18n','no.kinds.available')"/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
		<xsl:if test="$handheld != 'true'">
			<div id="ui-elements" class="ui-elements">
				<xsl:apply-templates select="." mode="ui.elements"/>
			</div>
		</xsl:if>	
	</xsl:template>
	
	<xsl:template match="x:x-data" mode="ui.elements">
		<form id="new-tidbitkind-form" action="{$web-context}/newTidbitKind.do" method="post" 
			class="simple-form">
			<h3>
				<xsl:value-of select="key('i18n','new.tidbitkind.title')"/>
			</h3>
			<div>
				<label for="new-tidbitkind-name">
					<xsl:value-of select="key('i18n','tidbitkind.name.displayName')"/>
				</label>
				<div>
					<input type="text" name="kind.name" id="new-tidbitkind-name"/>
				</div>
			</div>
			<div>
				<label for="new-tidbitkind-comment">
					<xsl:value-of select="key('i18n','tidbitkind.comment.displayName')"/>
				</label>
				<div>
					<textarea name="kind.comment" id="new-tidbitkind-comment">
						<xsl:text> </xsl:text>
					</textarea>
					<div class="caption"><xsl:value-of 
						select="key('i18n','optional.caption')"/></div>
				</div>
			</div>
			<div class="submit">
				<input value="{key('i18n','add.displayName')}" type="submit" />
			</div>
			<div><xsl:comment>This is here to "clear" the floats.</xsl:comment></div>
		</form>
		<form id="edit-kind-form" action="{$web-context}/saveTidbitKind.do" method="post" 
			class="simple-form">
			<h3>
				<xsl:value-of select="key('i18n','edit.tidbitkind.title')"/>
			</h3>
			<div>
				<label for="edit-kind-name">
					<xsl:value-of select="key('i18n','tidbitkind.name.displayName')"/>
				</label>
				<div>
					<input type="text" name="kind.name" id="edit-kind-name" value=""/>
				</div>
			</div>
			<div>
				<label for="edit-kind-comment">
					<xsl:value-of select="key('i18n','tidbitkind.comment.displayName')"/>
				</label>
				<div>
					<textarea name="kind.comment" id="edit-kind-comment" class="large">
						<xsl:text> </xsl:text>
					</textarea>
					<div class="caption"><xsl:value-of 
						select="key('i18n','optional.caption')"/></div>
				</div>
			</div>
			<xsl:if test="x:x-model[1]/t:model[1]/t:kind">
				<div>
					<label for="edit-kind-reassign">
						<xsl:value-of select="key('i18n','tidbitkind.reassign.displayName')"/>
					</label>
					<div>
						<select name="reassign" id="edit-kind-reassign">
							<xsl:apply-templates select="x:x-model[1]/t:model[1]/t:kind" mode="options"/>
						</select>
						<div class="caption"><xsl:value-of 
							select="key('i18n','reassign.caption')"/></div>
					</div>
				</div>
			</xsl:if>
			<div class="submit">
				<input type="hidden" id="edit-kind-id" name="kind.kindId" value="" />
				<input type="hidden" id="edit-kind-delete" name="delete" value="false" />
				<input value="{key('i18n','save.displayName')}" id="edit-kind-submit" type="submit" />
				<input value="{key('i18n','delete.displayName')}" id="edit-kind-submit-delete" type="submit" />
			</div>
			<div><xsl:comment>This is here to "clear" the floats.</xsl:comment></div>
		</form>		
	</xsl:template>
	
	<xsl:template match="t:kind">
		<tr>
			<xsl:attribute name="class">
				<xsl:text>tidbit-data-row</xsl:text>
				<xsl:if test="(position() mod 2) = 0">
					<xsl:text> even</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<th class="start">
				<xsl:variable name="page.offset" 
					select="/x:x-data/x:x-model[1]/t:model[1]/t:search-results/t:pagination/@page-offset"/>
				<xsl:choose>
					<xsl:when test="$page.offset &gt; 0">
						<xsl:value-of select="position() + ($page.offset * /x:x-data/x:x-model[1]/t:model[1]/t:search-results/t:pagination/@page-size)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="position()"/>
					</xsl:otherwise>
				</xsl:choose>
			</th>
			<xsl:if test="$handheld != 'true'">
				<td class="edit-button">
					<div class="edit-kind-btn" id="kind-{@kind-id}-edit" style="display: none;">
						<span class="alt-hide"><xsl:value-of select="key('i18n','edit')"/></span>
					</div>
				</td>
			</xsl:if>
			<td>
				<xsl:choose>
					<xsl:when test="$handheld = 'true'">
						<a title="{key('i18n','edit.tidbitkind.title')}">
							<xsl:attribute name="href">
								<xsl:value-of select="$web-context"/>
								<xsl:text>/saveTidbitKind.do?kind.kindId=</xsl:text>
								<xsl:value-of select="@kind-id"/>
							</xsl:attribute>
							<xsl:value-of select="@name"/>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@name"/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<td>
				<xsl:value-of select="t:comment" disable-output-escaping="no" xml:space="preserve"/>
			</td>
			<td>
				<xsl:value-of select="date:format-date(string(@creation-date),'d MMM yyyy')"/>
				<xsl:if test="@created-by">
					<xsl:text> </xsl:text>
					<xsl:value-of select="key('i18n','by')"/>
					<xsl:text> </xsl:text>
					<xsl:value-of select="@created-by"/>
				</xsl:if>
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
	
	<xsl:template name="render.page.option">
		<xsl:param name="page" select="0"/>
		<xsl:param name="total-pages" select="ceiling(/x:x-data/x:x-model[1]/t:model[1]/t:search-results/@total-results 
			div /x:x-data/x:x-model[1]/t:model[1]/t:search-results/t:pagination/@page-size)"/>
		<xsl:if test="$page &lt; $total-pages">
			<option value="{$page}">
				<xsl:if test="$page = /x:x-data/x:x-model[1]/t:model[1]/t:search-results/t:pagination/@page-offset">
					<xsl:attribute name="selected">selected</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="$page + 1"/>
			</option>
			<xsl:call-template name="render.page.option">
				<xsl:with-param name="total-pages" select="$total-pages"/>
				<xsl:with-param name="page" select="$page + 1"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="t:kind" mode="options">
		<option value="{@kind-id}">
			<xsl:value-of select="@name"/>
		</option>
	</xsl:template>
	
</xsl:stylesheet>
