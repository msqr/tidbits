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
 */
Tidbits.Class.Matrix.prototype.easeOut = function(elm) {
	var self = this;
	elm.addEventListener(self.support.trEndEvent,  function(event) {
		elm.style[self.support.trProp] = '';
	 }, false );
	elm.style[self.support.trProp] = self.support.trTransform +' 0.3s ease-out';
	this.apply(elm);
};


/**
 * A single "card" object in the Tidbits UI.
 * 
 * <p>A "card" is a group of Tidbits that share a common name.</p>
 * 
 * @param {Object} data the JSON data representing a single Tidbit
 * @param {Tidbits.Class.Cards} cards 
 * @returns {Tidbits.Class.Card}
 * @class
 */
Tidbits.Class.Card = function(data, cards) {
	// {"id":-6, "name":"Website", "kind":"URL", "value":"http://my.web.site/", "createdBy":"test", "creationDate":"9 May 2012", "modifyDate":"9 May 2012"},
	//var touchStart = undefined, touchMove = undefined, touchEnd = undefined;
	var handleRefresh = undefined, handleAdd = undefined;
	
	this.name = data.name;
	this.cards = cards;
	this.info = {}; // {URL: [{id:-6, value:'http://my.web.site/'}]}
	this.refreshElement = $('<button class="action"><i class="action ticon icon-refresh-t">\uf021</i></button>');
	this.addElement = $('<button class="action"><i class="action icon-plus"></i></button>');
	this.element = $('<div class="tidbit"/>')
		.append(this.addElement)
		.append(this.refreshElement)
		.append($('<h2/>').text(data.name))
		;
	this.listElement = $('<dl/>');
	this.element.append(this.listElement);
	this.addDetails(data);
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
		cards.popToTop(self);
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

	handleRefresh = function(e) {
		e.preventDefault();
		e.stopPropagation();
		var image = self.refreshElement.find('i');
		image.addClass('hit');
		jQuery.getJSON('search.json?query=name:"' +encodeURIComponent(self.name) +'"', function(data) {
			self.cards.refreshData(data);
			
			// in case the fetch was super quick, wait before removing the "hit" class
			// so it animates at least a little
			setTimeout(function() {
				image.removeClass('hit');
			}, 500);
		});
	};
	
	// open the "add" modal, with the name pre-populated
	handleAdd = function(e) {
		e.preventDefault();
		e.stopPropagation();
		$('#add-tidbit-name').val(self.name);
		$('#add-tidbit-modal').modal('show');
		$('#add-tidbit-kind').focus();
	};

	this.refreshElement.get(0).addEventListener(Tidbits.touchEventNames.start, handleRefresh, false);
	this.addElement.get(0).addEventListener(Tidbits.touchEventNames.start, handleAdd, false);
	elm.addEventListener(Tidbits.touchEventNames.start, touchStart, false);
	elm.addEventListener(Tidbits.touchEventNames.end, touchEnd, false);
};

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

Tidbits.Class.Card.prototype.addDetails = function(data) {
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

Tidbits.Class.Card.prototype.insertIntoDocument = function(container) {
	// create list elements
	this.refresh();
	
	var docWidth = $(window).width();
	var docHeight = $(window).height();
	var width = this.maxWidth;
	var height = this.maxHeight;
	
	// start from random position on circle centered in page
	var r = Math.max(docWidth, docHeight) + width;
	var randomAngle = Math.PI * 2 * Math.random();
	var startX = (docWidth / 2.0) + (Math.cos(randomAngle) * r);
	var startY = (docWidth / 2.0) + (Math.sin(randomAngle) * r);
	var endX = this.cards.margins.left + Math.floor(Math.random() * (
			docWidth - this.cards.margins.left - this.cards.margins.right - width));
	var endY = this.cards.margins.top + Math.floor(Math.random() * (
			docHeight - this.cards.margins.top - this.cards.margins.bottom - height));
	var endAngle = this.maxAngle * 2 * Math.random() - this.maxAngle;
	this.element.css('transform', 'matrix(1,0,0,1,'+startX +','+startY+')');
	$(container).append(this.element);
	
	var el = this.element;
	var elm = el.get(0);
	var matrix = this.matrix;
	setTimeout(function() {
		matrix.setRotation(endAngle);
		matrix.setTranslation(endX, endY);
		matrix.easeOut(elm);
	}, 10);
};

Tidbits.Class.Card.prototype.refresh = function() {
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

Tidbits.Class.Card.prototype.getInfo = function() {
	return this.info;
};

/**
 * A collection of card objects.
 * 
 * @class
 * @param {Element} container to insert all card objects into
 * @param {Object} margins top, left, right, bottom margins
 * @returns {Tidbits.Class.Cards}
 */
Tidbits.Class.Cards = function(container, margins) {
	var cards = {};
	var cardz = [];
	var cardsContainer = $(container);
	var self = this;
	var kinds = [];
	
	var populateTidbits = function(data, ascending) {
		if ( data === undefined || !jQuery.isArray(data.tidbits) ) {
			return;
		}
		var i, len;
		var tidbit;
		for ( i = 0, len = data.tidbits.length; i < len; i++ ) {
			tidbit = data.tidbits[i];
			if ( cards[tidbit.name] === undefined ) {
				cards[tidbit.name] = new Tidbits.Class.Card(tidbit, self);
				if ( ascending === true ) {
					cardz.push(cards[tidbit.name]);
				} else {
					cardz.splice(0, 0, cards[tidbit.name]);
				}
			} else {
				cards[tidbit.name].addDetails(tidbit);
			}
		}
		for ( i = 0, len = cardz.length; i < len; i++ ) {
			var zIndex = Number(cardz[i].element.css('z-index'));
			if ( isNaN(zIndex) || zIndex < 1 ) { // the card may already be in the DOM
				cardz[i].element.css('z-index', (i+1));
				cardz[i].insertIntoDocument(cardsContainer);
			} else {
				cardz[i].refresh();
				cardz[i].element.css('z-index', (i+1));
			}
		}
	};
	
	// [{id:1, name:Password},...]
	var populateKinds = function(data) {
		if ( data === undefined || !jQuery.isArray(data) ) {
			return;
		}
		kinds = data;
		var select = $('#add-tidbit-kind');
		var i, len;
		for ( i = 0, len = data.length; i < len; i++ ) {
			$('<option />').attr({'value': data[i].id}).text(data[i].name).appendTo(select);
		}
	};
	
	var replaceAllTidbitsWithSearchResults = function(data) {
		// TODO animate cards out
		cardsContainer.children('.tidbit').remove();
		cards = {};
		cardz = [];
		populateTidbits(data);
	};
	
	// private init
	(function() {
		jQuery.getJSON('search.json', populateTidbits);
		
	    $('#add-tidbit-modal').on('show', function () {
	    	if ( kinds.length === 0 ) {
	    		jQuery.getJSON('kinds.json', populateKinds);
	    	}
	    }).submit(function(event) {
			event.preventDefault();
			$(this).modal('hide').ajaxSubmit(function(data, statusText) {
				if ( 'success' === statusText ) {
					populateTidbits(data, true);
				} else {
					Tidbits.errorAlert('<h4 class="alert-heading">' 
							+Tidbits.i18n('tidbit.save.error.title') 
							+'</h4>' +statusText);
				}
			});
		});
	    
	    
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
	    }).find('input').focus();
	})();
	
	/**
	 * Move a specific card so it appears above all other cards,
	 * by adjusting its z-index value.
	 * 
	 * @param {Object} the card to move to the top
	 */
	this.popToTop = function(card) {
		var idx = cardz.indexOf(card);
		var i, len;
		if ( idx !== -1 && ((idx + 1) !== cardz.length) ) {
			// move card to top of stack
			cardz.push(cardz.splice(idx, 1)[0]);
			
			// adjust z index of shuffled cards
			for ( i = idx, len = cardz.length; i < len; i++ ) {
				cardz[i].element.css('z-index', i);
			}
		}
	};
	
	this.refreshData = function(data, ascending) {
		populateTidbits(data, ascending);
	};
	
	this.margins = margins;
};

$(document).ready(function() {
	// prevent elastic scrolling
	document.body.addEventListener('touchmove', function(event) {
	  event.preventDefault();
	}, false);
	
	Tidbits.Runtime.i18n = new XwebLocaleClass();
	jQuery.getJSON('messages.json', function(data) {
		Tidbits.Runtime.i18n.initJson(data);
	});
	
	var nav = $('#navbar');
	Tidbits.Runtime.cards = new Tidbits.Class.Cards('#card-container',
			{top:nav.height(), left:20, right:20, bottom:20});
});
