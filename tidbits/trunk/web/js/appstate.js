/**
 * ApplicationState: class to keep track of current state of application.
 */
var ApplicationState = Class.create();
ApplicationState.prototype = {
	initialize: function() {
		this.selected = new Array(); // selected items
		this.context = '/mywebapp'; // default context
		
		if ((typeof Prototype=='undefined') ||
				parseFloat(Prototype.Version.split(".")[0] + "." +
		         Prototype.Version.split(".")[1]) < 1.4)
			throw("MatteState requires the Prototype JavaScript framework >= 1.4.0");
			
		var me = this;
		
		/* AUGH, Firefox fails on this with Scriptaculous there, too. Safari OK
		$A(document.getElementsByTagName("script")).findAll( function(s) {
			return (s.src && s.src.match(/behaviours\.js(\?.*)?$/))
		}).each( function(s) {
			var path = s.src.replace(/behaviours\.js(\?.*)?$/,'');
			var myContext = s.src.match(/\?.*context=([^ &]+)/);
			if ( myContext ) me.setContext(myContext[1]);
		});
		*/
		var s = $('appstate-js');
		var myContext = s.src.match(/\?.*context=([^ &]+)/);
		if ( myContext ) me.setContext(myContext[1]);

	},
	
	/**
	 * Set the web context path (eg. '/ma2').
	 * @param theContext {String} the web context path to set
	 */
	setContext: function(theContext) {
		this.context = theContext;
	}
	
}

var WorkingPeriodicalExecutor = Class.create();
WorkingPeriodicalExecutor.prototype = {
	initialize: function(frequency, callback) {
		this.frequency = frequency || 2;
		this.callback = callback || function () {
			new Insertion.Bottom('system-working','.');
		};
		this.currentlyExecuting = false;
		this.working = $('system-working');
		this.originalWorkingValue = this.working.firstChild.nodeValue;
		this.registerCallback();
	},

	registerCallback: function() {
		if ( !this.timer ) {
			this.timer = setInterval(this.onTimerEvent.bind(this), this.frequency * 1000);
		}
	},
	
	start: function() {
		this.registerCallback();
	},
	
	stop: function() {
		clearTimeout(this.timer);
		this.timer = null;
		Element.update(this.working,this.originalWorkingValue);
	},
	
	onTimerEvent: function() {
		if (!this.currentlyExecuting) {
			try {
				this.currentlyExecuting = true;
				this.callback();
			} finally {
				this.currentlyExecuting = false;
			}
		}
	}
}


