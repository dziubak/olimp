package com.parse.olimp.service;

import lombok.extern.log4j.Log4j2;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class MarketService {

    public List<String> getAllMarket(Elements elements){
        List<String> markets = new ArrayList<>();
        elements.forEach(generalMarket -> {
            markets.add(generalMarket.text());
        });

        return markets;
    }

    public String getNextMarket(List<String> markets, String currentValue) {
        List<String> list = new ArrayList<>(markets);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(currentValue)) {
                try {
                    if(i == list.size() - 1){
                        return null;
                    }
                    return list.get(i + 1);
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
            }
        }
        return null;
    }
}
