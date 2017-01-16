@echo off 
cd %~dp0
thrift-0.9.3.exe -r -gen java message.thrift

@echo compeleted
pause