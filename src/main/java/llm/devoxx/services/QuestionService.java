package llm.devoxx.services;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.language.LanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Metadata;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import llm.devoxx.json.Answer;
import llm.devoxx.json.CompleteAnswer;
import llm.devoxx.json.Question;
import llm.devoxx.util.Constants;
import llm.devoxx.util.DocumentChat;
import llm.devoxx.util.Tools;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class QuestionService {

    @Inject
    Tools tools;

    @Inject
    EmbeddingModel embeddingModel;

    private final PromptTemplate promptTemplate = PromptTemplate.from(
            """
                    Answer the following question :
                   
                    Question:
                    {{question}}
                    
                    Base your answer on the following information:
                    {{information}}
                    """
    );

    private final ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

    public CompleteAnswer processQuestion(Question question) {

        EmbeddingStore<TextSegment> store = tools.getStore();

        LanguageModel languageModel = tools.createLanguageModel();

        Embedding queryEmbedded = embeddingModel.embed(question.getQuestion()).content();

        EmbeddingSearchResult<TextSegment> docs = store.search(EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedded)
                .build());

        List<EmbeddingMatch<TextSegment>> relevant = docs.matches();

        relevant.sort((o1, o2) -> o2.score().compareTo(o1.score()));

        List<Answer> answers = new ArrayList<>();

        log.info("Generating answer for question: \"{}\"", question.getQuestion());
        for (var rel : relevant) {
            answers.add(new Answer(rel.embedded().text(), rel.score(), rel.embedded().metadata()));

        }

        log.info("Generating answer for question: \"{}\"", question.getQuestion());

        if (question.isGenerateAnswer()) {
            String information = relevant.stream().map(rlv -> rlv.embedded().text()).collect(Collectors.joining("\n\n"));
            Prompt prompt = getPrompt(question, information);

            Response<String> generatedAnswer = languageModel.generate(prompt);
            return new CompleteAnswer(generatedAnswer.content(), answers);
        }

        return new CompleteAnswer(Constants.EMPTY_STRING, answers);

    }

    private Prompt getPrompt(Question question, String information) {
        Map<String, Object> templateParameters = new HashMap<>();
        templateParameters.put("question", question.getQuestion());
        templateParameters.put("information", information);
        return promptTemplate.apply(templateParameters);
    }

    public CompleteAnswer chatWithDocsAndMemory(Question question) {

        EmbeddingStore<TextSegment> store = tools.getStore();

        ChatLanguageModel chatModel = tools.createChatModel();

        Embedding queryEmbedded = embeddingModel.embed(question.getQuestion()).content();

        List<EmbeddingMatch<TextSegment>> relevant = store.findRelevant(queryEmbedded,3, 0.55);

        List<Answer> answers = new ArrayList<>();
        StringBuilder info = new StringBuilder();

        for (EmbeddingMatch<TextSegment> match : relevant) {
            answers.add(new Answer(match.embedded().text(), match.score(), match.embedded().metadata()));
            if (!info.isEmpty()) {
                info.append(System.lineSeparator());
            }
            info.append(match.embedded().text());
        }

        String information = info.toString();
        Prompt prompt = getPrompt(question, information);

        QueryTransformer queryTransformer = new CompressingQueryTransformer(chatModel);
        Query query = Query.from(prompt.text(), new Metadata(UserMessage.from(prompt.text()), chatMemory.messages(),
                chatMemory.messages()));
        queryTransformer.transform(query);

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.55)
                .build();

        ContentInjector contentInjector = DefaultContentInjector.builder()
                .metadataKeysToInclude(List.of("title", "index", "url"))
                .build();

        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryTransformer(queryTransformer)
                .contentRetriever(contentRetriever)
                .contentInjector(contentInjector)
                .build();

        DocumentChat chat = AiServices.builder(DocumentChat.class)
                .chatLanguageModel(chatModel)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemory(chatMemory)
                .build();

        String reponse = chat.answer(question.getQuestion());



        return new CompleteAnswer(reponse, answers);

    }

}
