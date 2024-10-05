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
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        documents.forEach(doc -> embedAndStoreDocuments(tools.getStore(), embeddingModel, doc));

    }

    private List<RagDocument> getAllDocuments(RagFolder folderPath) {
        File folder = new File(folderPath.getPath());

        List<RagDocument> documents = new ArrayList<>();

        if (!folder.exists() || !folder.isDirectory()) {

            log.error("{} does not exist or is not a directory", folder.getName());

            return documents;
        }

        // txt files are computed
        documents = getRagDocumentsFromTxt(folderPath, folder);
        // and also json files
        documents.addAll(getRagDocumentsFromJson(folder));
        if (CollectionUtils.isEmpty(documents)) {
            return null;
        }

        return documents;

    }

    private List<RagDocument> getRagDocumentsFromJson(File folder) {
        FilenameFilter filter = (dir, name) -> name.endsWith(".json");
        File[] files = folder.listFiles(filter);

        if (files == null || files.length == 0) {
            log.warn("{} contains no Json file !", folder.getName());
            return new ArrayList<>();
        }
        int count = 0;
        List<RagDocument> documents = new ArrayList<>();
        Gson gson = new Gson();
        for (File file : files) {

            try {
                String filecontent = Files.readString(file.toPath());
                JsonArray jsonObject = JsonParser.parseString(filecontent).getAsJsonArray();
                List<JsonElement> allArticles = jsonObject.getAsJsonArray().asList();
                for (JsonElement article : allArticles) {
                    // use gson mapper to deserialize
                    RagDocument rd = gson.fromJson(article, RagDocument.class);

                    documents.add(rd);
                    count++;

                }
            } catch (JsonSyntaxException e) {
                log.error("Error while deserializing document at position {} from file {}", count, file.getName(), e);
            }
            catch (Exception e) {
                log.error("Error while reading file {}", file.getName(), e);
            }
        }
        log.info("{} documents have been extracted from folder {}",count,folder.getName());
        return documents;
    }

    private List<RagDocument> getRagDocumentsFromTxt(RagFolder folderPath, File folder) {
        FilenameFilter filter = (dir, name) -> name.endsWith(".txt");
        File[] files = folder.listFiles(filter);
        List<RagDocument> documents = new ArrayList<>();

        if (files == null || files.length == 0) {
            log.warn("{} contains no text File.", folder.getName());
            return documents;
        }

        int count = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (File file : files) {
            try {
                List<String> content = Files.readAllLines(file.toPath());
                String name = file.getName();
                String path = file.getPath();
                RagDocument doc = new RagDocument(name, path, content);
                String dateTime = LocalDate.now().format(formatter);
                doc.setDatetime(dateTime);
                doc.setKeywords(List.of("Fichier"));
                documents.add(doc);
                count++;

                if (folderPath.getLimit() > 0 && count >= folderPath.getLimit()) {
                    break;
                }

            } catch (IOException e) {
                log.error("Error while reading file {}", file.getName(), e);
            }

        }

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
