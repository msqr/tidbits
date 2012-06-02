<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<title>@APP_NAME@ Version</title>
		<style type="text/css">
			pre {
				white-space: pre;
				font-family: monospace;
			}
		</style>
	</head>
	<body>
<h1>@APP_NAME@ Version</h1>
<pre>
App:     @APP_NAME@ 
Build:   @BUILD_VERSION@
Date:    @BUILD_DATE@
Target:  @BUILD_TARGET_ENV@
Host:    <%= java.net.InetAddress.getLocalHost().getHostName() %>
</pre>
	</body>
</html>
