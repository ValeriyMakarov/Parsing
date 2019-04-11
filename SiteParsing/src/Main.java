
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    String file_name_data = "site_data.data";
    static File site_folder= null;
    static File site_data = null;
    static final File main_folder = new File("folder");
    static final File data = new File("folder/data.data");
    @FXML private VBox info;
    @FXML private Button parse;
    @FXML private Button save;
    @FXML private Button open;
    @FXML private Button add;
    @FXML private Button delete;
    @FXML private TableView<SiteUrl> table;
    @FXML private TableColumn<SiteUrl, String> table_column;
    @FXML private TextField text_field;
    Read site = null;
    ListArticles articles;
    private ObservableList<SiteUrl> list_with_urls;

    @FXML void initialize(){
        list_with_urls=readList();
        table_column.setCellValueFactory(new PropertyValueFactory<>("url"));
        table.setItems(list_with_urls);
        table.setPlaceholder(new Label("Добавьте ссылку"));

        save.setOnMouseClicked(event -> {
            ListArticles to_save = new ListArticles();
            if(!site_folder.exists())site_folder.mkdir();
            else {
                for (String file_path : site_folder.list()) {
                    String temp = main_folder + "/" + site.getSite_url().substring("https://".length());
                    if(site.getSite_page()!=null){
                        temp+="+"+site.getSite_page().substring(1)+"/" + file_path;
                    }
                    else{
                        temp+="/" + file_path;
                    }
                    File to_delete = new File(temp);
                }
                site_folder.delete();
                site_folder.mkdir();
                try {
                    site_data = new File(site_folder + "/" + file_name_data);
                    site_data.createNewFile();

                }catch (Exception e){System.out.println(e);}
            }
            for(int i=0; i<articles.size(); i++){
                String article_url;
                List<String> images =new ArrayList<>();
                String text;
                if(articles.get(i).getImages().size()!=0) {
                    try {
                        images.add(downloadImage(articles.get(i).getImages().get(0), site_folder));
                    }catch (Exception ex){System.out.println(ex);}
                }
                article_url = articles.get(i).getArticle_url();
                text = articles.get(i).getText();
                to_save.add(new Article(article_url,images,text));
            }

            saveArticles(to_save,site_data.getPath());
            save.setDisable(true);
        });
        open.setOnMouseClicked(event -> {
            Pane pane;
            ImageView imageView;
            Label label;
            Image image;
            List<Pane> panes= new ArrayList<>();
            VBox box;
            Hyperlink link;
            articles = readArticles(site_folder.getPath()+"/site_data.data");
            for(int i=0; i<articles.size(); i++){
                imageView = new ImageView();
                label = new Label();
                if(articles.get(i).getImages().size()!=0 && !articles.get(i).getImages().get(0).equals("")) {
                    //!!!!!!!!!!!!!!!!!!
                    File f = new File(articles.get(i).getImages().get(0));
                    image = new Image("file:"+f.getAbsolutePath());
                }
                else image = null;
                box = new VBox();
                pane = new Pane();
                pane.setStyle("-fx-border-width:1; -fx-border-color:silver;");
                pane.setPadding(new Insets(10,10,10,10));
                pane.getChildren().add(box);
                box.setSpacing(10);
                link = new Hyperlink();
                box.getChildren().addAll(imageView, label, link);
                link.setText(articles.get(i).getArticle_url());

                imageView.setImage(image);
                label.setText("Статья: \n"+articles.get(i).getText()+"\nСсылка: ");
                panes.add(pane);
                imageView.setFitWidth(480);
                imageView.setPreserveRatio(true);
            }
            if(articles.size()==0)info.getChildren().add(
                    new Label("Ничего не найдено"));
            info.getChildren().addAll(panes);
        });
        parse.setOnMouseClicked(event -> {
            save.setDisable(false);
            Pane pane;
            ImageView imageView;
            Label label;
            Image image;
            List<Pane> panes= new ArrayList<>();
            VBox box;
            Hyperlink link;
            articles = site.readSite();
            for(int i=0; i<articles.size(); i++){
                imageView = new ImageView();
                label = new Label();
                if(articles.get(i).getImages().size()!=0 && !articles.get(i).getImages().get(0).equals("")) {

                    image = new Image(articles.get(i).getImages().get(0));
                }
                else image = null;
                box = new VBox();
                pane = new Pane();
                pane.setStyle("-fx-border-width:1; -fx-border-color:silver;");
                pane.setPadding(new Insets(10,10,10,10));
                pane.getChildren().add(box);
                box.setSpacing(10);
                link = new Hyperlink();
                box.getChildren().addAll(imageView, label, link);
                link.setText(articles.get(i).getArticle_url());

                imageView.setImage(image);
                label.setText("Статья: \n"+articles.get(i).getText()+"\nСсылка: ");
                panes.add(pane);
                imageView.setFitWidth(480);
                imageView.setPreserveRatio(true);
            }
            if(articles.size()==0)info.getChildren().add(
                    new Label("Ничего не найдено"));
            info.getChildren().addAll(panes);
        });
        add.setOnMouseClicked(event -> {
            list_with_urls.add(new SiteUrl(text_field.getText()));
            saveList(list_with_urls);
            table.refresh();
            text_field.setText("");
            add.setDisable(true);
        });
        delete.setOnMouseClicked(event -> {
            SiteUrl site_url = table.getSelectionModel().getSelectedItem();
            if(site_url!=null){
                for (String file_path : site_folder.list()) {
                    String temp=main_folder + "/" + site.getSite_url().substring("https://".length());
                    if(site.getSite_page()!=null)temp+="+"+site.getSite_page().substring(1);
                    File to_delete = new File(temp+"/"+ file_path);
                    to_delete.delete();
                }
                site_folder.delete();
                list_with_urls.remove(list_with_urls.indexOf(site_url));
                table.refresh();
                saveList(list_with_urls);
                delete.setDisable(true);
                table.getSelectionModel().select(null);
            }
        });
        text_field.setOnAction(event -> {
            if((text_field.getText().matches("https:.*\\.(ru|com|ua)")||
                    (text_field.getText().matches("https:.*\\.(ru|com|ua)(/.+)"))
                    && text_field.getText()!=null)){
                try{
                    Read test = new Read(text_field.getText());
                }catch (Exception e){}
                add.setDisable(false);
            }
            else add.setDisable(true);
        });
        table.setOnMouseClicked(event -> {
            try {
                SiteUrl site_url = table.getSelectionModel().getSelectedItem();

                if (site_url != null) {
                    info.getChildren().remove(0, info.getChildren().size());
                    delete.setDisable(false);
                    parse.setDisable(false);
                    open.setDisable(false);

                    site = new Read(site_url.getUrl());


                    String temp = main_folder + "/" + site.getSite_url().substring("https://".length());
                    if (site.getSite_page() != null) temp += "+" + site.getSite_page().substring(1);
                    site_folder = new File(temp);

                    if (!site_folder.exists()) {
                        site_folder.mkdir();

                        site_data = new File(site_folder + "/" + file_name_data);
                        try {
                            site_data.createNewFile();
                            FileOutputStream file_out = new FileOutputStream(site_data);
                            ObjectOutputStream obj_out = new ObjectOutputStream(file_out);
                            obj_out.writeObject(new ListArticles());
                            obj_out.close();
                            file_out.close();
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    }
                }
            }catch (Exception e){System.out.println(e);}
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Парсинг");
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(550);

        primaryStage.setScene(new Scene(root,800,475));
        primaryStage.show();


    }


    public static void main(String[] args) {
        if(!data.exists()) {
            try {
                data.createNewFile();
                FileOutputStream file_out = new FileOutputStream(data);
                ObjectOutputStream obj_out = new ObjectOutputStream(file_out);
                obj_out.writeObject(new ArrayList<>());
                obj_out.close();
                file_out.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        launch(args);

    }
    void saveArticles(ListArticles object, String file){
        try {
            FileOutputStream file_out = new FileOutputStream(file);
            ObjectOutputStream obj_out = new ObjectOutputStream(file_out);
            obj_out.writeObject(object);
            obj_out.close();
            file_out.close();
        }catch (Exception ex){
            System.out.println(ex);
            System.exit(-2);
        }
    }
    ListArticles readArticles(String file){
        ListArticles read_obj = null;
        FileInputStream file_in=null;
        ObjectInputStream obj_in= null;
        try {
            file_in = new FileInputStream(file);
            obj_in = new ObjectInputStream(file_in);
            read_obj = (ListArticles) obj_in.readObject();
            obj_in.close();
            file_in.close();
        }catch (Exception ex){}
        return read_obj;
    }
    void saveList(ObservableList<SiteUrl> list){
        List<SiteUrl> new_list = new ArrayList<>();
        for(SiteUrl site_url: list){
            new_list.add(site_url);
        }
        try {
            FileOutputStream file_out = new FileOutputStream(data);
            ObjectOutputStream obj_out = new ObjectOutputStream(file_out);
            obj_out.writeObject(new_list);
            obj_out.close();
            file_out.close();
        }catch (Exception ex){
            System.out.println(ex);
            System.exit(-1);
        }
    }
    ObservableList<SiteUrl> readList(){
        List<SiteUrl> read_obj = null;
        ObservableList<SiteUrl> new_list = FXCollections.observableArrayList();
        FileInputStream file_in=null;
        ObjectInputStream obj_in= null;
        try {
            file_in = new FileInputStream(data);
            obj_in = new ObjectInputStream(file_in);
            read_obj = (List<SiteUrl>) obj_in.readObject();
            obj_in.close();
            file_in.close();
        }catch (Exception ex){
            System.out.println(ex);
            System.exit(-1);
        }
        for(SiteUrl site_url: read_obj){
            new_list.add(site_url);
        }
        return new_list;
    }
    public String downloadImage(String image_url, File folder_name) throws Exception{

        if(image_url.length()==0 || image_url==null)throw new IllegalArgumentException("No url");
        URL url = new URL(image_url);

        String image_path = folder_name + "/data"+new File(folder_name.getPath()).list().length +
                image_url.substring(image_url.lastIndexOf("."));

        InputStream in = url.openStream();
        OutputStream out = new BufferedOutputStream(new FileOutputStream(image_path));

        for (int b; (b = in.read()) != -1; ) {
            out.write(b);
        }
        out.close();
        in.close();
        return image_path;
    }
}
