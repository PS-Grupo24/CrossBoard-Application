package org.example.project.model

class Row private constructor(val index:Int, boardDim:Int) {
    val number = boardDim - index

    companion object{
        operator fun invoke(i:Int, boardDim: Int):Row{
            require(i in 0..<boardDim){"Index must be between 0 and Board Dimension."}
            return Row(i, boardDim)
        }
    }
}