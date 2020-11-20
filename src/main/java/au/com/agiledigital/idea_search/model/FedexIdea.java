package au.com.agiledigital.idea_search.model;

import jdk.nashorn.internal.ir.annotations.Immutable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Immutable
public class FedexIdea {
    private final long globalId;
    private final long id;
    private final long contentId;
    private final String technology;
    private final String creator;
    private final String title;

    @JsonCreator
    private FedexIdea(@JsonProperty("globalId") long globalId, @JsonProperty("id") long id, @JsonProperty("contentId") long contentId, @JsonProperty("technology") String technology, @JsonProperty("creator") String creator, @JsonProperty("title") String title) {
        this.globalId = globalId;
        this.id = id;
        this.contentId = contentId;
        this.technology = technology;
        this.creator = creator;
        this.title = title;
    }

    public long getGlobalId() {
        return this.globalId;
    }

    public long getId() {
        return this.id;
    }

    public long getContentId() {
        return this.contentId;
    }

    public String getTechnology() {
        return this.technology;
    }

    public String getCreator() {
        return this.creator;
    }

    public String getTitle() {
        return this.title;
    }

    public String toString() {
        return "Idea [globalId=" + this.globalId + ", id=" + this.id + ", contentId=" + this.contentId + ", technology=" + this.technology + ", creator=" + this.creator + ", title=" + this.title + "]";
    }

    public static class Builder {
        private long globalId;
        private long id;
        private long contentId;
        private String technology;
        private String creator;
        private String title;

        public Builder() {
        }

        public Builder(FedexIdea fedexIdea) {
            this.globalId = fedexIdea.globalId;
            this.id = fedexIdea.id;
            this.contentId = fedexIdea.contentId;
            this.technology = fedexIdea.technology;
            this.creator = fedexIdea.creator;
            this.title = fedexIdea.title;
        }

        public FedexIdea.Builder withTechnology(String technology) {
            this.technology = technology;
            return this;
        }

        public FedexIdea.Builder withGlobalId(long globalId){
            this.globalId = globalId;
            return this;
        }

        public FedexIdea.Builder withId(long id){
            this.id = id;
            return this;
        }

        public  FedexIdea.Builder withContentId(long contentId) {
            this.contentId = contentId;
            return this;
        }

        public  FedexIdea.Builder withCreator(String creator){
            this.creator = creator;
            return this;
        }

        public FedexIdea build() {
            return new FedexIdea(this.globalId, this.id, this.contentId, this.technology, this.creator, this.title);
        }
    }
}

