package com.nelson.aguiar.graaltest.service

import com.nelson.aguiar.graaltest.http.client.ClientProduct
import com.nelson.aguiar.graaltest.http.client.InventoryClient
import com.nelson.aguiar.graaltest.http.client.resources.Inventory
import com.nelson.aguiar.graaltest.http.client.resources.Product
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.awaitSingle
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Service
class AsyncProductsService(
    val clientProduct: ClientProduct,
    val executorService: ExecutorService = Executors.newVirtualThreadPerTaskExecutor(),
    val inventoryClient: InventoryClient
) {
    private val logger = KLogging().logger("AsyncProductsService")

    suspend fun getProductsFlowExample(products: List<String>) {
        coroutineScope {
            productFlow(products)
                .buffer()
                .map {
                    val inventory = async { inventoryClient.getInventory(it.idProduto) }
                    it.inventory = inventory.await()
                    System.err.println("Produto enriquecido => $it")
                }.collect()
        }


        /*productFlow(products)
            .onCompletion {
                System.err.println("Cabou")
            }.collect {
                System.err.println("pegou sacoisa aqui: $it")
            }*//*.onEach {
                    System.err.println("OnEach pegou sacoisa aqui: $it")
                }*/

    }

    suspend fun getProducts(skus: List<String>) {
        withContext(Dispatchers.IO) {
            skus.pmap { p ->
                try {
                    val product = async {
                        //System.err.println("getting sku $p  -- thread: ${Thread.currentThread().id}")
                        clientProduct.getProduct(p)
                    }
                    val p2 = async {
                        //System.err.println("Async block two getting sku $p  -- thread: ${Thread.currentThread().id}")
                        inventoryClient.getInventory(p)
                    }
                    product.await().inventory = p2.await()
                    System.err.println("===> ${product.await()} }")
                } catch (exception: Exception) {
                    System.err.println(exception.stackTrace)
                    // throw exception
                }
            }
        }
    }



    suspend fun productFlow(products: List<String>): Flow<Product> {
        return channelFlow {
            products.pmap { p ->
                try {
                    if (p.equals("110")) throw RuntimeException("110 Brugou")
                    if (p.equals("500")) delay(5000)
                    System.err.println("getting sku $p  -- thread: ${Thread.currentThread().id}")
                    val pro = async { clientProduct.getProduct(p) }
                    send(pro.await())
                } catch (e: Exception) {
                    System.err.println("aqui num sei qq deu ===> $e")
                }

            }
        }.flowOn(Dispatchers.IO)
    }

    fun <A, B> List<A>.pmap(f: suspend (A) -> B): List<B> = runBlocking {
        map { async { f(it) } }.map { it.await() }
    }

}
