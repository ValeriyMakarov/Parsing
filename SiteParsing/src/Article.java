import javafx.collections.ObservableList;
import javafx.scene.image.*;

import java.io.Serializable;
import java.util.List;

public class Article implements Serializable {
    private String article_url;
    private List<String> images;
    private String text;
    public Article(String article_url, List<String> images, String text){
        this.article_url = article_url;
        this.images = images;
        this.text = text;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getArticle_url() {
        return article_url;
    }

    public String getText() {
        return text;
    }
}
