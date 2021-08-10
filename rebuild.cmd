@echo off
set PATH=C:\Programme\Java\jdk1.5.0_09\bin;%PATH%
set JAVA_HOME=C:\Programme\Java\jdk1.5.0_09\bin
call ant -buildfile build.xml rebuild
pause