package llm.devoxx.services;

import com.google.gson.*;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import llm.devoxx.json.RagDocument;
import llm.devoxx.json.RagFolder;
import llm.devoxx.util.Tools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Slf4j
public class EmbeddingService {
    @Inject
    Tools tools;

    @Inject
    EmbeddingModel embeddingModel;

    public void embedDocument(RagDocument documentRequest) {
        ElasticsearchEmbeddingStore store = tools.getStore();
        embedAndStoreDocuments(store, embeddingModel, documentRequest);
    }

    public void embeddFolder(RagFolder folderPath) {

        List<RagDocument> documents = getAllDocuments(folderPath);

        if (CollectionUtils.isEmpty(documents)) {
            return;
        }

        documents.forEach(doc ->  embedAndStoreDocuments(tools.getStore(), embeddingModel, doc));


    }

    private List<RagDocument> getAllDocuments(RagFolder folderPath) {
        File folder = new File(folderPath.getPath());

        List<RagDocument> documents = new ArrayList<>();

        if (!folder.exists() || !folder.isDirectory()) {

            log.error("{} does not exist or is not a directory", folder.getName());

            return documents;
        }

        try {
            documents = getRagDocuments(folderPath);
        } catch (IOException e) {
            log.warn("Erreur lors de l'extraction des documents", e);
        }

        return documents;

    }

    private List<RagDocument> getRagDocuments(RagFolder folderPath) throws IOException {
        Path documentsPath = Paths.get(folderPath.getPath());
        List<RagDocument> documents = new ArrayList<>();
        Gson gson = new Gson();
        PathMatcher txtMatcher = FileSystems.getDefault().getPathMatcher("glob:*.txt");
        PathMatcher jsonMatcher = FileSystems.getDefault().getPathMatcher("glob:*.json");
        Files.walkFileTree(documentsPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                log.debug("Visit {}", file);
                if (txtMatcher.matches(file.getFileName())) {
                    List<String> content = Files.readAllLines(file);
                    String name = file.getFileName().toString();
                    String path = file.toString();
                    documents.add(new RagDocument(name, path, content));
                }
                if (jsonMatcher.matches(file.getFileName())) {
                    String filecontent = Files.readString(file);
                    JsonArray jsonObject = JsonParser.parseString(filecontent).getAsJsonArray();
                    List<JsonElement> allArticles = jsonObject.getAsJsonArray().asList();
                    for (JsonElement article : allArticles) {
                        // use gson mapper to deserialize
                        RagDocument rd = gson.fromJson(article, RagDocument.class);
                        documents.add(rd);
                    }
                }
                if (folderPath.getLimit() > 0 && documents.size() >= folderPath.getLimit()) {
                    return FileVisitResult.TERMINATE;
                }
                return super.visitFile(file, attrs);
            }
        });
        log.info("{} documents have been extracted from folder {}",documents.size(),folderPath.getPath());
        return documents;
    }

    private void embedAndStoreDocuments(EmbeddingStore<TextSegment> store, EmbeddingModel model,
                                        RagDocument ragDocumentdocument) {
        Map<String, String> metas = Map.of(
                "url", ragDocumentdocument.getUrl(), 
                "title", ragDocumentdocument.getTitle(),
                "datetime",ragDocumentdocument.getDatetime(),
                "keywords", String.join(", ", ragDocumentdocument.getKeywords()));
        
        // Join with double lineSeparator (\n\n) in order to leverage DocumentByParagraphSplitter 
        String content = String.join(System.lineSeparator()+System.lineSeparator(), ragDocumentdocument.getContent());
        
        Document document = new Document(content, new Metadata(metas));
        DocumentSplitter splitter = new DocumentByParagraphSplitter(800, 50);

        List<TextSegment> segments = splitter.split(document);
        List<Embedding> embeddings = model.embedAll(segments).content();
        store.addAll(embeddings, segments);
        log.info("document {} has been stored as {} chunks",ragDocumentdocument.getUrl(),segments.size());
    }

}
