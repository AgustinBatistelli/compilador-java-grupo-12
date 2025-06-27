@echo off
PATH=C:\TASM;

tasm final.asm
IF ERRORLEVEL 1 goto error

tasm numbers.asm
IF ERRORLEVEL 1 goto error

TLINK /3 /x /v final.obj numbers.obj
IF ERRORLEVEL 1 goto error

final
pause
goto end

:error
echo Hubo errores. Presiona una tecla para continuar...
pause

:end
