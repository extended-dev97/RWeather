package com.extended.rweather.test

open class Example {
    open fun printString(): Int {
        return 111
    }
}

class Example2: Example() {
    override fun printString(): Int {
        return super.printString() + 100
    }
}

// var str = Example2().printString()