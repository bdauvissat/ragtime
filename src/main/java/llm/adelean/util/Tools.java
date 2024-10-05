package llm.adelean.util;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.language.LanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaLanguageModel;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.elasticsearch.client.RestClient;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Optional;

@Singleton
@Slf4j
public class Tools {

    @ConfigProperty(name = "elastic.username")
    Optional<String> elasticUsername;

    @ConfigProperty(name = "elastic.password")
    Optional<String> elasticPassword;

    @ConfigProperty(name = "elastic.url")
    String elasticUrl;

    @ConfigProperty(name = "elastic.indexName")
    String elasticIndexName;

    @ConfigProperty(name = "ollama.url")
    String ollamaUrl;

    @ConfigProperty(name = "ollama.model")
    String ollamaModel;

    @ConfigProperty(name = "ollama.duration")
    int ollamaDuration;

    @Getter
    private ElasticsearchEmbeddingStore store;

    @PostConstruct
    public void initialize() {

        RestClient restClient = getRestClient();

        ElasticsearchEmbeddingStore.Builder storeBuilder = ElasticsearchEmbeddingStore.builder().restClient(restClient);
        store = storeBuilder
                .indexName(elasticIndexName)
                .build();
                log.info("Elasticsearch Indexing Client OK on {}",elasticUrl);

    }

    private @NotNull RestClient getRestClient() {
        if (elasticUsername.isEmpty()) {
            log.error("Username is empty");
        }

        if (elasticPassword.isEmpty()) {
            log.error("Password is empty");
        }

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticUsername.get(),
                elasticPassword.get()));

        return RestClient
                .builder(HttpHost.create(elasticUrl))
                .setHttpClientConfigCallback( hcb -> {
                    hcb.setDefaultCredentialsProvider(credentialsProvider);
                    return hcb;
                })
                .build();
    }


    public LanguageModel createLanguageModel() {
        return getLanguageModelBuilder()
                .timeout(Duration.ofSeconds(ollamaDuration))
                .build();

    }

    public ChatLanguageModel createChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(ollamaUrl)
                .modelName(ollamaModel)
                .timeout(Duration.ofSeconds(ollamaDuration))
                .build();
    }

    private OllamaLanguageModel.OllamaLanguageModelBuilder getLanguageModelBuilder() {
        return OllamaLanguageModel.builder()
                .baseUrl(ollamaUrl)
                .modelName(ollamaModel);
    }

}
