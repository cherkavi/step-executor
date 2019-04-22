# Instruction how to update remote host

* build all artifacts
```
./01_build.bat 
```

* start virtual machine ( VirtualBox should be installed )
```
vagrant up
```

* connect to remote machine
```
vagrant ssh
```

* check destination point for the script
1. *inventory.ini* file should contains right ip address
2. *playbook.yml* file "vars" block contains all ports and root folder for installation
* execute script 
```
./02_update-remote-host.sh ssh_user=<your user name> ssh_password=<your password>
```
* shutdown virtual machine
```
vagrant halt
```
* execute DDL for database on remote host
```
./status-holder-db/start.sh
./status-holder/start-with-update.sh
./stop-all.sh
```
