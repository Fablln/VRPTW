@echo off 

set MYPROG=java -jar solverVRP.jar

for /F "tokens=1,2 delims= " %%i in (instances01.txt) do (
	echo %MYPROG% -i %%i %%j
	%MYPROG% -i %%i %%j
)

set MYPROG=java -jar solverVRP.jar

for /F "tokens=1,2 delims= " %%i in (instances02.txt) do (
	echo %MYPROG% -i %%i %%j
	%MYPROG% -i %%i %%j
)

set MYPROG=java -jar solverVRP.jar

for /F "tokens=1,2 delims= " %%i in (instances03.txt) do (
	echo %MYPROG% -i %%i %%j
	%MYPROG% -i %%i %%j
)
