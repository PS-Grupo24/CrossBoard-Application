package org.example.project.model

class Row private constructor(val index:Int) {
    val number = BOARD_DIM - index

    companion object{
        operator fun invoke(i:Int):Row{
            require(i in 0..<BOARD_DIM){"Index must be between 0 and Board Dimension."}
            return Row(i)
        }
    }
}