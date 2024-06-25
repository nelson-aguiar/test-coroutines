package com.nelson.aguiar.graaltest

import org.junit.Test


class Test {

    private val list: List<Item> = mutableListOf()

    @Test
    fun test() {
        for(i in 1..20){
            val item = Item("teste $i", "${100+i}" )
            list.addLast(item)
        }
        list.forEach{
            System.err.println(it.toString())
        }
    }

    data class Item(val nome: String, val code: String)


}