package nl.molnet.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import nl.molnet.model.common.ModelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PocStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PocStarter.class);

    public static void main(String [ ] args) {
        EventResultBean eventResultBean1 = new EventResultBean(1, "schaatsen", 1, 30, 24, new BigDecimal("33.2"), "Number1", "Christiaan", "Mol", "Nederland");
        EventResultBean eventResultBean2 = new EventResultBean(2, "schaatsen", 2, 29, 19, new BigDecimal("29.2"), "Number2", "Wouter", "Mol", "Nederland");
        EventResultBean eventResultBean3 = new EventResultBean(3, "schaatsen", 3, 29, 11, new BigDecimal("23.2"), "Number3", "Adriaan", "Mol", "Nederland");
        EventResultBean eventResultBean4 = new EventResultBean(4, "schaatsen", 4, 25, 22, new BigDecimal("8.2"), "Number4", "Bassie", "Mol", "Nederland");

        ElasticsearchRepository elasticsearchRepository = new ElasticsearchRepository();

        String index = "activeeventresults";
        String type = "eventresults";

        List<ModelObject> objects = new ArrayList<ModelObject>();
        objects.add(eventResultBean1);
        objects.add(eventResultBean2);
        objects.add(eventResultBean3);
        objects.add(eventResultBean4);

        elasticsearchRepository.persistEntities(index, type, objects);

//        elasticsearchRepository.deleteIndex(index, type);

//        List<String> fields = ImmutableList.of("firstname", "lastname");
//
//        List<ModelObject> searchEntities = elasticsearchRepository.searchEntities(index, type, "mol-123", fields, EventResultBean.class);
//        for (ModelObject modelObject : searchEntities) {
//            System.out.println("!: " +modelObject);
//        }
    }

}
