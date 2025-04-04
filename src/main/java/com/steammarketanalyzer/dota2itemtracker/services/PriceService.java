package com.steammarketanalyzer.dota2itemtracker.services;

import com.google.gson.Gson;
import com.steammarketanalyzer.dota2itemtracker.dto.PriceOverviewDto;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpHeaders;
import reactor.netty.transport.ProxyProvider;
import reactor.util.retry.Retry;
import reactor.netty.http.client.HttpClient;


import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;

@Service
public class PriceService {
    private final Gson gson = new Gson();
    private final Environment environment;
    private WebClient webClientWithoutProxy;
    private WebClient webClientWithProxy;

    @Value("${proxy.host}")
    private String proxyHost;
    @Value("${proxy.port}")
    private int proxyPort;
    @Value("${proxy.useProxy}")
    private boolean useProxy;


    @Autowired
    public PriceService(WebClient webClient, Environment environment) {
        this.environment = environment;
        this.webClientWithoutProxy = webClient;
    }

    // Инициализация после инжекта proxyHost и proxyPort
    @PostConstruct
    public void init() throws NoSuchAlgorithmException {

        // Настройка HttpClient с прокси и SSL
        HttpClient httpClient = HttpClient.create()
                .proxy(proxy -> proxy
                        .type(ProxyProvider.Proxy.HTTP)
                        .host(proxyHost)
                        .port(proxyPort))
                .secure(t -> t.sslContext(SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE))) // Отключаем проверку сертификатов
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)  // Тайм-аут подключения
                .responseTimeout(Duration.ofSeconds(5));  // Тайм-аут ответа

        // Создаем WebClient с этим HttpClient
        this.webClientWithProxy = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("https://steamcommunity.com/market") // Указываем базовый URL
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")  // Заголовки по умолчанию
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

    }


    // Метод для получения информации о ценах предметов
    public PriceOverviewDto getItemPriceOverview(String marketHashName) {
        String url = "/priceoverview?appid=570&currency=1&market_hash_name=" + marketHashName;

        // Получаем актуальное значение useProxy
        //boolean useProxy = Boolean.parseBoolean(environment.getProperty("proxy.useProxy", "false"));

        WebClient client = useProxy ? webClientWithProxy : webClientWithoutProxy;

        String response = client.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(10, Duration.ofSeconds(10))
                        .filter(throwable -> throwable instanceof WebClientResponseException.TooManyRequests ||
                                throwable instanceof WebClientRequestException))
                .block();

        return gson.fromJson(response, PriceOverviewDto.class);
    }
}

