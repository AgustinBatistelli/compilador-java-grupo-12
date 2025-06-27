include macros2.asm
include number.asm
.model LARGE
.386
.stack 200h
.data
    _cteStr1 db "a es más grande que b y c es más grande que b", '$'
    _cteStr2 db "a es más grande que b", '$'
    _cteStr3 db "a es más chico o igual a b", '$'
    _cteStr4 db "a no es más grande que b", '$'
    _cteStr5 db "a es más grande que b o c es más grande que b", '$'
    _cteStr6 db "ewr", '$'
    _cteStr7 db "c es mayor que a, que es mayor que b", '$'
    _cteStr8 db "a es mayor que b pero c no es mayor que a", '$'
    _cteStr9 db "b es mayor o igual a a", '$'
    _cteStr10 db "todos los valores coinciden", '$'
    _cteStr11 db "a es menor que b y b es menor que c", '$'
    _cteStr12 db "a no es igual a b", '$'
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
    varStr1 db 50 dup(?)
    _cte130814 dd 130814.0
    _cte2 dd 2.0
    _cte4 dd 4.0
    _cte21 dd 21.0
    _cte1 dd 1.0
    
    _cte10 dd 10.0
    _cte8 dd 8.0
    _cte27 dd 27.0
    _cte500 dd 500.0
    _cte34 dd 34.0
    _cte3 dd 3.0
    _ctef99999dot99 dd 99999.99
    _ctef99dot0 dd 99.0
    _ctef0dot9999 dd 0.9999
    _cteStr13 db "@sdADaSjfla%dfg", '$'
    _cteStr14 db "asldk  fh sjf", '$'
    
    
    
    
    
    _cte5 dd 5.0
    _cte7 dd 7.0
    
    
    
    
    _cte75 dd 75.0
    
    
    _ctef4dot1 dd 4.1
    _ctefm1dot5 dd -1.5
    _ctef3dot0 dd 3.0
    _ctefm4dot0 dd -4.0
    _ctefm2dot0 dd -2.0
    _ctef2dot5 dd 2.5
    _ctefm3dot0 dd -3.0
    _ctef5dot5 dd 5.5
    _ctef1dot5 dd 1.5
    _ctefm3dot5 dd -3.5
    _ctefm6dot0 dd -6.0
    _ctef1dot0 dd 1.0
    _ctef2dot0 dd 2.0
    @cant dd ?
    @suma dd ?
    @multi dd ?
    _cte0 dd 0.0
.code
start:
    mov ax,@data
    mov ds,ax
    FLD _cte130814
    FSTP varInt11
    FLD _cte2
    FSTP varFloat4
    FLD _cte4
    FSTP varInt1
    FLD varFloat4
    FLD varInt1
    FLD _cte21
    FSUB
    FMUL
    FLD _cte4
    FDIV
    FSTP varFloat3
    FLD _cte1
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
    mov ah, 09h
    mov dx, offset _cteStr1
    int 21h
    
    ELSE1:
    ENDIF1:
    FLD _cte10
    FSTP varInt6
    FLD _cte8
    FSTP varInt7
    FLD _cte4
    FSTP varInt2
    FLD _cte27
    FLD varFloat3
    FSUB
    FSTP varFloat5
    FLD varInt6
    FLD _cte500
    FADD
    FSTP varFloat5
    FLD _cte34
    FLD _cte3
    FMUL
    FSTP varFloat5
    FLD varInt7
    FLD varInt2
    FDIV
    FSTP varFloat5
    FLD _ctef99999dot99
    FSTP varFloat1
    FLD _ctef99dot0
    FSTP varFloat1
    FLD _ctef0dot9999
    FSTP varFloat1
    ; Asignación de string _cteStr13 a varStr1
    lea si, _cteStr13
    lea di, varStr1
    copy_string_loop_0:
    lodsb
    stosb
    cmp al, '$'
    jne copy_string_loop_0
    ; Asignación de string _cteStr14 a varStr1
    lea si, _cteStr14
    lea di, varStr1
    copy_string_loop_1:
    lodsb
    stosb
    cmp al, '$'
    jne copy_string_loop_1
    ; IF > varFloat1 varFloat2
    FLD varFloat1
    FLD varFloat2
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JBE ELSE2
    mov ah, 09h
    mov dx, offset _cteStr2
    int 21h
    
    JMP ENDIF2
    ELSE2:
    mov ah, 09h
    mov dx, offset _cteStr3
    int 21h
    
    ENDIF2:
    FLD _cte1
    FSTP varFloat1
    FLD _cte1
    FSTP varFloat2
    FLD _cte2
    FSTP varFloat3
    ; NOT > varFloat1 varFloat2
    FLD varFloat1
    FLD varFloat2
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JA ELSE3
    mov ah, 09h
    mov dx, offset _cteStr4
    int 21h
    
    ELSE3:
    ENDIF3:
    FLD _cte1
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
    JBE COND_OR_4
    ; IF > varFloat3 varFloat2
    FLD varFloat3
    FLD varFloat2
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JBE ELSE4
    COND_OR_4:
    mov ah, 09h
    mov dx, offset _cteStr5
    int 21h
    
    ELSE4:
    ENDIF4:
    GetFloat varInt13
    FLD varInt13
    FRNDINT 
    FSTP varInt13
    FLD _cte1
    FSTP varFloat1
    FLD _cte3
    FSTP varFloat2
    WHILE_START1:
    ; WHILE varFloat1 > varFloat2
    FLD varFloat1
    FLD varFloat2
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JBE WHILE_END1
    mov ah, 09h
    mov dx, offset _cteStr2
    int 21h
    
    FLD varFloat1
    FLD _cte1
    FADD
    FSTP varFloat1
    JMP WHILE_START1
    WHILE_END1:
    mov ah, 09h
    mov dx, offset _cteStr6
    int 21h
    
    DisplayFloat varInt11, 2
    
    FLD _cte5
    FSTP varFloat1
    FLD _cte3
    FSTP varFloat2
    FLD _cte7
    FSTP varFloat3
    ; IF > varFloat1 varFloat2
    FLD varFloat1
    FLD varFloat2
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JBE ELSE5
    ; IF > varFloat3 varFloat1
    FLD varFloat3
    FLD varFloat1
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JBE ELSE6
    mov ah, 09h
    mov dx, offset _cteStr7
    int 21h
    
    JMP ENDIF6
    ELSE6:
    mov ah, 09h
    mov dx, offset _cteStr8
    int 21h
    
    ENDIF6:
    JMP ENDIF5
    ELSE5:
    mov ah, 09h
    mov dx, offset _cteStr9
    int 21h
    
    ENDIF5:
    ; IF == varFloat1 5
    FLD varFloat1
    FLD _cte5
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JNE ELSE7
    ; IF == varFloat2 3
    FLD varFloat2
    FLD _cte3
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JNE ELSE8
    ; IF == varFloat3 7
    FLD varFloat3
    FLD _cte7
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JNE ELSE9
    mov ah, 09h
    mov dx, offset _cteStr10
    int 21h
    
    ELSE9:
    ENDIF9:
    ELSE8:
    ENDIF8:
    ELSE7:
    ENDIF7:
    FLD _cte75
    FSTP varFloat5
    FLD _cte8
    FSTP varInt5
    FLD _cte3
    FSTP varInt7
    FLD _cte4
    FLD _cte5
    FADD
    FLD _cte7
    FLD _cte2
    FSUB
    FMUL
    FLD _cte3
    FDIV
    FSTP varFloat1
    FLD varInt7
    FLD varInt5
    FLD varInt7
    FSUB
    FDIV
    FSTP varFloat2
    FLD varInt14
    FLD varInt6
    FLD varInt14
    FADD
    FDIV
    FSTP varFloat3
    FLD _cte3
    FSTP varFloat1
    FLD _cte4
    FSTP varFloat2
    FLD _cte5
    FSTP varFloat3
    ; IF < varFloat1 varFloat2
    FLD varFloat1
    FLD varFloat2
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE10
    ; IF < varFloat2 varFloat3
    FLD varFloat2
    FLD varFloat3
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE10
    mov ah, 09h
    mov dx, offset _cteStr11
    int 21h
    
    ELSE10:
    ENDIF10:
    ; NOT == varFloat1 varFloat2
    FLD varFloat1
    FLD varFloat2
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JE ELSE11
    mov ah, 09h
    mov dx, offset _cteStr12
    int 21h
    
    ELSE11:
    ENDIF11:
    FLD _cte0
    FSTP varInt12
    WHILE_START2:
    ; WHILE varInt12 < 10
    FLD varInt12
    FLD _cte10
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE WHILE_END2
    DisplayFloat varInt12, 2
    
    FLD varInt12
    FLD _cte1
    FADD
    FSTP varInt12
    JMP WHILE_START2
    WHILE_END2:
    FLD _ctef4dot1
    FSTP varFloat1
    FLD _ctefm1dot5
    FSTP varFloat2
    FLD _ctef3dot0
    FSTP varFloat3
    FLD _cte0
    FSTP @cant
    FLD _cte0
    FSTP @suma
    FLD _cte1
    FSTP @multi
    ; IF < varFloat3 0
    FLD varFloat3
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE12
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD varFloat3
    FMUL
    FSTP @multi
    FLD @suma
    FLD varFloat3
    FADD
    FSTP @suma
    ELSE12:
    ENDIF12:
    ; IF < varFloat2 0
    FLD varFloat2
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE13
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD varFloat2
    FMUL
    FSTP @multi
    FLD @suma
    FLD varFloat2
    FADD
    FSTP @suma
    ELSE13:
    ENDIF13:
    ; IF < varFloat1 0
    FLD varFloat1
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE14
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD varFloat1
    FMUL
    FSTP @multi
    FLD @suma
    FLD varFloat1
    FADD
    FSTP @suma
    ELSE14:
    ENDIF14:
    ; IF < -4.0 0
    FLD _ctefm4dot0
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE15
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD _ctefm4dot0
    FMUL
    FSTP @multi
    FLD @suma
    FLD _ctefm4dot0
    FADD
    FSTP @suma
    ELSE15:
    ENDIF15:
    ; IF < -2.0 0
    FLD _ctefm2dot0
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE16
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD _ctefm2dot0
    FMUL
    FSTP @multi
    FLD @suma
    FLD _ctefm2dot0
    FADD
    FSTP @suma
    ELSE16:
    ENDIF16:
    ; IF == %% 0
    FLD @cant
    FLD _cte2
    FPREM
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JNE ELSE17
    FLD @suma
    FSTP varFloat5
    JMP ENDIF17
    ELSE17:
    FLD @multi
    FSTP varFloat5
    ENDIF17:
    FLD _ctefm1dot5
    FSTP varFloat1
    FLD _ctef2dot5
    FSTP varFloat2
    FLD _ctefm3dot0
    FSTP varFloat3
    FLD _ctef5dot5
    FSTP varFloat4
    FLD _cte0
    FSTP @cant
    FLD _cte0
    FSTP @suma
    FLD _cte1
    FSTP @multi
    ; IF < varFloat4 0
    FLD varFloat4
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE18
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD varFloat4
    FMUL
    FSTP @multi
    FLD @suma
    FLD varFloat4
    FADD
    FSTP @suma
    ELSE18:
    ENDIF18:
    ; IF < varFloat3 0
    FLD varFloat3
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE19
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD varFloat3
    FMUL
    FSTP @multi
    FLD @suma
    FLD varFloat3
    FADD
    FSTP @suma
    ELSE19:
    ENDIF19:
    ; IF < varFloat2 0
    FLD varFloat2
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE20
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD varFloat2
    FMUL
    FSTP @multi
    FLD @suma
    FLD varFloat2
    FADD
    FSTP @suma
    ELSE20:
    ENDIF20:
    ; IF < varFloat1 0
    FLD varFloat1
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE21
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD varFloat1
    FMUL
    FSTP @multi
    FLD @suma
    FLD varFloat1
    FADD
    FSTP @suma
    ELSE21:
    ENDIF21:
    ; IF < 3.0 0
    FLD _ctef3dot0
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE22
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD _ctef3dot0
    FMUL
    FSTP @multi
    FLD @suma
    FLD _ctef3dot0
    FADD
    FSTP @suma
    ELSE22:
    ENDIF22:
    ; IF == %% 0
    FLD @cant
    FLD _cte2
    FPREM
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JNE ELSE23
    FLD @suma
    FSTP varFloat5
    JMP ENDIF23
    ELSE23:
    FLD @multi
    FSTP varFloat5
    ENDIF23:
    FLD _cte0
    FSTP @cant
    FLD _cte0
    FSTP @suma
    FLD _cte1
    FSTP @multi
    ; IF < 5.5 0
    FLD _ctef5dot5
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE24
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD _ctef5dot5
    FMUL
    FSTP @multi
    FLD @suma
    FLD _ctef5dot5
    FADD
    FSTP @suma
    ELSE24:
    ENDIF24:
    ; IF < 3.0 0
    FLD _ctef3dot0
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE25
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD _ctef3dot0
    FMUL
    FSTP @multi
    FLD @suma
    FLD _ctef3dot0
    FADD
    FSTP @suma
    ELSE25:
    ENDIF25:
    ; IF < 1.5 0
    FLD _ctef1dot5
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE26
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD _ctef1dot5
    FMUL
    FSTP @multi
    FLD @suma
    FLD _ctef1dot5
    FADD
    FSTP @suma
    ELSE26:
    ENDIF26:
    ; IF == %% 0
    FLD @cant
    FLD _cte2
    FPREM
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JNE ELSE27
    FLD @suma
    FSTP varFloat5
    JMP ENDIF27
    ELSE27:
    FLD @multi
    FSTP varFloat5
    ENDIF27:
    FLD _cte0
    FSTP @cant
    FLD _cte0
    FSTP @suma
    FLD _cte1
    FSTP @multi
    ; IF < -3.5 0
    FLD _ctefm3dot5
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE28
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD _ctefm3dot5
    FMUL
    FSTP @multi
    FLD @suma
    FLD _ctefm3dot5
    FADD
    FSTP @suma
    ELSE28:
    ENDIF28:
    ; IF == %% 0
    FLD @cant
    FLD _cte2
    FPREM
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JNE ELSE29
    FLD @suma
    FSTP varFloat5
    JMP ENDIF29
    ELSE29:
    FLD @multi
    FSTP varFloat5
    ENDIF29:
    FLD _ctefm2dot0
    FSTP varFloat1
    FLD _ctefm4dot0
    FSTP varFloat2
    FLD _ctefm6dot0
    FSTP varFloat3
    FLD _cte0
    FSTP @cant
    FLD _cte0
    FSTP @suma
    FLD _cte1
    FSTP @multi
    ; IF < varFloat3 0
    FLD varFloat3
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE30
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD varFloat3
    FMUL
    FSTP @multi
    FLD @suma
    FLD varFloat3
    FADD
    FSTP @suma
    ELSE30:
    ENDIF30:
    ; IF < varFloat2 0
    FLD varFloat2
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE31
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD varFloat2
    FMUL
    FSTP @multi
    FLD @suma
    FLD varFloat2
    FADD
    FSTP @suma
    ELSE31:
    ENDIF31:
    ; IF < varFloat1 0
    FLD varFloat1
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE32
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD varFloat1
    FMUL
    FSTP @multi
    FLD @suma
    FLD varFloat1
    FADD
    FSTP @suma
    ELSE32:
    ENDIF32:
    ; IF == %% 0
    FLD @cant
    FLD _cte2
    FPREM
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JNE ELSE33
    FLD @suma
    FSTP varFloat5
    JMP ENDIF33
    ELSE33:
    FLD @multi
    FSTP varFloat5
    ENDIF33:
    FLD _ctef1dot0
    FSTP varFloat4
    FLD _ctef2dot0
    FSTP varFloat5
    FLD _ctef3dot0
    FSTP varFloat1
    FLD _cte0
    FSTP @cant
    FLD _cte0
    FSTP @suma
    FLD _cte1
    FSTP @multi
    ; IF < varFloat1 0
    FLD varFloat1
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE34
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD varFloat1
    FMUL
    FSTP @multi
    FLD @suma
    FLD varFloat1
    FADD
    FSTP @suma
    ELSE34:
    ENDIF34:
    ; IF < varFloat5 0
    FLD varFloat5
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE35
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD varFloat5
    FMUL
    FSTP @multi
    FLD @suma
    FLD varFloat5
    FADD
    FSTP @suma
    ELSE35:
    ENDIF35:
    ; IF < varFloat4 0
    FLD varFloat4
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JAE ELSE36
    FLD @cant
    FLD _cte1
    FADD
    FSTP @cant
    FLD @multi
    FLD varFloat4
    FMUL
    FSTP @multi
    FLD @suma
    FLD varFloat4
    FADD
    FSTP @suma
    ELSE36:
    ENDIF36:
    ; IF == %% 0
    FLD @cant
    FLD _cte2
    FPREM
    FLD _cte0
    FXCH
    FCOMPP
    FSTSW AX
    SAHF
    JNE ELSE37
    FLD @suma
    FSTP varFloat5
    JMP ENDIF37
    ELSE37:
    FLD @multi
    FSTP varFloat5
    ENDIF37:

    mov ax, 4C00h
    int 21h
end start
