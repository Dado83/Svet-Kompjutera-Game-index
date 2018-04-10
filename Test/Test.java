import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.Set;

import org.junit.Before;

import SK.Igra;
import SK.Model;

class Test {

	Igra igra = new Igra("naslov", "autor", 90, "2018", "www.link.com");
	Model model = new Model();
	Set<Igra> modelSet;
	
	@Before
	private void init() {
		modelSet = model.getGameIndex();
	}
	
	@org.junit.jupiter.api.Test
	void testIgra() {
		assertEquals("naslov", igra.getNaslov());
		assertEquals("autor", igra.getAutor());
		assertEquals(90, igra.getOcjena());
		assertEquals("2018", igra.getGodina());
	}
	
	@org.junit.jupiter.api.Test
	void testModel() {
		assertNotNull(model.getGameIndex());
	}

}
