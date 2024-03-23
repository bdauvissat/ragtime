package llm.devoxx.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import llm.devoxx.json.CompleteAnswer;
import llm.devoxx.json.Question;
import llm.devoxx.services.AnswerService;

@Path(("/answer"))
public class AnswerResource {

    @Inject
    AnswerService answerService;


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public CompleteAnswer firstAnswer(Question question) {
        return answerService.processQuestion(question);
    }

}
