package com.parse.olimp.service;

import lombok.extern.log4j.Log4j2;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
public class MarketService {

    public Set<String> getAllMarket(Elements elements){
        LinkedHashSet<String> markets = new LinkedHashSet<>();
        elements.forEach(generalMarket -> {
            markets.add(generalMarket.text());
        });
        markets.add("General");

        return markets;
    }

    public String getNextMarket(Set<String> markets, String currentValue){
        try {
            List<String> list = new ArrayList<>(markets);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(currentValue)) {
                    return list.get(i + 1);
                }
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return null;
    }
}
