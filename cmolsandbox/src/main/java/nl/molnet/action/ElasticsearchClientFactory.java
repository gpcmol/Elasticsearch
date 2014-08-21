package nl.molnet.action;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

public class ElasticsearchClientFactory {
    private static final String HTTP_LOCALHOST_9200 = "http://localhost:9200";

    private static JestClient JEST_CLIENT = null;

    private ElasticsearchClientFactory() {
    }

    public static synchronized JestClient getInstance(final String elasticsearchUrl) {
        if  (JEST_CLIENT == null) {
            JestClientFactory factory = new JestClientFactory();
            factory.setHttpClientConfig(new HttpClientConfig.Builder(elasticsearchUrl != null ? elasticsearchUrl
                    : HTTP_LOCALHOST_9200).multiThreaded(true).build());
            JEST_CLIENT = factory.getObject();
        }
        return JEST_CLIENT;
    }

    public static synchronized JestClient getInstance() {
        return getInstance(null);
    }

}
