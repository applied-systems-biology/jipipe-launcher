package org.hkijena.jipipe.launcher.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.hkijena.jipipe.api.JIPipeProgressInfo;
import org.hkijena.jipipe.extensions.settings.RuntimeSettings;
import org.hkijena.jipipe.utils.WebUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JIPipeInstanceDownload {
    private String name;
    private String url;
    private List<String> urlMultiPart = new ArrayList<>();
    private String multiPartOutputName;
    private JIPipeInstanceDownloadType type = JIPipeInstanceDownloadType.FullPackage;
    private List<String> operatingSystems = new ArrayList<>();

    public JIPipeInstanceDownload() {
    }

    public JIPipeInstanceDownload(JIPipeInstanceDownload other) {
        this.name = other.name;
        this.url = other.url;
        this.urlMultiPart = new ArrayList<>(other.urlMultiPart);
        this.multiPartOutputName = other.multiPartOutputName;
        this.type = other.type;
        this.operatingSystems = new ArrayList<>(other.operatingSystems);
    }


    @JsonGetter("name")
    public String getName() {
        return name;
    }

    @JsonGetter("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonGetter("type")
    public JIPipeInstanceDownloadType getType() {
        return type;
    }

    @JsonSetter("type")
    public void setType(JIPipeInstanceDownloadType type) {
        this.type = type;
    }

    @JsonGetter("operating-systems")
    public List<String> getOperatingSystems() {
        return operatingSystems;
    }

    @JsonSetter("operating-systems")
    public void setOperatingSystems(List<String> operatingSystems) {
        this.operatingSystems = operatingSystems;
    }

    @JsonGetter("url")
    public String getUrl() {
        return url;
    }

    @JsonSetter("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonGetter("url-multipart")
    public List<String> getUrlMultiPart() {
        return urlMultiPart;
    }

    @JsonSetter("url-multipart")
    public void setUrlMultiPart(List<String> urlMultiPart) {
        this.urlMultiPart = urlMultiPart;
    }

    @JsonGetter("multipart-output-name")
    public String getMultiPartOutputName() {
        return multiPartOutputName;
    }

    @JsonSetter("multipart-output-name")
    public void setMultiPartOutputName(String multiPartOutputName) {
        this.multiPartOutputName = multiPartOutputName;
    }
    
    public JIPipeInstanceDownloadResult download(JIPipeProgressInfo progressInfo) {
        Path outputFile;
        String extension;

        if (getUrlMultiPart() != null && !getUrlMultiPart().isEmpty()) {
            progressInfo.log("The archive was split into the following parts by the developer: ");
            for (String url : getUrlMultiPart()) {
                progressInfo.log(" - " + url);
            }

            // Detect extension
            if (getMultiPartOutputName().endsWith(".tar.gz")) {
                extension = ".tar.gz";
            } else if (getMultiPartOutputName().endsWith(".tar.xz")) {
                extension = ".tar.xz";
            } else {
                String[] split = getMultiPartOutputName().split("\\.");
                extension = "." + split[split.length - 1];
            }

            List<Path> multiPartFiles = new ArrayList<>();

            List<String> urlMultiPart = getUrlMultiPart();
            for (int i = 0; i < urlMultiPart.size(); i++) {
                JIPipeProgressInfo partProgress = progressInfo.resolveAndLog("Download part", i, urlMultiPart.size());
                String url = urlMultiPart.get(i);
                Path multiPartTmpFile = RuntimeSettings.generateTempFile("repository", extension);

                try {
                    WebUtils.downloadNative(new URL(url), multiPartTmpFile, "Download part", partProgress);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }

                multiPartFiles.add(multiPartTmpFile);
            }

            // Concat the multipart files
            outputFile = RuntimeSettings.generateTempFile("repository", extension);
            progressInfo.log("Following files will be combined into " + outputFile);
            for (Path multiPartFile : multiPartFiles) {
                progressInfo.log(" - " + multiPartFile);
            }
            try (FileOutputStream stream = new FileOutputStream(outputFile.toFile())) {
                for (int i = 0; i < multiPartFiles.size(); i++) {
                    JIPipeProgressInfo partProgress = progressInfo.resolveAndLog("Merge part", i, urlMultiPart.size());
                    Path partFile = multiPartFiles.get(i);
                    Files.copy(partFile, stream);
                    stream.flush();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Cleanup
            for (Path path : multiPartFiles) {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    progressInfo.log("Could not clean up temporary file " + path);
                    progressInfo.log(e.toString());
                }
            }

        } else {
            progressInfo.log("The following URL will be downloaded: " + getUrl());

            // Detect extension
            if (getUrl().endsWith(".tar.gz")) {
                extension = ".tar.gz";
            } else if (getUrl().endsWith(".tar.xz")) {
                extension = ".tar.xz";
            } else {
                String[] split = getUrl().split("\\.");
                extension = "." + split[split.length - 1];
            }

            // Set output file and download directly
            outputFile = RuntimeSettings.generateTempFile("repository", extension);
            try {
                WebUtils.downloadNative(new URL(getUrl()), outputFile, "Download package", progressInfo.resolve("Download package"));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        return new JIPipeInstanceDownloadResult(outputFile, extension);
    }

    public String renderUrl() {
        if(urlMultiPart.isEmpty()) {
            return url;
        }
        else {
            return String.join("\n", urlMultiPart);
        }
    }
}
