package com.nelson.aguiar.graaltest.http.restapi

import com.nelson.aguiar.graaltest.service.AsyncProductsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.UUID
import java.util.stream.IntStream
import kotlin.streams.toList

@RestController
@RequestMapping("/products")
class ProductsApi(val asyncProductsService: AsyncProductsService) {


    @GetMapping()
    suspend fun getProducts() :String{
        val start = Instant.now()
        val listSkus = IntStream.range(100, 10000).mapToObj { it.toString() }.toList()
        withContext(Dispatchers.IO){
            asyncProductsService.getProductsAndDispatcher(listSkus);
        }

        val end = Instant.now()

        val duration = java.time.Duration.between( start, end,)

        System.err.println("Duration:"+java.time.Duration.between( start, end,))
        return "${listSkus.size} request at ==> $duration"

    }

    @GetMapping("/job")
    suspend fun job() :String{
        val id =asyncProductsService.executeJob()
        return "execunting job id $id"
    }
    @DeleteMapping("/job/{jobId}")
    suspend fun jobDelete(@PathVariable("jobId") jobId: UUID) :String{
        asyncProductsService.deleteJob(jobId)
        return "deleting"
    }
}