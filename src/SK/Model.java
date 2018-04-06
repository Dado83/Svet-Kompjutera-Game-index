package SK;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;


public class Model {
    
    private Set<Igra> gameIndex = new HashSet<>();
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
		
    public Set<Igra> getGameIndex() {
        try {
            System.out.println("Pocinjem ucitavat linkove...");
            URL url = new URL("http://fairplay.hol.es/skIgre");
            ObjectInputStream inputStream = new ObjectInputStream(url.openStream());
            gameIndex = (HashSet<Igra>)inputStream.readObject();
            System.out.println("Broj linkova: " + gameIndex.size());
            return gameIndex;
        } catch (MalformedURLException ex) {
            Logger.getLogger(Igra.class.getName()).log(Level.SEVERE, null, ex);
            Platform.exit();
        } catch (IOException ex) {
            Logger.getLogger(Igra.class.getName()).log(Level.SEVERE, null, ex);
            Platform.exit();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Igra.class.getName()).log(Level.SEVERE, null, ex);
            Platform.exit();
        }
        return null;
        }
    
    
    public List<Igra> gameIndexSearch(Set<Igra> gameInd, String naslov, String autor, 
    									   int ocjena, int god1, int god2)
    										throws FileNotFoundException, IOException {   
        List<Igra> gameIndex = new ArrayList<>(gameInd);
        Collections.sort(gameIndex , (Igra o1, Igra o2) -> o2.link.compareTo(o1.link));
        List<Igra> searchResult = new ArrayList<>();
        gameIndex.stream().filter((index) -> ((index.getNaslov().toLowerCase().contains(naslov.toLowerCase()))
        								  && (index.getAutor().toLowerCase().contains(autor.toLowerCase())) 
        								  && (index.getOcjena() >= ocjena) && (index.getGod() >= god1) 
        								  && (index.getGod() <= god2)))
                .forEachOrdered((i) -> {searchResult.add(i);});
            searchResultHTML = writeToHTML(searchResult);
            numberOfResults = searchResult.size();
            return searchResult;
        }
     
   
    public String writeToHTML(List<Igra> a) {
        StringBuilder html = new StringBuilder();
        html.append("<table>");
        html.append("<tr><th>datum</th><th>naslov</th><th>autor</th><th>ocjena</th></tr>");
        a.forEach((i) -> {
            html.append("<tr><td>").append(i.getGodina()).append("</td><td><a href=").append(i.link).append(">")
                    .append(i.getNaslov()).append("</a></td><td>").append(i.getAutor()).append("</td><td>")
                    .append(i.getOcjena()).append("</td></tr>");
        });
        html.append("</table>");
        return html.toString(); 
     }
    
}