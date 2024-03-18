package llm.devoxx.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import java.util.Set;


@JsonIgnoreProperties(ignoreUnknown=true)
public class A2nDocument {
    private String id;
    private String hash;
    private Map<String, Object> json;
    private Map<String, Object> attachment;
    private Set<String> geopoints;
    private Map<String, Object> entities;
    private Map<String, Object> metadata;
    private String language;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }


    public Map<String, Object> getJson() {
        return json;
    }

    public void setJson(Map<String, Object> json) {
        this.json = json;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    public Set<String> getGeopoints() {
        return geopoints;
    }

    public void setGeopoints(Set<String> geopoints) {
        this.geopoints = geopoints;
    }

    public Map<String, Object> getEntities() {
        return entities;
    }

    public void setEntities(Map<String, Object> entities) {
        this.entities = entities;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}

