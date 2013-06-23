import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import com.spacepocalypse.beermap2.util.security.SimplePasswordTools;


public class SimplePasswordToolsTest {
	@Test
	public void testHashPasswordAndSalt() {
		try {
			System.out.println(SimplePasswordTools.hashPassAndSalt("password", "vtqvdobut3ag6pd9cbnsi70bvs"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
