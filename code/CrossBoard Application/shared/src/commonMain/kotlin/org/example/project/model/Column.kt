package org.example.project.model

class Column private constructor(val symbol:Char) {
    val index = symbol - 'a'

    companion object{
        operator fun invoke(c:Char) : Column{
            require(c in 'a' .. 'z'){"Column symbol must be between 'a' and 'z'"}
            return Column(c)
        }
    }
}