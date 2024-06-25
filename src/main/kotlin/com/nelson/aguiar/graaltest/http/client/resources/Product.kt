package com.nelson.aguiar.graaltest.http.client.resources

import com.fasterxml.jackson.annotation.JsonProperty

data class Product(val idProduto: String,
                   @JsonProperty("SKU")
                   val sku: String,
                   @JsonProperty("nomeProduto")
                   val nome: String?) {

    override fun toString(): String {
        return "idProduto: $idProduto => sku: $sku, nome: $nome"
    }
}
