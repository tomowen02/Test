package com.skloch.game;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.utils.Array;
import screens.GameScreen;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class that maps Object's event strings to actual Java functions.
 */
public class EventManager {
    private final GameScreen gameScreen;
    private final HashMap<String, Event> objectInteractions;
    private final Array<String> talkTopics;
    private final float FADE_DURATION = 0.5f;

    /**
     * A class that maps Object's event strings to actual Java functions.
     * To run a function call event(eventString), to add arguments add dashes.
     * E.g. a call to the Piazza function with an arg of 1 would be: "piazza-1"
     * Which the function interprets as "study at the piazza for 1 hour".
     * Object's event strings can be set in the Tiled map editor with a property called "event"
     *
     * @param gameScreen An instance of the GameScreen containing a player and dialogue box
     */
    public EventManager (GameScreen gameScreen) {
        this.gameScreen = gameScreen;

        // Define what to say when interacting with an object whose text won't change
        objectInteractions = new HashMap<String, Event>();
        objectInteractions.put("chest", new Event("chest", "Open the chest?", 0));
        objectInteractions.put("comp_sci", new Event("comp_sci", "Would you like to study?", 20));
        objectInteractions.put("chat", new Event("chat", "Would you like to chat to your friends?", 10));
        objectInteractions.put("basketball", new Event("basketball", "Would you like to play basketball?", 20));
        objectInteractions.put("eat", new Event("eat", "Would you like to eat?", 10));
        objectInteractions.put("accomodation", new Event("accomodation", "Go to sleep for the night?\nYour alarm is set for 8am.", 0));
        objectInteractions.put("rch", new Event("rch", "Would you like to study in the Ron Cook Hub?", 10)); // Changes, dynamically returned in getObjectInteraction
        objectInteractions.put("tree", new Event("tree", "Speak to the tree?", 5));
        objectInteractions.put("teleport", new Event("teleport", "Would you like to move location?", 0));
        objectInteractions.put("ducks", new Event("ducks", "Would you like to feed the ducks?", 10));
        objectInteractions.put("cook", new Event("cook", "Would you like to cook?", 30));

        // Some random topics that can be chatted about
        String[] topics = {"Dogs", "Cats", "Exams", "Celebrities", "Flatmates", "Video games", "Sports", "Food", "Fashion"};
        talkTopics = new Array<String>(topics);
        String[] meals = {"Pesto pasta", "Red pesto pasta", "Plain pasta", "Plain pesto", "Butter pasta", "Salt + Pepper pasta", "Pasta on toast", "Plain toast"};
    }

    public void event (String eventKey, String params) {
        String[] args;
        if (!params.isEmpty()) {
            args = params.split(";");
        } else {
            args = new String[0];
        }
        // Important functions, most likely called after displaying text
        switch (eventKey) {
            case "fadefromblack":
                fadeFromBlack();
                break;
            case "fadetoblack":
                fadeToBlack();
                break;
            case "gameover":
                gameScreen.GameOver();
                break;
        }

        // Events related to objects
        switch (eventKey) {
            case "tree":
                treeEvent();
                if (args.length >= 1 && objectInteractions.containsKey(args[0])) {
                    objectInteractions.get(args[0]).perform();
                }
                break;
            case "chest":
                chestEvent();
                if (objectInteractions.containsKey(args[0])) {
                    objectInteractions.get(args[0]).perform();
                }
                break;
            case "chat":
                chatEvent(args);
                break;
            case "eat":
                eatEvent(args);
                break;
            case "comp_sci":
                compSciEvent(args);
                break;
            case "rch":
                compSciEvent(args);
                break;
            case "accomodation":
                accomEvent(args);
                break;
            case "teleport":
                teleportEvent(args);
                break;
            case "basketball":
                basketballEvent(args);
                break;
            case "ducks":
                ducksEvent(args);
                break;
            case "cook":
                cookEvent(args);
                break;
            case "exit":
                // Should do nothing and just close the dialogue menu
                gameScreen.dialogueBox.hide();
                break;
            default:
                objectEvent(eventKey);
                break;

        }
    }

    public void advanceDay() {
        for (Event event : objectInteractions.values()) {
            event.dayAdvanced();
        }
    }

    /**
     * Gets the interaction text associated with each object via a key
     * @param key
     * @return The object interaction text
     */
    public String getObjectInteraction(String key) {
        if (key.contains("teleport")) {
            return "teleport";
        } else {
            return objectInteractions.get(key).getText();
        }
    }

    /**
     * @return True if the object has some custom text to display that isn't just "This is an x!"
     */
    public boolean hasCustomObjectInteraction(String key) {
        return objectInteractions.containsKey(key);
    }

    /**
     * Sets the text when talking to a tree
     */
    public void treeEvent() {
        gameScreen.dialogueBox.hideSelectBox();
        gameScreen.dialogueBox.setText("The tree doesn't say anything back.");
    }


    public void chestEvent() {
        gameScreen.dialogueBox.hideSelectBox();
        gameScreen.dialogueBox.setText("Wow! This chest is full of so many magical items! I wonder how they will help you out on your journey! Boy, this is an awfully long piece of text, I wonder if someone is testing something?\n...\n...\n...\nHow cool!");

    }

    /**
     * Sets the text when talking to an object without a dedicated function
     */
    public void objectEvent(String object) {
        gameScreen.dialogueBox.hideSelectBox();
        gameScreen.dialogueBox.setText("This is a " +  object + "!");
    }

    /**
     * Lets the player study at the piazza for x num of hours, decreases the player's energy and increments the
     * game time.
     *
     * @param args Arguments to be passed, should contain the hours the player wants to study. E.g. ["piazza", "1"]
     */
    public void chatEvent(String[] args) {
        if (gameScreen.getSeconds() > 8*60) {
            int energyCost = objectInteractions.get("chat").getEnergyCost();
            // If the player is too tired to meet friends
            if (gameScreen.getEnergy() < energyCost) {
                gameScreen.dialogueBox.setText("You are too tired to meet your friends right now!");

            } else if (args.length == 0) {
                // Ask the player to chat about something (makes no difference)
                String[] topics = randomTopics(3);
                gameScreen.dialogueBox.setText("What do you want to chat about?");
                String[] events = new String[] {"chat", "chat", "chat"};
                gameScreen.dialogueBox.getSelectBox().setOptions(topics, events, topics);
            } else {
                // Say that the player chatted about this topic for 1-3 hours
                // RNG factor adds a slight difficulty (may consume too much energy to study)
                int hours = ThreadLocalRandom.current().nextInt(1, 4);
                gameScreen.dialogueBox.setText(String.format("You talked about %s for %d hours!", args[0].toLowerCase(), hours));
                gameScreen.decreaseEnergy(energyCost * hours);
                gameScreen.passTime(hours * 60); // in seconds
                gameScreen.addRecreationalHours(hours);
                if (objectInteractions.containsKey(args[0])) {
                    objectInteractions.get(args[0]).perform();
                }
            }
        } else {
            gameScreen.dialogueBox.setText("It's too early in the morning to meet your friends, go to bed!");
        }
    }

    /**
     * @param amount The amount of topics to return
     * @return An array of x random topics the player can chat about
     */
    private String[] randomTopics(int amount) {
        // Returns an array of 3 random topics
        Array<String> topics = new Array<>(amount);

        for (int i = 0;i<amount;i++) {
            String choice = talkTopics.random();
            // If statement to ensure topic hasn't already been selected
            if (!topics.contains(choice, false)) {
                topics.add(choice);
            } else {
                i -= 1;
            }
        }

        return topics.toArray(String.class);
    }

    /**
     * The event to be run when interacting with the computer science building
     * Gives the player the option to study for 2, 3 or 4 hours
     * @param args
     */
    public void compSciEvent(String[] args) {
        if (gameScreen.getSeconds() > 8*60) {
            int energyCost = objectInteractions.get("comp_sci").getEnergyCost();
            // If the player is too tired for any studying:
            if (gameScreen.getEnergy() < energyCost) {
                gameScreen.dialogueBox.hideSelectBox();
                gameScreen.dialogueBox.setText("You are too tired to study right now!");
            } else if (args.length == 0) {
                // If the player has not yet chosen how many hours, ask
                gameScreen.dialogueBox.setText("Study for how long?");
                String[] options = new String[] {"2 Hours (20)", "3 Hours (30)", "4 Hours (40)"};
                String[] events = new String[] {"comp_sci", "comp_sci", "comp_sci"};
                String[] params = new String[] {"2", "3", "4"};
                gameScreen.dialogueBox.getSelectBox().setOptions(options, events, params);
            } else {
                int hours = Integer.parseInt(args[0]);
                // If the player does not have enough energy for the selected hours
                if (gameScreen.getEnergy() < hours*energyCost) {
                    gameScreen.dialogueBox.setText("You don't have the energy to study for this long!");
                } else {
                    // If they do have the energy to study
                    gameScreen.dialogueBox.setText(String.format("You studied for %s hours!\nYou lost %d energy", args[0], hours*energyCost));
                    gameScreen.decreaseEnergy(energyCost * hours);
                    gameScreen.addStudyHours(hours);
                    gameScreen.passTime(hours * 60); // in seconds
                    if (objectInteractions.containsKey(args[0])) {
                        objectInteractions.get(args[0]).perform();
                    }
                }
            }
        } else {
            gameScreen.dialogueBox.setText("It's too early in the morning to study, go to bed!");
        }
    }


    /**
     * The event to be run when the player interacts with the ron cooke hub
     * Gives the player the choice to eat breakfast, lunch or dinner depending on the time of day
     * @param args
     */
    public void eatEvent(String[] args) {
        String eventKey = "eat";
        if (args.length >= 1) {
            eventKey = args[0];
        }
        if (gameScreen.getSeconds() > 8*60) {
            int energyCost = objectInteractions.get(eventKey).getEnergyCost();
            if (gameScreen.getEnergy() < energyCost) {
                gameScreen.dialogueBox.setText("You are too tired to eat right now!");
            } else {
                gameScreen.dialogueBox.setText(String.format("You took an hour to eat %s!\nYou lost %d energy!", gameScreen.getMeal(), energyCost));
                gameScreen.decreaseEnergy(energyCost);
                gameScreen.passTime(60); // in seconds
                if (objectInteractions.containsKey(eventKey)) {
                    objectInteractions.get(eventKey).perform();
                }
            }
        } else {
            gameScreen.dialogueBox.setText("It's too early in the morning to eat food, go to bed!");
        }

    }

    /**
     * Lets the player go to sleep, fades the screen to black then shows a dialogue about the amount of sleep
     * the player gets
     * Then queues up fadeFromBlack to be called when this dialogue closes
     * @see GameScreen fadeToBlack function
     * @param args Unused currently
     */
    public void accomEvent(String[] args) {
        gameScreen.setSleeping(true);
        gameScreen.dialogueBox.hide();

        // Calculate the hours slept to the nearest hour
        // Wakes the player up at 8am
        float secondsSlept;
        if (gameScreen.getSeconds() < 60*8) {
            secondsSlept = (60*8 - gameScreen.getSeconds());
        } else {
            // Account for the wakeup time being in the next day
            secondsSlept = (((60*8) + 1440) - gameScreen.getSeconds());
        }
        int hoursSlept = Math.round(secondsSlept / 60f);

        RunnableAction setTextAction = new RunnableAction();
        setTextAction.setRunnable(() -> {
            if (gameScreen.getSleeping()) {
                gameScreen.dialogueBox.show();
                gameScreen.dialogueBox.setText(String.format("You slept for %d hours!\nYou recovered %d energy!", hoursSlept, Math.min(100, hoursSlept*13)), "fadefromblack");
                // Restore energy and pass time
                gameScreen.setEnergy(hoursSlept*13);
                gameScreen.passTime(secondsSlept);
                gameScreen.addSleptHours(hoursSlept);
            }
        });

        fadeToBlack(setTextAction);
    }

    public void teleportEvent(String[] args) {
        try {
//            gameScreen.dialogueBox.hide();
            String mapPath = args[0];
            if (args.length == 1) {
                gameScreen.mapManager.loadMap(mapPath);
            } else {
                String spawn = args[1]; // in the form "x,y"
                String[] spawnCoords = spawn.split(",");
                float x = Float.parseFloat(spawnCoords[0]);
                float y = Float.parseFloat(spawnCoords[1]);
                gameScreen.mapManager.loadMap(mapPath);
                gameScreen.player.setPos(x, y);
            }
            gameScreen.dialogueBox.hide();
        } catch (Exception e) {
            gameScreen.dialogueBox.setText("Teleport failed!");
        }

    }

    public void ducksEvent(String[] args) {
        if (gameScreen.getSeconds() > 8*60) {
            int energyCost = objectInteractions.get("ducks").getEnergyCost();
            if (gameScreen.getEnergy() < energyCost) {
                gameScreen.dialogueBox.setText("You are too tired to feed the ducks right now!");
            } else {
                gameScreen.dialogueBox.setText("You fed the ducks for an hour!\nYou lost "+energyCost+" energy!");
                gameScreen.decreaseEnergy(energyCost);
                gameScreen.passTime(60);
                gameScreen.addRecreationalHours(1);
            }
        } else {
            gameScreen.dialogueBox.setText("It's too early in the morning to feed the ducks, the ducks are asleep!");
        }
    }

    public void basketballEvent(String[] args) {
        if (gameScreen.getSeconds() > 8*60) {
            int energyCost = objectInteractions.get("basketball").getEnergyCost();
            if (gameScreen.getEnergy() < energyCost) {
                gameScreen.dialogueBox.setText("You are too tired to play basketball right now!");
            } else {
                gameScreen.dialogueBox.setText("You played for an hour!\nYou lost "+energyCost+" energy!");
                gameScreen.decreaseEnergy(energyCost);
                gameScreen.passTime(60);
                gameScreen.addRecreationalHours(1);
            }
        } else {
            gameScreen.dialogueBox.setText("It's too early in the morning to play basketball!");
        }
    }

    public void cookEvent(String[] args) {
        if (gameScreen.getSeconds() > 8*60) {
            int energyCost = objectInteractions.get("cook").getEnergyCost();
            if (gameScreen.getEnergy() < energyCost) {
                gameScreen.dialogueBox.setText("You are too tired to cook right now. You might burn the house down!");
            } else {
                gameScreen.dialogueBox.setText(String.format("You took an hour to cook %s.\nYou lost %d energy!", gameScreen.getMeal(), energyCost));
                gameScreen.decreaseEnergy(energyCost);
                gameScreen.passTime(60); // in seconds
                if (args.length >= 1 && objectInteractions.containsKey(args[0])) {
                    objectInteractions.get(args[0]).perform();
                }
            }
        } else {
            gameScreen.dialogueBox.setText("It's too early in the morning to cook a meal.");
        }
    }

    /**
     * Fades the screen to black
     */
    public void fadeToBlack() {
        gameScreen.blackScreen.addAction(Actions.fadeIn(FADE_DURATION));
    }

    /**
     * Fades the screen to black, then runs a runnable after it is done
     * @param runnable A runnable to execute after fading is finished
     */
    public void fadeToBlack(RunnableAction runnable) {
        gameScreen.blackScreen.addAction(Actions.sequence(Actions.fadeIn(FADE_DURATION), runnable));
    }

    /**
     * Fades the screen back in from black, displays a good morning message if the player was sleeping
     */
    public void fadeFromBlack() {
        // If the player is sleeping, queue up a message to be sent
        if (gameScreen.getSleeping()) {
            RunnableAction setTextAction = new RunnableAction();
            setTextAction.setRunnable(() -> {
                if (gameScreen.getSleeping()) {
                    gameScreen.dialogueBox.show();
                    // Show a text displaying how many days they have left in the game
                    gameScreen.dialogueBox.setText(gameScreen.getWakeUpMessage());
                    gameScreen.setSleeping(false);
                }
            });

            // Queue up events
            gameScreen.blackScreen.addAction(Actions.sequence(Actions.fadeOut(FADE_DURATION), setTextAction));
        } else {
            gameScreen.blackScreen.addAction(Actions.fadeOut(FADE_DURATION));
        }
    }
}
