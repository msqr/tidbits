// Copyright (c) 2006 Matt Magoffin
// 
// ===================================================================
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of
// the License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
// 02111-1307 USA
// ===================================================================
// $Id: mmagoff-locale.js,v 1.1 2006/07/25 09:31:51 matt Exp $
// ===================================================================
// 
// Derived from code in Scriptaculous, Copyright (c) 2005 Thomas Fuchs 
// (http://script.aculo.us, http://mir.aculo.us)


var DEFAULT_LANG = 'en';

var MmagoffLocale = {
  SupportedLangs: {'en':true},

  bundle: {},
  
  load_msg: function(messages) {
    document.write('<script type="text/javascript" src="'+messages+'"></script>');
  },
  
  load: function() {
    if((typeof Prototype=='undefined') ||
      parseFloat(Prototype.Version.split(".")[0] + "." +
                 Prototype.Version.split(".")[1]) < 1.4)
      throw("MmagoffLocale requires the Prototype JavaScript framework >= 1.4.0");
    
    var s = $('locale-js');
    var path = s.src.replace(/mmagoff-locale\.js(\?.*)?$/,'');
    var myLang = s.src.match(/\?.*lang=([a-z,]*)/);
    if ( myLang ) myLang = myLang[1];
    if ( !MmagoffLocale.SupportedLangs[myLang] ) myLang = DEFAULT_LANG;
    MmagoffLocale.load_msg(path+'mmagoff-messages_'+myLang+'.js')
  },
  
	init: function(messages) {
		if ( messages ) this.bundle = messages;
	},
	
	/**
	 * Initialize from DOM nodes of type <msg key='x'>value</msg>.
	  */
	initXmsg: function(xMsgNodes) {
		if ( !(xMsgNodes && xMsgNodes.length) ) return;
		for ( var i = 0; i < xMsgNodes.length; i++ ) {
			if ( !xMsgNodes[i].hasChildNodes() ) continue;
			var key = xMsgNodes[i].getAttribute('key');
			xMsgNodes[i].normalize();
			var value = xMsgNodes[i].firstChild.nodeValue;
			this.bundle[key] = value;
		}
	},
  
  i18n : function(key,params) {
	var msg = this.bundle[key];
	if ( !msg ) {
		msg = '';
	} else if ( params ) {
		var i = 0;
		for ( i = 0; i < params.length; i++ ) {
			msg = msg.replace(new RegExp('\\{'+(i+1)+'\\}','g'),params[i]);
		}
	}
	return msg;
  }
}

MmagoffLocale.load();
