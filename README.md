# 本地环境
运行：mvn spring-boot:run

http://localhost:8080/

# AREX命令

## JVM  参数
-javaagent:{your_path}/arex-agent.jar
-Darex.service.name=46e0b16f4a7f62c0
-Darex.storage.service.host=127.0.0.1:8093
-Darex.enable.debug=true

## 启动
docker-compose up -d

## 停止
docker-compose down -v