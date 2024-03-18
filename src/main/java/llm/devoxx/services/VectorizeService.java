package llm.devoxx.services;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import llm.devoxx.json.RagDocument;
import llm.devoxx.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class VectorizeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VectorizeService.class);
    @Inject
    Tools tools;

    public void convertToVector(RagDocument documentRequest) {

        ElasticsearchEmbeddingStore store = tools.getStore();

        Map<String, String> metas = Map.of("url", documentRequest.getUrl(), "title", documentRequest.getTitle());

        Document document = new Document(documentRequest.getContent(), new Metadata(metas));

        DocumentSplitter splitter = new DocumentByParagraphSplitter(1000, 50);

        List<TextSegment> segments = splitter.split(document);

        EmbeddingModel embeddingModel = tools.createEmbeddingModel();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        store.addAll(embeddings, segments);


    }

}
