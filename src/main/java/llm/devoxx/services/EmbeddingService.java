package llm.devoxx.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class EmbeddingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddingService.class);
    @Inject
    Tools tools;

    @Inject
    EmbeddingModel embeddingModel;

    public void embedDocument(RagDocument documentRequest) {

        ElasticsearchEmbeddingStore store = tools.getStore();
        //EmbeddingModel embeddingModel = tools.createEmbeddingModel();

        embedAndStoreDocuments(store, embeddingModel, documentRequest);

    }

    public void embeddFolder(RagFolder folderPath) {

        List<RagDocument> documents = getAllDocuments(folderPath);

        if (CollectionUtils.isEmpty(documents)) {
            return;
        }

        ElasticsearchEmbeddingStore store = tools.getStore();
        //EmbeddingModel embeddingModel = tools.createEmbeddingModel();

        for (RagDocument doc : documents) {

            embedAndStoreDocuments(store, embeddingModel, doc);

        }

    }

    private List<RagDocument> getAllDocuments(RagFolder folderPath) {
        File folder = new File(folderPath.getPath());

        if (!folder.exists() || !folder.isDirectory()) {

            LOGGER.error("{} does not exist or is not a directory", folder.getName());

            return null;
        }

        // txt files are computed
        List<RagDocument> documents = getRagDocumentsFromTxt(folderPath, folder);
        documents.addAll(getRagDocumentsFromJson(folderPath, folder));
        if (documents == null) return null;

        if (CollectionUtils.isEmpty(documents)) {
            return null;
        }

        return documents;

    }

    private Collection<? extends RagDocument> getRagDocumentsFromJson(RagFolder folderPath, File folder) {
        FilenameFilter filter = (dir, name) -> name.endsWith(".json");
        File[] files = folder.listFiles(filter);

        if (files == null || files.length == 0) {
            LOGGER.error("{} is emtpy", folder.getName());
            return null;
        }

        List<RagDocument> documents = new ArrayList<>();

        for (File file : files) {

            try {
                String content = Files.readString(file.toPath());

                JsonArray jsonObject = JsonParser.parseString(content).getAsJsonArray();

                List<JsonElement> allArticles = jsonObject.getAsJsonArray().asList();

                for (JsonElement article : allArticles) {

                    String name = article.getAsJsonObject().get("title").getAsString();
                    String path = article.getAsJsonObject().get("url").getAsString();
                    JsonArray articleContent = article.getAsJsonObject().get("body").getAsJsonObject().get("sections").getAsJsonArray().get(0).getAsJsonObject().get("paragraphs").getAsJsonObject().get("_data").getAsJsonArray();
                    String myContent =
                            articleContent.asList().stream().map( ac -> ac.getAsString()).collect(Collectors.joining(System.lineSeparator()));
                    documents.add(new RagDocument(name, path, myContent));
                }

            } catch (IOException e) {
                LOGGER.error("Error while reading file {}", file.getName(), e);
            }

        }

        return documents;

    }

    private List<RagDocument> getRagDocumentsFromTxt(RagFolder folderPath, File folder) {
        FilenameFilter filter = (dir, name) -> name.endsWith(".txt");

        File[] files = folder.listFiles(filter);

        if (files == null || files.length == 0) {
            LOGGER.error("{} is emtpy", folder.getName());
            return null;
        }

        List<RagDocument> documents = new ArrayList<>();

        int count = 0;

        for (File file : files) {

            try {
                String content = Files.readString(file.toPath());
                String name = file.getName();
                String path = file.getPath();
                documents.add(new RagDocument(name, path, content));
                count++;

                if (folderPath.getLimit() > 0 && count >= folderPath.getLimit()) {
                    break;
                }

            } catch (IOException e) {
                LOGGER.error("Error while reading file {}", file.getName(), e);
            }

        }
        return documents;
    }

    private void embedAndStoreDocuments(EmbeddingStore<TextSegment> store, EmbeddingModel model,
                                        RagDocument ragDocumentdocument) {
        Map<String, String> metas = Map.of("url", ragDocumentdocument.getUrl(), "title", ragDocumentdocument.getTitle());

        Document document = new Document(ragDocumentdocument.getContent(), new Metadata(metas));

        DocumentSplitter splitter = new DocumentByParagraphSplitter(1000, 50);

        List<TextSegment> segments = splitter.split(document);
        List<Embedding> embeddings = model.embedAll(segments).content();
        store.addAll(embeddings, segments);
    }

}
