java \
-Dserver.port={{ port_deployer }} \
-Dlogging.file={{ root_folder }}/deployer/deployer.log -Dlogging.path={{ root_folder }}/deployer -Dlogging.level.root=info \
-Dsteps={{ root_folder }}/deployer/steps.json -DfailSteps={{ root_folder }}/deployer/failSteps.json \
-jar {{ root_folder }}/deployer/deployer.jar
