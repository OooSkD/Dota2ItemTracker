package com.steammarketanalyzer.dota2itemtracker.utils;


import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.LoopResources;
import reactor.netty.transport.ProxyProvider;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class IpCheck {
    public static void main(String[] args) {
        String filePath = "src/main/resources/other/proxies.txt"; // Файл с прокси
        List<String> proxyList;

        // Читаем файл
        try {
            proxyList = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
            return;
        }

        // Обрабатываем каждый прокси
        for (String proxyLine : proxyList) {
            String[] parts = proxyLine.split(":");
            if (parts.length != 2) {
                System.err.println(proxyLine + " error (неверный формат)");
                continue;
            }

            String proxyHost = parts[0].trim();
            int proxyPort;

            try {
                proxyPort = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                System.err.println(proxyLine + " error (некорректный порт)");
                continue;
            }

            // Проверяем прокси
            checkProxy(proxyHost, proxyPort, proxyLine);
        }
    }

    private static void checkProxy(String proxyHost, int proxyPort, String originalLine) {
        // Настроим HttpClient с прокси
        HttpClient httpClient = HttpClient.create()
                .proxy(proxy -> proxy
                        .type(ProxyProvider.Proxy.HTTP)
                        .host(proxyHost)
                        .port(proxyPort))
                .secure(t -> t.sslContext(SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE))) // Отключаем проверку сертификатов
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(5));

        // Создадим WebClient с этим HttpClient
        WebClient client = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("https://api64.ipify.org?format=json")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        // Отправляем запрос
        try {
            String response = client.get()
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.err.println("!!! " +originalLine + " " + response);
            System.err.println("!!!");
        } catch (WebClientRequestException e) {
            System.err.println(originalLine + " error");
        }
    }
}


//43.153.91.13:13001
//43.135.147.140:13001
//43.153.92.57:13001
//43.153.62.242:13001
//170.106.170.3:13001
//43.153.78.139:13001
