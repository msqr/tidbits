function doStandardAjaxResult(xmlRequest,dialogForm,dismissCallback,msgPane,msgContentPane) {
	msgPane = $(msgPane||'message-pane');
	msgContentPane = $(msgContentPane||'message-content-pane');
	
	var msg = xpathDomEval('/x:x-data/x:x-messages[1]/x:msg[1]',xmlRequest.responseXML);
	var fullMessage = msg.stringValue();

	var haveError = false;
	var errorIdx = 1;
	do {
		var error = xpathDomEval('/x:x-data/x:x-errors[1]/x:error['+errorIdx+']',xmlRequest.responseXML);
		if ( error.nodeSetValue().length > 0 ) {
			fullMessage += '<div class="error">' +error.stringValue() +'</div>';
			errorIdx++;
		} else {
			haveError = false;
		}
	} while ( haveError );
	
	if ( errorIdx == 1 ) { // no errors
		if ( AppState.dialogVisible ) {
			doStandardDialogHide(); // close dialog if showing
		}
		/*if ( dialogForm != null ) {
			dialogForm.reset(); // reset form if available
		}*/
	} else if ( AppState.dialogVisible && dialogForm != null ) {
		Form.focusFirstElement(dialogForm);
	}
	
	
	if ( dismissCallback && errorIdx == 1 ) { // only apply dismiss callback if no error
		document.getElementsByClassName('close-x',msgPane).each(function(el) {
			var oldOnClick = el.onclick;
			el.onclick = function() {
				oldOnClick.call(el);
				dismissCallback.call(el);
				el.onclick = oldOnClick;
			};
		});
	}
	
	doStandardMessageDisplay(fullMessage,dismissCallback,msgPane,msgContentPane);
}

/**
 * Default method for displaying a message to the user.
 */
function doStandardMessageDisplay(fullMessage,dismissCallback,msgPane,msgContentPane) {
	msgPane = $(msgPane||'message-pane');
	msgContentPane = $(msgContentPane||'message-content-pane');

	Element.update(msgContentPane,fullMessage);
	Effect.Center(msgPane);
	if ( !Element.visible(msgPane) ) {
		new Effect.Appear(msgPane, { 
			duration: 0.5,
			afterFinish: function() {
				if ( !dismissCallback ) {
					// wait a few seconds, then hide msg
					setTimeout(function() {
						if ( Element.visible(msgPane) ) {
							new Effect.Fade(msgPane, {duration: 0.5});
						}
					}, 5000);
				}
			}
			});
	}

	var dim = Element.getDimensions(msgPane);
	var width = dim.width - 10;
	var height = dim.height -10;
	if ( width > 0 && height > 0 ) {
		var bgUrl = AppState.context +'/shadow.do?w=' +width 
			+'&h=' +height +'&b=6&r=3&c=3289650';
		msgPane.style.backgroundImage = 'url(' +bgUrl +')';
		msgPane.style.backgroundRepeat = 'no-repeat';
		msgPane.style.backgroundPosition = '-3px -3px';
	}
}

/**
 * Default method for displaying a dialog box to user. It assumes
 * the dialog content comes from some element of the ui-elements
 * element, which is assumed to hide the content.
 */
function doStandardDialogDisplay(dialogContent,dialogPane,dialogContentPane,afterAppear) {
	dialogContent = $(dialogContent);
	if ( !dialogContent ) {
		alert("No dialog content found for [" +dialogContent +"]");
		return;
	}
	dialogPane = $(dialogPane||'dialog-pane');
	if ( !dialogPane ) {
		alert("No dialog found for [" +dialogPane +"]");
		return;
	}
	dialogContentPane = $(dialogContentPane||'dialog-content-pane');
	if ( !dialogContentPane ) {
		alert("No dialog content pane found for [" +dialogContentPane +"]");
		return;
	}

	// move dialog content into dialog-pane
	dialogContent.parentNode.removeChild(dialogContent);
	
	if ( dialogContentPane.hasChildNodes() ) {
		// move current child node back to ui-elements
		var uiElements = $('ui-elements');
		uiElements.appendChild(dialogContentPane.removeChild(dialogContentPane.firstChild));
	}
	dialogContentPane.appendChild(dialogContent);
	
	Effect.Center(dialogPane);
	
	if ( !Element.visible(dialogPane) ) {
		new Effect.Appear(dialogPane, { 
			duration: 0.8,
			afterFinish: function() {
				if ( afterAppear ) {
					afterAppear.apply(dialogPane);
				}
			}
			});
		AppState.dialogVisible = true;
	} else {
		if ( afterAppear ) {
			afterAppear.apply(dialogPane);
		}
	}

	var dim = Element.getDimensions(dialogPane);
	var width = dim.width - 10;
	var height = dim.height -10;
	if ( width > 0 && height > 0 ) {
		var bgUrl = AppState.context +'/shadow.do?w=' +width 
			+'&h=' +height +'&b=6&r=3&c=3289650';
		dialogPane.style.backgroundImage = 'url(' +bgUrl +')';
		dialogPane.style.backgroundRepeat = 'no-repeat';
		dialogPane.style.backgroundPosition = '-3px -3px';
	}
}

function doStandardDialogHide(dialogPane) {
	dialogPane = $(dialogPane||'dialog-pane');
	if ( Element.visible(dialogPane) ) {
		new Effect.Fade(dialogPane, { 
			duration: 0.4,
			afterFinish: function() {
				AppState.dialogVisible = false;
			}
		});
	}	
}

function doShowStandardForm(type, kind, noFocus) {
	var doFocus = function() {
		if ( !noFocus && !IE ) { // do not allow because IE freaks
			var form = $(kind+'-'+type+'-form');
			if ( form ) {
				form.focusFirstElement();
			}
		}
	}
	doStandardDialogDisplay(kind+'-'+type+'-form', null, null, doFocus);
}

function showStandardForm(link, type, kind, noFocus) {
	var spanLink = Builder.node('span',{'class':'a'},link.firstChild.nodeValue);
	spanLink.onclick = function() {
		doShowStandardForm(type, kind, noFocus);
	};
	link.parentNode.replaceChild(spanLink, link);
}

function clearChildren(node) {
	node = $(node);
	while ( node.hasChildNodes() ) {
		node.removeChild(node.firstChild);
	}
}

/**
 * Select a menu option based on a given value.
 * 
 * @param menu the select menu
 * @param value the value to select
 */
function selectMenuValue(menu, value) {
	menu = $(menu);
	var options = menu.getElementsByTagName('option');
	var optionToSelect = $A(options).detect(function(option) {
		if ( value == option.value ) return true;
	});
	if ( !optionToSelect ) {
		alert("Error: unable to find [" +value +"] in [" +menu.name +"]");
		return;
	}
	menu.selectedIndex = optionToSelect.index;
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

var workingUpdater = null;
var globalAjaxHandlers = {
	onCreate: function() {
		new Effect.Appear('system-working', { duration: 0.2, from: 0.0, to: 1.7 });
		//Element.show('system-working');
		if ( workingUpdater == null ) {
			workingUpdater = new WorkingPeriodicalExecutor();
		} /*else {
			workingUpdater.start();
		}*/
	},

	onComplete: function() {
		if ( Ajax.activeRequestCount == 0 ) {
			new Effect.Fade('system-working', { duration: 0.2, from: 1.0, to: 0.0 });
			//Element.hide('system-working');
			if ( workingUpdater != null ) {
				workingUpdater.stop();
			}
		}
	},
	
	onException: function(request,exception) {
		if ( Ajax.activeRequestCount == 0 ) {
			new Effect.Fade('system-working', { duration: 0.2 });
			//Element.hide('system-working');
			if ( workingUpdater != null ) {
				workingUpdater.stop();
			}
		}
		alert("An error occurred processing the request: " +exception);
	},
	
	onFailure: function(request) {
		alert('Failure: ' +request.status +' -- ' +request.statusText +': ' 
			+request.responseText);
	}
}

Ajax.Responders.register(globalAjaxHandlers);

function updateTidbitKinds() {
	var addSelect = $('new-tidbit-kind');
	var editSelect = $('edit-tidbit-kind');
	if ( addSelect || editSelect ) {
		new Ajax.Request(AppState.context+'/kinds.do', {
			parameters: '', 
			onSuccess: function(xmlRequest) {
				var nodes = xpathDomEval('/x:x-data/x:x-model[1]/t:model[1]/t:kind',
					xmlRequest.responseXML).nodeSetValue();
				if ( addSelect ) {
					updateTidbitKindsSelect(addSelect, nodes);
				}
				if ( editSelect ) {
					updateTidbitKindsSelect(editSelect, nodes);
				}
			}});
	}
}

function initXwebLocale() {
	new Ajax.Request(AppState.context+'/messages.json', {
		method: 'get',
		parameters: '', 
		onSuccess: function(xmlRequest) {
			var data = eval(xmlRequest.responseText);
			XwebLocale.initJson(data);
		}});
}

function updateTidbitKindsSelect(kindSelect, nodes) {
	clearChildren(kindSelect);
	for ( var i = 0; i < nodes.length; i++ ) {
		var option = Builder.node('option',{
			'value': nodes[i].getAttribute('kind-id')
			}, nodes[i].getAttribute('name'));
		kindSelect.appendChild(option);
	}
}

function setupEditButton(button) {
	var id = getTidbitId(button);
	if ( !id) return;
	button.onclick = function() {
		new Ajax.Request(AppState.context+'/tidbit.do', {
			parameters: 'tidbitId='+id, 
			onSuccess: function(xmlRequest) {
				var nodes = xpathDomEval('/x:x-data/x:x-model[1]/t:model[1]/t:tidbit[1]',
					xmlRequest.responseXML).nodeSetValue();
				if ( nodes.length < 1 ) return;
				var tidbitNode = nodes[0];
				var name = tidbitNode.getAttribute('name');
				var data = xpathDomEval('t:data[1]', tidbitNode).stringValue();
				var comment = xpathDomEval('t:comment[1]', tidbitNode).stringValue();
				var kindId = xpathDomEval('t:kind[1]/@kind-id', tidbitNode).stringValue();
				
				// update form fields
				$('edit-tidbit-delete').value = 'false';
				$('edit-tidbit-id').value = id;
				$('edit-tidbit-name').value = name;
				$('edit-tidbit-data').value = data;
				$('edit-tidbit-comment').value = comment;
				selectMenuValue('edit-tidbit-kind', kindId);

				// show form
				doShowStandardForm('tidbit','edit',false);
			}});
	};
}

function setupEditKindButton(button) {
	var id = getKindId(button);
	if ( !id) return;
	button.onclick = function() {
		new Ajax.Request(AppState.context+'/kind.do', {
			parameters: 'kindId='+id, 
			onSuccess: function(xmlRequest) {
				var nodes = xpathDomEval('/x:x-data/x:x-model[1]/t:model[1]/t:kind[1]',
					xmlRequest.responseXML).nodeSetValue();
				if ( nodes.length < 1 ) return;
				var kindNode = nodes[0];
				var name = kindNode.getAttribute('name');
				var comment = xpathDomEval('t:comment[1]', kindNode).stringValue();
				
				// update form fields
				$('edit-kind-delete').value = 'false';
				$('edit-kind-id').value = id;
				$('edit-kind-name').value = name;
				$('edit-kind-comment').value = comment;

				// show form
				doShowStandardForm('kind','edit',false);
			}});
	};
}

function setupEditRowMouseover(row) {
	var editBtn = $(row).getElementsByClassName('edit-btn');
	if ( !editBtn || editBtn.length < 1 ) {
		editBtn = $(row).getElementsByClassName('edit-kind-btn');
		if ( !editBtn || editBtn.length < 1 ) {
			return;
		}
	}
	editBtn = editBtn[0];
	row.onmouseover = function() {
		if ( AppState.editBtn && editBtn != AppState.editBtn ) {
			Element.hide(AppState.editBtn);
		}
		Element.show(editBtn);
		AppState.editBtn = editBtn;
	};
}

function setupDataClick(cell) {
	var id = getTidbitId(cell);
	cell.onclick = function() {
		new Ajax.Request(AppState.context+'/tidbit.do', {
			parameters: 'tidbitId='+id, 
			onSuccess: function(xmlRequest) {
				var nodes = xpathDomEval('/x:x-data/x:x-model[1]/t:model[1]/t:tidbit[1]',
					xmlRequest.responseXML).nodeSetValue();
				if ( nodes.length < 1 ) return;
				var tidbitNode = nodes[0];
				var data = xpathDomEval('t:data[1]', tidbitNode).stringValue();

				// show msg
				doStandardMessageDisplay('<div class="data-display">' +data +'</div>', 
					function(){});
			}});
	}
}

function setupPageFormPager(select) {
	select.onchange = function() {
		select.form.submit();
	}
}

var globalTidbitRules = {
	'body' : function(el) {
		initXwebLocale();
		updateTidbitKinds();
	},
	
	'.close-x' : function(el) {
		el.onclick = function() {
			doStandardDialogHide(el.parentNode);
		};
	},
	
	'.dialog-cancel-submit' : function(el) {
		el.onclick = function() {
			doStandardDialogHide();
			return false;
		};
	},
	
	'textarea' : function(el) {
		// remove blank whitespace from textarea elements, because of XSL putting
		// empty space in them so the don't end up like <textarea/>
		if ( $F(el).search(/^[\s\n]+$/) == 0 ) {
			Field.clear(el);
		}
	},

	'#new-tidbit-form': function(el) {
	
	},
	
	'#new-kind-form': function(el) {
	
	},
	
	'#edit-tidbit-form': function(form) {
		form.onsubmit = function() {
			if ( $F('edit-tidbit-delete') == 'true' ) {
				if ( !confirm(XwebLocale.i18n('really.delete')) ) {
					return false;
				}
			}
			return true;
		};
	},
	
	'#page-form': function(el) {
		
	},
	
	'#page-form-page': function(el) {
		setupPageFormPager(el);
	},
	
	'#search-tidbit-form': function(el) {
	
	},
	
	'#edit-tidbit-submit-delete': function(el) {
		el.onclick = function() {
			$('edit-tidbit-delete').value = 'true';
		};
	},
	
	'#edit-tidbit-submit': function(el) {
		el.onclick = function() {
			$('edit-tidbit-delete').value = 'false';
		};
	},
	
	'#edit-kind-form': function(form) {
		form.onsubmit = function() {
			if ( $F('edit-kind-delete') == 'true' ) {
				if ( !confirm(XwebLocale.i18n('really.delete')) ) {
					return false;
				}
			}
			return true;
		};
	},
	
	'#edit-kind-submit-delete': function(el) {
		el.onclick = function() {
			$('edit-kind-delete').value = 'true';
		};
	},
	
	'#edit-kind-submit': function(el) {
		el.onclick = function() {
			$('edit-kind-delete').value = 'false';
		};
	},
	
	'.tidbit-data-row': function(el) {
		setupEditRowMouseover(el);
	},
	
	'td.data': function(el) {
		setupDataClick(el);
	},
	
	'.edit-btn': function(el) {
		setupEditButton(el);
	},
	
	'.edit-kind-btn': function(el) {
		setupEditKindButton(el);
	},
	
	'#main-nav': function(el) {
		Element.addClassName(el, 'main-nav');
	},
	
	'#content-pane': function(el) {
		Element.addClassName(el, 'content-pane');
	},
	
	'#top-hide': function(el) {
		Element.addClassName(el, 'top-hide');
		Element.show(el);
	},
	
	'.link-add-tidbit': function(el) {
		showStandardForm(el,'tidbit','new',false);
	},
	
	'.link-add-kind': function(el) {
		showStandardForm(el,'kind','new',false);
	},
	
	'.link-import-csv': function(el) {
		showStandardForm(el,'csv','import',true);
	},
	
	'.link-search-tidbit': function(el) {
		showStandardForm(el,'tidbit','search',false);
	}
	
};

var XwebLocale = new XwebLocaleClass();
var AppState = new ApplicationState();
