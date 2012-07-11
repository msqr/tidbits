=======================================================================
Tidbits: the web-based personal digital wallet
Live Demo @VERSION@ @BUILD_DATE@
=======================================================================

This is the Live Demo distribution of Tidbits. This is a complete 
stand-alone Tidbits application pre-configured to run without any 
modification so you can see how Tidbits works. The only thing you need 
besides the Live Demo (which you have if you're reading this) is 
a Java 5 JDK (i.e. version 1.5) or higher. Note that a Java JRE is not 
sufficient, you'll need the full JDK installed. You can get a JDK 
from Sun at http://java.sun.com/. Other vendors make JDKs, any one of 
them should work as long as they are version 1.5 or higher.

Starting the Live Demo  -----------------------------------------------

To start Tidbits simply run './start.sh' or '.\start.bat' script (as 
appropriate for your operating system). This will start the Tidbits 
application running with the included servlet container. After a brief 
moment you should be able to go to the following URL in a browser:

  http://localhost:8080/tidbits

If you get an error that the JAVA_HOME environment variable is not 
defined, you need to set this environment variable, which varies 
depending on the operating system you're using. For Unix-like OSes 
using sh-derived shells you can run

  $ JAVA_HOME=/path/to/jdk ./start.sh
  
For Windows users you can run

  > set JAVA_HOME=\path\to\jdk
  > .\start.bat
  

Stopping the Live Demo  -----------------------------------------------

To stop Tidbits simply run './stop.sh' or '.\stop.bat' script (as 
appropriate for your operating system).


Logging into the Live Demo --------------------------------------------

The Live Demo comes configured with a single user so you can log 
into the application and browse or add tidbits, etc. Log in with the 
following information:

  Username: demo
  Password: demo
  

Troubleshooting the Live Demo -----------------------------------------

If you run into problems getting the Live Demo running, look at the 
<LIVE DEMO HOME>/apache-tomcat/logs directory for the application log 
files. They may provide clues as to the cause of the problem.
