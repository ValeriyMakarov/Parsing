import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListArticles implements Serializable {
    private List<Article> articles = new ArrayList<>();
    public ListArticles(){}
    public void add(Article article){
        if(articles.size()==0){
            articles.add(article);
            return;
        }
        boolean exists=false;
        for(int i=0;i<articles.size();i++) {
            if (articles.get(i).getArticle_url().equals(article.getArticle_url())){
                exists=true;
                break;
            }
        }
        if(!exists)articles.add(article);
    }
    public Article get(int number){
        return articles.get(number);
    }
    public int size(){
        return articles.size();
    }
}
