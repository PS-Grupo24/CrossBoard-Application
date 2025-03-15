package org.example.project.model

data class Square(val row:Row, val column: Column){
    override fun toString(): String {
        return "(${row.number}),(${column.symbol})"
    }
}