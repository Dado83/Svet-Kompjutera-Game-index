
import java.util.Set;
import org.junit.Before;
import SK.GameReview;
import SK.Model;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


class Test {

    GameReview igra = new GameReview("naslov", "autor", 90, "2018", "www.link.com");
    Model model = new Model();
    Set<GameReview> modelSet;

    @Before
    private void init() {
        modelSet = model.getGameIndex();
    }

    @org.junit.Test
    void testIgra() {
        assertEquals("naslov", igra.getTitle());
        assertEquals("autor", igra.getAuthor());
        assertEquals(90, igra.getScore());
        assertEquals("2018", igra.getYear());
    }

    @org.junit.Test
    void testModel() {
        assertNotNull(model.getGameIndex());
    }

}
