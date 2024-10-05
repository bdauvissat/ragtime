package llm.adelean.json;

public class RagFolder {

    private String path;

    private int limit;

    public RagFolder() {
    }

    public RagFolder(String path, int limit) {
        this.path = path;
        this.limit = limit;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
