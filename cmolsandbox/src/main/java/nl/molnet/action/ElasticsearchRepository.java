package nl.molnet.action;

import io.searchbox.action.AbstractAction;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.SearchResult.Hit;
import io.searchbox.core.Update;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.molnet.model.common.ModelObject;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticsearchRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchRepository.class);

    public ElasticsearchRepository() {
    }

    public JestResult persistEntities(final String index, final String type, final List<ModelObject> objects) {
        createIndex(index, type);
        
        List<Index> indexEntities = new ArrayList<Index>(objects.size());
        for (ModelObject modelObject : objects) {
            indexEntities.add(new Index.Builder(modelObject).build());
        }

        Bulk bulk = new Bulk.Builder().defaultIndex(index).defaultType(type).addAction(indexEntities).build();

        return executeAction(bulk);
    }

    public JestResult updateEntities(final String index, final String type, final List<ModelObject> objects) {
        List<Update> updateEntities = new ArrayList<Update>(objects.size());
        for (ModelObject modelObject : objects) {
            updateEntities.add(new Update.Builder(modelObject).build());
        }

        Bulk bulk = new Bulk.Builder().defaultIndex(index).defaultType(type).addAction(updateEntities).build();

        return executeAction(bulk);
    }

    public JestResult deleteEntities(final String index, final String type, final List<ModelObject> objects) {
        List<Delete> deleteEntities = new ArrayList<Delete>(objects.size());
        for (ModelObject modelObject : objects) {
            LOGGER.info("Deleting object: " + modelObject);
            deleteEntities.add(new Delete.Builder(String.valueOf(modelObject.getDocumentId())).build());
        }

        Bulk bulk = new Bulk.Builder().defaultIndex(index).defaultType(type).addAction(deleteEntities).build();

        return executeAction(bulk);
    }

    public List<ModelObject> searchEntities(final String index, final String type, final String findString,
            final List<String> fields, Class<? extends ModelObject> target) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(findString, fields.toArray(new String[fields.size()])));

        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(index).addType(type).build();

        List<ModelObject> results = new ArrayList<ModelObject>();
        
        SearchResult searchResult = executeSearch(search);
        
        if (searchResult.isSucceeded()) {
            List<?> hits = searchResult.getHits(target);
            for (Iterator iterator = hits.iterator(); iterator.hasNext();) {
                Hit<ModelObject, Void> hit = (Hit<ModelObject, Void>) iterator.next();
                results.add(hit.source);
            }
        }

        return results;
    }

    public JestResult createIndex(final String index, final String type) {
        IndicesExists exists = new IndicesExists.Builder(index).build();
        JestResult result = executeAction(exists);

        if (!result.isSucceeded()) {
            CreateIndex createIndex = new CreateIndex.Builder(index).build();
            LOGGER.info("Create index: " + index + " " +type);
            return executeAction(createIndex);
        }
        return result;
    }

    public JestResult deleteIndex(final String index, final String type) {
        IndicesExists exists = new IndicesExists.Builder(index).build();
        JestResult result = executeAction(exists);

        if (result.isSucceeded()) {
            DeleteIndex deleteIndex = new DeleteIndex.Builder(index).build();
            LOGGER.info("Delete index: " + index + " " +type);
            return executeAction(deleteIndex);
        }
        return result;
    }

    private SearchResult executeSearch(AbstractAction<SearchResult> action) {
        SearchResult execute = null;
        try {
            execute = getJestClient().execute(action);
        } catch (Exception e) {
            LOGGER.error("Exception occurred executing search action ", e);
        }
        return execute;
    }

    private JestResult executeAction(AbstractAction<JestResult> action) {
        JestResult execute = null;
        try {
            execute = getJestClient().execute(action);
        } catch (Exception e) {
            LOGGER.error("Exception occurred executing CRUD action ", e);
        }
        return execute;
    }

    private JestClient getJestClient() {
        return ElasticsearchClientFactory.getInstance();
    }

}
