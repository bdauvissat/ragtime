package llm.devoxx.json;

public class VectorDocumentRequest {

    private A2nDocument a2nDocument;
    private String index;

    public VectorDocumentRequest(A2nDocument a2nDocument, String index) {
        this.a2nDocument = a2nDocument;
        this.setIndex(index);
    }

    public VectorDocumentRequest() {
    }

    public A2nDocument getDocument() {
        return a2nDocument;
    }

    public void setAllDocument(A2nDocument A2nDocument) {
        this.a2nDocument = A2nDocument;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        int timestampIndex = index.lastIndexOf("-");
        if (timestampIndex > 0) {
            this.index = index.substring(0, timestampIndex);
        } else {
            this.index = index;
        }
    }
}
