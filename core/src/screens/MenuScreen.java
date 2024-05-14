package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.skloch.game.HustleGame;

/**
 * A screen to display the game menu to the player has the buttons "Start", "Settings", "Credits, "Exit"
 * Also displays a tutorial window and an avatar select screen
 */
public class MenuScreen implements Screen {
    private final HustleGame game;
    private final Stage menuStage;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Image titleImage;
    private final LeaderboardWindow leaderboardWindow;

    public static String TITLE_IMAGE_PATH = "title.png";

    /**
     * A class to display a menu screen, initially gives the player 4 options, Start, Settings, Credits, Quit
     * Upon hitting start, a tutorial window is shown, and then an avatar select screen is shown, and then it is
     * switched to GameScreen.
     * Settings switches to SettingsScreen
     * Credits switches to CreditsScreen
     * Quit exits the game
     *
     * @param game An instance of HustleGame with loaded variables
     */
    public MenuScreen(final HustleGame game) {
        this.game = game;
        this.game.menuScreen = this;
        // Create stage to draw UI on
        menuStage = new Stage(new FitViewport(game.WIDTH, game.HEIGHT));
        Gdx.input.setInputProcessor(menuStage);

        camera = new OrthographicCamera();
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, camera);
        camera.setToOrtho(false, game.WIDTH, game.HEIGHT);

        leaderboardWindow = new LeaderboardWindow(game.leaderboard, menuStage, game.skin, viewport);

        // Set the size of the background to the viewport size, only need to do this once, this is then used by all
        // screens as an easy way of having a blue background
        game.blueBackground.getRoot().findActor("blue image").setSize(viewport.getWorldWidth(), viewport.getWorldHeight());

        // Title image
        titleImage = new Image(new Texture(Gdx.files.internal(TITLE_IMAGE_PATH)));
        titleImage.setPosition((viewport.getWorldWidth() / 2f) - (titleImage.getWidth() / 2f), 500);
        menuStage.addActor(titleImage);

        // Play menu music
        game.soundManager.playMenuMusic();

        // Make avatar select table
        Table avatarSelectTable = makeAvatarSelectTable();
        menuStage.addActor(avatarSelectTable);
        avatarSelectTable.setVisible(false);

        // Make name select table
        Table nameSelectTable = makeNameSelectTable(avatarSelectTable);
        menuStage.addActor(nameSelectTable);
        nameSelectTable.setVisible(false);

        // Make tutorial window
        Window tutorialWindow = makeTutorialWindow(nameSelectTable);
        menuStage.addActor(tutorialWindow);
        tutorialWindow.setVisible(false);

        // Make table to draw buttons and title
        Table buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.setWidth(600);
        menuStage.addActor(buttonTable);


        // Create the buttons
        TextButton startButton = new TextButton("New Game", game.skin);
        TextButton leaderboardButton = new TextButton("Leaderboard", game.skin);
        TextButton settingsButton = new TextButton("Settings", game.skin);
        TextButton creditsButton = new TextButton("Credits", game.skin);
        TextButton exitButton = new TextButton("Exit", game.skin);

        // Set button widths
        int buttonWidth = 340;
        settingsButton.setWidth(buttonWidth);
        creditsButton.setWidth(buttonWidth);

        // Add everything to the table using row() to go to a new line
        buttonTable.add(startButton).padBottom(10).padTop(280).width(buttonWidth);
        buttonTable.row();
        buttonTable.add(leaderboardButton).padBottom(10).width(buttonWidth);
        buttonTable.row();
        // Adding settings and credits button on the same row
        Table horizontalTable = new Table();
        horizontalTable.add(settingsButton).width(buttonWidth).padRight(10);
        horizontalTable.add(creditsButton).width(buttonWidth);
        buttonTable.add(horizontalTable).center().padBottom(10);
        buttonTable.row();
        // Exit
        buttonTable.add(exitButton).center().width(buttonWidth);
        buttonTable.top();

        // Add listeners to the buttons so they do things when pressed

        // START GAME BUTTON - Displays the tutorial window
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.soundManager.playButton();
                buttonTable.setVisible(false);
                titleImage.setVisible(false);
                tutorialWindow.setVisible(true);
            }
        }
        );

        // LEARDBOARD BUTTON - Displays the leaderboard
        leaderboardButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.soundManager.playButton();
                    leaderboardWindow.show();
                }
            }
        );

        // SETTINGS BUTTON
        Screen thisScreen = this;
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.soundManager.playButton();
                game.setScreen(new SettingsScreen(game, thisScreen));
            }
        });

        // CREDITS BUTTON
        creditsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.soundManager.playButton();
                game.setScreen(new CreditScreen(game, thisScreen));
            }
        });

        // EXIT BUTTON
        exitButton.addListener(new ChangeListener() {
               @Override
               public void changed(ChangeEvent event, Actor actor) {
                   game.soundManager.playButton();
                   game.dispose();
                   dispose();
                   Gdx.app.exit();
               }
           }
        );

        game.batch.setProjectionMatrix(camera.combined);

    }

    /**
     * Renders the main menu, and any windows that are displaying information
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.blueBackground.draw();

        // Make the stage follow actions and draw itself
        menuStage.setViewport(viewport);
        menuStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        menuStage.draw();

    }

    /**
     * Correctly resizes the menu screen
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        menuStage.getViewport().update(width, height);
    }

    // Other required methods
    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    /**
     * Correctly sizes the game when resuming it after a pause or switching screens
     * Fixes a small graphical bug
     */
    public void resume() {
        Gdx.input.setInputProcessor(menuStage);

        // See the comment in the resume() function in GameScreen to see why this pointless line exists
        Gdx.input.setCursorPosition(Gdx.input.getX(), Gdx.input.getY());
    }

    /**
     * Dispose of all menu assets
     */
    @Override
    public void dispose() {
        menuStage.dispose();
    }

    /**
     * Generates a window to teach the player how to play the game
     * Displays the tutorial text shown in Text/tutorial_text.txt
     *
     * @return A small window to explain the game
     */
    public Window makeTutorialWindow(Table nextTable) {
        Window tutWindow = new Window("", game.skin);
        Table tutTable = new Table();
        tutWindow.add(tutTable).prefHeight(600).prefWidth(800-20);

        // Title
        Label title = new Label("How to play", game.skin, "button");
        tutTable.add(title).padTop(10);
        tutTable.row();

        // Table for things inside the scrollable widget
        Table scrollTable = new Table();

        // Scrollable widget
        ScrollPane scrollWindow = new ScrollPane(scrollTable, game.skin);
        scrollWindow.setFadeScrollBars(false);

        tutTable.add(scrollWindow).padTop(20).height(350).width(870);
        tutTable.row();

        Label text = new Label(game.tutorialText, game.skin, "interaction");
        text.setWrap(true);
        scrollTable.add(text).width(820f).padLeft(20);

        // Exit button
        TextButton continueButton = new TextButton("Continue", game.skin);
        tutTable.add(continueButton).bottom().width(300).padTop(10);

        tutWindow.pack();

        tutWindow.setSize(900, 600);

        // Centre the window
        tutWindow.setX((viewport.getWorldWidth() / 2) - (tutWindow.getWidth() / 2));
        tutWindow.setY((viewport.getWorldHeight() / 2) - (tutWindow.getHeight() / 2));



        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.soundManager.playButton();
                tutWindow.setVisible(false);
                nextTable.setVisible(true);
            }
        });

        return tutWindow;
    }

    /**
     * Creates a name selection screen
     *
     * @return A table containing UI elements
     */
    public Table makeNameSelectTable (Table nextTable) {
        Table table = new Table();
        table.setFillParent(true);
        table.top();

        // Prompt
        Label title = new Label("Enter your name", game.skin, "button");
        table.add(title).padBottom(120).padTop(80);
        table.row();

        // Name input
        TextField nameInput = new TextField("", game.secondarySkin);
        table.add(nameInput).width(300).padBottom(20);
        table.row();

        // Exit button
        TextButton continueButton = new TextButton("Continue", game.skin);
        table.add(continueButton).bottom().width(300).padTop(10);

        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String name = nameInput.getText();
                if (name.equals("")) {
                    return;
                }
                game.playerName = name;
                game.soundManager.playButton();
                table.setVisible(false);
                nextTable.setVisible(true);
            }
        });

        return table;
    }


    /**
     * Creates an avatar selection screen, consisting of a label and two buttons
     *
     * @return A table containing UI elements
     */
    public Table makeAvatarSelectTable () {
        Table table = new Table();
        table.setFillParent(true);
        table.top();

        // Prompt
        Label title = new Label("Select your avatar", game.skin, "button");
        table.add(title).padBottom(120).padTop(80);
        table.row();

        // Image buttons
        Table buttonTable = new Table();
        table.add(buttonTable).width(600);

        ImageButton choice1 = new ImageButton(game.skin, "avatar1");
        buttonTable.add(choice1).left().expandX();
        ImageButton choice2 = new ImageButton(game.skin, "avatar2");
        buttonTable.add(choice2).right().expandX();

        choice1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.soundManager.playButton();
                GameScreen gameScreen = new GameScreen(game, 1);
                game.setScreen(gameScreen);
                game.soundManager.stopMenuMusic();
                dispose();
            }
        });

        choice2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.soundManager.playButton();
                game.setScreen(new GameScreen(game, 2));
                game.soundManager.stopMenuMusic();
                dispose();
            }
        });

        return table;
    }

}
