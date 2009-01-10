var tidbitRules = Object.extend({
	
	'h1.title': function(el) {
		var imgNode = Builder.node('img',{
			'id':'logo',
			'class':'title',
			'src':AppState.context+'/img/logo.png',
			'alt':'TidBits'});
		var aNode = Builder.node('a',{
			'href':AppState.context+'/home.do?query=&page=0',
			'title':MmagoffLocale.i18n('home')});
		aNode.appendChild(imgNode);
		el.parentNode.replaceChild(aNode, el);
	}
	
}, globalTidbitRules);

var IE = false;
Behaviour.register(tidbitRules);
