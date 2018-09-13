package SK;

import gamereview.GameReview;
import gamereview.Model;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


public class SKController implements Initializable {

    @FXML
    private WebView webView;
    @FXML
    private TextField title;
    @FXML
    private TextField author;
    @FXML
    private TextField score;
    @FXML
    private Label numberOfLinks;
    @FXML
    private Spinner<Integer> minYear = new Spinner<>(1998, 2018, 1998, 1);
    @FXML
    private Spinner<Integer> maxYear = new Spinner<>(1998, 2018, 2018, 1);

    SpinnerValueFactory<Integer> min = new SpinnerValueFactory.IntegerSpinnerValueFactory(1998, 2018, 1998);
    SpinnerValueFactory<Integer> max = new SpinnerValueFactory.IntegerSpinnerValueFactory(1998, 2018, 2018);

    private static final Logger LOGGER = Logger.getLogger(SKController.class.getName());

    Set<GameReview> gameIndex = new HashSet<>();
    String gameIndexList;
    WebEngine browser;
    Model model;

    String message = "ucitavam linkove, moze potrajati ako je konekcija spora...";
    String startPage = "<div style='font-size:30px; position:absolute; top:40%; left:20%'>" + message + "</div>";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("Entering initialize()\n");
        model = new Model();
        minYear.setValueFactory(min);
        maxYear.setValueFactory(max);
        browser = webView.getEngine();
        gameIndex = model.getGameIndex();
        List<GameReview> gameIndexA = new ArrayList<>(gameIndex);
        Collections.sort(gameIndexA, (GameReview o1, GameReview o2) -> o2.getLink().compareTo(o1.getLink()));
        gameIndexList = model.writeToHTML(gameIndexA);
        numberOfLinks.setText("" + gameIndex.size());
        LOGGER.info("Leaving initialize()\n");
        browser.loadContent(gameIndexList);
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
        LOGGER.info("Entering search()\n");
        int scoreValue;
        if (score.getText().isEmpty()) {
            scoreValue = -1;
        } else {
            scoreValue = Integer.parseInt(score.getText());
        }
        browser.loadContent(model.writeToHTML(model.gameIndexSearch(gameIndex, title.getText(),
                author.getText(), scoreValue, minYear.getValue(), maxYear.getValue())));
        numberOfLinks.setText("" + model.getNumberOfResults());
        LOGGER.info("Leaving search()\n");
    }

    @FXML
    public void saveSearchToDesktop() throws IOException, URISyntaxException {
        LOGGER.info("Entering saveSearchToDesktop()\n");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                new File(System.getProperty("user.home"), "\\desktop\\SK igre.html")))) {
            writer.write(model.getSearchResultHTML());
            writer.flush();
        }
        LOGGER.info("Leaving saveSearchToDesktop()\n");
    }

    @FXML
    public void openAboutWindow() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/about.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon/dp.png")));
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    public void openUpdateWindow() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/update.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setOpacity(0.95);
        stage.getIcons().add(new Image(getClass().getResource("/icon/sk.png").toString()));
        stage.show();
    }

}
