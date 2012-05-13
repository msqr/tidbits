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
	this.name = data.name;
	this.info = {}; // {URL: [{id:-6, value:'http://my.web.site/'}]}
	this.element = $('<div class="tidbit"/>')
		.append($('<h2/>').text(data.name))
		.css({'cursor': 'move', '-webkit-animation-timing-function':'ease-in-out'});
	this.listElement = $('<dl/>');
	this.element.append(this.listElement);
	this.addDetails(data);

	var self = this;
	var el = this.element;
	var p1 = {x:0, y:0, t:0};
	var p2 = {x:0, y:0, t:0};
	var touchStart = undefined, touchMove = undefined, touchEnd = undefined, cardTranslate = undefined;
	var minInertiaDistance = 0;
	
	// momentum?
	var momenumHowMuch = 30;  // change this for greater or lesser momentum
	var momentumMinDrift = 0; // minimum drift after a drag move
	
	touchStart = function(e) {
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
		el.get(0).addEventListener(Tidbits.touchEventNames.move, touchMove, false);
		el.get(0).addEventListener(Tidbits.touchEventNames.end, touchEnd, false);
	};
	
	cardTranslate = function(pageX, pageY, timeStamp) {
		if ( pageX >= 0 && pageY >= 0 ) {
			var deltaX = pageX - p1.x;
			var deltaY = pageY - p1.y;
			var matrix = el.css('transform');
			//console.log(matrix +', ' +pageX +', ' +pageY +', ' + deltaX +', ' +deltaY +', ' +timeStamp);
			var currTranslate = matrix.match(/,\s*(-?\d+),\s*(-?\d+)\)$/);
			var translate = 'translate('
				+(deltaX + (currTranslate === null ? 0 : Number(currTranslate[1])))+'px,'
				+(deltaY + (currTranslate === null ? 0 : Number(currTranslate[2]))) +'px)';
			el.css('transform', translate);
		}
	};
	
	touchMove = function(e) {
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
	
	touchEnd = function(e) {
		if ( e === undefined ) {
			console.log('no end event');
			return;
		}
		e.preventDefault();
		e.stopPropagation();
		el.get(0).removeEventListener(Tidbits.touchEventNames.move, touchMove, false);
		el.get(0).removeEventListener(Tidbits.touchEventNames.end, touchEnd, false);
		
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
			el.css({'-webkit-transition' : '-webkit-transform 0.2s ease-out'});
			el.get(0).addEventListener('webkitTransitionEnd',  function(event) {
				 el.css('-webkit-transition', '');
			 }, false );
			cardTranslate(newX, newY, e.timeStamp);
		}
	};

	el.get(0).addEventListener(Tidbits.touchEventNames.start, touchStart, false);
};

Tidbits.Class.Card.prototype.addDetails = function(data) {
	if ( data === undefined || data.kind === undefined ) {
		return;
	}
	if ( this.info[data.kind] === undefined ) {
		this.info[data.kind] = [];
	}
	this.info[data.kind].push({id:data.id, value:data.value});
};

Tidbits.Class.Card.prototype.insertIntoDocument = function(container) {
	// create list elements
	var prop = undefined;
	var me = this;
	for ( prop in this.info ) {
		this.listElement.append($('<dt/>').text(prop));
		this.info[prop].forEach(function(detail) {
			me.listElement.append($('<dd/>').text(detail.value));
		});
	}
	$(container).append(this.element);
};

Tidbits.Class.Card.prototype.getInfo = function() {
	return this.info;
};

/**
 * A collection of card objects.
 * 
 * @class
 * @param {Element} container to insert all card objects into
 * @returns {Tidbits.Class.Cards}
 */
Tidbits.Class.Cards = function(container) {
	var cards = {};
	var cardz = [];
	var cardsContainer = $(container);
	var self = this;
	var kinds = [];
	
	var populateTidbits = function(data) {
		if ( data === undefined || !jQuery.isArray(data.tidbits) ) {
			return;
		}
		var i, len;
		var tidbit;
		for ( i = 0, len = data.tidbits.length; i < len; i++ ) {
			tidbit = data.tidbits[i];
			if ( cards[tidbit.name] === undefined ) {
				cards[tidbit.name] = new Tidbits.Class.Card(tidbit, self);
				cardz.splice(0, 0, cards[tidbit.name]);
			} else {
				cards[tidbit.name].addDetails(tidbit);
			}
		}
		for ( i = 0, len = cardz.length; i < len; i++ ) {
			var zIndex = Number(cardz[i].element.css('z-index'));
			if ( isNaN(zIndex) || zIndex < 0 ) { // the card may already be in the DOM
				cardz[i].element.css('z-index', (i+1));
				cardz[i].insertIntoDocument(cardsContainer);
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
					populateTidbits(data);
				} else {
					Tidbits.errorAlert('<h4 class="alert-heading">' 
							+Tidbits.i18n('tidbit.save.error.title') 
							+'</h4>' +statusText);
				}
			});
		});
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

	Tidbits.Runtime.cards = new Tidbits.Class.Cards('#card-container');
});
