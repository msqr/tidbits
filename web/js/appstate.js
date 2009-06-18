/**
 * ApplicationState: class to keep track of current state of application.
 */
var ApplicationState = Class.create();
ApplicationState.prototype = {
	initialize: function() {
		this.selected = new Array(); // selected items
		this.context = '/mywebapp'; // default context
		this.lang = 'en_US'; // default lang
		
		if ((typeof Prototype=='undefined') ||
				parseFloat(Prototype.Version.split(".")[0] + "." +
		         Prototype.Version.split(".")[1]) < 1.4) {
			throw("ApplicationState requires the Prototype JavaScript framework >= 1.4.0");
		}
			
		var s = $('appstate-js');
		var myContext = s.src.match(/\?.*context=([^ &]+)&lang=([^ &]+)/);
		if ( myContext ) {
			this.setContext(myContext[1]);
			this.setLang(myContext[2]);
		}

	},
	
	/**
	 * Set the web context path (eg. '/tidbits').
	 * @param theContext {String} the web context path to set
	 */
	setContext: function(theContext) {
		this.context = theContext;
	},
	
	/**
	 * Set the language (eg. 'en_US').
	 * @param theLang {String} the language to set
	 */
	setLang: function(theLang) {
		this.lang = theLang;
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


