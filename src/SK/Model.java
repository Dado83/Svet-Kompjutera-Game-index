package SK;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;


public class Model {
    
	private static final Logger LOGGER = Logger.getLogger(Model.class.getName());
    private Set<GameReview> gameIndex;
    private String searchResultHTML;
    private int numberOfResults;
    
    
    public int getNumberOfResults() {
    	return numberOfResults;
    }
    
    public void setSearchResultHTML(String search) {
    	searchResultHTML = search;
    }
    
    public String getSearchResultHTML() {
		return searchResultHTML;
	}
	
    
    @SuppressWarnings("unchecked")
	public Set<GameReview> getGameIndex() {
    	LOGGER.info("Entering getGameIndex()\n");
    	StringBuilder gameIndexGson;
        try {
        	LOGGER.log(Level.INFO, "Pocinjem ucitavat linkove...\n");
            URL url = new URL("http://fairplay.hol.es/SKGameIndex.txt");
            Gson gson = new Gson();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String string = "";
            gameIndexGson = new StringBuilder();
            while ((string = reader.readLine()) != null) {
            	gameIndexGson.append(string);
            	}
            }     
            Type token = new TypeToken<Set<GameReview>>() {}.getType();
            gameIndex = (Set<GameReview>) gson.fromJson(gameIndexGson.toString(), token);
            LOGGER.log(Level.INFO, "Broj linkova: " + gameIndex.size() +"\n"); 
            return gameIndex;
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, "URL problem in getGameIndex()\n", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "IO problem in getGameIndex()\n", ex);
        }
        LOGGER.info("Leaving getGameIndex()\n");
        Set<GameReview> set = new HashSet<>();
        set.add(new GameReview("", "", "Slaba konekcija, ne mogu u�itati linkove...", ""));
        return set; 
        }
    
    
    public List<GameReview> gameIndexSearch(Set<GameReview> gameInd, String naslov, String autor, 
    		int ocjena, int god1, int god2) throws FileNotFoundException, IOException { 
    	LOGGER.info("Entering gameIndexSearch()\n");
        List<GameReview> gameIndex = new ArrayList<>(gameInd);
        Collections.sort(gameIndex , (GameReview o1, GameReview o2) -> o2.getLink().compareTo(o1.getLink()));
        List<GameReview> searchResult = new ArrayList<>();
        gameIndex.stream().filter((index) -> ((index.getTitle().toLowerCase().contains(naslov.toLowerCase()))
        								  && (index.getAuthor().toLowerCase().contains(autor.toLowerCase())) 
        								  && (index.getScore() >= ocjena) && (index.getYear() >= god1) 
        								  && (index.getYear() <= god2)))
                .forEachOrdered((i) -> {searchResult.add(i);});
            searchResultHTML = writeToHTML(searchResult);
            numberOfResults = searchResult.size();
            LOGGER.info("Leaving gameIndexSearch()\n");
            return searchResult;
        }
     
   
    public String writeToHTML(List<GameReview> a) {
    	LOGGER.info("Entering writeToHTML()\n");
        StringBuilder html = new StringBuilder();
        html.append("<table>");
        html.append("<tr><th>datum</th><th>naslov</th><th>autor</th><th>ocjena</th></tr>");
        a.forEach((i) -> {
            html.append("<tr><td>").append(i.getDate()).append("</td><td><a href=").append(i.getLink()).append(">")
                    .append(i.getTitle()).append("</a></td><td>").append(i.getAuthor()).append("</td><td>")
                    .append(i.getScore()).append("</td></tr>");
        });
        html.append("</table>");
        LOGGER.info("Leaving writeToHTML()\n");
        return html.toString(); 
     }
    
}