package co.hygames.gamebox.cloud.json;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Niklas Eicker
 */
public class VersionData implements Serializable {
    @SerializedName("version")
    @Expose
    private String version;

    @SerializedName("dependencies")
    @Expose
    private List<DependencyData> dependencies = null;

    @SerializedName("releaseNotes")
    @Expose
    private List<String> releaseNotes = null;

    private final static long serialVersionUID = -2433806999627043447L;

    public VersionData() {
    }

    public VersionData(String version, List<DependencyData> dependencies, List<String> releaseNotes) {
        this.version = version;
        this.dependencies = dependencies;
        this.releaseNotes = releaseNotes;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public VersionData withVersion(String version) {
        this.version = version;
        return this;
    }

    public List<DependencyData> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<DependencyData> dependencies) {
        this.dependencies = dependencies;
    }

    public VersionData withDependencies(List<DependencyData> dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public List<String> getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(List<String> releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public VersionData withReleaseNotes(List<String> releaseNotes) {
        this.releaseNotes = releaseNotes;
        return this;
    }
}
