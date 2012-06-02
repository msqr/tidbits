=======================================================================
Tidbits: the web-based personal digital wallet
Version @VERSION@ @BUILD_DATE@
=======================================================================

This is the binary distribution of Tidbits. Tidbits is a personal 
digital wallet. You can use it to store passwords, logins, URLs, or
just about any small tidbit of data you might easily forget. You can
then access all of this information by logging into Tidbits with your
single Tidbits password. Tidbits is a web-based application which means
you need an Internet-accessible server machine you can deploy it to 
(i.e. a hosting provider or your own personal server).

Tidbits includes special support for certain mobile devices, such as 
the Palm Treo phones. If one of these devices is detected, Tidbits
will run with a slimmed-down interface better suited to these devices.

INSTALLATION ==========================================================

Tidbits is a Java web application, and requires a J2EE servlet 
container to run in, such as Tomcat or JBoss. Tidbits also requires
a relational database to store the data in. Tidbits has been tested
with PostgreSQL, MySQL, and Apache Derby.

FIRST-TIME DATABASE CREATION ==========================================

  You must initialize your database for first-time use. Currently 
  PostgreSQL 7.x or 8.x and MySQL 4.1 or later are directly supported:
  
  POSTGRES ------------------------------------------------------------
  
  1) Create a database user for Tidbits
    
    As a Postgres super-user, execute:
    
    $ createuser -ADEP tidbits
    
    You will be prompted to enter the tidbits user's password. If you 
    get a permissions error when you attempt to run this command, 
    make sure you are executing it as a Postgres user with sufficient
    privileges. You can explicity run as the Postgres super-user with
    
    $ createuser -U postgres -ADEP tidbits
    
    in which you may be prompted for the postgres user's password.
    
  2) Create a database for Tidbits
  
    As a Postgres super-user, execute:
    
    $ createdb -E UNICODE -O tidbits tidbits
    
    Again, you may need to pass the -U flag to specify a Postgres
    super-user account to execute the command as. The database
    must be created with the Unicode encoding!
    
  MYSQL ---------------------------------------------------------------
  
  1) Create a database for Tidbits
  
    As a MySQL super-user, execute the following in the MySQL shell,
    i.e. enter 'mysql -u root mysql' to enter the MySQL shell as the 
    'root' user:
    
    mysql> create database tidbits character set utf8;
    
  2) Grant privileges for an Tidbits database user
  
    Still in the MySQL shell, execute:
    
    mysql> grant all privileges on tidbits.* to tidbits \
    identified by 'tidbits';
    
    Here the "identified by 'tidbits'" creates the user's password, 
    which you should set to whatever you please. Note this allows the 
    database user 'tidbits' to connect to the 'tidbits' database from 
    any host. If you plan on running the Tidbits application on the 
    same machine as MySLQ is running on, you could limit the tidbits 
    user to connect only from the local machine with
    
    mysql> grant all privileges on tidbits.* to 'tidbits'@'localhost' 
        -> identified by 'tidbits';
    
  3) Flush the privileges
  
    To make sure MySQL is ready to be used with the new tidbits user,
    execute the following (still in the MySQL shell):
    
    mysql> flush privileges;
    

FIRST-TIME DATABASE SETUP =============================================

  After creating the database for the first time, you must run
  some SQL scripts to create the Tidbits database tables.
  
  POSTGRES ------------------------------------------------------------
  
  Execute:
  
  $ psql -U tidbits -d tidbits-f setup/sql/postgres/create-system.sql
  
  Ignore any warnings about "table X does not exist". Then repeat
  the above command for the following SQL scripts (substitue 
  these paths for the -f argument):
  
  - setup/sql/postgres/create-tables.sql
  
  MYSQL ---------------------------------------------------------------
  
  Execute:
  
  $ mysql -f -u tidbits -p tidbits <setup/sql/mysql/create-tables.sql
  
  Enter the tidbits user password if prompted. Ignore any warnings 
  about "table X doesn't exist". Then repeat the above command for the 
  following SQL scripts (substitute these paths for the 
  'setup/sql/mysql/create-tables.sql' above):
  
  - setup/sql/mysql/create-tables.sql
  

FIRST-TIME APPSERVER DATASOURCE SETUP =================================

  Tidbits depends on the application server it is running in to provide 
  a JDBC DataSource to connect to the database with. Thus you must 
  configure the DataSource the first time you install Tidbits.
  
  For Tomcat 5.5, create the DataSource first by editing the 
  <TOMCAT HOME>/conf/server.xml file and add the following to the 
  <GlobalNamingResources> section (adjust the parameter values as 
  necessary for your environment, but if you are following these 
  directions from the start, these should work for you):
  
  POSTGRES ------------------------------------------------------------
  
    <Resource name="jdbc/tidbits" 
      type="javax.sql.DataSource" scope="Shareable"
      driverClassName="org.postgresql.Driver" 
      url="jdbc:postgresql://localhost:5432/tidbits"
      username="tidbits" password="tidbits" maxWait="5000"
      maxActive="5" maxIdle="2" removeAbandoned="true"
      removeAbandonedTimeout="60" logAbandoned="true"
      validationQuery="select CURRENT_TIMESTAMP"
      />
    
  Then, if you don't already have the Postgres JDBC driver added to 
  Tomcat, copy the setup/lib/postgresql-8.1-407.jdbc3.jar to the 
  <TOMCAT HOME>/common/lib directory.

  MYSQL ---------------------------------------------------------------
  
  For MySQL, change the 'url' and 'driverClassName' values from the 
  XML snippet to:
  
  jdbc:mysql://localhost:3306/tidbits
  com.mysql.jdbc.Driver
  
  It should look similar to this:
  
    <Resource name="jdbc/tidbits" 
      type="javax.sql.DataSource" scope="Shareable"
      driverClassName="com.mysql.jdbc.Driver" 
      url="jdbc:mysql://localhost:3306/tidbits"
      username="tidbits" password="tidbits" maxWait="5000"
      maxActive="5" maxIdle="2" removeAbandoned="true"
      removeAbandonedTimeout="60" logAbandoned="true"
      validationQuery="select CURRENT_TIMESTAMP"
      />
    
  Then, if you don't already have the MySQL JDBC driver added to 
  Tomcat, copy the setup/lib/mysql-connector-java-3.1.13-bin.jar
  to the <TOMCAT HOME>/common/lib directory.


APPLICATION SETUP =====================================================

  To install, copy the WAR file included in this package your 
  application server's deployment directory. For Tomcat this defaults
  to <TOMCAT HOME>/webapps.
  
  Finally, you might want to adjust the application logging, which 
  by default will attempt to log via Log4J to the console. You 
  can adjust the verbosity and location of this log by unpacking the 
  WAR and editing the <TIDBITS HOME>/WEB-INF/classes/log4j.properties 
  Log4J configuration. Then either change your application server 
  configuration to point to the unpacked WAR directory or zip up the 
  unpacked WAR directory back to the original file name.

  
FIRST-TIME USE ========================================================

  Start up your application server. Once started, visit 
  
  http://<your server>:<port>/tidbits
  
  where <your server> is the machine Tidbits is running on (i.e. 
  localhost) and <port> is the port the applicaiton server is 
  listening on (i.e. for Tomcat this defaults to 8080).
  
  You should see the Tidbits Setup Wizard page. The Setup Wizard will 
  guide you through configuring the remaining Tidbits options.

