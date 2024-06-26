package com.skloch.game;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
/**
 * A class to display a dialogue box for text and options on the screen.
 *
 */
public class DialogueBox {
    private final Window dialogueWindow;
    private final Label textLabel;
    private final Skin skin;
    private final int MAXCHARS;
    private final SelectBox selectBox;
    private Array<String> textLines;
    private int linePointer = 0;
    private String eventKey = null;
    private String eventParams = null;
    private float textCounter = 0;
    private boolean scrollingText = false;



    public DialogueBox (Skin skin) {
        // Define some key values
        int WIDTH = 800;
        int HEIGHT = 200;
        MAXCHARS = 35;
        this.skin = skin;

        // Create the window for the dialogue box
        dialogueWindow = new Window("", skin);

        // Create the table for the text in the dialogue box
        Table dialogueTable = new Table();
        dialogueWindow.addActor(dialogueTable);
        dialogueTable.setFillParent(true);

        textLabel = new Label("Are you sure you want to sleep at the Piazza? This will cost you 10 energy", skin, "dialogue");
        dialogueTable.add(textLabel).expand().width(WIDTH - 80).top().padTop(40);
        textLabel.setWrap(false);


        dialogueWindow.setWidth(WIDTH);
        dialogueWindow.setHeight(HEIGHT);

        // Create selection box to allow user to make choices when interacting with objects (class defined below)
        this.selectBox = new SelectBox();
        selectBox.setOptions(new String[]{"Yes", "No"}, new String[]{"piazza", "close"}); //!TEMP

        setText("Are you sure you want to sleep at the Piazza? This will cost you 10 energy");

    }

    /**
     * A class displaying a little selection box to the user when an input is needed in dialog
     */
    public class SelectBox {
        private final Window selectWindow;
        private final Table selectTable;
        private int choiceIndex = 0;
        private String[] options;
        private String[] events;
        private String[] eventParams;
        private final Array<Label> optionPointers = new Array<>();
        public SelectBox () {
            selectWindow = new Window("", skin);
            selectTable = new Table();
            selectWindow.add(selectTable);



            selectWindow.setPosition(
                    dialogueWindow.getX() + dialogueWindow.getWidth() - selectWindow.getWidth(),
                    dialogueWindow.getY() + dialogueWindow.getHeight()-24
            );



        }

        /**
         * Sets the options visible to the player when asking for a choice.
         * Also sets which events to call from each option.
         * Event strings are translated into events in EventManager
         * @see EventManager
         *
         * @param options The options available to the player e.g. "Yes" and "No"
         * @param events The events called to the option of the same index E.g. "piazza" and "closeDialogue"
         */
        public void setOptions (String[] options, String[] events, String[] eventParams) {
            selectTable.clearChildren();

            this.options = options;
            this.events = events;
            this.eventParams = eventParams;
            optionPointers.clear();

            for (String option : options) {
                // Add each pointer to an array so it can be shown/hidden later without searching the table
                Label pointer = new Label(">", skin, "dialogue");
                optionPointers.add(pointer);
                selectTable.add(pointer).padRight(10).padLeft(10);
                pointer.setVisible(false);

                selectTable.add(new Label(option, skin, "dialogue")).left().padRight(10);
                selectTable.row();
            }

            selectTable.pack();
            selectWindow.setWidth(selectTable.getWidth()+70);
            selectWindow.setHeight(selectTable.getHeight()+70);

            // selectWindow.add(selectTable);

            // Recenter
            selectWindow.setPosition(
                    dialogueWindow.getX() + dialogueWindow.getWidth() - selectWindow.getWidth(),
                    dialogueWindow.getY() + dialogueWindow.getHeight()-24
            );

            // Show first pointer
            setChoice(0);
            show();
        }

        public void setOptions(String[] options, String[] events) {
            String[] params = new String[events.length];
            for (int i = 0; i < events.length; i++) {
                params[i] = "";
            }
            setOptions(options, events, params);
        }

        /**
         * Moves the player's choice up one selection
         * Also hides the pointer at the old index, and shows the pointer at the new index
         */
        public void choiceUp () {
            optionPointers.get(choiceIndex).setVisible(false);
            choiceIndex -= 1;
            // If statement to prevent the user from choosing outside the options range
            if (choiceIndex < 0) {
                choiceIndex = 0;
            }
            optionPointers.get(choiceIndex).setVisible(true);

        }

        /**
         * The same as choiceUp but in the opposite direction
         */
        public void choiceDown () {
            optionPointers.get(choiceIndex).setVisible(false);
            choiceIndex += 1;
            // If statement to prevent the user from choosing outside the options range
            if (choiceIndex >= options.length) {
                choiceIndex = options.length - 1;
            }
            optionPointers.get(choiceIndex).setVisible(true);
        }

        /**
         * Returns the event string associated with the selected choice
         * Call hide() afterwards to close the menu
         *
         * @return An event string to be passed to EventManager
         */

        public String getChoice () {
            return events[choiceIndex];
        }

        public String getParams() {
            return eventParams[choiceIndex];
        }

        /**
         * Gets the window of the select box
         *
         * @return The window of the select box
         */
        public Window getWindow() {
            return selectWindow;
        }

        /**
         * Hides the selection widget
         */
        public void hide() {
            selectWindow.setVisible(false);
        }

        /**
         * Shows the selection widget
         */
        public void show() {
            selectWindow.setVisible(true);
        }

        /**
         * Returns whether the selection box is visible or not
         *
         * @return true if the selection box is visible
         */
        public boolean isVisible() {
            return selectWindow.isVisible();
        }

        /**
         * Sets the player's choice to a specific value, used to default to "No" for most options
         *
         * @param index The new choice index
         */
        public void setChoice(int index) {
            if (choiceIndex < options.length) {
                // Don't try and set option 4 to invisible if we only have 2 options
                optionPointers.get(choiceIndex).setVisible(false);
            }
            choiceIndex = index;
            optionPointers.get(choiceIndex).setVisible(true);
        }
    }


    /**
     * Sets the dialogue box and all its elements to a position onscreen
     *
     * @param x The x coordinate of the bottom left corner
     * @param y The y coordinate
     */
    public void setPos(float x, float y) {
        dialogueWindow.setPosition(x, y);

        selectBox.selectWindow.setPosition(
                x + dialogueWindow.getWidth() - selectBox.selectWindow.getWidth(),
                y + dialogueWindow.getHeight()-24
        );
    }

    /**
     * Sets the text to be displayed on the dialogue box, automatically wraps it correctly
     * @param text Text to be displayed.
     */
    public void setText(String text) {
        initialiseLabelText(text);
        scrollingText = true;
        textCounter = 0;
    }

    public void setText(String text, String eventKey) {
        initialiseLabelText(text);
        this.eventKey = eventKey;
        this.eventParams = "";
        scrollingText = true;
        textCounter = 0;

    }

    /**
     * Sets the text to be displayed on the dialogue box, automatically wraps it correctly
     * Additionally, schedules an event to be called after the text is done displaying
     * @param text The text to display
     * @param eventKey The event key to be triggered
     */
    public void setText(String text, String eventKey, String eventParams) {
        initialiseLabelText(text);
        this.eventKey = eventKey;
        this.eventParams = eventParams;
        scrollingText = true;
        textCounter = 0;

    }

    public void scrollText(float speed) {
        if (scrollingText) {
            textCounter += speed;
            if (Math.round(textCounter) >= textLines.get(linePointer).length()) {
                scrollingText = false;
                textLabel.setText(textLines.get(linePointer));
            }
            textLabel.setText(textLines.get(linePointer).substring(0, Math.round(textCounter)));
        }
    }

    /**
     * Formats the text to be displayed on a label widget. Adds a newline character every MAXCHARS num of characters
     * accounts for any occurring linebreaks to take use of the size of the most space possible.
     * Stores the formatted text in 3 chunks, which are then queued up to be pushed to the label whenever the user
     * presses e.
     *
     * @param text The text to format and push to the label
     */
    public void initialiseLabelText(String text) {
        // Add a newline every 36 chars
        StringBuilder newString = new StringBuilder();
        int lastSpace = 0;
        int index = 0;
        int totalIndex = 0;

        // Add newline characters where the length of a section between two linebreaks is greater than MAXCHARS
        for (char c : text.toCharArray()) {
            // Account for any occurring linebreaks
            if (c == '\n') {
                index = 0;
            }

            if (index >= MAXCHARS) {
                // If the current line is a space, just add a newline instead of a space
                if (c == ' ') {
                    newString.append("\n");
                    totalIndex += 1;
                    index = 0;
                } else {
                    // If not, Replace the last space with a linebreak and add the char
                    // If the last linebreak is 0 or greater than MAXCHARS away, just add a break now
                    if (lastSpace == 0 || (totalIndex - lastSpace) >= MAXCHARS) {
                        newString.append("\n");
                        index = 0;
                    } else {
                        newString = new StringBuilder(newString.substring(0, lastSpace) + "\n" + newString.substring(lastSpace + 1));
                        newString.append(c);
                        index = totalIndex - lastSpace;
                        totalIndex += 1;
                    }
                }
            } else {
                newString.append(c);
                if (c == ' ') {
                    lastSpace = totalIndex;
                }

                index += 1;
                totalIndex += 1;
            }
        }

        // Split the newString into chunks with 3 linebreaks
        textLines = new Array<>();
        int numBreaks = 0;
        StringBuilder subString = new StringBuilder();

        for (String s: newString.toString().split("\n")) {
            if (numBreaks == 2) {
                subString.append(s);
                textLines.add(subString.toString());
                subString = new StringBuilder();
                numBreaks = 0;
            } else {
                subString.append(s).append("\n");
                numBreaks += 1;
            }
        }
        if (!subString.toString().equals("")) {
            textLines.add(subString.toString());
        }

        textLabel.setText(textLines.get(0));
        linePointer = 0;
    }

    /**
     * Makes the dialogue box visible, along with any elements that need to be shown
     */
    public void show() {
        dialogueWindow.setVisible(true);
    }

    /**
     * Hides the dialogue box and all of its elements
     */
    public void hide() {
        dialogueWindow.setVisible(false);
        selectBox.hide();
    }

    /**
     * Pressing 'confirm' on the dialogue box
     * Either selects the choice if the selectbox is open, or advances text if not
     */
    public void enter(EventManager eventManager) {
        if (selectBox.isVisible()) {
            selectBox.hide();
            eventManager.event(selectBox.getChoice(), selectBox.getParams());
        } else {
            advanceText(eventManager);
        }
    }

    /**
     * Continues on to the next bit of text, or closes the window if the end is reached
     */
    private void advanceText(EventManager eventManager) {
        if (scrollingText) {
            scrollingText = false;
            textCounter = 0;
            textLabel.setText(textLines.get(linePointer));

        } else {
            linePointer += 1;
            if (linePointer >= textLines.size) {
                hide();
                scrollingText = false;
                textCounter = 0;
                if (eventKey != null) {
                    eventManager.event(eventKey, eventParams);
                    eventKey = null;
                }
            } else {
                textCounter = 0;
                scrollingText = true;
//            textLabel.setText(textLines.get(linePointer));
            }
        }
    }

    /**
     * Hides just the selectbox window
     */
    public void hideSelectBox() {
        selectBox.hide();
    }

    /**
     * Checks if the main dialogue box is visible
     * @return true if it is visible, false otherwise
     */
    public boolean isVisible() {
        return dialogueWindow.isVisible();
    }

    /**
     * Gets the window widget of the dialogue box
     *
     * @return A window widget
     */
    public Window getWindow() {
        return dialogueWindow;
    }

    /**
     * Returns the width of the main dialogue screen widget
     * @return The width
     */

    public float getWidth() {
        return dialogueWindow.getWidth();
    }

    /**
     * Returns the height of the main dialogue screen widget
     * @return The height
     */
    public float getHeight() {
        return dialogueWindow.getHeight();
    }

    /**
     * Returns the created selectbox class
     * @return A SelectBox class
     */
    public SelectBox getSelectBox() {
        return selectBox;
    }




}
