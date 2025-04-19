package com.mtraina.KafkaClient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KafkaClientApplication

fun main(args: Array<String>) {
	runApplication<KafkaClientApplication>(*args)
}
