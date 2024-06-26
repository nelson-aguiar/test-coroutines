package com.nelson.aguiar.graaltest.http.client

import com.nelson.aguiar.graaltest.http.client.resources.Inventory
import com.nelson.aguiar.graaltest.http.client.resources.Product
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import reactor.core.publisher.Mono

@HttpExchange(url = "/inventory/products", contentType = "pplication/json",accept = ["application/json"])
interface InventoryClient {
    @GetExchange("/{idProduto}")
    suspend fun getInventory(@PathVariable("idProduto") idProduto: String): Inventory
    @GetExchange("/{idProduto}")
    fun getInventoryReactive(@PathVariable("idProduto") idProduto: String): Mono<Inventory>
}

