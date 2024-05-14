import com.badlogic.gdx.Gdx;
import com.skloch.game.HustleGame;
import com.skloch.game.Player;
import com.skloch.game.SoundManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import screens.GameScreen;
import screens.MenuScreen;

import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class AssetTests {
    @Test
    public void testPrimarySkinExists() {
        assertTrue("Primary skin file does not exist",
                Gdx.files.internal(HustleGame.PRIMARY_SKIN).exists());
    }

    @Test
    public void testSecondarySkinExists() {
        assertTrue("Primary skin file does not exist",
                Gdx.files.internal(HustleGame.SECONDARY_SKIN).exists());
    }

    @Test
    public void testBlackSquareExists() {
        assertTrue("Black square image does not exist",
                Gdx.files.internal(GameScreen.BLACK_SQUARE_PATH).exists());
    }

    @Test
    public void testEnergyBarExists() {
        assertTrue("Energy bar image does not exist",
                Gdx.files.internal(GameScreen.ENERGY_BAR_PATH).exists());
    }

    @Test
    public void testEnergyBarOutlineExists() {
        assertTrue("Energy bar outline image does not exist",
                Gdx.files.internal(GameScreen.ENERGY_BAR_OUTLINE_PATH).exists());
    }

    @Test
    public void testTitleImageExists() {
        assertTrue("Title image does not exist",
                Gdx.files.internal(MenuScreen.TITLE_IMAGE_PATH).exists());
    }

    @Test
    public void testWhiteSquareExists() {
        assertTrue("White square image does not exist",
                Gdx.files.internal(HustleGame.WHITE_SQUARE_PATH).exists());
    }

    @Test
    public void testCreditsExists() {
        assertTrue("Credits file does not exist",
                Gdx.files.internal(HustleGame.CREDITS_PATH).exists());
    }

    @Test
    public void testTutorialExists() {
        assertTrue("Tutorial file does not exist",
                Gdx.files.internal(HustleGame.TUTORIAL_PATH).exists());
    }

    @Test
    public void testPlayerSpriteAtlasExists() {
        assertTrue("Player sprite atlas does not exist",
                Gdx.files.internal(Player.SPRITE_ATLAS_PATH).exists());
    }

    // Sound assets
    @Test
    public void testOverworldMusicExists() {
        assertTrue("Overworld music file does not exist",
                Gdx.files.internal(SoundManager.OVERWORLD_MUSIC_PATH).exists());
    }

    @Test
    public void testMenuMusicExists() {
        assertTrue("Menu music file does not exist",
                Gdx.files.internal(SoundManager.MENU_MUSIC_PATH).exists());
    }

    @Test
    public void testFootstep1Exists() {
        assertTrue("Footstep1 sound file does not exist",
                Gdx.files.internal(SoundManager.FOOTSTEP1_PATH).exists());
    }

    @Test
    public void testFootstep2Exists() {
        assertTrue("Footstep2 sound file does not exist",
                Gdx.files.internal(SoundManager.FOOTSTEP2_PATH).exists());
    }

    @Test
    public void testPauseSoundExists() {
        assertTrue("Pause sound file does not exist",
                Gdx.files.internal(SoundManager.PAUSE_SOUND_PATH).exists());
    }

    @Test
    public void testDialogueOpenSoundExists() {
        assertTrue("Dialogue open sound file does not exist",
                Gdx.files.internal(SoundManager.DIALOGUE_OPEN_SOUND_PATH).exists());
    }

    @Test
    public void testDialogueOptionSoundExists() {
        assertTrue("Dialogue option sound file does not exist",
                Gdx.files.internal(SoundManager.DIALOGUE_OPTION_SOUND_PATH).exists());
    }

    @Test
    public void testButtonSoundExists() {
        assertTrue("Button sound file does not exist",
                Gdx.files.internal(SoundManager.BUTTON_SOUND_PATH).exists());
    }
}