@echo off 
echo now : %~dp0
SET AGENT_LIB=%~dp0..\lib
@rem some Java parameters

if not defined JAVA_HOME (
  echo "Error: JAVA_HOME is not set."
  goto P
)

SET JAVA=%JAVA_HOME%\bin\java

@rem  CLASSPATH initially
for /r %AGENT_LIB% %%i in (*.jar) do (
    call set CLASSPATH=%%CLASSPATH%%;%%i
)
@rem get arguments
echo %CLASSPATH%

call %JAVA% -cp %CLASSPATH% com.on36.haetae.tools.CommandStartup %*
  
pause

:P 
pause 
exit 