package org.hkijena.jipipe.launcher.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;
import java.util.List;

public class JIPipeInstanceChangeLog {
    private String url;
    private String urlDev;
    private List<String> summary = new ArrayList<>();

    public JIPipeInstanceChangeLog() {
    }

    public JIPipeInstanceChangeLog(JIPipeInstanceChangeLog other) {
        this.url = other.url;
        this.urlDev = other.urlDev;
        this.summary = new ArrayList<>(other.summary);
    }

    @JsonGetter("url")
    public String getUrl() {
        return url;
    }

    @JsonSetter("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonGetter("url-dev")
    public String getUrlDev() {
        return urlDev;
    }

    @JsonSetter("url-dev")
    public void setUrlDev(String urlDev) {
        this.urlDev = urlDev;
    }

    @JsonGetter("summary")
    public List<String> getSummary() {
        return summary;
    }

    @JsonSetter("summary")
    public void setSummary(List<String> summary) {
        this.summary = summary;
    }
}
