package com.nelson.aguiar.graaltest.service

import com.nelson.aguiar.graaltest.http.client.ClientHitHub
import mu.KLogging
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.jobs.context.JobContext
import org.jobrunr.jobs.filters.JobFilter
import org.jobrunr.scheduling.BackgroundJob
import org.jobrunr.scheduling.JobScheduler
import org.jobrunr.storage.StorageProvider
import org.springframework.stereotype.Service
import java.util.LinkedList
import java.util.Stack
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.IntStream

@Service
class SampleJobService(val clientHitHub: ClientHitHub, val storageProvider: StorageProvider) {

    private val count = AtomicInteger()

    @Job(name = "The sample job with variable %0", retries = 2)
    fun executeSampleJob(variable: String?= "non-named", passedList: MutableList<String>, jobContext: JobContext) {
        logger.info("The sample job has begun. The variable you passed is {} size:{}", variable, passedList.size)

        val progressBar = jobContext.progressBar(passedList.size)

        passedList.forEachIndexed{index, e ->
            BackgroundJob.enqueue{clientHitHub.getProductsNonCouroutines(e)}
            Thread.sleep(5000)
            progressBar.increaseByOne()
            logger.info { jobContext.logger().info("executado indice $index")}
        }

        /*val listOfProcess: List<String> = mutableListOf()
        try {
            Thread.sleep(EXECUTION_TIME)

            passedList.forEach{clientHitHub.getProductsNonCouroutines(it)
                Thread.sleep(10000)
                logger.info { clientHitHub.getProductsNonCouroutines(it) }
                listOfProcess.addLast(it)
            }
        } catch (e: InterruptedException) {

            passedList.removeAll(listOfProcess)
            logger.error("Error while executing sample job named = $variable", e)
        } finally {
            count.incrementAndGet()
            logger.info("Sample job $variable has finished...")
        }*/
    }

    val numberOfInvocations: Int
        get() = count.get()

    companion object:KLogging() {
        const val EXECUTION_TIME: Long = 30000L
    }
}