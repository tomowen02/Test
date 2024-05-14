package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

/**
 * A class handling loading, playing and disposing of sounds.
 */
public class SoundManager implements Disposable {
    public Music overworldMusic;
    public Music menuMusic;
    private final Sound footstep1;
    private final Sound footstep2;
    public boolean footstepBool;
    private float footstepTimer;
    private float sfxVolume = 0.8f;
    private float musicVolume = 0.8f;
    private final Sound pauseSound;
    private final Sound dialogueOpenSound;
    private final Sound dialogueOptionSound;
    private final Sound buttonSound;

    public static String OVERWORLD_MUSIC_PATH = "Music/OverworldMusic.mp3";
    public static String MENU_MUSIC_PATH = "Music/Streetlights.ogg";
    public static String FOOTSTEP1_PATH = "Sounds/footstep1 grass.ogg";
    public static String FOOTSTEP2_PATH = "Sounds/footstep2 grass.ogg";
    public static String PAUSE_SOUND_PATH = "Sounds/Pause01.wav";
    public static String DIALOGUE_OPEN_SOUND_PATH = "Sounds/DialogueOpen.wav";
    public static String DIALOGUE_OPTION_SOUND_PATH = "Sounds/DialogueOption.wav";
    public static String BUTTON_SOUND_PATH = "Sounds/Button.wav";

    /**
     * A class to handle playing sounds in the game, handles loading and playing of music and sounds
     * so a GameScreen can just call "play overworld music" without needing to know the track title.
     * Also handles disposing sounds and music
     */
    public SoundManager () {
        // Load music
        overworldMusic = Gdx.audio.newMusic(Gdx.files.internal(OVERWORLD_MUSIC_PATH));
        overworldMusic.setLooping(true);
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal(MENU_MUSIC_PATH));
        menuMusic.setLooping(true);

        // Load SFX
        footstep1 = Gdx.audio.newSound(Gdx.files.internal(FOOTSTEP1_PATH));
        footstep2 = Gdx.audio.newSound(Gdx.files.internal(FOOTSTEP2_PATH));
        pauseSound = Gdx.audio.newSound(Gdx.files.internal(PAUSE_SOUND_PATH));
        dialogueOpenSound = Gdx.audio.newSound(Gdx.files.internal(DIALOGUE_OPEN_SOUND_PATH));
        dialogueOptionSound = Gdx.audio.newSound(Gdx.files.internal(DIALOGUE_OPTION_SOUND_PATH));
        buttonSound = Gdx.audio.newSound(Gdx.files.internal(BUTTON_SOUND_PATH));
    }

    /**
     * Sets the volume of the music
     * @param volume
     */
    public void setMusicVolume (float volume) {
        this.musicVolume = volume;
        overworldMusic.setVolume(musicVolume);
        menuMusic.setVolume(musicVolume);
    }

    /**
     * Sets the sound effect volume
     * @param volume
     */
    public void setSfxVolume (float volume) {
        this.sfxVolume = volume;
    }

    /**
     * A sound for when the pause menu appears
     */
    public void playPauseSound() {pauseSound.play(sfxVolume);}

    /**
     * A sound for when the dialogue box appears
     */
    public void playDialogueOpen() {dialogueOpenSound.play(sfxVolume);}

    /**
     * A sound for when the arrow in the selectBox is moved
     */
    public void playDialogueOption() {dialogueOptionSound.play(sfxVolume);}

    /**
     * A sound for when a button is pressed
     */
    public void playButton() {buttonSound.play(sfxVolume);}

    /**
     * Plays the music for the overworld (main game)
     */
    public void playOverworldMusic() {overworldMusic.play();}

    /**
     * Stops the music for the overworld
     */
    public void stopOverworldMusic() {overworldMusic.stop();}

    /**
     * Plays the music for the menu
     */
    public void playMenuMusic() {menuMusic.play();}

    /**
     * Stops the music for the menu
     */
    public void stopMenuMusic() {menuMusic.stop();}

    /**
     * Pauses the overworld music, so it can be resumed from this point later
     */
    public void pauseOverworldMusic() {overworldMusic.pause();}

    /**
     * @return The current music volume
     */
    public float getMusicVolume() {return musicVolume;}

    /**
     * @return The current sound effect volume
     */
    public float getSfxVolume() {return sfxVolume;}

    /**
     * Updates the timers for sounds that repeat regularly, needs to be called every render cycle.
     * Specifically handles triggering footsteps when the player is moving
     * @param delta Time passed since the last render
     */
    public void processTimers(float delta) {
        // Decrements timers for any recurring sounds, like footsteps
        // Events that make these sounds can then check that their specific timer is zero and play a noise
        footstepTimer -= delta;
        if (footstepTimer < 0) {
            footstepTimer = 0;
        }
    }

    /**
     * Plays an alternating footstep sound when the player is moving and the footstepTimer variable has hit zero.
     * Uses two different SFX to sound more realistic and to allow the timing to be configured
     */
    public void playFootstep() {
        // If it is time to play a footstep, play one
        if (footstepTimer <= 0) {
            footstepTimer = 0.5f; // Delay between each footstep sound, increase to have slower steps
            if (!footstepBool) {
                footstep1.play(sfxVolume);
                footstepBool = true;
            } else {
                footstep2.play(sfxVolume);
                footstepBool = false;
            }
        }
    }


    /**
     * Disposes of all music and sound effects
     */
    @Override
    public void dispose () {
        overworldMusic.dispose();
        menuMusic.dispose();
        footstep1.dispose();
        footstep2.dispose();
        pauseSound.dispose();
        dialogueOpenSound.dispose();
        dialogueOptionSound.dispose();
    }
}
