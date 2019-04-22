echo "        db.h2 : " `ps aux | grep h2-1.4.192.jar    | grep java | awk '{print  $2}'`
echo "status-holder : " `ps aux | grep status-holder.jar | grep java | awk '{print  $2}'`
echo "     uploader : " `ps aux | grep uploader.jar      | grep java | awk '{print  $2}'`
echo "     deployer : " `ps aux | grep deployer.jar      | grep java | awk '{print  $2}'`
echo "      monitor : " `ps aux | grep monitor.jar       | grep java | awk '{print  $2}'`