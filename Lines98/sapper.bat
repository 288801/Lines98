@echo off

set CD=%~dp0

rem можно указать, где лежит JDK, если без этого не находится команда java
::set JAVA_HOME=C:\Program_Files\Java\jdk8

set JAVAW=javaw
if not "%JAVA_HOME%"=="" (
  set JAVAW="%JAVA_HOME%\bin\%JAVAW%"
)

%JAVAW% -classpath "%CD%\out\production\SapperGame" ru.vsu.cs.course1.game.Main %*
