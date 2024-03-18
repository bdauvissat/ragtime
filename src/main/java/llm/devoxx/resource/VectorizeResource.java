package llm.devoxx.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import llm.devoxx.json.RagDocument;
import llm.devoxx.services.VectorizeService;

@Path(("/vector"))
public class VectorizeResource {

    @Inject
    VectorizeService vectorizeService;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public void storeVectors (RagDocument document) {
        vectorizeService.convertToVector(document);
    }

}
