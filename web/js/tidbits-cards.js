/**
 * The Tidbits namespace.
 * 
 * @namespace namespace
 */
var Tidbits = {

	/**
	 * Namespace for classes. 
	 * @namespace
	 */
	Class: {},
	
	/**
	 * Namespace for runtime state. 
	 * @namespace
	 */
	Runtime: {},

	/**
	 * Names to use for user-interaction events.
	 * 
	 * <p>On non-touch devices these equate to <em>mousedown</em>, 
	 * <em>mouseup</em>, etc. On touch-enabled devices these equate to
	 * <em>touchstart</em>, <em>touchend</em>, etc.</p>
	 */
	touchEventNames : {
		start: "mousedown",
		move: "mousemove",
		end: "mouseup",
		cancel: "touchcancel"
	},
	
	/**
	 * Flag indicating if the client supports touch events.
	 * 
	 * @returns {Boolean} <em>true</em> if have touch support
	 */
	hasTouchSupport : (function() {
		if ( 'createTouch' in document) { // True on the iPhone
			return true;
		}
		try {
			var event = document.createEvent('TouchEvent');
			return !!event.initTouchEvent;
		} catch( error ) {
			return false;
		}
	}()),
	
	hiRes : (window.devicePixelRatio === undefined ? false : window.devicePixelRatio > 1),

	anyEvent : function(event) {
		event.preventDefault();
		if ( event.targetTouches !== undefined ) {
			return (event.targetTouches.length > 0 ? event.targetTouches[0] : undefined);
		}
		return event;
	},
	
	/**
	 * Display an error alert message.
	 * 
	 * @param {Object} contents the HTML to display in the alert
	 */
	errorAlert : function(contents) {
		$('body').append(
				$('<div class="alert alert-error"><button class="close" data-dismiss="alert">&times;</button></div>')
				.append(contents));
	},
	
	/**
	 * Render an i18n message.
	 * 
	 * <p>This is simply a shortcut for calling <code>Tidbits.Runtime.i18n.i18n()</code>.</p>
	 * 
	 * @returns {String} the message
	 */
	i18n : function() {
		return Tidbits.Runtime.i18n.i18n.apply(Tidbits.Runtime.i18n, arguments);
	}
	
};

Tidbits.touchEventNames = (function() {
	return Tidbits.hasTouchSupport ? {
			start: "touchstart",
			move: "touchmove",
			end: "touchend",
			cancel: "touchcancel"
		} : Tidbits.touchEventNames;
}());

/**
 * Simple implementation of a 2D CSS transform matrix.
 * 
 * @class
 * @returns {Tidbits.Class.Matrix}
 */
Tidbits.Class.Matrix = function() {
	// TODO: cross-browser test for -o, -moz -ie prefix
	var supportFloat32Array = "Float32Array" in window;
	this.matrix = (supportFloat32Array ? new Float32Array(6) : [1,0,0,1,0,0]);
};

/**
 * Cross-browser support for various matrix properties.
 */
Tidbits.Class.Matrix.prototype.support = (function() {
	// adapted from jquery.transform2d.js
	var divStyle = document.createElement("div").style;
	var suffix = "Transform";
	var testProperties = [
		"O" + suffix,
		"ms" + suffix,
		"Webkit" + suffix,
		"Moz" + suffix
	];
	var eventProperties = ["oTransitionEnd","MSTransitionEnd","webkitTransitionEnd","transitionend"];
	var transitionProperties = ["OTransition","MSTransition","WebkitTransition","MozTransition"];
	var transitionTransform = ["-o-transform", "-ms-transform", "-webkit-transform", "-moz-transform"];
	var tProp = "Transform", 
		trProp = "Transition",
		trTransform = "transform",
		trEndEvent = "transitionEnd";
	var i = testProperties.length;
	while ( i-- ) {
		if ( testProperties[i] in divStyle ) {
			tProp = testProperties[i];
			trProp = transitionProperties[i];
			trTransform = transitionTransform[i];
			trEndEvent = eventProperties[i];
			break;
		}
	}
	
	return {
		use3d : (window.devicePixelRatio !== undefined && window.devicePixelRatio > 1 ? true : false),
		tProp : tProp,
		trProp : trProp,
		trTransform : trTransform,
		trEndEvent : trEndEvent
	};
})();

/**
 * Generate a CSS matrix3d() function string from the current matrix.
 * 
 * @returns {String} the CSS matrix3d() function
 */
Tidbits.Class.Matrix.prototype.toMatrix3D = function() {
	return "matrix3d(" 
			+ this.matrix[0] +"," +this.matrix[1] +",0,0,"
			+ this.matrix[2] +',' +this.matrix[3] +",0,0,"
			+ "0,0,1,0,"
			+ this.matrix[4] +',' +this.matrix[5] +",0,1)";
};

/**
 * Generate a CSS matrix() function string from the current matrix.
 * 
 * @returns {String} the CSS matrix() function
 */
Tidbits.Class.Matrix.prototype.toMatrix2D = function() {
	return "matrix(" 
			+ this.matrix[0] +"," +this.matrix[1] +","
			+ this.matrix[2] +',' +this.matrix[3] +","
			+ this.matrix[4] +',' +this.matrix[5] 
			+")";
};

/**
 * Set the z-axis rotation of the matrix.
 * 
 * @param {Number} angle the rotation angle, in radians
 */
Tidbits.Class.Matrix.prototype.setRotation = function(angle) {
	// TODO this clears any scale, should we care?
	var a = Math.cos(angle);
	var b = Math.sin(angle);
	this.matrix[0] = this.matrix[3] = a;
	this.matrix[1] = (0-b);
	this.matrix[2] = b;
};

/**
 * Set a uniform x,y scaling factor of the matrix.
 * @param {Number} s the scale factor
 */
Tidbits.Class.Matrix.prototype.setScale = function(s) {
	// TODO this clears any rotation, should we care?
	this.matrix[0] = s;
	this.matrix[3] = s;
};

/**
 * Set the current 2D translate of the matrix.
 * 
 * @param {Number} x the x offset
 * @param {Number} y the y offset
 */
Tidbits.Class.Matrix.prototype.setTranslation = function(x, y) {
	this.matrix[4] = x;
	this.matrix[5] = y;
};

/**
 * Append a 2D translate to the current matrix.
 * 
 * @param {Number} x the x offset
 * @param {Number} y the y offset
 */
Tidbits.Class.Matrix.prototype.translate = function(x, y) {
	this.matrix[4] += x;
	this.matrix[5] += y;
};

/**
 * Apply the matrix transform to an element.
 * 
 * <p>Hi hi-res displays, the {@link #toMatrix3D()} transform is used,
 * otherwise {@link #toMatrix2D()} is used. Found that legibility of 
 * text was too blurry on older displays when 3D transform was applied,
 * but 3D transform provide better performance.</p>
 * 
 * @param {Element} elm the element to apply the transform to
 */
Tidbits.Class.Matrix.prototype.apply = function(elm) {
	var m = (this.support.use3d === true ? this.toMatrix3D() : this.toMatrix2D());
	elm.style[this.support.tProp] = m;
};

/**
 * Apply the matrix transform to an element, with an "ease out" transition.
 * 
 * <p>Calls {@link #apply(elm)} internally.</p>
 * 
 * @param {Element} elm the element to apply the transform to
 * @param {String} timing the CSS timing function to use
 * @param {String} duration the CSS duration to use
 * @param {Function} finished an optional callback function to execute when 
 * the animation completes
 * 
 */
Tidbits.Class.Matrix.prototype.animate = function(elm, timing, duration, finished) {
	var self = this;
	elm.addEventListener(self.support.trEndEvent,  function(event) {
		elm.style[self.support.trProp] = '';
	 }, false );
	var cssValue = self.support.trTransform 
		+' ' 
		+(duration !== undefined ? duration : '0.3s')
		+' ' 
		+(timing !== undefined ? timing : 'ease-out');
	elm.style[self.support.trProp] = cssValue;
	this.apply(elm);
};


/**
 * Apply the matrix transform to an element, with an "ease out" transition.
 * 
 * <p>Calls {@link #apply(elm)} internally.</p>
 * 
 * @param {Element} elm the element to apply the transform to
 */
Tidbits.Class.Matrix.prototype.easeOut = function(elm) {
	this.animate(elm, 'ease-out');
};

/**
 * A Bit of information, as a collection of Tidbits with a common name.
 * 
 * @class
 * @param {Object} data the info model data
 * @param {Tidbits.Class.Bits} the manager
 * @param {Element} container the container element to use to hold the DOM elements for this Bit
 * @returns {Tidbits.Class.Bit}
 */
Tidbits.Class.Bit = function(data, bits, container) {
	// {"id":-6, "name":"Website", "kind":"URL", "value":"http://my.web.site/", "createdBy":"test", "creationDate":"9 May 2012", "modifyDate":"9 May 2012"},
	this.bits = bits;
	this.name = data.name;
	this.info = {}; // {URL: [{id:-6, value:'http://my.web.site/'}]}
	this.refreshElement = $('<button class="action"><i class="action ticon icon-refresh-t">\uf021</i></button>');
	this.addElement = $('<button class="action"><i class="action icon-plus"></i></button>');
	this.element = $(container)
		.append(this.addElement)
		.append(this.refreshElement)
		.append($('<h2/>').text(data.name))
		;
	this.listElement = $('<dl/>');
	this.element.append(this.listElement);
	this.addDetails(data);

	var self = this;

	var handleRefresh = function(e) {
		e.preventDefault();
		e.stopPropagation();
		var image = self.refreshElement.find('i');
		image.addClass('hit');
		jQuery.getJSON('search.json?query=name:"' +encodeURIComponent(self.name) +'"', function(data) {
			self.bits.refreshData(data);
			
			// in case the fetch was super quick, wait before removing the "hit" class
			// so it animates at least a little
			setTimeout(function() {
				image.removeClass('hit');
			}, 500);
		});
	};
	
	// open the "add" modal, with the name pre-populated
	var handleAdd = function(e) {
		e.preventDefault();
		e.stopPropagation();
		$('#add-tidbit-name').val(self.name);
		$('#add-tidbit-modal').modal('show');
		$('#add-tidbit-kind').focus();
	};

	this.refreshElement.get(0).addEventListener(Tidbits.touchEventNames.start, handleRefresh, false);
	this.addElement.get(0).addEventListener(Tidbits.touchEventNames.start, handleAdd, false);
};

/**
 * Add info details to the info model.
 * 
 * @param {Object} data the info data to insert
 */
Tidbits.Class.Bit.prototype.addDetails = function(data) {
	if ( data === undefined || data.kind === undefined ) {
		return;
	}
	var info = this.info[data.kind];
	if ( info === undefined ) {
		this.info[data.kind] = [];
		info = this.info[data.kind];
	}
	var i, len;
	for ( i = 0, len = info.length; i < len; i++ ) {
		if ( info[i].id === data.id ) {
			// update existing value
			info[i].value = data.value;
			return;
		}
	}
	info.push({id:data.id, value:data.value});
};

/**
 * Call-back function for extending classes to implement.
 * 
 * <p>This method will be called by the {@link #insertIntoDocument(Element)}
 * function.</p>
 * 
 * @param {Element} container the DOM element this Bit will be inserted into
 */
Tidbits.Class.Bit.prototype.willInsertIntoDocument = function(container) {};

/**
 * Refresh own DOM structure and then insert into the container DOM element.
 * 
 * @param {Element} container the DOM element to insert this Bit into
 */
Tidbits.Class.Bit.prototype.insertIntoDocument = function(container) {
	// create list elements
	this.refresh();
	this.willInsertIntoDocument(container);
	$(container).append(this.element);
};

/**
 * Refresh own DOM structure.
 * 
 * <p>This will delete all children elements of this Bit's container
 * element and re-create them using the current model info.</p>
 */
Tidbits.Class.Bit.prototype.refresh = function() {
	this.listElement.empty();
	var prop = undefined;
	var i, len, details;
	for ( prop in this.info ) {
		this.listElement.append($('<dt/>').text(prop));
		details = this.info[prop];
		for ( i = 0, len = details.length; i < len; i++ ) {
			this.listElement.append($('<dd/>').text(details[i].value));
		}
	}
};

/**
 * Get the info model object.
 * 
 * @returns {Object} the bit info
 */
Tidbits.Class.Bit.prototype.getInfo = function() {
	return this.info;
};

/**
 * A single "card" object in the Tidbits UI.
 * 
 * <p>A "card" is a group of Tidbits that share a common name.</p>
 * 
 * @param {Object} data the JSON data representing a single Tidbit
 * @param {Tidbits.Class.Bits} bits the manager
 * @returns {Tidbits.Class.Card}
 * @class
 */
Tidbits.Class.Card = function(data, bits) {
	var superclass = Tidbits.Class.Bit;
	superclass.call(this, data, bits, '<div class="tidbit card"/>');

	this.maxWidth = 240;
	this.maxHeight = 180;
	this.maxAngle = Math.PI / 6.0;
	this.matrix = new Tidbits.Class.Matrix();
	this.fac = false; // front and center, used to toggle touch behavior

	var self = this;
	var el = this.element;
	var elm = el.get(0);
	var p1 = {x:0, y:0, t:0};
	var p2 = {x:0, y:0, t:0};
	var minInertiaDistance = 0;
	var lastTapEnd = 0;
	
	// momentum?
	var momenumHowMuch = 30;  // change this for greater or lesser momentum
	var momentumMinDrift = 0; // minimum drift after a drag move
	
	var cardTranslate = function(pageX, pageY, timeStamp, ease) {
		if ( pageX >= 0 && pageY >= 0 ) {
			var deltaX = pageX - p1.x;
			var deltaY = pageY - p1.y;
			self.matrix.translate(deltaX, deltaY);
			if ( ease === true ) {
				self.matrix.easeOut(elm);
			} else {
				self.matrix.apply(elm);
			}
		}
	};
	
	var touchMove = function(e) {
		var event = Tidbits.anyEvent(e);
		if ( event === undefined ) {
			return;
		}
		e.stopPropagation();
		
		var pageX = event.pageX;
		var pageY = event.pageY;
		cardTranslate(pageX, pageY, e.timeStamp);
		
		p2.x = p1.x, p2.y = p1.y, p2.t = p1.t;
		p1.x = pageX;
		p1.y = pageY;
		p1.t = e.timeStamp;
	};
	
	var touchStart = function(e) {
		if ( self.fac === true ) {
			// this card is front and center, so don't deail with tracking touches
			return;
		}
		var event = Tidbits.anyEvent(e);
		if ( event === undefined ) {
			return;
		}
		e.stopPropagation();
		bits.popToTop(self);
		var pageX = event.pageX < 0 ? 0 : event.pageX;
		var pageY = event.pageY < 0 ? 0 : event.pageY;
		p1 = {x:pageX, y:pageY, t:e.timeStamp};
		p2 = {x:pageX, y:pageY, t:e.timeStamp};
		elm.addEventListener(Tidbits.touchEventNames.move, touchMove, false);
	};
	
	var touchEnd = function(e) {
		if ( e === undefined ) {
			return;
		}
		elm.removeEventListener(Tidbits.touchEventNames.move, touchMove, false);
		e.preventDefault();
		e.stopPropagation();

		// test for double-tap
		var tapTimeDiff = (lastTapEnd === 0 ? 0 : e.timeStamp - lastTapEnd);
		lastTapEnd = e.timeStamp;
		if ( tapTimeDiff > 0 && tapTimeDiff < 500 ) {
			// double tap here, so toggle the front and center aspect of this card
			if ( self.fac === true ) {
				self.awayWithYou();
			} else {
				self.frontAndCenter();
			}
			return;
		}
		
		var pageX = p1.x;
		var pageY = p1.y;
		var distX = Math.abs(pageX - p2.x);
		var distY = Math.abs(pageY - p2.y);
        var dDist = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
        var dSpeed, dVelX, dVelY, newX, newY;
		if ( dDist > minInertiaDistance ) {
			dSpeed = Math.round((dDist / (p1.t - p2.t)) * 100) / 100;
			dVelX = (momentumMinDrift+(Math.round(distX*dSpeed*momenumHowMuch / (distX+distY))));
			dVelY = (momentumMinDrift+(Math.round(distY*dSpeed*momenumHowMuch / (distX+distY))));

			newX = pageX + (pageX > p2.x ? dVelX : -dVelX);
			newY = pageY + (pageY > p2.y ? dVelY : -dVelY);
			cardTranslate(newX, newY, e.timeStamp, true);
		}
	};

	elm.addEventListener(Tidbits.touchEventNames.start, touchStart, false);
	elm.addEventListener(Tidbits.touchEventNames.end, touchEnd, false);
};

Tidbits.Class.Card.prototype = Object.create(Tidbits.Class.Bit.prototype);
Tidbits.Class.Card.prototype.constructor = Tidbits.Class.Card;

Tidbits.Class.Card.prototype.frontAndCenter = function() {
	var viewWidth = window.innerWidth;
	var viewHeight = window.innerHeight;
	var maxSize = {width:viewWidth - 40, height:viewHeight - 40};
	var aSize = {width:this.element.width(), height:this.element.height()};
	
	var scale = 1.0;
	if ( aSize.width > 0 && aSize.height > 0.0 ) {
		var dw = maxSize.width / aSize.width;
		var dh = maxSize.height / aSize.height;
		scale = dw < dh ? dw : dh;
	}
	/*
	var fitSize = {
			width:Math.min(Math.floor(maxSize.width), Math.ceil(aSize.width * scale)),
			height:Math.min(Math.floor(maxSize.height), Math.ceil(aSize.height * scale))
			};
	console.log("scale = " +scale + ", fitSize = " +fitSize.width +"x" +fitSize.height);
	*/
	
	// create a temp Matrix to transform the card to, when unfocus() we can restore the card's
	// former Matrix
	var m = new Tidbits.Class.Matrix();
	m.setScale(scale);
	m.setTranslation((viewWidth - aSize.width) / 2, (viewHeight - aSize.height) / 2);
	m.easeOut(this.element.get(0));
	this.element.addClass('fac');
	this.fac = true;
};

Tidbits.Class.Card.prototype.awayWithYou = function() {
	this.matrix.easeOut(this.element.get(0));
	this.element.removeClass('fac');
	this.fac = false;
};

Tidbits.Class.Card.prototype.willInsertIntoDocument = function(container) {
	var docWidth = $(window).width();
	var docHeight = $(window).height();
	var width = this.maxWidth;
	var height = this.maxHeight;
	
	// start from random position on circle centered in page
	var r = Math.max(docWidth, docHeight) + width;
	var randomAngle = Math.PI * 2 * Math.random();
	var startX = (docWidth / 2.0) + (Math.cos(randomAngle) * r);
	var startY = (docWidth / 2.0) + (Math.sin(randomAngle) * r);
	var endX = this.bits.margins.left + Math.floor(Math.random() * (
			docWidth - this.bits.margins.left - this.bits.margins.right - width));
	var endY = this.bits.margins.top + Math.floor(Math.random() * (
			docHeight - this.bits.margins.top - this.bits.margins.bottom - height));
	var endAngle = this.maxAngle * 2 * Math.random() - this.maxAngle;
	this.element.css('transform', 'matrix(1,0,0,1,'+startX +','+startY+')');
	
	var el = this.element;
	var elm = el.get(0);
	var matrix = this.matrix;
	setTimeout(function() {
		matrix.setRotation(endAngle);
		matrix.setTranslation(endX, endY);
		matrix.easeOut(elm);
	}, 100);
};

/**
 * A collection of Bit objects.
 * 
 * @class
 * @param {Element} container to insert all card objects into
 * @param {Object} margins top, left, right, bottom margins
 * @returns {Tidbits.Class.Bits}
 */
Tidbits.Class.Bits = function(container, margins) {
	var bits = {};
	var bitz = [];
	var cardsContainer = $(container);
	var self = this;
	var kinds = [];
	var cardMode = false;
	
	var populateTidbits = function(data, ascending) {
		if ( data === undefined || !jQuery.isArray(data.tidbits) ) {
			return;
		}
		var i, len;
		var tidbit;
		for ( i = 0, len = data.tidbits.length; i < len; i++ ) {
			tidbit = data.tidbits[i];
			if ( bits[tidbit.name] === undefined ) {
				bits[tidbit.name] = (cardMode === true 
						? new Tidbits.Class.Card(tidbit, self)
						: new Tidbits.Class.Bit(tidbit, self, '<div class="tidbit"/>'));
				if ( ascending === true ) {
					bitz.push(bits[tidbit.name]);
				} else {
					bitz.splice(0, 0, bits[tidbit.name]);
				}
			} else {
				bits[tidbit.name].addDetails(tidbit);
			}
		}
		if ( cardMode === true ) {
			for ( i = 0, len = bitz.length; i < len; i++ ) {
				var zIndex = Number(bitz[i].element.css('z-index'));
				if ( isNaN(zIndex) || zIndex < 1 ) { // the card may already be in the DOM
					bitz[i].element.css('z-index', (i+1));
					bitz[i].insertIntoDocument(cardsContainer);
				} else {
					bitz[i].refresh();
					bitz[i].element.css('z-index', (i+1));
				}
			}
		} else {
			// for non-card mode, insert in reverse order, so newest on top
			for ( i = bitz.length - 1; i >= 0; i-- ) {
				var zIndex = Number(bitz[i].element.css('z-index'));
				if ( isNaN(zIndex) || zIndex < 1 ) { // the card may already be in the DOM
					bitz[i].element.css('z-index', 1);
					bitz[i].insertIntoDocument(cardsContainer);
				} else {
					bitz[i].refresh();
				}
			}
			
		}
	};
	
	var handleKindEditClick = undefined;
	
	var handleKindEditSubmit = function(event) {
		event.preventDefault();
		event.stopPropagation();
		var me = $(this);
		var id = me.find('input[name=id]').val();
		var name = me.find('input[name=name]').val();
		me.ajaxSubmit(function(data, statusText) {
			if ( 'success' === statusText && Array.isArray(data) && data.length > 0 ) {
				// restore click to edit handler
				me.parent().bind('click', handleKindEditClick);
				
				// set @data-id, in case submitted new value
				var newId = data[0].id;
				me.parent().attr({'data-id':newId});
				
				// replace form with text
				me.replaceWith(name);
				
				if ( id !== '' ) {
					// update existing category
					$('option[value="'+id+'"]').text(name);
				} else {
					// add new category
					$('#add-tidbit-kind').append($('<option/>').attr({value:newId}).text(name));
				}
			} else {
				Tidbits.errorAlert('<h4 class="alert-heading">' 
						+Tidbits.i18n('kind.save.error.title') 
						+'</h4>' +statusText);
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
		form.ajaxSubmit(function(data, statusText) {
			if ( 'success' === statusText ) {
				// delete row
				form.parent().parent().remove();
				
				// delete <option>
				$('option[value="'+id+'"]').remove();
			} else {
				Tidbits.errorAlert('<h4 class="alert-heading">' 
						+Tidbits.i18n('kind.save.error.title') 
						+'</h4>' +statusText);
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
			$(form).replaceWith(name);
			var reassignSelect = $('<select name="reassign"/>');
			$('#kind-table-body td').each(function() {
				var td = $(this);
				var cellId = td.attr('data-id');
				if ( cellId !== id ) {
					reassignSelect.append($('<option/>').attr({value:cellId}).text(td.text()));
				}
			});
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
			$('<div class="help-block"/>')
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
		if ( data === undefined || !jQuery.isArray(data) ) {
			return;
		}
		kinds = data;
		var select = $('#add-tidbit-kind');
		var tbody = $('#kind-table-body');
		var i, len;
		for ( i = 0, len = data.length; i < len; i++ ) {
			$('<option />').attr({'value': data[i].id}).text(data[i].name).appendTo(select);
			addManageTidbitKindRow(tbody, data[i]);
		}
	};
	
	var replaceAllTidbitsWithSearchResults = function(data) {
		// animate bits out, if they have a matrix
		var i , len, m;
		
		var reset = function() {
			bits = {};
			bitz = [];
			populateTidbits(data);
		};
		
		if ( cardMode === true ) {
			var screenHeight = $(window).height();
			var oldBitz = bitz; // local reference for animation complete
			var onComplete = function() {
				$(this).remove();
			};
			for ( i = 0, len = oldBitz.length; i < len; i++ ) {
				m = oldBitz[i].matrix;
				if ( m !== undefined ) {
					m.translate(0, screenHeight + 20);
					m.animate(oldBitz[i].element.get(0), 'ease-in', '0.3s', onComplete);
				}
			}
			setTimeout(reset, 250);
		} else {
			cardsContainer.children('.tidbit').remove();
			reset();
		}
	};
	
	// private init
	(function() {
		var screenWidth = $(window).width();
		console.log("Screen width: " +screenWidth);
		if ( screenWidth > 480 ) {
			cardMode = true;
			
			// prevent elastic scrolling
			document.body.addEventListener('touchmove', function(event) {
			  event.preventDefault();
			}, false);
		};
		
		jQuery.getJSON('search.json', populateTidbits);
		
	    $('#add-tidbit-modal').on('show', function () {
	    	if ( kinds.length === 0 ) {
	    		jQuery.getJSON('kinds.json', populateKinds);
	    	}
	    	$('#add-tidbit-name').focus();
	    });
	    
	    $('#tidbit-form').submit(function(event) {
			event.preventDefault();
			$('#add-tidbit-modal').modal('hide');
			$(this).ajaxSubmit(function(data, statusText) {
				if ( 'success' === statusText ) {
					populateTidbits(data, true);
				} else {
					Tidbits.errorAlert('<h4 class="alert-heading">' 
							+Tidbits.i18n('tidbit.save.error.title') 
							+'</h4>' +statusText);
				}
			});
		});
	    
	    $('#add-new-kind-btn').click(handleAddNewKind);
	    
	    var handleModalFlip = function(event) {
	    	// flip the card around
	    	event.preventDefault();
	    	var el = $('#add-tidbit-modal .flipper');
	    	var currTransform = el.css('transform');
	    	if ( currTransform === 'none' ) {
	    		el.css('transform', 'rotateY(180deg)');
	    	} else {
	    		el.css('transform', '');
	    	}
	    };
	    $('#manage-categories-btn').click(handleModalFlip);
	    $('#manage-tidbit-btn').click(handleModalFlip);
	    
	    
	    $('#nav-search-tidbit-form').submit(function(event) {
			event.preventDefault();
			$(this).ajaxSubmit(function(data, statusText) {
				if ( 'success' === statusText ) {
					replaceAllTidbitsWithSearchResults(data);
				} else {
					Tidbits.errorAlert('<h4 class="alert-heading">' 
							+Tidbits.i18n('tidbit.search.error.title') 
							+'</h4>' +statusText);
				}
			});
	    });
	    if ( cardMode === true ) {
		    $('#nav-search-tidbit-form input[type=text]').focus();
	    }
	})();
	
	/**
	 * Move a specific Bit so it appears above all other bits,
	 * by adjusting its z-index value.
	 * 
	 * @param {Tidbits.Class.Bit} the bit to move to the top
	 */
	this.popToTop = function(bit) {
		var idx = bitz.indexOf(bit);
		var i, len;
		if ( idx !== -1 && ((idx + 1) !== bitz.length) ) {
			// move card to top of stack
			bitz.push(bitz.splice(idx, 1)[0]);
			
			// adjust z index of shuffled bits
			for ( i = idx, len = bitz.length; i < len; i++ ) {
				bitz[i].element.css('z-index', i);
			}
		}
	};
	
	this.refreshData = function(data, ascending) {
		populateTidbits(data, ascending);
	};
	
	this.margins = margins;
};

$(document).ready(function() {
	Tidbits.Runtime.i18n = new XwebLocaleClass();
	jQuery.getJSON('messages.json', function(data) {
		Tidbits.Runtime.i18n.initJson(data);
	});
	
	Tidbits.Runtime.bits = new Tidbits.Class.Bits('#card-container',
			{top:($('#navbar').height() + 20), left:20, right:20, bottom:20});
});
