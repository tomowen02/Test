import com.badlogic.gdx.Gdx;
import com.skloch.game.MapManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import screens.GameScreen;

import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class MapTests {
    @Test
    public void testDefaultMapExists() {
        assertTrue("Map file does not exist",
                Gdx.files.internal(GameScreen.MAP_PATH).exists());
    }

//    @Test
//    public void loadMap() {
//        MapManager mapManager = new MapManager();
//        mapManager.loadMap(GameScreen.MAP_PATH);
//    }
}
