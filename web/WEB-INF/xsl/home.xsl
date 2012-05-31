<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:t="http://msqr.us/xsd/TidBits"
	xmlns:x="http://msqr.us/xsd/jaxb-web"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="t x date">

	<!-- imports -->
	<xsl:import href="tmpl/default-layout.xsl"/>
	
	<xsl:output method="xml" omit-xml-declaration="yes" indent="yes" media-type="text/html"/>

	<xsl:template match="x:x-data" mode="page-body-id">card-container</xsl:template>
	
	<xsl:template match="x:x-data" mode="page-body-class">app</xsl:template>
	
	<xsl:template match="x:x-data" mode="page-main-nav">
		<xsl:call-template name="main-nav">
			<xsl:with-param name="page" select="'home'"/>
		</xsl:call-template>
	</xsl:template>	
	
	<xsl:template match="x:x-data" mode="page-body">
		<div class="editor flipper-container" id="tidbit-editor" style="visibility: hidden;">
			<div class="flipper">
				<form class="face front form-horizontal" id="tidbit-form"
					method="post" action="{$web-context}/saveTidbit.do">
					<div class="header">
						<h3><xsl:value-of select="key('i18n', 'new.tidbit.title')"/></h3>
					</div>
					<fieldset class="body">
						<input id="edit-tidbit-id" name="id" type="hidden" />
						<div class="control-group">
							<label for="add-tidbit-name" class="control-label">
								<xsl:value-of select="key('i18n','tidbit.name.displayName')"/>
							</label>
							<div class="controls">
								<input id="add-tidbit-name" name="name" type="text" class="span5" />
							</div>
						</div>
						<div class="control-group">
							<label for="add-tidbit-kind" class="control-label">
								<xsl:value-of select="key('i18n','tidbit.kind.displayName')"/>
							</label>
							<div class="controls">
								<select id="add-tidbit-kind" name="kind.id" class="span5">
									<xsl:text> </xsl:text>
								</select>
							</div>
						</div>
						<div class="control-group">
							<label for="add-tidbit-data" class="control-label">
								<xsl:value-of select="key('i18n','tidbit.data.displayName')"/>
							</label>
							<div class="controls">
								<textarea id="add-tidbit-data" name="data" class="span5" rows="4">
									<xsl:text> </xsl:text>
								</textarea>
							</div>
						</div>
						<div class="control-group hidden-phone">
							<label for="add-tidbit-comments" class="control-label">
								<xsl:value-of select="key('i18n','tidbit.comment.displayName')"/>
							</label>
							<div class="controls">
								<textarea id="add-tidbit-comments" name="comment" class="span5" rows="3">
									<xsl:text> </xsl:text>
								</textarea>
								<span class="label">
									<xsl:value-of select="key('i18n','optional.caption')"/>
								</span>
							</div>
						</div>
					</fieldset>
					<div class="footer modal-footer">
						<button type="button" class="btn pull-left" id="manage-categories-btn">
							<i class="icon-list"><xsl:text> </xsl:text></i>
						</button>
						<button type="button" class="btn" data-dismiss="editor">
							<xsl:value-of select="key('i18n','close')"/>
						</button>
						<button type="submit" class="btn btn-primary">
							<xsl:value-of select="key('i18n','save.displayName')"/>
						</button>
					</div>
				</form>
				
				<div class="face back form-horizontal">
					<div class="header">
						<button type="button" class="btn btn-mini btn-primary pull-right" id="add-new-kind-btn">
							<i class="icon-plus"><xsl:text> </xsl:text></i>
						</button>
						<h3><xsl:value-of select="key('i18n', 'edit.kinds.title')"/></h3>
					</div>
					<fieldset class="body">
						<table class="table table-striped">
							<tbody id="kind-table-body">
								<xsl:text> </xsl:text>
							</tbody>
						</table>
					</fieldset>
					<div class="footer modal-footer">
						<button type="button" class="btn" data-dismiss="editor">
							<xsl:value-of select="key('i18n','close')"/>
						</button>
						<button type="submit" class="btn btn-primary" id="manage-tidbit-btn">
							<xsl:value-of select="key('i18n','done.displayName')"/>
						</button>
					</div>
				</div>
				
				<div class="face bottom form-horizontal">
					<div class="header">
						<button type="button" class="btn btn-mini btn-primary pull-right" id="add-tidbit-value-btn">
							<i class="icon-plus"><xsl:text> </xsl:text></i>
						</button>
						<h3><xsl:value-of select="key('i18n','manage.tidbit.title')"/></h3>
					</div>
					<div class="body">
						<div class="alert alert-info">
							<button class="close btn btn-mini" type="button" data-dismiss="alert">
								<i class="icon-remove"><xsl:text> </xsl:text></i>
							</button>
							<xsl:value-of select="key('i18n','manage.tidbit.info')"/>
						</div>
						<div id="bit-edit-listing">
							<xsl:text> </xsl:text>
						</div>
					</div>
					<div class="footer modal-footer">
						<button type="button" class="btn" data-dismiss="editor">
							<xsl:value-of select="key('i18n','close')"/>
						</button>
						<button type="submit" class="btn btn-primary" id="manage-tidbit-btn">
							<xsl:value-of select="key('i18n','done.displayName')"/>
						</button>
					</div>
				</div>
			</div>
		</div>
		
		<!--div id="content-pane" class="alt_pagination">
			<xsl:if test="$err">
				<xsl:apply-templates select="x:x-errors" mode="error-intro"/>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$handheld = 'false'">
					<div id="search-results-info">
						<form id="page-form" action="{$web-context}{$ctx/x:path}" method="post">
							<div>
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
							</div>
						</form>
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
		</xsl:if-->
	</xsl:template>
	
	<xsl:template match="x:x-data" mode="ui.elements">
		<!-- TODO: add CSV import back
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
		-->
	</xsl:template>
	
</xsl:stylesheet>
