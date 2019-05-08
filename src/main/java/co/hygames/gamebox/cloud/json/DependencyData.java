package co.hygames.gamebox.cloud.json;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Niklas Eicker
 */
public class DependencyData implements Serializable {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("versionRange")
    @Expose
    private String versionRange;

    private final static long serialVersionUID = 3080774369300795773L;

    public DependencyData() {
    }

    public DependencyData(String name, String versionRange) {
        super();
        this.name = name;
        this.versionRange = versionRange;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DependencyData withName(String name) {
        this.name = name;
        return this;
    }

    public String getVersionRange() {
        return versionRange;
    }

    public void setVersionRange(String versionRange) {
        this.versionRange = versionRange;
    }

    public DependencyData withVersionRange(String versionRange) {
        this.versionRange = versionRange;
        return this;
    }
}
