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
						<button type="button" class="btn pull-left btn-danger" id="delete-tidbit-btn">
							<i class="icon-trash"><xsl:text> </xsl:text></i>
							<xsl:text> </xsl:text>
							<xsl:value-of select="key('i18n','delete.displayName')"/>
						</button>
						<button type="button" class="btn cancel">
							<xsl:value-of select="key('i18n','cancel.displayName')"/>
						</button>
						<button type="submit" class="btn btn-primary">
							<xsl:value-of select="key('i18n','save.displayName')"/>
						</button>
					</div>
				</form>
				
				<div class="face bottom form-horizontal">
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
				
				<div class="face left form-horizontal">
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
						<button type="button" class="btn btn-primary" data-dismiss="editor">
							<xsl:value-of select="key('i18n','done.displayName')"/>
						</button>
					</div>
				</div>
			</div>
		</div>

		<div class="importer flipper-container" id="tidbit-importer" style="visibility: hidden;">
			<div class="flipper">
				<form class="face front form-horizontal" id="import-form" action="{$web-context}/import.do" 
					method="post" enctype="multipart/form-data">
					<div class="header">
						<h3><xsl:value-of select="key('i18n','import.csv.title')"/></h3>
					</div>
					<fieldset class="body">
						<input type="hidden" name="page" value="import"/>
						<input type="hidden" name="_to" value="verify"/>
						<xsl:value-of select="key('i18n','import.csv.intro')" disable-output-escaping="yes"/>
						<div class="control-group">
							<label for="import-file" class="control-label">
								<xsl:value-of select="key('i18n','import.cvs.file.displayName')"/>
							</label>
							<div class="controls">
								<input type="file" name="file" id="import-file"/>
							</div>
						</div>
					</fieldset>
					<div class="footer modal-footer">
						<button type="button" class="btn pull-left" data-dismiss="importer">
							<xsl:value-of select="key('i18n','close')"/>
						</button>
						<button type="submit" class="btn btn-primary">
							<xsl:value-of select="key('i18n','import.displayName')"/>
						</button>
					</div>
				</form>
				
				<form class="face back form-horizontal" id="import-verify-form" 
					action="{$web-context}/import.do" method="post">
					<div class="header">
						<h3><xsl:value-of select="key('i18n', 'import.verify.title')"/></h3>
					</div>
					<fieldset class="body">
						<input type="hidden" name="page" value="verify"/>
						<input type="hidden" name="_to" value="save"/>
						<p>
							<xsl:value-of select="key('i18n','import.verify.intro')" disable-output-escaping="yes"/>
						</p>
						<table class="table table-striped">
							<tbody id="import-table-body">
								<xsl:text> </xsl:text>
							</tbody>
						</table>
					</fieldset>
					<div class="footer modal-footer">
						<button type="button" class="btn pull-left" data-dismiss="importer">
							<xsl:value-of select="key('i18n','close')"/>
						</button>
						<button type="button" class="btn" id="import-verify-back">
							<xsl:value-of select="key('i18n','back.displayName')"/>
						</button>
						<button type="submit" name="_to" value="save" class="btn btn-primary">
							<xsl:value-of select="key('i18n','save.displayName')"/>
						</button>
					</div>
				</form>
			</div>
		</div>
	</xsl:template>
	
</xsl:stylesheet>
