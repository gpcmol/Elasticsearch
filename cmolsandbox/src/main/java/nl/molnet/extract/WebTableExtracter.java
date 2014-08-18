package nl.molnet.extract;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.indices.CreateIndex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.CaseFormat;

public class WebTableExtracter {

    private static final String SEPARATOR = ";";
    private static final String HTTP_LOCALHOST_9200 = "http://localhost:9200";

    public static void main(String[] args) throws IOException {
        wikiFastest100MeterRunners();
    }
    
    private static void wikiFastest100MeterRunners() {
        WebTableExtracter webTableExtracter = new WebTableExtracter();
        String wikiUrl = "http://en.wikipedia.org/wiki/100_metres";
        boolean hasHeader = true;
        int tableIndex = 3;
        String tableName = "average_wage_europe";
        webTableExtracter.indexWikiTable(wikiUrl, tableName, hasHeader, tableIndex);
    }

    private static void wikiEuropeAverageWage() {
        WebTableExtracter webTableExtracter = new WebTableExtracter();
        String wikiUrl = "http://en.wikipedia.org/wiki/List_of_European_countries_by_average_wage";
        boolean hasHeader = true;
        int tableIndex = 0;
        String tableName = "average_wage_europe";
        webTableExtracter.indexWikiTable(wikiUrl, tableName, hasHeader, tableIndex);
    }

    private static void wikiAffluenceUs() {
        WebTableExtracter webTableExtracter = new WebTableExtracter();
        String wikiUrl = "http://en.wikipedia.org/wiki/Affluence_in_the_United_States";
        boolean hasHeader = true;
        int tableIndex = 0;
        String tableName = "affluence_us";
        webTableExtracter.indexWikiTable(wikiUrl, tableName, hasHeader, tableIndex);
    }

    private static void wikiFormula1() {
        WebTableExtracter webTableExtracter = new WebTableExtracter();
        String wikiUrl = "http://en.wikipedia.org/wiki/List_of_Formula_One_circuits";
        boolean hasHeader = true;
        int tableIndex = 1;
        String tableName = "f1_circuits";
        webTableExtracter.indexWikiTable(wikiUrl, tableName, hasHeader, tableIndex);
    }

    public void indexWikiTable(final String wikiUrl, final String indexName, final boolean hasHeader,
            final int tableIndex) {
        // create index
        JestClient client = getClient(null);
        try {
            client.execute(new CreateIndex.Builder(indexName).build());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // get table data
        List<List<String>> wikiData = this.getWikiTable(wikiUrl, tableIndex, hasHeader);

        if (wikiData.size() > 1) {
            List<String> header = wikiData.get(0);
            System.out.println("HEADER");
            for (String string : header) {
                System.out.print(string + SEPARATOR);
            }
            System.out.println("");

            List<List<String>> body = wikiData.subList(1, wikiData.size() - 1);
            for (List<String> list : body) {
                for (String string : list) {
                    System.out.print(string + SEPARATOR);
                }
                System.out.println("");
            }
        }

        // Index index = new Index.Builder(source).index("wiki").type(indexName).build();
        // client.execute(index);
        //
    }

    private JestClient getClient(final String elasticSearchUrl) {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder(elasticSearchUrl != null ? elasticSearchUrl
                : HTTP_LOCALHOST_9200).multiThreaded(true).build());
        JestClient client = factory.getObject();
        return client;
    }

    /**
     * Get information from wiki table
     * 
     * @param url
     *            wiki url
     * @param tableIndex
     *            the n-th table on the wiki page
     * @param hasHeader
     *            has the table a header
     * @return arraylist of table
     */
    private List<List<String>> getWikiTable(final String url, final int tableIndex, final boolean hasHeader) {
        List<List<String>> results = new ArrayList<List<String>>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements tables = doc.getElementsByClass("wikitable");

            Element table = tables.get(tableIndex);

            Elements rowElements = table.getElementsByTag("tr");

            Iterator<Element> rowIterator = rowElements.iterator();
            Element headerElement = rowIterator.next();
            List<String> headerValues = this.getCellsFromRowElement(headerElement, hasHeader);
            results.add(headerValues);
            while (rowIterator.hasNext()) {
                Element row = rowIterator.next();
                List<String> bodyValues = this.getCellsFromRowElement(row, false);
                results.add(bodyValues);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    private List<String> getCellsFromRowElement(final Element row, final boolean isHeader) {
        List<String> results = new ArrayList<String>();
        Iterator<Element> cellIterator = row.select("td,th").iterator();
        while (cellIterator.hasNext()) {
            String text = cellIterator.next().text();

            // make clean header
            if (isHeader) {
                text = makeStringClean(text); // text.replaceAll("\\P{L}+", "");
                text = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, text);
                text = text.replaceAll("__", "_");
            }

            results.add(text);
        }
        return results;
    }

    private String makeStringClean(String string) {
        string = string.replaceAll("(?![@',&])\\p{Punct}", "");
        string = string.replaceAll("\\s+", "_");
        return string;
    }

}
