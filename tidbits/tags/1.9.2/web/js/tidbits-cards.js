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
				$('<div class="alert alert-error"><button class="close btn" data-dismiss="alert"><i class="icon-remove"></i></button></div>')
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
	},
	
	isLoginRequest : function(xhr) {
		if ( xhr.getResponseHeader("X-Login-Required") === "true" ) {
			return true;
		}
		return false;
	},
	
	defaultAjaxErrorHandler : function(msgKey, xhr, statusText, error) {
		if ( Tidbits.isLoginRequest(xhr) ) {
			// redirect to logon page
			setTimeout('window.location = "login.do"', 100);
		} else {
			Tidbits.errorAlert('<h4 class="alert-heading">' 
					+Tidbits.i18n(msgKey) 
					+'</h4>' 
					+(error.message !== undefined ? error.message : Tidbits.i18n('error.unknown')));
		}
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
	var supportFloat32Array = "Float32Array" in window;
	this.matrix = (function() {
			var result;
			if ( supportFloat32Array ) {
				result = new Float32Array(6);
				result[0] = 1;
				result[3] = 1;
			} else {
				result = [1,0,0,1,0,0];
			}
			return result;
		})();
	
	/**
	 * Cross-browser support for various matrix properties.
	 */
	this.support = (function() {
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
};

Tidbits.Class.Matrix.prototype = {
		
	constructor : Tidbits.Class.Matrix,
	
	/**
	 * Generate a CSS matrix3d() function string from the current matrix.
	 * 
	 * @returns {String} the CSS matrix3d() function
	 */
	toMatrix3D : function() {
		return "matrix3d(" 
				+ this.matrix[0] +"," +this.matrix[1] +",0,0,"
				+ this.matrix[2] +',' +this.matrix[3] +",0,0,"
				+ "0,0,1,0,"
				+ this.matrix[4] +',' +this.matrix[5] +",0,1)";
	},
	
	/**
	 * Generate a CSS matrix() function string from the current matrix.
	 * 
	 * @returns {String} the CSS matrix() function
	 */
	toMatrix2D : function() {
		return "matrix(" 
				+ this.matrix[0] +"," +this.matrix[1] +","
				+ this.matrix[2] +',' +this.matrix[3] +","
				+ this.matrix[4] +',' +this.matrix[5] 
				+")";
	},

	/**
	 * Set the z-axis rotation of the matrix.
	 * 
	 * @param {Number} angle the rotation angle, in radians
	 */
	setRotation : function(angle) {
		// TODO this clears any scale, should we care?
		var a = Math.cos(angle);
		var b = Math.sin(angle);
		this.matrix[0] = this.matrix[3] = a;
		this.matrix[1] = (0-b);
		this.matrix[2] = b;
	},
	
	/**
	 * Set a uniform x,y scaling factor of the matrix.
	 * @param {Number} s the scale factor
	 */
	setScale : function(s) {
		// TODO this clears any rotation, should we care?
		this.matrix[0] = s;
		this.matrix[3] = s;
	},
	
	/**
	 * Set the current 2D translate of the matrix.
	 * 
	 * @param {Number} x the x offset
	 * @param {Number} y the y offset
	 */
	setTranslation : function(x, y) {
		this.matrix[4] = x;
		this.matrix[5] = y;
	},
	
	/**
	 * Append a 2D translate to the current matrix.
	 * 
	 * @param {Number} x the x offset
	 * @param {Number} y the y offset
	 */
	translate : function(x, y) {
		this.matrix[4] += x;
		this.matrix[5] += y;
	},
	
	/**
	 * Get the current 2D translation value.
	 * 
	 * @returns {Object} object with x,y Number properties
	 */
	getTranslation : function() {
		return {x:this.matrix[4], y:this.matrix[5]};
	},
	
	/**
	 * Get the 2D distance between a location and this matrix's translation.
	 * 
	 * @param location a location object, with x,y Number properties
	 * @returns {Number} the calculated distance
	 */
	getDistanceFrom : function(location) {
		return Math.sqrt(Math.pow((location.x - this.matrix[4]), 2), 
				Math.pow((location.y - this.matrix[5]), 2));
	},
	
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
	apply : function(elm) {
		var m = (this.support.use3d === true ? this.toMatrix3D() : this.toMatrix2D());
		elm.style[this.support.tProp] = m;
	},
	
	/**
	 * Apply a one-time animation callback listener.
	 * 
	 * @param elm the element to add the one-time listener to
	 * @param finished
	 */
	animateListen : function(elm, finished) {
		var listener = undefined;
		var self = this;
		listener = function(event) {
			if ( event.target === elm ) {
				elm.removeEventListener(self.support.trEndEvent, listener, false);
				finished.apply(elm);
			}
		};
		elm.addEventListener(self.support.trEndEvent, listener, false);
	},
	
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
	animate : function(elm, timing, duration, finished) {
		var self = this;
		this.animateListen(elm, function() {
			elm.style[self.support.trProp] = '';
			if ( finished !== undefined ) {
				finished.apply(elm);
			}
		});
		var cssValue = this.support.trTransform 
			+' ' 
			+(duration !== undefined ? duration : '0.3s')
			+' ' 
			+(timing !== undefined ? timing : 'ease-out');
		elm.style[this.support.trProp] = cssValue;
		this.apply(elm);
	},
	
	
	/**
	 * Apply the matrix transform to an element, with an "ease out" transition.
	 * 
	 * <p>Calls {@link #animate(elm)} internally.</p>
	 * 
	 * @param {Element} elm the element to apply the transform to
	 * @param {Function} finished an optional callback function to execute when 
	 */
	easeOut : function(elm, finished) {
		this.animate(elm, 'ease-out', undefined, finished);
	},
	
	/**
	 * Apply the matrix transform to an element, with an "ease in" transition.
	 * 
	 * <p>Calls {@link #animate(elm)} internally.</p>
	 * 
	 * @param {Element} elm the element to apply the transform to
	 * @param {Function} finished an optional callback function to execute when 
	 */
	easeIn : function(elm, finished) {
		this.animate(elm, 'ease-in', undefined, finished);
	}
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
	// {"id":-6, "name":"Website", "kind":"URL", "kindId":-1, "value":"http://my.web.site/", "createdBy":"test", "creationDate":"9 May 2012", "modifyDate":"9 May 2012"},
	this.bits = bits;
	this.name = data.name;
	this.info = {}; // map of kindId to array of{id:-6, value:'http://my.web.site/'}
	this.refreshElement = $('<button class="action"><i class="action ticon icon-refresh-t">\uf021</i></button>');
	this.addElement = $('<button class="action"><i class="action icon-edit"></i></button>');
	this.element = $(container)
		.append(this.addElement)
		.append(this.refreshElement)
		.append($('<h2/>').text(data.name))
		;
	this.listElement = $('<dl class="clearfix"/>');
	this.element.append($('<div class="content"/>').append(this.listElement));
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
		if ( bits.isCardMode() === false ) {
			$('html,body').scrollTop(0);
		}
		Tidbits.Runtime.editor.edit(self);
	};

	// use 'start' event here because 'click' never captured with card handling events
	this.refreshElement.get(0).addEventListener(Tidbits.touchEventNames.start, handleRefresh, false);
	this.addElement.get(0).addEventListener(Tidbits.touchEventNames.start, handleAdd, false);
};

Tidbits.Class.Bit.prototype = {
	getName : function() {
		return this.name;
	},
	
	removeDetail : function(id) {
		
	},
	
	/**
	 * Add info details to the info model.
	 * 
	 * @param {Object} data the info data to insert
	 */
	addDetails : function(data) {
		if ( data === undefined || data.kindId === undefined ) {
			return;
		}
		// {id:1, kind:"Foo", kindId:2, name:"Bar",...}

		// the kindId might have changed for this bit! so we search by id
		var kindId = undefined;
		var detail = undefined;
		var details = undefined;
		var i, len;
		for ( kindId in this.info ) {
			kindId = Number(kindId);
			details = this.info[kindId];
			for ( i = 0, len = details.length; i < len; i++ ) {
				if ( details[i].id === data.id ) {
					detail = details[i];
					break;
				}
			}
			if ( detail !== undefined ) {
				if ( kindId !== data.kindId ) {
					// changed kindId, remove and add under new kindId
					details.splice(i, 1);
				} else {
					// simple update existing value
					detail.value = data.value;
					return;
				}
				break;
			}
		}
		
		if ( detail === undefined ) {
			// wasn't found under different key, so add as new
			detail = {id:data.id, value:data.value};
		}

		details = this.info[data.kindId];
		if ( details === undefined ) {
			this.info[data.kindId] = [];
			details = this.info[data.kindId];
		}
		details.push(detail);
	},
	
	/**
	 * Call-back function for extending classes to implement.
	 * 
	 * <p>This method will be called by the {@link #insertIntoDocument(Element)}
	 * function.</p>
	 * 
	 * @param {Element} container the DOM element this Bit will be inserted into
	 */
	willInsertIntoDocument : function(container) {},
	
	/**
	 * Refresh own DOM structure and then insert into the container DOM element.
	 * 
	 * @param {Element} container the DOM element to insert this Bit into
	 */
	insertIntoDocument : function(container) {
		// create list elements
		this.refresh();
		this.willInsertIntoDocument(container);
		$(container).append(this.element);
	},
	
	/**
	 * Refresh own DOM structure.
	 * 
	 * <p>This will delete all children elements of this Bit's container
	 * element and re-create them using the current model info.</p>
	 */
	refresh : function() {
		this.listElement.empty();
		var kindId = undefined;
		var i, len, details;
		for ( kindId in this.info ) {
			this.listElement.append($('<dt/>').text(
					Tidbits.Runtime.kinds.getName(kindId)));
			details = this.info[kindId];
			for ( i = 0, len = details.length; i < len; i++ ) {
				this.listElement.append($('<dd/>').text(details[i].value));
			}
		}
	},
	
	/**
	 * Get the info model object.
	 * 
	 * @returns {Object} the bit info
	 */
	getInfo : function() {
		return this.info;
	}
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
	this.longTouchMs = 1250;
	this.longTouchWiggle = 8;
	this.matrix = new Tidbits.Class.Matrix();
	this.fac = false; // front and center, used to toggle touch behavior

	var self = this;
	var el = this.element;
	var elm = el.get(0);
	var m0 = {x:0, y:0};
	var p1 = {x:0, y:0, t:0};
	var p2 = {x:0, y:0, t:0};
	var minInertiaDistance = 0;
	var lastTapEnd = 0;
	var longTouchTimer = undefined;
	var longTouch = undefined;
	
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
	
	var cancelLongTouch = function() {
		if ( longTouchTimer !== undefined ) {
			clearTimeout(longTouchTimer);
			longTouchTimer = undefined;
		}
	};
	
	var startLongTouch = function() {
		cancelLongTouch();
		m0 = self.matrix.getTranslation();
		longTouchTimer = setTimeout(longTouch, self.longTouchMs);
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
		
		// allow long tap still, as long as we didn't move much
		if ( longTouchTimer !== undefined ) {
			var distanceFromStart = self.matrix.getDistanceFrom(m0);
			if ( distanceFromStart >  self.longTouchWiggle ) {
				console.log("Cancelling long touch timer, distance from start is " + distanceFromStart);
				cancelLongTouch();
			}
		}
		
		p2.x = p1.x, p2.y = p1.y, p2.t = p1.t;
		p1.x = pageX;
		p1.y = pageY;
		p1.t = e.timeStamp;
	};
	
	var touchStart = function(e) {
		// long touch support
		startLongTouch();
		
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
	
	var touchCancel = function(e) {
		cancelLongTouch();
		elm.removeEventListener(Tidbits.touchEventNames.move, touchMove, false);
		if ( e === undefined ) {
			return;
		}
		e.preventDefault();
		e.stopPropagation();
	};
	
	var touchEnd = function(e) {
		touchCancel(e);
		if ( e === undefined ) {
			return;
		}

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

	longTouch = function() {
		touchCancel();
		// not using this for the moment... console.log("long touch");
	};
	
	elm.addEventListener(Tidbits.touchEventNames.start, touchStart, false);
	elm.addEventListener(Tidbits.touchEventNames.cancel, touchCancel, false);
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
	this.margins = margins;

	var bits = {};
	var bitz = [];
	var cardsContainer = $(container);
	var self = this;
	var cardMode = false;
	
	var populateTidbits = function(data, ascending) {
		if ( data === undefined || !Array.isArray(data.tidbits) ) {
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
		if ( screenWidth > 480 ) {
			cardMode = true;
			
			// prevent elastic scrolling
			document.addEventListener('touchmove', function(event) {
			  event.preventDefault();
			}, false);
		};
		
		jQuery.getJSON('search.json', populateTidbits);
		
	    $('#nav-search-tidbit-form').submit(function(event) {
			event.preventDefault();
			$(this).ajaxSubmit({
				dataType: 'json',
				error : function(xhr, statusText, error) {
					Tidbits.defaultAjaxErrorHandler('tidbit.search.error.title', xhr, statusText, error);
				},
				success : replaceAllTidbitsWithSearchResults
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
	
	/**
	 * Get a Bit by it's name.
	 * 
	 * @param {String} name the name of the Bit to get
	 * @returns the Bit, or undefined
	 */
	this.getBitByName = function(name) {
		return bits[name];
	};
	
	/**
	 * Add a Bit from a post result.
	 * 
	 * <p>The data should be in the form 
	 * <pre>{tidbits:[{id:1,name:"Foo",...},...]}</pre></p>
	 * 
	 * @param {Object} data the Bit data
	 * @param {Array} data.tidbits the array of tidbit data to add
	 * @returns {Tidbits.Class.Bit} the added Bit
	 */
	this.addBit = function(data) {
		if ( data !== undefined && Array.isArray(data.tidbits) && data.tidbits.length > 0 ) {
			populateTidbits(data, true);
			return this.getBitByName(data.tidbits[0].name);
		}
	};
	
	/**
	 * Test if rendered Bit or Card objects.
	 * 
	 * @returns {Boolean} true if rendered bits as cards
	 */
	this.isCardMode = function() {
		return cardMode === true;
	};
};

/**
 * The Tidbits editor.
 * 
 * @param {Element} container the editor container
 * @returns {Tidbits.Class.Editor}
 */
Tidbits.Class.Editor = function(container) {
	this.element = $(container);
	this.elm = this.element.get(0);
	this.matrix = new Tidbits.Class.Matrix();
	this.bit = undefined;
	this.editModel = {};
	
	var self = this;
	
	this.flipper = this.element.find('.flipper');
	this.front = this.element.find('.front');
	this.back = this.element.find('.back');
	this.bottom = this.element.find('.bottom');
	
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
	    
	    $('#add-tidbit-value-btn').click(function(e) {
	    	e.preventDefault();
	    	self.displayForm({id:'',value:''});
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

Tidbits.Class.Editor.prototype = {
		
	postedForm : function(data) {
		if ( this.bit !== undefined ) {
			this.bit.addDetails(data.tidbits[0]);
			
			// TODO: implement updateBit method, instead of recreate entire list each time?
			this.setBit(this.bit);
		} else {
			this.setBit(Tidbits.Runtime.bits.addBit(data));
		}
		this.displayList();
	},
		
	toggleFormAndKinds : function() {
		if ( this.flipper.hasClass('alt') ) {
			this.flipper.removeClass('alt');
			this.bottom.removeClass('alt');
			this.flipper.css('transform', '');
		} else {
			this.flipper.addClass('alt');
			this.flipper.css('transform', 'rotateY(180deg)');
			this.bottom.addClass('alt');
		}
	},
	
	displayKinds : function() {
		if ( !this.flipper.hasClass('alt') ) {
			this.toggleFormAndKinds();
		}
	},
	
	displayForm : function(crumb, kindId) {
		if ( crumb !== undefined ) {
			$('#edit-tidbit-id').val(crumb.id);
			$('#add-tidbit-data').val(crumb.value);
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
		this.hideList();
		if ( this.flipper.hasClass('alt') ) {
			this.toggleFormAndKinds();
		}
	},
	
	toggleList : function() {
		var show = (this.bottom.css('display') === 'none');
		var alt = this.bottom.hasClass('alt');
		var newTransform = (show === true ? 'rotateX(-180deg)' : '');
		var otherSide = this.back;
		if ( alt == true ) {
			newTransform = 'rotateY(180deg) ' +newTransform;
			otherSide = this.front;
		}
		if ( show === true ) {
			otherSide.css('display', 'none');
			this.bottom.css('display', 'block');
		} else {
			var bottom = this.bottom;
			this.matrix.animateListen(this.flipper.get(0), function() {
	    		otherSide.css('display', 'block');
	    		bottom.css('display', 'none');
			});
		}
		this.flipper.css('transform', newTransform);
	},
	
	displayList : function() {
		if ( this.bottom.css('display') === 'none' ) {
			this.toggleList();
		}
	},
	
	hideList : function() {
		if ( this.bottom.css('display') !== 'none' ) {
			this.toggleList();
		}
	},
	
	editCrumb : function(id, kindId) {
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
		console.log("Edit crumb: " +crumb);
		this.displayForm(crumb, kindId);
	},
	
	setBit : function(bit) {
		this.bit = bit;
		
		// update view according to model
		var info = (bit === undefined ? {} : bit.getInfo());
		
		this.element.find('div.bottom h3').text(
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
	},
	
	/**
	 * Display the editor for a specific Bit.
	 * 
	 * @param {Tidbits.Class.Bit} bit the Bit to edit
	 */
	edit : function(bit) {
		this.setBit(bit);
		if ( bit === undefined ) {
			// creating a new one from scratch
			this.displayForm({id:'',value:''});
		} else {
			// editing existing bit
			this.displayList();
		}
		this.show();
	},
	
	/**
	 * Display the editor for a new Bit.
	 */
	show : function() {
		var win = $(window);
		var halfWidth = this.element.width() / 2;
		var halfHeight = this.element.height() / 2;
		var centerX = Math.floor(win.width() / 2 - halfWidth);
		var centerY = Math.floor(win.height() / 2 - halfHeight);
		var self = this;
		var move = function() {
			self.matrix.setTranslation(centerX, centerY);
			self.matrix.easeOut(self.elm, function() {
		    	$('#add-tidbit-name').focus();
			});
		};
		if ( this.element.css('visibility') === 'hidden' ) {
			this.matrix.setTranslation(centerX, halfHeight * -3);
			this.matrix.apply(this.elm);
			this.element.css('visibility', '');
			setTimeout(move, 10);
		} else {
			move();
		}
	},
	
	hide : function() {
		var height = this.element.height();
		this.matrix.setTranslation(
				this.matrix.getTranslation().x, 
				Math.ceil(height * -1.5));
		this.matrix.easeIn(this.elm);
	}
};

/**
 * Manage the list of kinds.
 * 
 * @returns {Tidbits.Class.Kinds}
 */
Tidbits.Class.Kinds = function() {
	this.kinds = []; 	// array of {id:1,name:"foo"}
	this.kindMap = {};	// map of id -> name
};

Tidbits.Class.Kinds.prototype = {
		
	/**
	 * Set the array of kind data.
	 * 
	 * <p>The data is expected to look like 
	 * <pre>[{id:1,name:"foo"},...]</pre>.</p>
	 * 
	 * @param data the array of Kind objects
	 */
	setKinds : function(data) {
		if ( !Array.isArray(data) ) {
			this.kinds = [];
			this.kindMap = {};
			return;
		}
		this.kinds = data;
		this.kindMap = {};
		var i, len;
		for ( i = 0, len = data.length; i < len; i++ ) {
			this.kindMap[data[i].id] = data[i].name;
		}
	},
	
	getKinds : function() {
		return this.kinds;
	},
	
	getName : function(kindId) {
		return this.kindMap[Number(kindId)];
	},
	
	/**
	 * Add a new Kind object, and return it's position in the array of kinds.
	 * 
	 * @param kind the kind to add
	 * @returns {Number} the index of the added kind
	 */
	addKind : function(kind) {
		// let's add it sorted by name
		var i = 0, len = this.kinds.length;
		while ( i < len && this.kinds[i].name < kind.name ) {
			i++;
		}
		this.kinds.splice(i, 0, kind);
		this.kindMap[kind.id] = kind.name;
		return i;
	},
	
	/**
	 * Remove a kind.
	 * 
	 * @param kindId the ID of the kind to remove
	 * @returns {Object} the removed kind
	 */
	removeKind : function(kindId) {
		var removed = undefined;
		if ( this.kindMap[kindId] !== undefined ) {
			var i, len;
			delete this.kindMap[kindId];
			for ( i = 0, len = this.kinds.length; i < len; i++ ) {
				if ( this.kinds[i].id === kindId ) {
					removed = this.kinds[i];
					this.kinds.splice(i, 1);
					break;
				}
			}
		}
		return removed;
	},
	
	/**
	 * Update the name of a kind, and return the new index of the kind.
	 * 
	 * @param {Object} kind the kind to update
	 * @return {Number} the index of the updated kind
	 */
	updateKind : function(kind) {
		var old = this.removeKind(kind.id);
		var idx = 0;
		if ( old !== undefined ) {
			idx = this.addKind(kind);
		}
		return idx;
	}
};

$(document).ready(function() {
	Tidbits.Runtime.i18n = new XwebLocaleClass();
	jQuery.getJSON('messages.json', function(data) {
		Tidbits.Runtime.i18n.initJson(data);
	});
	
	$('textarea').val(''); // from XSLT, remove whitespace
	
	Tidbits.Runtime.kinds = new Tidbits.Class.Kinds();
	Tidbits.Runtime.bits = new Tidbits.Class.Bits('#card-container',
			{top:($('#navbar').height() + 20), left:20, right:20, bottom:20});
	Tidbits.Runtime.editor = new Tidbits.Class.Editor('#tidbit-editor');
	$('#add-new-tidbit-btn').click(function(e) {
		e.preventDefault();
		Tidbits.Runtime.editor.edit();
	});

});
