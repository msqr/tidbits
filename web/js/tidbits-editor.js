/**
 * The Tidbits editor - a modal dialog that presents 3 "faces" of a cube
 * to edit the Tidbit property list, an edit form for a single property,
 * and a list of available kinds.
 * 
 * @param {Element} container the editor container
 * @returns {Tidbits.Class.Editor}
 */
Tidbits.Class.Editor = function(container) {
	var superclass = Tidbits.Class.ModalWidget;
	superclass.call(this, container);

	var self = this;

	this.bit = undefined;
	this.editModel = {};
	this.left = this.element.find('.left');
	this.bottom = this.element.find('.bottom');
	this.visibleFace = 'front';
	
	var handleKindEditClick = undefined;
	
	var handleKindEditSubmit = function(event) {
		event.preventDefault();
		event.stopPropagation();
		var me = $(this);
		var id = me.find('input[name=id]').val();
		var name = me.find('input[name=name]').val();
		me.ajaxSubmit({
			dataType: 'json',
			error : function(xhr, statusText, error) {
				Tidbits.defaultAjaxErrorHandler('kind.save.error.title', xhr, statusText, error);
			},
			success : function(data) {
				if ( !Array.isArray(data) || data.length < 1 ) {
					return;
				}
				var td = me.parent();
				var newKind = data[0];
				
				// set @data-id, in case submitted new value
				var newId = newKind.id;
				td.attr({'data-id':newId});
				
				// replace form with text
				me.replaceWith(name);
				
				var idx = 0;
				var opt = undefined;
				
				if ( id !== '' ) {
					// update existing category
					idx = Tidbits.Runtime.kinds.updateKind(newKind);
					opt = $('option[value="'+id+'"]').text(name);
				} else {
					// add new category
					idx = Tidbits.Runtime.kinds.addKind(newKind);
					opt = $('<option/>').attr({value:newId}).text(name);
				}
				
				// re-order options and list
				if ( idx === 0 ) {
					td.parent().parent().prepend(td.parent());
					$('#add-tidbit-kind').prepend(opt);
				} else {
					var table = td.parent().parent();
					var tr = td.parent().remove();
					table.find('tr').eq(idx-1).after(tr);
					opt.remove();
					$('#add-tidbit-kind option').eq(idx-1).after(opt);
				}
				
				// restore click to edit handler
				td.bind('click', handleKindEditClick);
			}
		});
	};
	
	var handleDeleteKindCancelClick = function(event) {
		event.preventDefault();
		event.stopPropagation();
		
		var form = $(this.form);
		var name = form.find('input[name=name]').val();
		var cell = form.parent();
		
		// restore click to edit handler
		cell.bind('click', handleKindEditClick);
		
		// replace form with text name
		cell.empty();
		cell.append(name);
	};
	
	var handleKindDeleteSubmit = function(event) {
		event.preventDefault();
		event.stopPropagation();
		var form = $(this);
		var id = form.find('input[name=id]').val();
		form.ajaxSubmit({
			dataType: 'json',
			error : function(xhr, statusText, error) {
				Tidbits.defaultAjaxErrorHandler('kind.save.error.title', xhr, statusText, error);
			},
			success : function(data) {
				// delete row
				form.parent().parent().remove();
				
				// delete <option>
				$('option[value="'+id+'"]').remove();
				
				// update model
				Tidbits.Runtime.kinds.removeKind(Number(id));
			}
		});
	};
	
	var handleDeleteKindClick = function(event) {
		event.preventDefault();
		var form = $(this.form);
		var id = form.find('input[name=id]').val();
		var name = form.find('input[name=name]').val();
		var cell = form.parent();
		if ( id === '' ) {
			// not really a kind, so just delete row
			cell.parent().remove();
		} else {
			form.replaceWith(name);
			id = Number(id);
			var reassignSelect = $('<select name="reassign"/>');
			var i, len;
			var kinds = Tidbits.Runtime.kinds.getKinds();
			for ( i = 0, len = kinds.length; i < len; i++ ) {
				if ( kinds[i].id !== id ) {
					reassignSelect.append($('<option/>').attr({value:kinds[i].id}).text(kinds[i].name));
				}
			}
			$('<form class="form-inline" method="post" action="deleteKind.do"/>')
				.append($('<input type="hidden" name="id"/>').val(id))
				.append($('<input type="hidden" name="name"/>').val(name))
				.append(reassignSelect)
				.append($('<button type="button" class="btn"/>').text(
						Tidbits.i18n('cancel.displayName')).click(handleDeleteKindCancelClick))
				.append($('<button type="submit" class="btn btn-danger"/>').text(
						Tidbits.i18n('delete.displayName')))
				.submit(handleKindDeleteSubmit)
				.appendTo(cell);
			$('<div class="alert alert-info"/>')
				.text(Tidbits.i18n('delete.kind.reassign.caption'))
				.appendTo(cell);
			
		}
	};
	
	handleKindEditClick = function(event) {
		event.preventDefault();
		var me = $(this);
		var kindId = me.attr('data-id');
		var form = $('<form class="form-inline" method="post" action="saveKind.do"/>')
				.append($('<input type="hidden" name="id"/>').val(kindId))
				.append($('<input type="text" name="name" autofocus="autofocus"/>').val(me.text()))
				.append($('<button type="submit" class="btn btn-primary"/>').text(
						Tidbits.i18n('save.displayName')))
				.append($('<button type="button" class="btn"/>').text(
						Tidbits.i18n('delete.kind.displayName')).click(handleDeleteKindClick))
				.submit(handleKindEditSubmit);
		me.html(form);
		me.unbind('click');
	};
	
	var addManageTidbitKindRow = function(tbody, data) {
		$('<tr/>').append(
				$('<td/>').attr({'data-id':data.id})
					.click(handleKindEditClick)
					.text(data.name)
				).appendTo(tbody);
	};
	
	
	var handleAddNewKind = function(event) {
		event.preventDefault();
		var tbody = $('#kind-table-body');
		addManageTidbitKindRow(tbody, {id:'', name:''});
		tbody.find('td:last').click();
	};
	
	// [{id:1, name:Password},...]
	var populateKinds = function(data) {
		if ( data === undefined || !Array.isArray(data) ) {
			return;
		}
		Tidbits.Runtime.kinds.setKinds(data);
		var select = $('#add-tidbit-kind');
		var tbody = $('#kind-table-body');
		var i, len;
		for ( i = 0, len = data.length; i < len; i++ ) {
			$('<option />').attr({'value': data[i].id}).text(data[i].name).appendTo(select);
			addManageTidbitKindRow(tbody, data[i]);
		}
	};
	
	// private init
	(function() {
		self.element.find('button.cancel').click(function(e) {
			e.preventDefault();
			if ( self.bit === undefined ) {
				// dismiss the editor
				self.hide();
			} else {
				// go to listing
				self.displayList();
			}
		});
		self.element.find('button[data-dismiss=editor]').click(function(e) {
			e.preventDefault();
			self.hide();
		});

	    jQuery.getJSON('kinds.json', populateKinds);
	    
	    $('#add-new-kind-btn').click(handleAddNewKind);

	    $('#manage-categories-btn').click(function(e) {
	    	e.preventDefault();
	    	self.displayKinds();
	    });
	    $('#manage-tidbit-btn').click(function(e) {
	    	e.preventDefault();
	    	self.displayForm();
	    });
	    
	    $('#delete-tidbit-btn').click(function(e) {
	    	e.preventDefault();
	    	var me = $(this);
	    	if ( me.attr('data-really') === 'yes' ) {
	    		self.resetDeleteButton(me);
	    		self.deleteTidbit($('#edit-tidbit-id').val());
	    	} else {
	    		// confirm
	    		me.attr('data-really', 'yes');
	    		me.text(Tidbits.i18n('delete.confirm.displayName'));
	    	}
	    });
	    
	    $('#add-tidbit-value-btn').click(function(e) {
	    	e.preventDefault();
	    	self.displayForm(Tidbits.emptyCrumb());
	    });
	    
	    $('#tidbit-form').submit(function(event) {
			event.preventDefault();
			$(this).ajaxSubmit({
				dataType: 'json',
				error : function(xhr, statusText, error) {
					Tidbits.defaultAjaxErrorHandler('tidbit.save.error.title', xhr, statusText, error);
				},
				success : function(data) {
					self.postedForm(data);
				}
			});
		});
	    
	    //flipper.find('button[data-dismiss=editor]').click(handleBottomFlip);
	})();
};

Tidbits.Class.Editor.prototype = Object.create(Tidbits.Class.ModalWidget.prototype);
Tidbits.Class.Editor.prototype.constructor = Tidbits.Class.Editor;

Tidbits.Class.Editor.prototype.deleteTidbit = function(id) {
	id = Number(id);
	var self = this;
	jQuery.ajax({
		type: 'POST',
		url: 'deleteTidbit.do', 
		dataType: 'json',
		data: {id:id}, 
		success: function(data, statusText) {
			if ( data.success === true ) {
				self.bit.removeDetail(id);
				// TODO: implement updateBit method, instead of recreate entire list each time?
				self.setBit(self.bit);
				self.bit.refresh();		
				// TODO: if no more details, should Bit be removed, and then hide editor?
				self.displayList();
			}
		},
		error: function(xhr, statusText, error) {
			Tidbits.defaultAjaxErrorHandler('tidbit.delete.error.title', xhr, statusText, error);
		}
	});
};
	
Tidbits.Class.Editor.prototype.postedForm = function(data) {
	if ( this.bit !== undefined ) {
		this.bit.addDetails(data.tidbits[0]);			
		// TODO: implement updateBit method, instead of recreate entire list each time?
		this.setBit(this.bit);
		this.bit.refresh();
	} else {
		this.setBit(Tidbits.Runtime.bits.addBit(data));
	}
	this.displayList();
};

Tidbits.Class.Editor.prototype.displayKinds = function() {
	this.displayFace('bottom');
};

Tidbits.Class.Editor.prototype.displayForm = function(crumb, kindId) {
	if ( crumb !== undefined ) {
		$('#edit-tidbit-id').val(crumb.id);
		$('#add-tidbit-data').val(crumb.value);
		$('#add-tidbit-comments').val(crumb.comment);
		this.element.find('.edit-only').toggleClass('hidden', (crumb.id === undefined || crumb.id.length < 1));
		$('#add-tidbit-created-by').text(crumb.createdBy);
	}
	var header = this.element.find('.front .header h3');
	var deleteBtn = $('#delete-tidbit-btn');
	this.resetDeleteButton(deleteBtn);
	if ( crumb !== undefined && crumb.id !== '' ) {
		deleteBtn.show();
		header.text(Tidbits.i18n('edit.tidbit.title'));
	} else {
		deleteBtn.hide();
		header.text(Tidbits.i18n('new.tidbit.title'));
	}
	$('#add-tidbit-name').val((this.bit === undefined ? '' : this.bit.getName()));
	
	var kindOpt;
	if ( kindId !== undefined ) {
		kindOpt = $('#add-tidbit-kind option[value="'+kindId+'"]');
	} else {
		kindOpt = $('#add-tidbit-kind option:first-child');
	}
	if ( kindOpt.size() > 0 ) {
		kindOpt.get(0).selected = true;
	}
	this.displayFace('front');
};

Tidbits.Class.Editor.prototype.displayList = function() {
	this.displayFace('left');
};

Tidbits.Class.Editor.prototype.editCrumb = function(id, kindId) {
	var crumb = undefined;
	var info = this.bit.getInfo();
	var i = 0, len = 0;
	
	var findCrumb = function(kindId) {
		var result = undefined;
		var group = info[kindId];
		if ( group !== undefined ) {
			for ( len = group.length; i < len; i++ ) {
				if ( group[i].id === id ) {
					result = group[i];
					break;
				}
			}
		}
		return result;
	};
	
	// try first via kindId, if available
	if ( kindId !== undefined ) {
		crumb = findCrumb(kindId);
	}
	if ( crumb === undefined ) {
		// global search
		for ( kindId in info ) {
			crumb = findCrumb(kindId);
			if ( crumb !== undefined ) {
				break;
			}
		}
	}
	if ( crumb === undefined ) {
		// TODO: no crumb found... alert?
		return;
	}
	this.displayForm(crumb, kindId);
};

Tidbits.Class.Editor.prototype.setBit = function(bit) {
	this.bit = bit;
	
	// update view according to model
	var info = (bit === undefined ? {} : bit.getInfo());
	
	this.element.find('.left .header h3').text(
			bit === undefined ? Tidbits.i18n('manage.tidbit.title') : bit.getName());
	
	// populate list
	var table = $('#bit-edit-listing').empty();
	var kindId = undefined;
	var kindValues = undefined;
	var i, len;
	var self = this;
	for ( kindId in info ) {
		$('<h4/>').text(
			Tidbits.Runtime.kinds.getName(kindId)).appendTo(table);
		kindValues = info[kindId];
		for ( i = 0, len = kindValues.length; i < len; i++ ) {
			$('<div class="detail"/>')
				.attr({
					'data-id':kindValues[i].id,
					'data-kindId':kindId
					})
				.text(kindValues[i].value)
				.click(function(e) {
					e.preventDefault();
					var me = $(this);
					self.editCrumb(Number(me.attr('data-id')), Number(me.attr('data-kindId')));
				})
				.appendTo(table);
		}
	};
};

/**
 * Display the editor for a specific Bit.
 * 
 * @param {Tidbits.Class.Bit} bit the Bit to edit
 */
Tidbits.Class.Editor.prototype.edit = function(bit) {
	this.setBit(bit);
	if ( bit === undefined ) {
		// creating a new one from scratch
		this.displayForm(Tidbits.emptyCrumb());
	} else {
		// editing existing bit
		this.displayList();
	}
	this.show();
};

Tidbits.Class.Editor.prototype.resetDeleteButton = function(elm) {
	var btn = (elm === undefined ? $('#delete-tidbit-btn') : $(elm));
	btn.removeAttr('data-really');
	btn.html('<i class="icon-trash"></i> ' +Tidbits.i18n('delete.displayName'));
};

Tidbits.Class.Editor.prototype.didShow = function() {
	$('#add-tidbit-name').focus();
};
