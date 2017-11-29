package pl.brostudio.ReactiveSpringClient

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient

@SpringBootApplication
class ReactiveSpringClientApplication {

    @Bean
    fun webClient():WebClient {
        return WebClient.create("http://localhost:8080/rest/employee")
    }

    @Bean
    fun commandLineRunner(webClient: WebClient) = CommandLineRunner {
        webClient.get()
                .uri("/all")
                .retrieve()
                .bodyToFlux(Employee::class.java)
                .filter{ it.name == "Marcin Nowakowski"}
                .flatMap { employee -> webClient
                        .get()
                        .uri("/{id}/events", employee.id)
                        .retrieve()
                        .bodyToFlux(EmployeeEvent::class.java)
                }
                .subscribe{print("${it}")}
    }
}

fun main(args: Array<String>) {
    runApplication<ReactiveSpringClientApplication>(*args)
}
