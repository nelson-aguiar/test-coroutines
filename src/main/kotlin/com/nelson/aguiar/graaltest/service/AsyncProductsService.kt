package com.nelson.aguiar.graaltest.service

import com.nelson.aguiar.graaltest.http.client.ClientHitHub
import com.nelson.aguiar.graaltest.http.client.resources.Product
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.awaitSingle
import mu.KLogging
import org.jobrunr.jobs.context.JobContext
import org.jobrunr.scheduling.JobScheduler
import org.jobrunr.server.dashboard.DashboardNotificationManager
import org.springframework.stereotype.Service
import org.springframework.web.servlet.function.ServerResponse.async
import reactor.kotlin.adapter.rxjava.toSingle
import java.util.LinkedList
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.concurrent.thread
import kotlin.streams.toList

@Service
class AsyncProductsService(val clientHitHub: ClientHitHub,
                            val executorService: ExecutorService = Executors.newSingleThreadExecutor(),
        val jobScheduler: JobScheduler,
        val sampleJobService: SampleJobService) {
    private val logger = KLogging().logger("AsyncProductsService")

    suspend fun getProductsAndDispatcher(products: List<String>) = coroutineScope {
        supervisorScope {
            return@supervisorScope test(products)
            .onCompletion {
                System.err.println("Cabou")
            }/*.onEach {
                System.err.println("OnEach pegou sacoisa aqui: $it")
            }*/.collect{
                System.err.println("pegou sacoisa aqui: $it")
            }
        }


           /* withContext(Dispatchers.IO){
                products.pmap { p ->
                    try{
                        val product = async {
                            System.err.println("getting sku $p  -- thread: ${Thread.currentThread().id}")
                            clientHitHub.getProducts(p)
                        }
                        val p2 = async {
                            System.err.println("Async block two getting sku $p  -- thread: ${Thread.currentThread().id}")
                            clientHitHub.getProducts(p)
                        }

                        System.err.println("$p ===> ${product.await()}--${p2.await()}")
                    } catch (exception: Exception){
                        System.err.println(exception.stackTrace)
                        // throw exception
                    }

                }

        }*/


    }

    suspend fun test(products: List<String>): Flow<Product> {
        return channelFlow{
            products.pmap { p->
                    try {
                        if (p.equals("110")) throw RuntimeException("110 Brugou")
                        //System.err.println("getting sku $p  -- thread: ${Thread.currentThread().id}")
                        val pro = async{ clientHitHub.getProductsNonCouroutines(p).awaitSingle()}
                        send(pro.await())
                    }catch (e: Exception){
                        System.err.println("aqui num sei qq deu")
                    }

                }
            }.flowOn(Dispatchers.IO)
        }

    fun executeJob() : UUID{
//        jobScheduler.enqueue { sampleJobService.executeSampleJob() }
        val ids =LinkedList<String>()
       IntStream.range(1,10).forEach{
           ids.add("teste $it")
       }
       val jobId = jobScheduler.enqueue { sampleJobService.executeSampleJob(UUID.randomUUID().toString(), ids, JobContext.Null) }.asUUID()
       return jobId

    }

    fun deleteJob(jobId: UUID) {
        jobScheduler.delete(jobId)
    }

    fun <A, B>List<A>.pmap(f: suspend (A) -> B): List<B> = runBlocking {
        map { async { f(it) } }.map { it.await() }
    }

}
