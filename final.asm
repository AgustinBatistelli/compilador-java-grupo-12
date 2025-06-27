include macros2.asm
include number.asm
.model LARGE
.386
.stack 200h
.data
    varFloat1 dd ?
    varFloat2 dd ?
    varFloat3 dd ?
    varFloat4 dd ?
    varFloat5 dd ?
    varInt1 dd ?
    varInt2 dd ?
    varInt3 dd ?
    varInt4 dd ?
    varInt5 dd ?
    varInt6 dd ?
    varInt7 dd ?
    varInt8 dd ?
    varInt9 dd ?
    varInt10 dd ?
    varInt11 dd ?
    varInt12 dd ?
    varInt13 dd ?
    varInt14 dd ?
    varStr1 db "", '$'
    _cte3 dd 3.0
    _cte1 dd 1.0
    _cte2 dd 2.0
    @cant dd ?
    @suma dd ?
    @multi dd ?
    _cte0 dd 0.0
.code
start:
    mov ax,@data
    mov ds,ax
    FLD _cte3
    FSTP varFloat1
    FLD _cte1
    FSTP varFloat2
    FLD _cte2
    FSTP varFloat3
    ; IF > varFloat1 varFloat2
    FLD varFloat1
    FLD varFloat2
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JBE ELSE1
    ; IF > varFloat3 varFloat2
    FLD varFloat3
    FLD varFloat2
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JBE ELSE1
    DisplayFloat varFloat1, 2
    
    ELSE1:
    ENDIF1:

    mov ax, 4C00h
    int 21h
end start
