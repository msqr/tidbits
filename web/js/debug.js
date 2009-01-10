// Show the debug window
var debugWindow;
function showDebug() {
 debugWindow = window.open("",
                  "Debug",
                  "left=0,top=0,width=300,height=700,scrollbars=yes,"
                  +"status=yes,resizable=yes");

  // open the document for writing
  debugWindow.document.open();
  debugWindow.document.write(
      "<HTML><HEAD><TITLE>Debug Window</TITLE></HEAD><BODY><PRE>\n");
}

// If the debug window exists, then write to it
function debug(text) {
  if (debugWindow && ! debugWindow.closed) {
    debugWindow.document.write(text+"\n");
  }
}

// If the debug window exists, then close it
function hideDebug() {
  if (debugWindow && ! debugWindow.closed) {
    debugWindow.close();
    debugWindow = null;
  }
}
