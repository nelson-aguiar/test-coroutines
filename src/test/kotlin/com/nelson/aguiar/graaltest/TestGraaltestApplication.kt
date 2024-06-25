package com.nelson.aguiar.graaltest

import org.springframework.boot.fromApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.with

@TestConfiguration(proxyBeanMethods = false)
class TestGraaltestApplication

fun main(args: Array<String>) {
	fromApplication<GraaltestApplication>().with(TestGraaltestApplication::class).run(*args)
}
