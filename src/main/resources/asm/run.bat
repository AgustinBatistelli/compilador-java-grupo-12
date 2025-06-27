PATH=C:\TASM;
tasm final.asm
tasm numbers.asm
TLINK /3 /x /v Final.obj numbers.obj
final
pause
