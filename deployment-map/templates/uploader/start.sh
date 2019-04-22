java \
-Ddeployer.url=http://{{inventory_hostname}}:{{port_deployer}} \
-Dstatus-holder.url=http://{{inventory_hostname}}:{{port_status_holder}}/status \
-Dserver.port={{port_uploader}} \
-Dstorage.location={{ root_folder }}/uploader-temp \
-jar {{ root_folder }}/uploader/uploader.jar