# 本地环境
运行：mvn spring-boot:run

http://localhost:8080/

# AREX命令

## JVM  参数
-javaagent:/Users/lijialing/Dev/arex-standalone-all/arex-agent.jar
-Darex.service.name=46e0b16f4a7f62c0
-Darex.storage.service.host=127.0.0.1:8093
-Darex.enable.debug=true

## 启动
docker-compose up -d

## 停止
docker-compose down -v


# Moonbox

## Java Agent
-javaagent:/Users/lijialing/Git/jd/arex/MoonBox/local-agent/moonbox-agent.tar
-DappName=todolist
-Dmoonbox.app.name=todolist
-Dmoonbox.server=http://127.0.0.1:9999


curl -0 sandboxDownLoad.tar http://127.0.0.1:9999/api/agent/downLoadSandBoxZipFile

任务启动参数：
curl -0 sandboxDownLoad.tar http://127.0.0.1:9999/api/agent/downLoadSandBoxZipFile 
&& curl -0 moonboxDownLoad.tar http://127.0.0.1:9999/api/agent/downLoadMoonBoxzipFile
&& mm -fr ~/sandbox && rm -fr ~/.sandbox-module 
&& tar -xzf sandboxDownLoad.tar -C ~/>> /dev/null 
&& tar -xzf moonboxDownLoad.tar -C ~/>> /dev/null 
&& dos2unix ~/sandbox/bin/sandbox.sh 
&& dos2unix ~/.sandbox-module/bin/start-remote-agent.sh 
&& rm -f moonboxDownLoad.tar sandboxDownLoad.tar 
&& sh ~/.sandbox- module/bin/start-remote-agent.sh moon-box-web rc_id_ be6lef782bd8435616b465ef06159c61%26http%3A%2F%2F127.0.0.1%3A8080%26INFO%26INFO
