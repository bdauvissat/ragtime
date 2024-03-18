package llm.devoxx.resource;

import llm.devoxx.json.CompleteAnswer;
import llm.devoxx.json.QuestionRequest;
import llm.devoxx.services.AnswerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path(("/answer"))
public class AnswerResource {

    @Inject
    AnswerService answerService;


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public CompleteAnswer firstAnswer(QuestionRequest question) {
        return answerService.processQuestion(question);
    }

}
