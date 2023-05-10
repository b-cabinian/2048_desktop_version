/*
Name: Brian Cabinian
Email: brian.cabinian@gmail.com
Date: 22-12-19
Project Name: Final_Cabinian_Brian
Course: CS 17.11A
Description: Tile class for 2048 game
*/

package edu.srjc.cabinian.brian.final_cabinian_brian;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.Font;


public class Tile extends Label
{
    private int value;
    private int gridIndex;
    private int row;
    private int col;

    public Tile(String s, int gridInd)
    {
        super(s);
        this.gridIndex = gridInd;
        this.row = gridInd / 4;
        this.col = gridInd % 4;
        this.value = Integer.parseInt(s);
        //this.setId("Tile-V" + s + "Index" + gridInd);

        //set layout variables
        this.setMinSize(105, 105);
        this.setPrefSize(105, 105);
        this.setMaxSize(105, 105);
        this.setAlignment(Pos.CENTER);

        double positionX = 0.0;
        double positionY = 0.0;

        positionX = (col * 121) + 16;
        positionY = (row * 121) + 16;
        this.setLayoutX(positionX);
        this.setLayoutY(positionY);

        //set styling
        //generic styling: font family, font weight, background rounding
        //specific styling: font size (has to get smaller to fit larger numbers), background color, font color
        Font tileFont = Font.loadFont(getClass().getResourceAsStream("fonts/ClearSans_Regular.ttf"), 16);
        this.setFont(tileFont);
        this.getStyleClass().add("tile");
        this.getStyleClass().add("tile-" + s);
    }

    public int getGridIndex()
    {
        return gridIndex;
    }

}
