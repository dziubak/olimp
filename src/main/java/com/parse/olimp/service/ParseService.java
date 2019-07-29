package com.parse.olimp.service;

import com.parse.olimp.entity.Market;
import com.parse.olimp.entity.Outcome;
import com.parse.olimp.entity.SportUrl;
import com.parse.olimp.entity.Tournament;
import lombok.extern.log4j.Log4j2;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.util.HttpConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.asynchttpclient.Dsl.asyncHttpClient;

@Log4j2
@Service
public class ParseService {

    private AsyncHttpClient asyncHttpClient = asyncHttpClient();

    private List<Tournament> tournamentList = new ArrayList<>();
    private HashMap<Integer, String> eventList = new HashMap<>();

    @Autowired
    private MarketService marketService;

    @Value("${url.main}")
    private String urlMain;

    @Value("${url.prefix}")
    private String urlSportPrefix;

    @Autowired
    private SportUrl sportUrl;

    @PostConstruct
    public void parse() throws ExecutionException, InterruptedException {
        System.out.println(sportUrl);
/*        Request getRequest = new RequestBuilder(HttpConstants.Methods.GET)
                .setUrl("https://ec6f68.olimp78a8.top/betting/soccer")
                .build();
        String responseHtml = asyncHttpClient.executeRequest(getRequest).get().getResponseBody();

        Document doc = Jsoup.parse(responseHtml);
        Element table = doc.select("#central_td > center > form > table").get(0);
        Elements rows = table.select("tr");

        //first row is the col names so skip it.
        for (int i = 1; i < rows.size(); i++) {
            try {
                Element row = rows.get(i);
                Elements cols = row.select("td");

                Element name = cols.get(1).select("a").get(0);
                String url = name.attr("href");

                Tournament tournament = new Tournament();
                tournament.setName(name.text());
                tournament.setLink(url);

                tournamentList.add(tournament);
            }catch (Exception ex){
                //not necessary exception, error in parse table with tournaments
                log.error(ex.getMessage());
            }
        }*/

 /*           try{
                Request getTournamentPage = new RequestBuilder(HttpConstants.Methods.GET)
                        .setUrl("https://ec6f68.olimp78a8.top/betting/" + "index.php?page=line&action=2&sel[]=11657")
                        .build();
                String responseTournamentPage = asyncHttpClient.executeRequest(getTournamentPage).get().getResponseBody();

                Document docTournamentPage = Jsoup.parse(responseTournamentPage);
                Element tableTournamentPage = docTournamentPage.select("#betline > table > tbody > tr:nth-child(2) > td > table").get(0);
                Elements rowsTournamentPage = tableTournamentPage.select("tr");

                List<Event> eventListByTournament = new ArrayList<>();
                for (int i = 0; i < rowsTournamentPage.size(); i++) {
                    try {
                        Element rowTournamentPage = rowsTournamentPage.get(i);
                        Elements colsTournamentPage = rowTournamentPage.select("td");

                        Element name = colsTournamentPage.get(1).select("div > div.gameNameLine > font > b").first();
                        Element linkElement = name.select("span").select("a").first();

                        String link = linkElement.attr("href");
                        String baseOfLink = "/index.php?page=line&addons=1&action=2&mid=";
                        int id = Integer.valueOf(link.replace(baseOfLink, ""));

                        Event event = new Event();
                        event.setId(id);
                        event.setName(name.text());
                        event.setLink(link);

                        eventListByTournament.add(event);

                        System.out.println(name.text() + " | " + link + " | " + id);
                    }catch (Exception ex){
                        //not necessary exception, error in parse table with tournaments
                    }
                }
                //TODO add event list to tournament
            }catch (Exception ex){
                log.error(ex);
            }*/

        try{
            Request getEventPage = new RequestBuilder(HttpConstants.Methods.GET)
                    .setUrl(urlMain + urlSportPrefix + "/index.php?page=line&addons=1&action=2&mid=50539840")
                    .build();
            String responseEventPage = asyncHttpClient.executeRequest(getEventPage).get().getResponseBody();

            Document docEventPage = Jsoup.parse(responseEventPage);
            Element tableEventPage = docEventPage.select("#betline > table > tbody > tr:nth-child(2) > td > table").get(0);
            Elements rowsTournamentPage = tableEventPage.select("tr");


            HashMap<String, List<Outcome>> marketList = new HashMap<>();
                try {
                    String dateTime = rowsTournamentPage.get(0).select("td").get(0).text();
                    System.out.println(dateTime);

                    Element generalMarketTable = rowsTournamentPage.get(1).select("#odd50539840").first();
                    Elements generalMarkets = generalMarketTable.select("b > i");

                    Set<String> markets = marketService.getAllMarket(generalMarkets);

                    markets.forEach(market -> {
                        if(market.equals("General")){
                            return;
                        }
                        Market marketObj = new Market();
                        marketObj.setName(market);

                        //Start parse market
                        String marketPartHtml = "";
                        try {
                            marketPartHtml = responseEventPage.substring(responseEventPage.indexOf(market),
                                responseEventPage.indexOf(marketService.getNextMarket(markets, market)));
                        }catch (Exception ex){
                            marketPartHtml = responseEventPage.substring(responseEventPage.indexOf(market) + 1,
                                    responseEventPage.indexOf(marketService.getNextMarket(markets, market)));
                        }

                        Document docMarketPart = Jsoup.parse(marketPartHtml);

/*                        Elements outcomes = docMarketPart.select("span");
                        for(int j=0; j < outcomes.size(); j++){
                            Element element = outcomes.select("span").get(j);
                            Elements elements = element.select("span");

                            try {
                                String dataId = elements.get(j).attr("data-id");
                                String id = dataId.substring(j, dataId.indexOf(":"));

                                String outcomeName = docMarketPart.select("nobr").get(1).text();
                                double outcomeKof = Double.valueOf(elements.get(1).text());
                            }catch (Exception ex){
                                log.error(ex.getMessage());
                            }
                        }*/

                        List<Outcome> outcomeList = new ArrayList<>();
                        Elements nobrs = docMarketPart.select("nobr");
                        for(int i1 = 0; i1 < nobrs.size(); i1 ++){
                            try {
                                Elements outcomesHtml = nobrs.select("span");
//                                String elementNobrToString = nobrs.get(i1).toString();

                                Element outcomeHtmlFirstName = outcomesHtml.get(2);
                                Element outcomeHtmlFirstKoefAndId = outcomesHtml.get(3);
                                String firstDataId = outcomeHtmlFirstKoefAndId.attr("data-id");

                                Outcome outcomeFirst = new Outcome(firstDataId, outcomeHtmlFirstName.text(), Double.valueOf(outcomeHtmlFirstKoefAndId.text()));
                                outcomeList.add(outcomeFirst);

/*                                int firstSpanTag = elementNobrToString.indexOf("span");
                                int secondSpanTag = elementNobrToString.indexOf("span", firstSpanTag + 1);
                                int thirdSpanTag = elementNobrToString.indexOf("span", secondSpanTag + 1);

                                outcome.setName(elementNobrToString.substring(elementNobrToString.indexOf("nobr") + 5, firstSpanTag - 1)
                                        .replace("&nbsp;", "").replace("\n", "").trim());*/

/*                                int fourthSpanTag = elementNobrToString.indexOf("span", thirdSpanTag + 1);
                                int fifthSpanTag = elementNobrToString.indexOf("span", fourthSpanTag + 1);

                                outcome.setName(elementNobrToString.substring(fourthSpanTag + 5, fifthSpanTag - 1).replace("&nbsp;", "")
                                        .replace("\n", "").trim());*/
//                                outcomeList.add(outcome);
                                outcomeFirst.toString();

                                Element outcomeHtmlSecondName = outcomesHtml.get(5);
                                Element outcomeHtmlSecondKoefAndId = outcomesHtml.get(6);
                                String secondDataId = outcomeHtmlSecondKoefAndId.attr("data-id");

                                Outcome outcomeSecond = new Outcome(secondDataId, outcomeHtmlSecondName.text(), Double.valueOf(outcomeHtmlSecondKoefAndId.text()));
                                outcomeList.add(outcomeSecond);

                                outcomeSecond.toString();
                            }catch (Exception ex){
                                log.error(ex.getMessage());
                            }
                        }
                        marketObj.setOutcomes(outcomeList);
                        marketList.put(market, outcomeList);
                    });

                    marketList.put("General", getGeneralOutcomeByEvent(generalMarketTable));

                }catch (Exception ex){
                    //not necessary exception, error in parse table with tournaments
                    log.error(ex);
                }
        }catch (Exception ex){
            log.error(ex);
        }
    }

    private List<Outcome> getGeneralOutcomeByEvent(Element generalMarketTable) {
        //9 - number of outcomes in general market
        int numberOfOutcomes = 9;
        List<Outcome> generalOutcomes = new ArrayList<>();
        for (int i = 0; i < numberOfOutcomes; i++){
            try {
                Element outcome = generalMarketTable.select("nobr:nth-child(" + i + ")").first();
                Element element = outcome.select("span").get(0);
                Elements elements = element.select("span");

                String dataId = elements.get(2).attr("data-id");
                String id = dataId.substring(0, dataId.indexOf(":"));
                String outcomeName = elements.get(1).text();
                double outcomeKof = Double.valueOf(elements.get(2).text());

                Outcome outcomeForList = new Outcome(id, outcomeName, outcomeKof);
                generalOutcomes.add(outcomeForList);
            }catch (Exception ex){
                log.error(ex.getMessage());
            }
        }
        return generalOutcomes;
    }

    public static void main(String[] args) {
        String qwerty = "qwerty";
        System.out.println(qwerty.substring(1));
    }

}
