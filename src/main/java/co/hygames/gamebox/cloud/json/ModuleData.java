package co.hygames.gamebox.cloud.json;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Niklas Eicker
 */
public class ModuleData implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("author")
    @Expose
    private String author;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("versions")
    @Expose
    private List<VersionData> versions = null;

    private final static long serialVersionUID = 4719087577866667965L;

    public ModuleData() {
    }

    public ModuleData(Integer id, String author, String name, String description, List<VersionData> versions) {
        this.id = id;
        this.author = author;
        this.name = name;
        this.description = description;
        this.versions = versions;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ModuleData withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ModuleData withAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModuleData withName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ModuleData withDescription(String description) {
        this.description = description;
        return this;
    }

    public List<VersionData> getVersions() {
        return versions;
    }

    public void setVersions(List<VersionData> versions) {
        this.versions = versions;
    }

    public ModuleData withVersions(List<VersionData> versions) {
        this.versions = versions;
        return this;
    }
}
