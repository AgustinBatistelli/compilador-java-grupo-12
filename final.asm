.model small
.stack 100h
.data
.data
    x dd ?
    _cte3 dd 3
    _cte1 dd 1
    _cte9 dd 9
.code
start:
    FLD _cte9
    FLD x
    FSUB
    
    FLD _cte1
    FLD _cte1
    FADD
    
    FLD x
    FLD _cte3
    FADD
    

    mov ax, 4C00h
    int 21h
end start
