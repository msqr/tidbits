<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:t="http://msqr.us/xsd/TidBits"
	xmlns:x="http://msqr.us/xsd/jaxb-web"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="t x date">
	
	<!-- imports -->
	<xsl:import href="tmpl/tidbit-form.xsl"/>
	<xsl:import href="tmpl/tidbitkind-form.xsl"/>
	<xsl:import href="tmpl/search-form.xsl"/>
	<xsl:import href="tmpl/default-layout.xsl"/>
	
	<xsl:template match="x:x-data" mode="page-main-nav">
		<xsl:call-template name="main-nav">
			<xsl:with-param name="page" select="'home'"/>
		</xsl:call-template>
		<form id="nav-search-tidbit-form" action="{$web-context}/search.do" method="post" class="simple-form">
			<div>
				<input name="query" id="search-tidbit-query" value="">
					<xsl:choose>
						<xsl:when test="$search-input-support = 'true'">
							<xsl:attribute name="type">
								<xsl:text>search</xsl:text>
							</xsl:attribute>
							<xsl:attribute name="placeholder">
								<xsl:value-of select="key('i18n','search.placeholder')"/>
							</xsl:attribute>
							<xsl:attribute name="autosave">
								<xsl:text>true</xsl:text>
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="type">
								<xsl:text>text</xsl:text>
							</xsl:attribute>
							<xsl:attribute name="class">
								<xsl:text>search-otherbrowser</xsl:text>
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</input>
				<input type="hidden" name="page" value="0"/>
			</div>
		</form>
	</xsl:template>	
	
	<xsl:template match="x:x-data" mode="page-body">
		<div id="content-pane" class="alt_pagination">
			<xsl:if test="$err">
				<xsl:apply-templates select="x:x-errors" mode="error-intro"/>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="x:x-model[1]/t:model[1]/t:search-results/t:tidbit">
					<div id="body-content">
						<table class="tidbits" id="datatable">
							<thead>
								<tr>
									<th><xsl:text> </xsl:text></th>
									<xsl:if test="$handheld != 'true'">
										<th><xsl:text> </xsl:text></th>
									</xsl:if>	
									<th>
										<xsl:value-of select="key('i18n','tidbit.name.displayName')"/>
									</th>
									<th>
										<xsl:value-of select="key('i18n','tidbit.kind.displayName')"/>
									</th>
									<xsl:if test="$handheld != 'true'">
										<th>
											<xsl:value-of select="key('i18n','tidbit.data.displayName')"/>
										</th>
									</xsl:if>
									<th>
										<xsl:value-of select="key('i18n','tidbit.comment.displayName')"/>
									</th>
									<xsl:if test="$handheld != 'true'">
										<th>
											<xsl:value-of select="key('i18n','tidbit.created.displayName')"/>
										</th>
										<th>
											<xsl:value-of select="key('i18n','tidbit.modified.displayName')"/>
										</th>
									</xsl:if>
								</tr>
							</thead>
							<tbody>
								<xsl:if test="$handheld = 'true'">
									<xsl:apply-templates select="x:x-model[1]/t:model[1]/t:search-results/t:tidbit"/>
								</xsl:if>
							</tbody>
						</table>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="key('i18n','no.tidbits.available')"/>
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
		<xsl:call-template name="tidbit.form">
			<xsl:with-param name="new" select="'true'"/>
		</xsl:call-template>
		<xsl:call-template name="tidbit.form">
			<xsl:with-param name="new" select="'false'"/>
		</xsl:call-template>
		<form id="import-csv-form" action="{$web-context}/importCsv.do" method="post" 
			enctype="multipart/form-data" class="simple-form">
			<h3>
				<xsl:value-of select="key('i18n','import.csv.title')"/>
			</h3>
			<div style="max-width: 300px;">
				<xsl:value-of select="key('i18n','import.csv.intro')" disable-output-escaping="yes"/>
			</div>
			<div>
				<label for="import-cvs-file">
					<xsl:value-of select="key('i18n','import.cvs.file.displayName')"/>
				</label>
				<div>
					<input type="file" name="file" id="import-cvs-file"/>
				</div>
			</div>
			<div class="submit">
				<input type="hidden" name="_target1" value=""/>
				<input value="{key('i18n','import.displayName')}" type="submit" />
			</div>
			<div><xsl:comment>This is here to "clear" the floats.</xsl:comment></div>
		</form>
		<!--xsl:call-template name="search.form"/-->
	</xsl:template>
	
	<xsl:template match="t:tidbit">
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
					<div class="edit-btn" id="tidbit-{@tidbit-id}-edit" style="display: none;">
						<span class="alt-hide"><xsl:value-of select="key('i18n','edit')"/></span>
					</div>				
				</td>
			</xsl:if>
			<td>
				<xsl:choose>
					<xsl:when test="$handheld = 'true'">
						<a title="{key('i18n','edit.tidbit.title')}">
							<xsl:attribute name="href">
								<xsl:value-of select="$web-context"/>
								<xsl:text>/saveTidbit.do?tidbit.tidbitId=</xsl:text>
								<xsl:value-of select="@tidbit-id"/>
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
				<xsl:value-of select="t:kind/@name"/>
			</td>
			<xsl:if test="$handheld != 'true'">
				<td class="data" id="tidbit-{@tidbit-id}-data">
					<div class="data-container">
						<xsl:choose>
							<xsl:when test="string-length(t:data) &gt; 0">
								<xsl:value-of select="t:data" disable-output-escaping="no" xml:space="preserve"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text> </xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</div>
				</td>
			</xsl:if>
			<td>
				<xsl:value-of select="t:comment" disable-output-escaping="no" xml:space="preserve"/>
			</td>
			<xsl:if test="$handheld != 'true'">
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
			</xsl:if>
		</tr>
		<xsl:if test="$handheld = 'true'">
			<tr>
				<xsl:attribute name="class">
					<xsl:text>tidbit-data-row</xsl:text>
					<xsl:if test="(position() mod 2) = 0">
						<xsl:text> even</xsl:text>
					</xsl:if>
				</xsl:attribute>
				<th class="start"><xsl:text> </xsl:text></th>
				<td colspan="3">
					<div class="data-display">
						<xsl:value-of select="t:data" disable-output-escaping="no" xml:space="preserve"/>
					</div>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
