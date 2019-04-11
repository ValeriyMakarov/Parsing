import java.io.Serializable;

public class SiteUrl implements Serializable {
    private String url;
    SiteUrl(String url){
        this.url=url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
