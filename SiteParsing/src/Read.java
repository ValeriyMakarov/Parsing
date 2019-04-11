import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Read {
    private String url;
    private String site_url;
    private String site_page;
    private Document site_html;
    private ListArticles list;
    Read(String url){
        this.url = url;
        try {
            splitLink();
        }catch (Exception e){System.out.println(e);}
    }
    public ListArticles readSite(){
        Elements elements_for_parsing;
        ListArticles all_articles = new ListArticles();
        try {
            site_html = Jsoup.connect(url).get();
            elements_for_parsing = separateCode();
            elements_for_parsing = searchArticles(elements_for_parsing);
            for(Element element:elements_for_parsing){
                all_articles.add(getArticles(element));
            }

            for(int i=0; i<all_articles.size(); i++){
                if(all_articles.get(i).getImages().size()!=0){
                    List<String> new_list = new ArrayList<>();
                    new_list.add(all_articles.get(i).getImages().get(0));
                    all_articles.get(i).setImages(new_list);
                }
            }
        }catch (Exception ex){System.out.println(ex);}
        list=all_articles;
        return all_articles;
    }
    private void splitLink() throws IllegalArgumentException{
        int max_index=-1;
        String max_item=null;
        for(String domen: Domains.getDomains()){
            if(url.indexOf(domen)>max_index){
                max_index=url.indexOf(domen);
                max_item=domen;
            }
        }
        if(max_index==-1 || max_item==null)
            throw new IllegalArgumentException("Incorrect link or unknown domain!");
        else{
            this.site_page = url.substring(max_index + max_item.length());
            this.site_url = url.substring(0, max_index + max_item.length());
            if(site_page.length()==0) site_page=null;
        }
    }
    private Elements separateCode(){
        /*1: отделить код от футера и хедера*/
        Elements elements = new Elements();
        for(Element element: site_html.body().children()){
            if(!element.nodeName().equals("header")         &&
               !element.nodeName().equals("footer")         &&
               !element.attr("id").matches(".*header.*")    &&
               !element.attr("id").matches(".*footer.*")    &&
               !element.attr("class").matches(".*header.*") &&
               !element.attr("class").matches(".*footer.*") &&
               !element.attr("role").matches(".*header.*")  &&
               !element.attr("role").matches(".*footer.*")){
                elements.add(element);
            }
        }
        return elements;
    }
    private Elements searchArticles(Elements elements_for_parsing){
        Elements elements = new Elements();
        Elements images;
        for(Element element: elements_for_parsing.select("a")){
            Element parent = element;
            boolean is_article=false;
            while (!parent.nodeName().equals("article")&&!parent.nodeName().equals("body")) {
                parent=parent.parent();
                if(parent.nodeName().equals("article")){is_article=true;}
            }
            if(is_article) {
                elements.add(parent);
            }
            else {
                parent = element;
                images = element.select("img");

                if (images.size() > 0 && images!=null &&
                !(images.size()==1 && images.get(0).attr("src").matches(".*logo.*"))){
                    boolean have_text=false;
                    while (!parent.nodeName().equals("body")&&parent.textNodes().size()<=0){
                        parent=parent.parent();
                        if(parent.textNodes().size()>0)have_text=true;
                    }
                    if(have_text) {
                        elements.add(parent);
                    }
                }
            }
        }
        return elements;
    }
    private Article getArticles(Element article_container){
        String text="";
        String link;
        List<String> images = new ArrayList<>();
        String temp;
        if(article_container.nodeName().equals("article")){
        /*    картинка style="background-image:url(ссылка)"
            ссылка select("a").first.attr("href")
            текст select h, p textnodes*/
            temp = article_container.attr("style");
            if(temp!=null && temp.length()!=0 && temp.matches("background-image:url\\(.*\\)")){
                images.add(temp.substring("background-image:url(".length(), temp.lastIndexOf(")")));
            }
            else{
                for(Element element: article_container.select("img")){
                    if(element.attr("src")!=null&&element.attr("src").length()!=0){
                        if(!element.attr("src").matches("https:.*"))
                            images.add(site_url+"/"+element.attr("src"));
                        else{images.add(element.attr("src"));}
                    }
                }
            }
            temp = article_container.selectFirst("a").attr("href");
            if(!temp.matches("https:.*"))
                link = site_url+"/"+temp;
            else link=temp;

            for(Element element: article_container.select("*")){
                if(element.nodeName().matches("h\\d+") || element.nodeName().equals("p"))
                text+=element.text()+"\n\n";
            }
        }
        else{
            for(Element element: article_container.select("img")){
                if(element.attr("src")!=null&&element.attr("src").length()!=0) {
                    if(!element.attr("src").matches("https:.*"))
                        images.add(site_url+"/"+element.attr("src"));
                    else{images.add(element.attr("src"));}
                }
            }
            temp = article_container.selectFirst("a").attr("href");
            if(!temp.matches("https:.*")){
                link = site_url+"/"+temp;
            }
            else{link=temp;}
            for(Element element: article_container.select("*")){
                if(element.nodeName().matches("h\\d+") || element.nodeName().equals("p"))
                    text+=element.text()+"\n\n";
            }
        }
        return new Article(link, images, text);
    }

    public ListArticles getList() {
        return list;
    }

    public String getSite_url() {
        return site_url;
    }

    public String getSite_page() {
        return site_page;
    }

}
