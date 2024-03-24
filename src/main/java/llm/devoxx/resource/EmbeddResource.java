package llm.devoxx.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import llm.devoxx.json.RagDocument;
import llm.devoxx.json.RagFolder;
import llm.devoxx.services.EmbeddingService;

@Path(("/embed"))
public class EmbeddResource {

    @Inject
    EmbeddingService embeddingService;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public void storeVectors (RagDocument document) {
        embeddingService.embedDocument(document);
    }

    @Path("/folder")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void embedFolder(RagFolder folder) {

        embeddingService.embeddFolder(folder);

    }

}
