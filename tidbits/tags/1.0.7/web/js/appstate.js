/**
 * ApplicationState: class to keep track of current state of application.
 */
var ApplicationState = function() {};
ApplicationState.prototype = {
	initialize: function() {
		this.context = '/mywebapp'; 	// default context
		this.lang = 'en_US'; 			// default lang
		
		var s = document.getElementById('appstate-js');
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
	
};
