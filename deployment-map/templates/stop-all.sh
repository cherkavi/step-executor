kill -9 `ps aux | grep h2-1.4.192.jar    | grep java | awk '{print $2}'`
kill -9 `ps aux | grep status-holder.jar | grep java | awk '{print $2}'`
kill -9 `ps aux | grep uploader.jar      | grep java | awk '{print $2}'`
kill -9 `ps aux | grep deployer.jar      | grep java | awk '{print $2}'`
kill -9 `ps aux | grep monitor.jar       | grep java | awk '{print $2}'`