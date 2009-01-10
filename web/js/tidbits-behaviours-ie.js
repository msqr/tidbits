var tidbitRules = Object.extend({
	
	'h1.title': function(el) {
		// nothing for IE
	}
	
}, globalTidbitRules);

var IE = true;
Behaviour.register(tidbitRules);
