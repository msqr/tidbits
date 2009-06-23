/* Depends on appstate.js */

jQuery.fn.log = function (msg) {
	if ( console && console.log ) {
	    console.log("%s: %o", msg, this);
	}
    return this;
};

var AppState = new ApplicationState();
AppState.initialize();
var $j = jQuery.noConflict();

function getJsonModelObject(data) {
	return data["magoffin.matt.xweb.MODEL"];
}

function initLocale() {
	$j.getJSON(AppState.context + '/messages.json',
		function(data){
			XwebLocale.initJson(data);
			$j(document).trigger("XwebLocaleReady", [XwebLocale]);
		});
}

/**
 * Get a Tidbit ID from an "id" attribute of the form "tidbit-XX"
 * where XX is the ID.
 */
function getTidbitId(node) {
	if ( node.getAttribute('id') == null ) return;
	var data = node.getAttribute('id').match(/tidbit-(\d+)/);
	if ( data ) {
		return data[1];
	}
	return null;
}

/**
 * Get a TidbitKind ID from an "id" attribute of the form "kind-XX"
 * where XX is the ID.
 */
function getKindId(node) {
	if ( node.getAttribute('id') == null ) return;
	var data = node.getAttribute('id').match(/kind-(\d+)/);
	if ( data ) {
		return data[1];
	}
	return null;
}

function populateTidbitKinds() {
	var selects = $j('#new-tidbit-kind,#edit-tidbit-kind');
	if ( selects.size() > 0 ) {
		$j.getJSON(AppState.context + '/kinds.json',
			function(data) {
				var kinds = getJsonModelObject(data).kind;
				if ( !(kinds && kinds.length) ) {
					return;
				}
				selects.empty();
				$j(kinds).each(function() {
					var newOpt = $j('<option value="' +this.kindId +'"/>').text(this.name);
					selects.append(newOpt);
				});
			});
	}
}

/**
 * Display a "dialog" object, centered in the window and on top of everything else.
 * 
 * This works by moving a DOM element from a hidden #ui-elements div into a hidden
 * "dialog" element, and then shows that "dialog". Any content in the dialog
 * when this method is called is moved back to the #ui-elements div before appending
 * the new content. In this way dialog content is swapped back and forth from the
 * #ui-elements container to the dialog container.
 * 
 * @param dialogContent the content to display
 * @param dialogPane the overall dialog element, or #dialog-pane if not provided
 * @param dialogContentPane the parent element for the dialog content, or 
 * #dialog-content-pane if not provided
 * @param afterAppear an optional callback to call after the dialog is shown
 */
function standardDialogDisplay(dialogContent,dialogPane,dialogContentPane,afterAppear) {
	dialogContent = $j(dialogContent);
	if ( dialogContent.size() < 1 ) {
		alert("No dialog content found for [" +dialogContent +"]");
		return;
	}
	dialogPane = $j(dialogPane||'#dialog-pane');
	if ( dialogPane.size() < 1 ) {
		alert("No dialog found for [" +dialogPane +"]");
		return;
	}
	dialogContentPane = $j(dialogContentPane||'#dialog-content-pane');
	if ( dialogContentPane.size() < 1 ) {
		alert("No dialog content pane found for [" +dialogContentPane +"]");
		return;
	}

	if ( dialogContentPane.children().size() > 0 ) {
		// move current child node back to ui-elements
		$j('#ui-elements').append(dialogContentPane.children());
	}
	dialogContentPane.append(dialogContent);
	
	dialogPane.center();
	
	if ( dialogPane.is(':hidden') ) {
		dialogPane.fadeIn('slow', function() {
			if ( afterAppear ) {
				afterAppear.apply(dialogPane);
			}
		});
		AppState.dialogVisible = true;
	} else {
		if ( afterAppear ) {
			afterAppear.apply(dialogPane);
		}
	}
	
	standardShadowDisplay(dialogPane);
}

/**
 * Show a form by calling standardDialogDisplay() and then focusing
 * the first form element.
 * 
 * @param type the form type
 * @param kind the form kind
 * @param callback a function callback to call after the form appears.
 * If not provided, will use a callback to focus the first form input
 */
function standardFormShow(type, kind, callback) {
	if ( !callback ) {
		callback = function() {
			$j('#'+kind+'-'+type+'-form input:first').focus();
		};
	}
	standardDialogDisplay('#'+kind+'-'+type+'-form', null, null, callback);
}

/**
 * Hide the "dialog" object using a fade-out effect.
 * 
 * @param dialogPane the object to hide, or #dialog-pane if not provided
 */
function standardDialogHide(dialogPane) {
	dialogPane = $j(dialogPane||'#dialog-pane');
	if ( dialogPane.is(':visible') ) {
		dialogPane.fadeOut('slow', function() {
			if ( $j('#dialog-pane').get(0) == dialogPane.get(0) ) {
				AppState.dialogVisible = false;
			}
		});
	}	
}

/**
 * Apply a CSS background shadow image to an object.
 * 
 * This relys on the application's /shadow.do service to generate
 * a shadow of the appropriate size of the passed in object.
 * 
 * @param msgPane the object to add the shadow to
 */
function standardShadowDisplay(msgPane) {
	msgPane = $j(msgPane);
	var width = msgPane.outerWidth() - 10;
	var height = msgPane.outerHeight() -10;
	if ( width > 0 && height > 0 ) {
		var bgUrl = AppState.context +'/shadow.do?w=' +width 
			+'&h=' +height +'&b=6&r=3&c=3289650';
		msgPane.css({
			'background-image': 'url(' +bgUrl +')',
			'background-repeat': 'no-repeat',
			'background-position': '-3px -3px'
		});
	}
}

/**
 * Display a message "dialog" object, centered in the window and on top of 
 * everything else.
 * 
 * If no dismissCallback is provided, the message "dialog" will be set to 
 * fade out automatically after 5 seconds.
 * 
 * @param fullMessage the HTML content of the message to display
 * @param dismissCallback optional callback to call after the message is shown
 * @param msgPane the overall message dialog object, or #message-pane if not provided
 * @param msgContentPane the parent element for the dialog content, or 
 * #message-content-pane if not provided
 */
function standardMessageDisplay(fullMessage,dismissCallback,msgPane,msgContentPane) {
	msgPane = $j(msgPane||'#message-pane');
	msgContentPane = $j(msgContentPane||'#message-content-pane');

	msgContentPane.html(fullMessage);
	msgPane.center();
	
	if ( msgPane.is(':hidden') ) {
		msgPane.fadeIn('slow', function() {
			if ( !dismissCallback ) {
				// wait a few seconds, then hide msg automatically
				setTimeout(function() {
					if ( msgPane.is(':visible') ) {
						msgPane.fadeOut('slow');
					}
				}, 5000);
			}
		});
	}

	standardShadowDisplay(msgPane);
}

function setupEditRowMouseover(row) {
	var jRow = $j(row);
	var btns = jRow.find('.edit-btn,.edit-kind-btn');
	if ( btns.size() < 1 ) {
		return;
	}
	jRow.mouseover(function() {
		if ( AppState.editBtn && btns.get(0) != AppState.editBtn ) {
			$j(AppState.editBtn).hide();
		}
		$j(btns).show();
		AppState.editBtn = btns.get(0);
	});
}

/**
 * Select a menu option based on a given value.
 * 
 * @param menu the select menu
 * @param value the value to select
 */
function setSelectedValue(menu, value) {
	$j(menu).find('option[value='+value+']').each(function() {
		menu.selectedIndex = this.index;
	});
}

function editButtonAction(id) {
	$j.getJSON(AppState.context +'/tidbit.json?tidbitId=' +id,
		function(data) {
			var tidbit = getJsonModelObject(data).tidbit;
			if ( !(tidbit && tidbit.length) ) {
				return;
			}
			tidbit = tidbit[0];
			
			// update form fields
			$j('#edit-tidbit-delete').val('false');
			$j('#edit-tidbit-id').val(id);
			$j('#edit-tidbit-name').val(tidbit.name);
			$j('#edit-tidbit-data').val(tidbit.data);
			$j('#edit-tidbit-comment').val(tidbit.comment);
			setSelectedValue('#edit-tidbit-kind', tidbit.kind['kind-id']);
			
			standardFormShow('tidbit','edit',false);
		});
}

function dataCellAction(id) {
	$j.getJSON(AppState.context +'/tidbit.json?tidbitId=' +id,
			function(data) {
				var tidbit = getJsonModelObject(data).tidbit;
				if ( !(tidbit && tidbit.length) ) {
					return;
				}
				tidbit = tidbit[0];
				standardMessageDisplay('<div class="data-display">' +tidbit.data +'</div>', 
						function(){});
			});
}

function setupEditKindButton(button) {
	var id = getKindId(button);
	if ( !id) return;
	$j(button).click(function() {
		$j.getJSON(AppState.context +'/kind.json?kindId=' +id,
			function(data) {
				var kind = getJsonModelObject(data).kind;
				if ( !(kind && kind.length) ) {
					return;
				}
				kind = kind[0];
				
				// update form fields
				// update form fields
				$j('#edit-kind-delete').val('false');
				$j('#edit-kind-id').val(id);
				$j('#edit-kind-name').val(kind.name);
				$j('#edit-kind-comment').val(kind.comment);

				// TODO update reassign list to remove editing kind
				
				standardFormShow('kind','edit',false);
			});
	});
}

// custom pagination for data table
$j.fn.dataTableExt.oPagination.select = {
		'fnInit': function (oSettings, fnCallbackDraw) {
			$j('#page-form-page').change(function() {
				var desiredPage = $j(this).val();
				var offset = desiredPage * oSettings._iDisplayLength;
				oSettings._iDisplayStart = offset;
				fnCallbackDraw(oSettings);
			});
		},
		
		'fnUpdate': function (oSettings, fnCallbackDraw) {
			var numPages = Math.ceil(oSettings._iRecordsTotal / oSettings._iDisplayLength);
			var selectedPage = oSettings._iDisplayStart / oSettings._iDisplayLength;
			var mySelect = $j('#page-form-page');
			if ( numPages == 1 ) {
				mySelect.parent().hide().prev('span').hide();
				return;
			}
			mySelect.parent().show().prev('span').show();
			if ( numPages == mySelect.children('option').size() ) {
				return;
			}
			mySelect.empty();
			for ( var i = 0; i < numPages; i++ ) {
				mySelect.append('<option value="' +i +'"'
						+(i == selectedPage ? ' selected="selected"' : '')
						+'">' +(i+1) +'</option>');
			}
		}
	};

function updateSearchQueryInfo(query, matches) {
	if ( query == null || query == '' ) {
		// show all
		$j('#matches-label').html(XwebLocale.i18n('search.result.total.tidbits'));
	} else {
		// query
		var span = $j('<span class="query"></span>');
		span.text(query);
		$j('#matches-label').empty()
			.append(XwebLocale.i18n('search.result.query.matches')+' ')
			.append(span);
	}
	$j('#matches-data').text(matches);
}

var XwebLocale = new XwebLocaleClass();
var dataTable;

//must wait for XwebLocale to be loaded for some stuff
$j(document).bind("XwebLocaleReady", function() {
	$j('h1.title').each(function() {
		var newTitle = '<div><a href="' 
			+AppState.context +'/home.do?query=&page=0" title="'
			+XwebLocale.i18n('link.home') +'">'
				+'<img id="logo" class="title" alt="' +XwebLocale.i18n('title')
				+'" src="' +AppState.context +'/img/logo.png" />'
			+'</a></div>';
		$j(this).replaceWith(newTitle);
	});
	
	$j('#main-nav').addClass('main-nav');
	
	$j('#content-pane').addClass('content-pane');
	
	$j('#top-hide').addClass('top-hide').show();
	
	
	var searchPlaceholder = XwebLocale.i18n('search.placeholder')
	$j('#nav-search-tidbit-form').submit(function() {
		var q = $j(this).find('input').val();
		dataTable.fnFilter(q,null,false);
		return false;
	}).find('input.search-otherbrowser').focus(function() {
		var currVal = $j(this).val();
		if ( currVal == searchPlaceholder ) {
			$j(this).val('').removeClass('placeholder');
		}
	}).blur(function() {
		var currVal = $j(this).val();
		if ( currVal == '' ) {
			$j(this).val(searchPlaceholder).addClass('placeholder');
		}
	}).each(function() {
		var currVal = $j(this).val();
		if ( currVal == '' ) {
			$j(this).val(searchPlaceholder).addClass('placeholder');
		}
	});
	
	$j('#page-form-pagesize').change(function() {
		var newSize = Number($j(this).val());
		dataTable.dataTableSettings[0]._iDisplayLength = newSize;
		dataTable.fnDraw();
	});
	
});

$j(document).ready(function() {
	initLocale();
	populateTidbitKinds();
	
	$j('.close-x').click(function() {
		standardDialogHide(this.parentNode);
	});

	$j('textarea').each(function() {
		// remove blank whitespace from textarea elements, because of XSL putting
		// empty space in them so they don't end up like <textarea/>
		if ( $j(this).text().search(/^[\s\n]+$/) == 0 ) {
			$j(this).empty();
		}
	});
	
	$j('#new-tidbit-form').each(function() {
		var action = $j(this).attr('action');
		action = action.replace('.do', '.json');
		$j(this).attr('action', action);
	}).submit(function() {
		$j(this).ajaxSubmit(function(responseText, statusText) {
			if ( 'success' == statusText ) {
				standardDialogHide();
				standardMessageDisplay(XwebLocale.i18n('new.tidbit.saved.intro'));
			}
			// else ... TODO
		});
		return false;
	});
	
	$j('#edit-tidbit-form').each(function() {
		var action = $j(this).attr('action');
		action = action.replace('.do', '.json');
		$j(this).attr('action', action);
	}).submit(function() {
		if ( $j('#edit-tidbit-delete').val() == 'true' ) {
			if ( !confirm(XwebLocale.i18n('really.delete')) ) {
				return false;
			}
		}
		$j(this).ajaxSubmit(function(responseText, statusText) {
			if ( 'success' == statusText ) {
				standardDialogHide();
				standardMessageDisplay(XwebLocale.i18n('edit.tidbit.saved.intro'));
			}
			// else ... TODO
		});
		return false;
	});

	$j('#edit-tidbit-submit-delete').click(function() {
		$j('#edit-tidbit-delete').val('true');
	});
	
	$j('#edit-tidbit-submit').click(function() {
		$j('#edit-tidbit-delete').val('false');
	});
	
	$j('#edit-kind-form').submit(function() {
		if ( $j('#edit-kind-delete').val() == 'true' ) {
			if ( !confirm(XwebLocale.i18n('really.delete')) ) {
				return false;
			}
		}
		return true;
	});
	
	$j('#edit-kind-submit-delete').click(function() {
		$j('#edit-kind-delete').val('true');
	});
	
	$j('#edit-kind-submit').click(function() {
		$j('edit-kind-delete').val('false');
	});
	
	$j('.tidbit-data-row').each(function() {
		setupEditRowMouseover(this);
	});

	$j('#datatable tbody tr').live('mouseover', function() {
		var jRow = $j(this);
		var btns = jRow.find('.edit-btn,.edit-kind-btn');
		if ( btns.size() < 1 ) {
			return;
		}
		if ( AppState.editBtn && btns.get(0) != AppState.editBtn ) {
			$j(AppState.editBtn).hide();
		}
		$j(btns).show();
		AppState.editBtn = btns.get(0);
	});
	
	$j('td.data').live('click', function() {
		var btns = $j(this).prevAll('td.edit-button').find('.edit-btn,.edit-kind-btn');
		if ( btns.size() < 1 ) {
			return;
		}
		var id = getTidbitId(btns.get(0));
		if ( !id ) {
			return;
		}
		dataCellAction(id);
	});
	
	$j('div.edit-btn').live('click', function() {
		var id = getTidbitId(this);
		if ( !id ) return;
		editButtonAction(id);	
	});
	
	$j('.edit-kind-btn').each(function() {
		setupEditKindButton(this);
	});
	
	$j('.link-search-tidbit').click(function() {
		standardFormShow('tidbit','search');
		return false;
	});
	
	$j('.link-add-tidbit').click(function() {
		standardFormShow('tidbit','new');
		return false;
	});
	
	$j('.link-add-kind').click(function() {
		standardFormShow('kind','new');
		return false;
	});
	
	$j('.link-import-csv').click(function() {
		standardFormShow('csv','import');
		return false;
	});

	dataTable = $j('#datatable').dataTable( {
		'bProcessing': false,
		'bPaginate': true,
		'bLengthChange': false,
		'bServerSide': true,
		'bFilter':  false,
		'bAutoWidth': false,
		'bInfo': false,
		'bSort': false,
		'sAjaxSource': AppState.context +'/search.json',
		'sPaginationType': 'select',
		'fnServerData': function ( sSource, aoData, fnCallback ) {
				$j.ajax({
					"dataType": 'json', 
					"type": "POST", 
					"url": sSource, 
					"data": aoData, 
					"success": function(data, textStatus) {
						if ( data.iTotalRecords ) {
							if ( data.iTotalRecords > 0 ) {
								$j('#body-content').show();
							} else {
								$j('#body-content').hide();
							}
						}
						var query = '';
						for ( var i = 0; i < aoData.length; i++ ) {
							if ( aoData[i].name == 'sSearch' ) {
								query = aoData[i].value;
								break;
							}
						}
						updateSearchQueryInfo(query, data.iTotalRecords);
						if ( fnCallback ) {
							fnCallback.call(this, data, textStatus);
						}
					}
				});
			},
		'aoColumns': [{ 'sClass': 'right' }, {
				'fnRender': function(obj) {
					return '<div class="edit-btn" id="tidbit-' +obj.aData[1] +'-edit" style="display: none;"></div>';
				},
				'sClass': 'edit-button'
			}, null, null, { 'sClass': 'data'}, null, null, null]
	});
	
	$j('#datatable_wrapper').find('div.dataTables_paginate').hide(); // TODO fix pagination
});
