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

Usage (to be completed)
------------

Using the plugin is simple (assuming familiarity with SSH and JMeter):

### SSH Close Named Connection

TBC

### SSH Dump Connections

TBC

### SSH Send Command Via Exec Channel On Named SSH Connection

TBC

### SSH Send SFTP Command on Named SFTP channel

TBC

### SSH Close Persistent SFTP channel

TBC

### SSH Close Persistent Shell channel

TBC



### SSH Open Named Connection

1. Create a new Test Plan
2. Add a Thread Group
3. Add a Sampler > SSH Command
4. Specify the host to connect to, port, username and password (unencrypted) or a key file, keepalive timer, connect timeout, tunnels to set up 
5. Add a Listener > View Results Tree
6. Run the test

### SSH Open Named Connection And Send Command On Exec Channel

1. Create a new Test Plan
2. Add a Thread Group
3. Add a Sampler > SSH Command
4. Specify the host to connect to, port, username and password (unencrypted) or a key file, and a command to execute via exec channel(such as date)
5. Add a Listener > View Results Tree
6. Run the test


### SSH Open Persistent Named SFTP Channel

1. Create a new Test Plan
2. Add a Thread Group
3. Add a Sampler > SSH SFTP
4. Specify the host to connect to, port, username and password (unencrypted) or a key file, and a SFTP command to execute (such as get)
5. Specify a source file to download, an output file or to print the content in the test
5. Add a Listener > View Results Tree
6. Run the test

### SSH Open Persistent Named Shell Channel

TBC

### SSH Persistent Shell Channel Send Command

TBC

### SSH Open Named Connection And Action On SFTP Channel

TBC


Dependencies
------------

Maven retrieves the following dependencies:

* SSH functionality is provided by the JSch library
* JMeter 2.3+ should be capable of running this plugin, tested mainly on 3.1

Built with
-----------
Eclipse Oxygen and Eclipse Mars on Windows, but should work fine with other environments too

Contributing
------------

1. Fork it.
2. Create a branch (`git checkout -b my_plugin`)
3. Commit your changes (`git commit -am "Added feature"`)
4. Push to the branch (`git push origin my_plugin`)
5. Create an issue with a link to your branch
6. Be patient and wait
