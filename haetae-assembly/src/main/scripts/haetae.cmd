@echo off 
SET AGENT_LIB=%~dp0\..\lib
@rem some Java parameters

if not defined JAVA_HOME (
  echo "Error: JAVA_HOME is not set."
  goto P
)

SET JAVA=%JAVA_HOME%\bin\java
SET CLASSPATH=;
@rem  CLASSPATH initially
for /r %AGENT_LIB% %%i in (*.jar) do (
 call set CLASSPATH=%%CLASSPATH%%;%%i
)

call set CLASSPATH=%%CLASSPATH%%;../conf

@rem get arguments
@rem echo %CLASSPATH%

call %JAVA% -cp %CLASSPATH% com.on36.haetae.tools.CommandStartup %*
  
:P
pause