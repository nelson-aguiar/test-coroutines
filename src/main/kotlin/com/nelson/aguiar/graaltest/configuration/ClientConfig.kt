package com.nelson.aguiar.graaltest.configuration

import com.nelson.aguiar.graaltest.http.client.ClientHitHub
import io.netty.channel.ChannelOption
import io.netty.channel.epoll.EpollChannelOption
import mu.KLogging
import org.jobrunr.jobs.mappers.JobMapper
import org.jobrunr.storage.InMemoryStorageProvider
import org.jobrunr.storage.StorageProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration
import java.util.function.Consumer


@Configuration
class ClientConfig(@Value("\${api.github.url}") var url: String) {

    private val logger = KLogging().logger("ClientConfig")

//    @Bean
//    fun storageProvider(jobMapper: JobMapper?): StorageProvider {
//        val storageProvider = InMemoryStorageProvider()
//        storageProvider.setJobMapper(jobMapper)
//        return storageProvider
//    }

    @Bean
    fun webClient(): WebClient {

        val connectionProvider = ConnectionProvider.builder("myConnectionPool")
                .maxConnections(10000)
                .pendingAcquireMaxCount(50000)
                .pendingAcquireTimeout(Duration.ofSeconds(120))
                .maxIdleTime(Duration.ofSeconds(60))
                .maxLifeTime(Duration.ofSeconds(60))
                .build();
        val client = HttpClient.create(connectionProvider).wiretap(true)
                .responseTimeout(Duration.ofSeconds(120))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(EpollChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                //.doOnConnected{conn -> conn.channel()}
        val clientHttpConnector = ReactorClientHttpConnector(client)

        System.err.println(url)
        return WebClient.builder()
                .baseUrl(url)
                .clientConnector(clientHttpConnector)
//                .filters {
//                    it.add(logRequest())
//                    it.add(logResponse())
//                }
                .build()
    }

    @Bean
    fun toDoService(): ClientHitHub {
        val httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                WebClientAdapter.create(webClient())).build()
        return httpServiceProxyFactory.createClient(ClientHitHub::class.java)
    }

    private fun logRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { clientRequest: ClientRequest ->
            logger.info("RequestLog: {} {}", clientRequest.method(), clientRequest.url())
            clientRequest.headers().forEach { name: String?, values: List<String?> -> values.forEach(Consumer<String?> { value: String? -> logger.info("{}={}", name, value) }) }
            Mono.just<ClientRequest>(clientRequest)
        }
    }

    private fun logResponse(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor() { clientResponse: ClientResponse ->
            logger.info("ResponseLog: {} {}", clientResponse.bodyToMono(String::class.java), clientResponse.headers())
            Mono.just<ClientResponse>(clientResponse)
        }
    }
}

