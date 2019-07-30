package com.parse.olimp.service;

import com.parse.olimp.entity.Market;
import com.parse.olimp.entity.Outcome;
import com.parse.olimp.entity.SportUrl;
import com.parse.olimp.entity.Tournament;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
                    .setUrl(urlMain + urlSportPrefix + "/index.php?page=line&addons=1&action=2&mid=50569565")
                    .build();
            String responseEventPage = asyncHttpClient.executeRequest(getEventPage).get().getResponseBody();

            Document docEventPage = Jsoup.parse(responseEventPage);
            Element tableEventPage = docEventPage.select("#betline > table > tbody > tr:nth-child(2) > td > table").get(0);
            Elements rowsTournamentPage = tableEventPage.select("tr");


            HashMap<String, List<Outcome>> marketList = new HashMap<>();
                try {
                    String dateTime = rowsTournamentPage.get(0).select("td").get(0).text();
                    System.out.println(dateTime);

                    Element generalMarketTable = rowsTournamentPage.get(1).select("#odd50569565").first();
                    Elements generalMarkets = generalMarketTable.select("b > i");

                    List<String> markets = marketService.getAllMarket(generalMarkets);
                    marketList.put("General", getGeneralOutcomeByEvent(generalMarketTable));

                    markets.forEach(market -> {
                        Market marketObj = new Market();
                        marketObj.setName(market);

                        //Start parse market
                        String marketPartHtml;
                        if(marketService.getNextMarket(markets, market) != null) {
                            marketPartHtml = responseEventPage.substring(responseEventPage.indexOf(market),
                                    responseEventPage.indexOf(marketService.getNextMarket(markets, market)));
                        } else {
                            String firstPartForParsLastMarket = responseEventPage.substring(0, responseEventPage.indexOf(market));
                            String shortPartPage = responseEventPage.replace(firstPartForParsLastMarket, "");

                            marketPartHtml = shortPartPage.substring(0, shortPartPage.indexOf("</div>"));
                        }

                        Document docMarketPart = Jsoup.parse(marketPartHtml);

/*                        List<Outcome> outcomeList = new ArrayList<>();
                        Elements nobrs = docMarketPart.select("nobr");
                                Elements outcomesHtml = nobrs.select("span");

                                for(int i1 = 1; i1 < outcomesHtml.size(); i1 = i1 + 4) {
                                    try {
                                        Element outcomeHtmlFirstName = outcomesHtml.get(i1);
                                    Element outcomeHtmlFirstKoefAndId = outcomesHtml.get(i1 + 1);
                                    String firstDataId = outcomeHtmlFirstKoefAndId.attr("data-id");

                                    Outcome outcomeFirst = new Outcome(firstDataId, outcomeHtmlFirstName.text(), Double.valueOf(outcomeHtmlFirstKoefAndId.text()));
                                    outcomeList.add(outcomeFirst);

                                    System.out.println(outcomeFirst.toString());
                                    }catch (Exception ex){
                                        log.error(ex);
                                    }
                                }*/
                        List<Outcome> outcomeList = new ArrayList<>();
                        Elements nobrs = docMarketPart.select("nobr");
                        for(int j1 = 0; j1 < nobrs.size(); j1++) {
                            Elements outcomesHtml = nobrs.get(j1).select("span");
                            try {
                                Element outcomeHtmlFirstName = outcomesHtml.get(1);
                                Element outcomeHtmlFirstKoefAndId = outcomesHtml.get(2);
                                String firstDataId = outcomeHtmlFirstKoefAndId.attr("data-id");

                                String name = outcomeHtmlFirstName.html();
                                if(!NumberUtils.isCreatable(name)){
                                    if(name.contains("<span")){
                                        //First market in nobr
                                        Outcome outcome = new Outcome();
                                        int firstSpanTag = name.indexOf("span");
                                        int secondSpanTag = getNextSpanTag(name, firstSpanTag);
                                        int thirdSpanTag = getNextSpanTag(name, secondSpanTag);
                                        int fourthSpanTag = getNextSpanTag(name, thirdSpanTag);
                                        int fifthSpanTag = getNextSpanTag(name, fourthSpanTag);

                                        outcome.setId(getElementInDocumentBetweenSpanTags(name, firstSpanTag, fourthSpanTag).attr("data-id"));
                                        outcome.setName(name.substring(name.indexOf("nobr") + 1, firstSpanTag - 1)
                                                .replace("&nbsp;", "").replace("\n", "").trim());
                                        outcome.setStatKef(Double.valueOf(getElementInDocumentBetweenSpanTags(name, secondSpanTag, thirdSpanTag).text()));

                                        System.out.println(outcome.toString());

                                        int sixthSpanTag = getNextSpanTag(name, fifthSpanTag);
                                        int sevenSpanTag = getNextSpanTag(name, sixthSpanTag);
                                        int eightSpanTag = getNextSpanTag(name, sevenSpanTag);

                                        //Second market in nobr
                                        Outcome outcome1 = new Outcome();
                                        outcome1.setId(getElementInDocumentBetweenSpanTags(name, fifthSpanTag, eightSpanTag).attr("data-id"));
                                        outcome1.setName(name.substring(fourthSpanTag, fifthSpanTag)
                                                .replace("&nbsp;", "")
                                                .replace("\n", "")
                                                .replace("span>", "")
                                                .replace(">", "")
                                                .replace("<", "").trim());
                                        outcome1.setStatKef(Double.valueOf(getElementInDocumentBetweenSpanTags(name, sixthSpanTag, sevenSpanTag).text()));

                                        System.out.println(outcome1.toString());
                                    }

                                    Outcome outcomeFirst = new Outcome(firstDataId, name, Double.valueOf(outcomeHtmlFirstKoefAndId.text()));
                                    System.out.println(outcomeFirst.toString());
                                }

                            } catch (Exception ex) {
                                log.error(ex);
                            }
                        }

                        marketObj.setOutcomes(outcomeList);
                        marketList.put(market, outcomeList);
                    });

                }catch (Exception ex){
                    //not necessary exception, error in parse table with tournaments
                    log.error(ex);
                }
        }catch (Exception ex){
            log.error(ex);
        }
    }

    private Element getElementInDocumentBetweenSpanTags(String name, int secondSpanTag, int thirdSpanTag) {
        Document docKoef = Jsoup.parse("<" + name.substring(secondSpanTag, thirdSpanTag) + "span>");
        return docKoef.select("span").first();
    }

    private int getNextSpanTag(String name, int beforeSpanTag) {
        return name.indexOf("span", beforeSpanTag + 1);
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
        System.out.println(NumberUtils.isCreatable("1.18"));
    }
}
