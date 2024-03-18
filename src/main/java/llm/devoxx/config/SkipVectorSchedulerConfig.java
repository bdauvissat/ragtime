package llm.devoxx.config;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import co.elastic.clients.elasticsearch.ElasticsearchClient;

@Singleton
public class SkipVectorSchedulerConfig implements Scheduled.SkipPredicate {

    @Inject
    ElasticsearchClient esClient;

    @Override
    public boolean test(ScheduledExecution execution) {
        return esClient == null;
    }
}
