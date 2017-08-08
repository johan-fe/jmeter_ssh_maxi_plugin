# jmeter_ssh_maxi_plugin 

This version of the jmeter ssh plugin is based on https://github.com/yciabaud/jmeter-ssh-sampler/

But eventually it became so different that I started a separate repository.
Several features were added:
1. Using persistent ssh sessions
2. Support for tunneling
3. support for more sftp commands
4. support for exec, shell, sftp type of channels
5. support for keepalive timer for persistent connection
6. added junit tests to dummy java server

Overview
------------

SSH Sampler for Jakarta JMeter that executes commands (eg, ls) over a numbber of persistent SSH sessions, and returns the output.
The output may then be parsed or logged by a listener for use elsewhere in the testing process.
This repository is a fork of http://code.google.com/p/jmeter-ssh-sampler/ to manage pull reqests.

A second component deals with SFTP to allow you to download files over persistent SSH connections to assert on their content.

Installation
------------

Installation is fairly straightforward, and only involves adding the plugin and JSch to the right directory:

1. Build with maven
2. configure maven properties so that jmeter_base_dir is set to point to the base directory of jmeter
3. Place JSch into JMeter's lib directory
4. build with eclipse package or copy release jar into jmeter_base_dir/ext/lib 
4. Run JMeter, and find different samplers to open ssh connections, channels, send commands etc. in the Samplers category

Usage 
------------

Using the plugin is simple (assuming familiarity with SSH and JMeter):
The samplers need to be executed in a certain order e.g. open ssh connection -open shell channel - send commands to shell channel -close shell channel -close SSH connection. 
Persistent SSH connections, channels can be opened in a setup thread group. Opening in other thread groups is also possible, but take into account the settings of your test plan (Run thread groups consecutively,Run teardown thread groups after shutdown of main threads, Functional test mode).
A teardown thread group can be used to close SSH connections and channels opened on these.

### SSH Open Named Connection

1. Create a new Test Plan
2. Add a Thread Group
3. Add a Sampler > SSH Command
4. Specify the host to connect to, port, username and password (unencrypted) or a key file, connection timeout, 
5. optionally define tunnels e.g. L20000:127.0.0.1:80, multiple tunnels can be setup, separate by semicolon, local and remote are supported 
6. define the name of the connection, you need this name to set up sftp or shell channels
7. Add a Listener > View Results Tree
8. add an SSH Close Named Connection to you test plan that is executed after the SSH Open Named Connection
9. Run the test

### SSH Close Named Connection

1. add this sampler to the test plan in such a way that it is executed after SSH Open Named Connection
2. fill in the connection name used in SSH Open Named Connection

### SSH Open Persistent Named Shell Channel

1. Add this sampler to the test plan so that it is executed after the SSH persistent named connection is opened by the SSH Open named connection sampler
2. fill in she Name of the persistent shell channel name will be opened
3. fill in the SSH connection name that was opened before

### SSH Persistent Shell Channel Send Command

1. Add sampler in test plan after sampler to open SSH connection and shell channel
2. Fill in SSH connection name and shell name 
3. Fill in command to execute on shell 
4. Fill in character encoding e.g. UTF-8 (used for prompt stripping)
5. select to strip echoed command or prompt 

### SSH Close Persistent Shell channel

1. fill in she shell channel name that was opened before
2. fill in the SSH connection name that was opened before

### SSH Open Named Connection And Send Command On Exec Channel

1. Create a new Test Plan
2. Add a Thread Group
3. Add a Sampler > SSH Command
4. Specify the host to connect to, port, username and password (unencrypted) or a key file, and a command to execute via exec channel(such as date)
5. set SSH parameters to close the connection after termination of the sampler or to keep it open (e.g. to open shell or FTP channels or send further commands to an exec channel. If kept open an SSH connection name must be given.
6. Add a Listener > View Results Tree
7. Run the test


### SSH Send Command Via Exec Channel On Named SSH Connection

1. Ensure the SSH Open Named Connection  sampler is executed before this sampler 
2. fill in the persistent SSH connection name 
3. fill in the command to execute over the exec channel
4. fill in parameters e.g. for stderr to be displayed


### SSH Dump Connections

Allows you to get a list of all persistent SSH connections with the channels opened on there
1. add a sampler in your test plan.
2. if you fill in no connection name, all connections will be shown in the dump
3. optionally a connection name can be filled in for filtering

### SSH Open Named Connection And Action On SFTP Channel

1. Create a new Test Plan
2. Add a Thread Group
3. Add a Sampler > SSH SFTP
4. Specify the host to connect to, port, username and password (unencrypted) or a key file, and a SFTP command to execute (such as get)
5. Specify a source file to download, an output file or to print the content in the test
6. if specified the SSH connection can be persisted and kept open for further use on if a name is specified for the SSH connection and sufficient keepalive time is specified 
7. Add a Listener > View Results Tree
8. Run the test

### SSH Open Persistent Named SFTP Channel

1. Create a new Test Plan
2. Add a Thread Group
3. Add a Sampler > SSH SFTP
4. Specify the host to connect to, port, username and password (unencrypted) or a key file, character encoding
5. Add a Listener > View Results Tree
6. Run the test
7. normally this sampler is followed by a number of samplers to send sftp commands and a the end by a sampler to close the SFTP channel

### SSH Send SFTP Command on Named SFTP channel

1. add this sampler to the test plan
2. ensure before its execution a persistent SSH connection and sftp channel thereon is opened.
3. enter the name of the SSH connection and the SFTP channel thereon
4. select the command to execute
5. enter source and destination file if needed
6. set the file properties if you want to use chmod, chown or chgrp command
supported commands:
1. get
2. put
3. rm 
4. rmdir
5. local rmdir
6. ls
7. rename
8. local ls -l
9. cd
10. local cd
11. pwd
12. local pwd
13. mkdir
14. stat
15. lstat
16. local ls
17. local mkdir
18. hard link
19. get server and client version
20. chmod
21. chgrp
22. chown

### SSH Close Persistent SFTP channel

1. used to close a named SFTP channel
2. specify the STFP channel name and SSH connection name 


Dependencies
------------

Maven retrieves the following dependencies:

* SSH functionality is provided by the JSch library
* JMeter 2.3+ should be capable of running this plugin, tested mainly on 3.1

Built with
-----------
Eclipse Oxygen and Eclipse Mars on Windows, but should work fine with other environments too.

Contributing
------------

1. Fork it.
2. Create a branch (`git checkout -b my_plugin`)
3. Commit your changes (`git commit -am "Added feature"`)
4. Push to the branch (`git push origin my_plugin`)
5. Create an issue with a link to your branch
6. Be patient and wait
