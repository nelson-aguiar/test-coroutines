package com.nelson.aguiar.graaltest.http.client

import com.nelson.aguiar.graaltest.http.client.resources.Product
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import reactor.core.publisher.Mono

@HttpExchange(url = "/products", contentType = "pplication/json",accept = ["application/json"])
interface ClientHitHub {

    @GetExchange("/{sku}")
    suspend fun getProducts(@PathVariable("sku") sku: String): Product
    @GetExchange("/{sku}")
    fun getProductsNonCouroutines(@PathVariable("sku") sku: String): Mono<Product>

}