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
	
	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" media-type="text/html"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" 
		doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/>

	<xsl:variable name="search-results" select="x:x-data/x:x-model[1]/t:model[1]/t:search-results"/>
	
	<xsl:template match="x:x-data" mode="page-main-nav">
		<xsl:call-template name="main-nav">
			<xsl:with-param name="page" select="'home'"/>
		</xsl:call-template>
		<form id="nav-search-tidbit-form" action="{$web-context}/search.do" method="post" class="form-search">
				<input name="query" id="search-tidbit-query" type="text" class="search-query">
					<xsl:attribute name="value">
						<xsl:if test="$handheld = 'true'">
							<xsl:value-of select="$search-results/@query"/>
						</xsl:if>
					</xsl:attribute>
				</input>
				<input type="hidden" name="page" value="0"/>
		</form>
	</xsl:template>	
	
	<xsl:template match="x:x-data" mode="page-body">
		<div id="content-pane" class="alt_pagination">
			<xsl:if test="$err">
				<xsl:apply-templates select="x:x-errors" mode="error-intro"/>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$handheld = 'false'">
					<div id="search-results-info">
						<!--form id="page-form" action="{$web-context}{$ctx/x:path}" method="post">
							<div-->
								<span class="header" id="matches-label">
									<xsl:value-of select="key('i18n','search.result.query.matches')"/>
									<xsl:text> </xsl:text>
									<span class="query">.</span>
								</span>
								<span class="data" id="matches-data">0</span>
								<span class="header" id="pages-label">
									<xsl:value-of select="key('i18n','search.result.pages')"/>
								</span>
								<span class="data" id="pages-data">
									<select name="page" id="page-form-page">
										<option value="0">1</option>
									</select>
								</span>
								<span class="header" id="page-size-label">
									<xsl:value-of select="key('i18n','search.result.page.size')"/>
								</span>
								<span class="data-last" id="page-size-data">
									<select size="1" name="pageSize" id="page-form-pagesize">
										<option value="10">10</option>
										<option value="25">25</option>
										<option value="50">50</option>
										<option value="100">100</option>
									</select>
								</span>
							<!--/div>
						</form-->
					</div>
				</xsl:when>
				<xsl:when test="string($search-results/@query) 
					or $search-results/@is-partial-result = 'true'">
					<div id="search-results-info">
						<form id="page-form" action="{$web-context}{$ctx/x:path}" method="post">
							<div>
								<span class="header">
									<xsl:choose>
										<xsl:when test="string($search-results/@query)">							
											<xsl:value-of select="key('i18n','search.result.query.matches')"/>
											<xsl:text> </xsl:text>
											<span class="query">
												<xsl:value-of select="$search-results/@query"/>
											</span>
										</xsl:when>
										<xsl:otherwise>	
											<xsl:value-of select="key('i18n','search.result.total.tidbits')"/>
										</xsl:otherwise>
									</xsl:choose>
								</span>
								<span class="data">
									<xsl:value-of select="$search-results/@total-results"/>
								</span>
								<xsl:if test="$search-results/@is-partial-result = 'true'">
									<span class="header">
										<xsl:value-of select="key('i18n','search.result.page.display')"/>
									</span>
									<span class="data">
										<xsl:value-of select="$search-results/t:pagination/@page-offset + 1"/>
										<xsl:choose>
											<xsl:when test="$handheld = 'true'">
												<xsl:text>/</xsl:text>
											</xsl:when>
											<xsl:otherwise>
												<xsl:text> </xsl:text>
												<xsl:value-of select="key('i18n','of')"/>
												<xsl:text> </xsl:text>
											</xsl:otherwise>
										</xsl:choose>
										<xsl:value-of select="ceiling($search-results/@total-results 
											div $search-results/t:pagination/@page-size)"/>
									</span>
									<xsl:if test="not($handheld = 'true')">
										<span class="header">
											<xsl:value-of select="key('i18n','search.result.pages')"/>
										</span>
									</xsl:if>
									<span class="data-last">
										<select name="page" id="page-form-page">
											<xsl:call-template name="render.page.option"/>
										</select>
									</span>
									<xsl:if test="$handheld = 'true'">
										<input type="submit" value="{key('i18n','go.displayName')}"/>
									</xsl:if>
								</xsl:if>
							</div>
						</form>
					</div>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$search-results/t:tidbit">
					<div id="body-content">
						<table class="table table-striped" id="datatable">
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
								<xsl:apply-templates select="x:x-model[1]/t:model[1]/t:search-results/t:tidbit"/>
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
