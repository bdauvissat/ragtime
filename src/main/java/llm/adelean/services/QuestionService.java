package llm.adelean.services;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.language.LanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import llm.adelean.json.Answer;
import llm.adelean.json.CompleteAnswer;
import llm.adelean.json.Question;
import llm.adelean.util.Constants;
import llm.adelean.util.Tools;
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

            PromptTemplate template = PromptTemplate.from(
                    """
                            Answer the following question :
                            
                            Question:
                            {{question}}
                            
                            Base your answer on the following information:
                            {{information}}"""
            );

            String information = relevant.stream().map(rlv -> rlv.embedded().text()).collect(Collectors.joining("\n\n"));
            Map<String, Object> templateParameters = new HashMap<>();
            templateParameters.put("question", question.getQuestion());
            templateParameters.put("information", information);
            Prompt prompt = template.apply(templateParameters);

            Response<String> generatedAnswer = languageModel.generate(prompt);
            return new CompleteAnswer(generatedAnswer.content(), answers);
        }

        return new CompleteAnswer(Constants.EMPTY_STRING, answers);

    }

}
