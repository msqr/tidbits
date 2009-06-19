/* Depends on initialized AppState instance of ApplicationState, from appstate.js */

jQuery.fn.log = function (msg) {
	if ( console && console.log ) {
	    console.log("%s: %o", msg, this);
	}
    return this;
};

var AppState = new ApplicationState();
AppState.initialize();
// TODO var IE = false;
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
		$j('ui-elements').append(dialogContentPane.children());
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

function setupDataClick(cell) {
	var id = getTidbitId(cell);
	$j(cell).click(function() {
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


function setupEditButton(button) {
	var id = getTidbitId(button);
	if ( !id) return;
	$j(button).click(function() {
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


var XwebLocale = new XwebLocaleClass();
$j(document).ready(function() {
	initLocale();
	populateTidbitKinds();
	
	$j('.close-x').click(function() {
		standardDialogHide(this.parentNode);
	});

	/* This doesn't seem to be used
	$j('.dialog-cancel-submit').click(function() {
			standardDialogHide();
			return false;
		});*/

	$j('textarea').each(function() {
		// remove blank whitespace from textarea elements, because of XSL putting
		// empty space in them so they don't end up like <textarea/>
		if ( $j(this).text().search(/^[\s\n]+$/) == 0 ) {
			$j(this).empty();
		}
	});
	
	$j('#edit-tidbit-form').submit(function() {
		if ( $j('#edit-tidbit-delete').val() == 'true' ) {
			if ( !confirm(XwebLocale.i18n('really.delete')) ) {
				return false;
			}
		}
		return true;
	});

	$j('#page-form-page').change(function() {
		this.form.submit();
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
	
	$j('td.data').each(function() {
		setupDataClick(this);
	});
	
	$j('.edit-btn').each(function() {
		setupEditButton(this);
	});
	
	$j('.edit-kind-btn').each(function() {
		setupEditKindButton(this);
	});
	
	$j('.link-search-tidbit').click(function() {
		standardFormShow('tidbit','search');
		return false;
	});
	
	$j('#main-nav').addClass('main-nav');
	
	$j('#content-pane').addClass('content-pane');
	
	$j('#top-hide').addClass('top-hide').show();
	
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

});
