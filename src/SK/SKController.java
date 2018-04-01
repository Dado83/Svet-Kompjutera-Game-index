package SK;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.*;
import javafx.stage.Stage;


public class SKController implements Initializable {

    @FXML
    private WebView web;
    @FXML
    private TextField naslov;
    @FXML
    private TextField autor;
    @FXML
    private TextField ocjena;
    
    HashSet<Igra> skLista;
    Model sk;
   
    String s = null;
    WebEngine browser;
    
    @FXML
    private Spinner<Integer> minG = new Spinner<>(1998, 2017, 1998, 1);;
    @FXML
    private Spinner<Integer> maxG = new Spinner<>(1998, 2017, 2017, 1);;
    
    SpinnerValueFactory<Integer> min = new SpinnerValueFactory.IntegerSpinnerValueFactory(1998, 2017, 1998);
    SpinnerValueFactory<Integer> max = new SpinnerValueFactory.IntegerSpinnerValueFactory(1998, 2017, 2017);
    @FXML
    private AnchorPane pane;
    @FXML
    private MenuItem update;
    
    Stage primarysStage;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        minG.setValueFactory(min);
        maxG.setValueFactory(max);
        sk = new Model();
        skLista = Model.gameIndex;
        ArrayList<Igra> igreLista = new ArrayList<>(skLista);
        Collections.sort(igreLista, (Igra o1, Igra o2) -> o2.link.compareTo(o1.link));
        s = sk.writeToHTML(igreLista);
        browser = web.getEngine();
        browser.loadContent(s);
        Model.webData = s;
    }    
     
    //resetovanje pretrage
    @FXML
    public void pocetna() {
        browser.loadContent(s);
        naslov.setText("");
        autor.setText("");
        ocjena.setText("");
        min.setValue(1000);
        max.setValue(3000);
        Model.webData = s;
    }
    
    //pretraga linkova
    @FXML
    public void pretraga() throws IOException {
        
    if (autor.getText().isEmpty() && ocjena.getText().isEmpty()){
        browser.loadContent(sk.writeToHTML(sk.gameIndexSearch(skLista, naslov.getText(), "", -1, minG.getValue(), maxG.getValue())));
    } else if (naslov.getText().isEmpty() && ocjena.getText().isEmpty()){
        browser.loadContent(sk.writeToHTML(sk.gameIndexSearch(skLista, "", autor.getText(), -1, minG.getValue(), maxG.getValue())));
    } else if (naslov.getText().isEmpty() && autor.getText().isEmpty()){
        browser.loadContent(sk.writeToHTML(sk.gameIndexSearch(skLista, "", "", Integer.parseInt(ocjena.getText()), minG.getValue(), maxG.getValue())));
    } else if (naslov.getText().isEmpty()){
        browser.loadContent(sk.writeToHTML(sk.gameIndexSearch(skLista, "", autor.getText(), Integer.parseInt(ocjena.getText()), minG.getValue(), maxG.getValue())));
    } else if (autor.getText().isEmpty()){
        browser.loadContent(sk.writeToHTML(sk.gameIndexSearch(skLista, naslov.getText(), "", Integer.parseInt(ocjena.getText()), minG.getValue(), maxG.getValue())));
    } else if(ocjena.getText().isEmpty()){
        browser.loadContent(sk.writeToHTML(sk.gameIndexSearch(skLista, naslov.getText(), autor.getText(), -1, minG.getValue(), maxG.getValue())));
    } else if (naslov.getText().isEmpty() && autor.getText().isEmpty() && ocjena.getText().isEmpty()){
        browser.loadContent(sk.writeToHTML(sk.gameIndexSearch(skLista, "", "", -1, minG.getValue(), maxG.getValue())));
    } else {
        browser.loadContent(sk.writeToHTML(sk.gameIndexSearch(skLista, naslov.getText(), autor.getText(), Integer.parseInt(ocjena.getText()), minG.getValue(), maxG.getValue())));
        }
    }
    
    
    //snimanje u linkova u fajl
    @FXML
    public void snimiHTML() throws IOException, URISyntaxException {
        
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(System.getProperty("user.home"), 
                                                                                           "\\desktop\\SK igre.html"))) {
            writer.write(Model.webData);
            writer.flush();
        } 
    }
    
    //info
    @FXML
    public void about() throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/about.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/dp.png")));
        stage.setResizable(false);
        stage.show();
    }
}