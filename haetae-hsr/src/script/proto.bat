@echo off 
cd %~dp0
protoc-3.1.exe --java_out=../main/java message.proto

@echo compeleted
pause