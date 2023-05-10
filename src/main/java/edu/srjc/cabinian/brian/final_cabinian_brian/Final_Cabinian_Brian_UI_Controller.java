/*
Name: Brian Cabinian
Email: brian.cabinian@gmail.com
Date: 22-12-19
Project Name: Final_Cabinian_Brian
Course: CS 17.11A
Description: UI controller for 2048 game.
Methods:
btnNewGame_Clicked
addNewTile
onKeyPressed,
playAgain
slideAnimation
gridReset
indexToTile
initialize
*/

package edu.srjc.cabinian.brian.final_cabinian_brian;

import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class Final_Cabinian_Brian_UI_Controller implements Initializable
{

    @FXML
    private GridPane board;

    @FXML
    private Button btnNewGame;

    @FXML
    private Label lblIntro;

    @FXML
    private Label lblTitle;

    private int score;
    private int bestScore;
    @FXML
    private Label lblScore;
    @FXML
    private Label lblBestScore;

    @FXML
    private Pane tileContainer;

    @FXML
    private BorderPane root;

    private Grid game;

    @FXML
    void btnNewGame_Clicked(MouseEvent event)
    {
        //clear board of tiles
        //numTiles is number of children - 1, for the GridPane
        int numTiles = tileContainer.getChildren().size() - 1;

        while (numTiles > 0)
        {
            Node child = tileContainer.getChildren().get(numTiles);

            //make sure we don't remove board
            if (child.getId() == null)
            {
                tileContainer.getChildren().remove(numTiles);
            }

            numTiles -= 1;
        }

        //if score higher than best score, save it
        if (score > bestScore)
        {
            bestScore = score;
            lblBestScore.setText(Integer.toString(bestScore));
        }

        //reinitialize
        initialize(null, null);
    }

    private void addNewTile()
    {
        int newTilePosition = 0;
        int numOpenSpots = 0;
        int newTileRandomIndex = 0;
        ArrayList<Integer> openSpots = this.game.findOpenSpots();
        int chance = 0;
        int newTileValue = 0;

        //randomly select number out of array
        numOpenSpots = openSpots.size();
        if (numOpenSpots == 0)
        {
            return;
        }

        newTileRandomIndex = ThreadLocalRandom.current().nextInt(0, numOpenSpots);
        newTilePosition = openSpots.get(newTileRandomIndex);

        //randomly select 2 or 4 with 90% chance of 2
        chance = ThreadLocalRandom.current().nextInt(0, 100);

        if (chance < 90)
        {
            newTileValue = 2;
        }
        else
        {
            newTileValue = 4;
        }

        //make new tile with that value and position
        String newTileValueString = Integer.toString(newTileValue);
        //testing, change back to newTileIndex
        Tile t = new Tile(newTileValueString, newTilePosition);
        tileContainer.getChildren().add(t);

        //animate entrance by growing the tile
        ScaleTransition st = new ScaleTransition(Duration.millis(200), t);
        st.setFromX(0.1f);
        st.setFromY(0.1f);
        st.setToX(1f);
        st.setToY(1f);
        st.setCycleCount(1);

        st.play();

        this.game.addTile(newTilePosition, newTileValue);
    }

    @FXML
    void onKeyPressed(KeyEvent event)
    {
        KeyCode keyCode = event.getCode();

        //do nothing if a non-arrow key is pressed
        if (!keyCode.isArrowKey())
        {
            return;
        }

        //make keycode human readable
        char dir = keyCode.getName().charAt(0);

        //find initial position of tiles
        ArrayList<Integer> startSpots = this.game.findOccupiedSpots();

        //find final positions of tiles
        //make new grid
        Grid intermediateGrid = this.game.makeIntermediateGrid(dir);
        ArrayList<Integer> endSpots = intermediateGrid.getEndSpots();

        //check for loser
        if (intermediateGrid.isLoser())
        {
            UiUtils.messageBox("You lost! Press the New Game button to play again");
        }

        //grab merges
        int[] merges = intermediateGrid.getMergeFlag();

        //update layout position on all labels after animation
        //well there should be no movement once its at the edge because they're not allowed to translate
        slideAnimation(startSpots, endSpots, dir, merges);

        //update score and Grid with merged values
        for (int m = 0; m < merges.length; m++)
        {
            if (merges[m] == 1)
            {
                //update grid data
                int tileValue = intermediateGrid.getTileValue(m);
                intermediateGrid.updateTile(m, 2*tileValue);

                //update score based on merges (add merged value)
                score += (2 * tileValue);
            }
        }

        //update score label
        lblScore.setText(Integer.toString(score));

        //make IntermediateGrid the new game grid
        this.game = new Grid();
        this.game.setGridValues(intermediateGrid.getGridValues(), 16);

        //add new tile
        addNewTile();

        //check for winner
        if (this.game.isWinner())
        {
            UiUtils.messageBox("You won! Press the New Game button to play again");
        }

    }

    private ButtonType playAgain(String message)
    {
        ButtonType answer = UiUtils.yesNoAnswerBox(message);

        return answer;
    }

    private void slideAnimation(ArrayList<Integer> startSpots, ArrayList<Integer> endSpots, char dir, int[] mergeSites)
    {
        ParallelTransition pt = new ParallelTransition();

        for(int i = 0; i < startSpots.size(); i++)
        {
            int start = startSpots.get(i);
            int end = endSpots.get(i);
            double translationLength = 0;
            Tile toMove = indexToTile(start);
            TranslateTransition translate = new TranslateTransition(Duration.millis(200), toMove);

            if (dir == 'L' || dir == 'R')
            {
                translationLength = 121 * (end - start);
                translate.setByX(translationLength);
            }
            else if (dir == 'U' || dir == 'D')
            {
                translationLength = 121 * ((end - start) / 4);
                translate.setByY(translationLength);
            }

            translate.setCycleCount(1);
            pt.getChildren().add(translate);
        }

        try
        {
            pt.play();
        }
        catch (Exception e)
        {
            System.out.println("This is an animmation error bug that doesn't disrupt gameplay:");
            System.out.println(e.getMessage());
        }

        pt.setOnFinished((finish) -> gridReset(mergeSites));

    }

    private void gridReset(int[] mergeSites)
    {
        //clear board of tiles
        //numTiles is number of children - 1, for the GridPane
        int numTiles = tileContainer.getChildren().size() - 1;

        while (numTiles > 0)
        {
            Node child = tileContainer.getChildren().get(numTiles);

            //make sure we don't remove board
            if (child.getId() == null)
            {
                tileContainer.getChildren().remove(numTiles);
            }

            numTiles -= 1;
        }

        int[] gridVals = this.game.getGridValues();

        for (int i = 0; i < gridVals.length; i++)
        {
            if (gridVals[i] != 0)
            {
                Tile t = new Tile(Integer.toString(gridVals[i]), i);
                tileContainer.getChildren().add(t);
                /* the merge animation makes things very laggy
                if (mergeSites[i] > 0)
                {
                    ScaleTransition st = new ScaleTransition(Duration.millis(150), t);
                    st.setFromX(1.08f);
                    st.setFromY(1.08f);
                    st.setToX(1f);
                    st.setToY(1f);
                    st.setCycleCount(1);

                    st.play();

                }
                 */
            }
        }
    }


    //int gridIndex -> Tile
    private Tile indexToTile(int gridIndex)
    {
        for (Node child : tileContainer.getChildren())
        {
            if (child.getId() == null)
            {
                Tile testTile = (Tile) child;
                if (gridIndex == testTile.getGridIndex())
                {
                    return testTile;
                }
            }
        }

        return null;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        score = 0;
        lblScore.setText("0");

        Font font = Font.loadFont(getClass().getResourceAsStream("fonts/ClearSans_Regular.ttf"), 16);

        lblTitle.setFont(font);
        lblIntro.setFont(font);
        btnNewGame.setFont(font);

        this.game = new Grid();
        //game.testFindNewIndex();
        //game.testFindEndSpots();
        //game.testIntermediateGrid();

        addNewTile();
        addNewTile();

        root.setFocusTraversable(true);
        root.requestFocus();
    }

}


