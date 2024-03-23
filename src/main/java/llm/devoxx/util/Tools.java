package llm.devoxx.util;

import co.elastic.clients.transport.TransportUtils;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.language.LanguageModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaLanguageModel;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import jakarta.inject.Singleton;
import llm.devoxx.config.CertificateTrustManager;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.time.Duration;
import java.util.Optional;

@Singleton
public class Tools {
    private static final Logger LOGGER = LoggerFactory.getLogger(Tools.class);
    @ConfigProperty(name = "elastic.fingerprint")
    Optional<String> fingerprint;

    @ConfigProperty(name = "elastic.username")
    Optional<String> elasticUsername;

    @ConfigProperty(name = "elastic.password")
    Optional<String> elasticPassword;

    @ConfigProperty(name = "elastic.host")
    String elasticHost;

    @ConfigProperty(name = "elastic.port")
    int elasticPort;

    @ConfigProperty(name = "elastic.protocol")
    String elasticProtocol;

    @ConfigProperty(name = "elastic.url")
    String elasticUrl;

    @ConfigProperty(name = "elastic.apiKey")
    Optional<String> elasticApiKey;

    @ConfigProperty(name = "elastic.dimension")
    int elasticDimension;

    @ConfigProperty(name = "elastic.indexName")
    String elasticIndexName;

    @ConfigProperty(name = "ollama.url")
    String ollamaUrl;

    @ConfigProperty(name = "ollama.model")
    String ollamaModel;

    @ConfigProperty(name = "ollama.duration")
    int ollamaDuration;

    @ConfigProperty(name = "ollama.retry")
    int ollamaRetry;

    public ElasticsearchEmbeddingStore getStore() {
        ElasticsearchEmbeddingStore.Builder storeBuilder = ElasticsearchEmbeddingStore.builder().serverUrl(elasticUrl);
        /* set basic auth credentials if elastic auth is enabled */
        if (elasticUsername.isPresent() && elasticPassword.isPresent()) {
            storeBuilder.userName(elasticUsername.get()).password(elasticPassword.get());
        }
        /* add elastic api key if it exists */
        elasticApiKey.ifPresent(storeBuilder::apiKey);

        return storeBuilder
                .restClient(buildRestClient())
                .dimension(elasticDimension)
                .indexName(elasticIndexName)
                .build();

    }

    private RestClient buildRestClient() {

        final SSLContext context = TransportUtils.sslContextFromCaFingerprint(fingerprint.orElse(Constants.EMPTY_STRING));
        try {
            context.init(null, new TrustManager[]{new CertificateTrustManager()}, null);
        } catch (KeyManagementException e) {
            LOGGER.error("Failure to correctly init SSLContext. Reason={}", e.getMessage());
        }

        RestClientBuilder builder = RestClient.builder(new HttpHost(elasticHost, elasticPort, elasticProtocol));
        if (elasticUsername.isPresent() && elasticPassword.isPresent()) {
            BasicCredentialsProvider creds = new BasicCredentialsProvider();
            creds.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticUsername.get(), elasticPassword.get()));
            builder.setHttpClientConfigCallback(ccb -> {
                if (fingerprint.isPresent()) {
                    ccb.setSSLContext(context).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                }
                ccb.setDefaultCredentialsProvider(creds);
                return ccb;
            });

        }
        return builder.build();

    }

    public EmbeddingModel createEmbeddingModel() {
        return new OllamaEmbeddingModel(ollamaUrl, ollamaModel, Duration.ofSeconds(ollamaDuration),ollamaRetry);

    }

    public LanguageModel createLanguageModel() {
        return getLanguageModelBuilder()
                .timeout(Duration.ofSeconds(ollamaDuration))
                .build();

    }

    private OllamaLanguageModel.OllamaLanguageModelBuilder getLanguageModelBuilder() {
        return OllamaLanguageModel.builder()
                .baseUrl(ollamaUrl)
                .modelName(ollamaModel);
    }

}
