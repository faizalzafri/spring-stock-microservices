package com.example.projects.stockservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Random;

@Service
public class StockPriceService {

    private static final Logger LOG = LoggerFactory.getLogger(StockPriceService.class);
    private final Random random = new Random();

    public BigDecimal getPrice(String quoteSymbol) {
        try {
            LOG.info("Fetching stock price for symbol {}", quoteSymbol);
            Stock stock = YahooFinance.get(quoteSymbol);
            if (Objects.nonNull(stock) && Objects.nonNull(stock.getQuote()) && Objects.nonNull(stock.getQuote().getPrice())) {
                BigDecimal price = stock.getQuote().getPrice();
                LOG.info("Successfully fetched live price for {}: ${}", quoteSymbol, price);
                return price;
            }
            LOG.warn("Stock data or price was null for {}. Invoking fallback mock", quoteSymbol);
            return generateMockPrice(quoteSymbol);
        } catch (IOException e) {
            LOG.error("Failed to fetch live stock price for {} due to an API error. Invoking fallback mock. Error {}", quoteSymbol, e.getMessage());
            return generateMockPrice(quoteSymbol);
        }
    }

    private BigDecimal generateMockPrice(String quoteSymbol) {
        double mock = 50.0 + (100 * random.nextDouble());
        BigDecimal price = BigDecimal.valueOf(mock).setScale(2, BigDecimal.ROUND_HALF_UP);
        LOG.info("Fallback mock price generated for {}: ${}", quoteSymbol, price);
        return price;
    }
}
