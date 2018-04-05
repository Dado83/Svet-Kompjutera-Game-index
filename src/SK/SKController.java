package SK;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


public class SKController implements Initializable {

    @FXML
    private MenuItem update;
	@FXML
    private WebView webView;
    @FXML
    private TextField title;
    @FXML
    private TextField author;
    @FXML
    private TextField score;
    @FXML
    private AnchorPane pane;
    @FXML
    private Label numberOfLinks;
    @FXML
    private Spinner<Integer> minYear = new Spinner<>(1998, 2018, 1998, 1);
    @FXML
    private Spinner<Integer> maxYear = new Spinner<>(1998, 2018, 2018, 1);
  
    SpinnerValueFactory<Integer> min = new SpinnerValueFactory.IntegerSpinnerValueFactory(1998, 2018, 1998);
    SpinnerValueFactory<Integer> max = new SpinnerValueFactory.IntegerSpinnerValueFactory(1998, 2018, 2018);
    HashSet<Igra> gameIndex = new HashSet<>();
    String message = "uèitavam linkove, može potrajati ako je konekcija spora...";
    String startPage = "<div style='font-size:30px; position:absolute; top:40%; left:20%'>"+ message +"</div>";
    String gameIndexList;
    WebEngine browser;
    Model model;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {	
    	model = new Model();
        minYear.setValueFactory(min);
        maxYear.setValueFactory(max);
        gameIndex = model.getGameIndex();
        ArrayList<Igra> gameIndexA = new ArrayList<>(gameIndex);
        Collections.sort(gameIndexA, (Igra o1, Igra o2) -> o2.link.compareTo(o1.link));
        gameIndexList = model.writeToHTML(gameIndexA);
        browser = webView.getEngine();
        browser.loadContent(gameIndexList);
        model.setSearchResultHTML(gameIndexList);
        numberOfLinks.setText("" + gameIndex.size());
    }    
     

    @FXML
    public void reset() { 	
        browser.loadContent(gameIndexList);
        title.setText("");
        author.setText("");
        score.setText("");
        min.setValue(1000);
        max.setValue(3000);
        model.setSearchResultHTML(gameIndexList);
        numberOfLinks.setText("" + gameIndex.size());
    }
    

    @FXML
    public void search() throws IOException {        
    if (author.getText().isEmpty() && score.getText().isEmpty()) {
        browser.loadContent(model.writeToHTML(model.gameIndexSearch(gameIndex, 
        		title.getText(), "", -1, minYear.getValue(), maxYear.getValue())));
    } else if (title.getText().isEmpty() && score.getText().isEmpty()) {
        browser.loadContent(model.writeToHTML(model.gameIndexSearch(gameIndex, 
        		"", author.getText(), -1, minYear.getValue(), maxYear.getValue())));
    } else if (title.getText().isEmpty() && author.getText().isEmpty()) {
        browser.loadContent(model.writeToHTML(model.gameIndexSearch(gameIndex, 
        		"", "", Integer.parseInt(score.getText()), minYear.getValue(), maxYear.getValue())));
    } else if (title.getText().isEmpty()) {
        browser.loadContent(model.writeToHTML(model.gameIndexSearch(gameIndex, 
        		"", author.getText(), Integer.parseInt(score.getText()), minYear.getValue(), maxYear.getValue())));
    } else if (author.getText().isEmpty()) {
        browser.loadContent(model.writeToHTML(model.gameIndexSearch(gameIndex, 
        		title.getText(), "", Integer.parseInt(score.getText()), minYear.getValue(), maxYear.getValue())));
    } else if(score.getText().isEmpty()) {
        browser.loadContent(model.writeToHTML(model.gameIndexSearch(gameIndex, 
        		title.getText(), author.getText(), -1, minYear.getValue(), maxYear.getValue())));
    } else if (title.getText().isEmpty() && author.getText().isEmpty() && score.getText().isEmpty()) {
        browser.loadContent(model.writeToHTML(model.gameIndexSearch(gameIndex, 
        		"", "", -1, minYear.getValue(), maxYear.getValue())));
    } else {
        browser.loadContent(model.writeToHTML(model.gameIndexSearch(gameIndex, 
        		title.getText(), author.getText(), Integer.parseInt(score.getText()), 
        		minYear.getValue(), maxYear.getValue())));
        }
    numberOfLinks.setText("" + model.getNumberOfResults());
    }
    
    
    @FXML
    public void saveSearchToDesktop() throws IOException, URISyntaxException {   	
        try (BufferedWriter writer = Files.newBufferedWriter(
        		Paths.get(System.getProperty("user.home"), "\\desktop\\SK igre.html"))) {
            writer.write(model.getSearchResultHTML());
            writer.flush();
        } 
    }
    
    
    @FXML
    public void openAboutWindow() throws IOException {    	
        Parent parent = FXMLLoader.load(getClass().getResource("/about.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/dp.png")));
        stage.setResizable(false);
        stage.show();
    }
}