
import java.util.Set;
import org.junit.Before;
import gamereview.GameReview;
import gamereview.Model;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class Test {

    GameReview igra = new GameReview("naslov", "autor", "90", "04.2018", "www.link.com", "PC");
    Model model = new Model();
    Set<GameReview> modelSet;

    @Before
    public void init() {
        modelSet = model.getGameIndex();
    }

    @org.junit.Test
    public void testIgra() {
        assertEquals("naslov", igra.getTitle());
        assertEquals("autor", igra.getAuthor());
        assertEquals("90", igra.getScore());
        assertEquals(2018, igra.getYear());
        assertEquals("www.link.com", igra.getLink());
        assertEquals("PC", igra.getPlatform());
    }

    @org.junit.Test
    public void testModel() {
        assertNotNull(model.getGameIndex());
    }

}
