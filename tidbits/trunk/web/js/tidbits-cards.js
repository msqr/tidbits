var Tidbits = {

	/** Namespace for classes. */
	Class: {},
	
	/** Namespace for runtime state. */
	Runtime: {},

	touchEventNames : {
		start: "mousedown",
		move: "mousemove",
		end: "mouseup",
		cancel: "touchcancel"
	},
	
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

/*
<div class="tidbit">
	<h2>Bank</h2>
	<dl>
		<dt>Password</dt>
		<dd>mypassword</dd>
		<dt>Username</dt>
		<dd>mylogin</dd>
		<dt>Note</dt>
		<dd>This is a long note, with a ton of stuff to say here. How about that?</dd>
		<dt>URL</dt>
		<dd><a href="http://someplace.com/">http://someplace.com/</a></dd>
	</dl>
</div>
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
		var event = e;
		if ( event === undefined ) {
			console.log('no end event');
			return;
		}
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
			el.get(0).addEventListener('webkitTransitionEnd',  function( event ) {
				console.log('momentum finished');
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

Tidbits.Class.Cards = function(container) {
	var cards = {};
	var cardz = [];
	var cardsContainer = $(container);
	var self = this;
	
	var populateTidbits = function(data) {
		if ( data === undefined || !jQuery.isArray(data.tidbits) ) {
			return;
		}
		var prop = undefined, i = 0;
		data.tidbits.forEach(function(tidbit) {
			if ( cards[tidbit.name] === undefined ) {
				cards[tidbit.name] = new Tidbits.Class.Card(tidbit, self);
			} else {
				cards[tidbit.name].addDetails(tidbit);
			}
		});
		for ( prop in cards ) {
			cards[prop].element.css('z-index', i);
			cards[prop].insertIntoDocument(cardsContainer);
			cardz.push(cards[prop]);
			i++;
		}
	};
	
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
	
	// private init
	(function() {
		jQuery.getJSON('search.json', populateTidbits);
	})();
};

$(document).ready(function() {
	// prevent elastic scrolling
	document.body.addEventListener('touchmove', function(event) {
	  event.preventDefault();
	}, false);
	Tidbits.Runtime.cards = new Tidbits.Class.Cards('#card-container');
});
