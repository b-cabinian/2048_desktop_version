/*
Name: Brian Cabinian
Email: brian.cabinian@gmail.com
Date: 22-12-19
Project Name: Final_Cabinian_Brian
Course: CS 17.11A
Description: Grid class for 2048 game
*/

package edu.srjc.cabinian.brian.final_cabinian_brian;

import java.util.ArrayList;
import java.util.Arrays;

public class Grid
{
    private int[] gridValues = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[] moveFlag = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[] mergeFlag = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private int numOccupiedSites = 0;

    private ArrayList<Integer> endSpots = new ArrayList<>();

    private static final int[] leftTileCheckOrder = {0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15};
    private static final int[] rightTileCheckOrder = {3, 7, 11, 15, 2, 6, 10, 14, 1, 5, 9, 13, 0, 4, 8, 12};
    private static final int[] upTileCheckOrder = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    private static final int[] downTileCheckOrder = {12, 13, 14, 15, 8, 9, 10, 11, 4, 5, 6, 7, 0, 1, 2, 3};

    //default constructor makes initial game grid
    public Grid()
    {
    }

    //update a single tile
    public void addTile(int gridIndex, int tileValue)
    {
        this.gridValues[gridIndex] = tileValue;
        this.numOccupiedSites += 1;
    }

    public void updateTile(int gridIndex, int tileValue)
    {
        this.gridValues[gridIndex] = tileValue;
    }

    public int getTileValue(int gridIndex)
    {
        return this.gridValues[gridIndex];
    }

    public ArrayList<Integer> getEndSpots()
    {
        return endSpots;
    }

    public int[] getMergeFlag()
    {
        return mergeFlag;
    }

    public int[] getGridValues()
    {
        return gridValues;
    }

    //Grid Dir -> IntermediateGrid
    //in intermediate grid it's possible for two tiles to be at the same spot
    //instead of doing 3d array, do another 2D array called mergeFlag
    //intermediateGrid also tracks which tiles moved
    public Grid makeIntermediateGrid(char dir)
    {
        //make a deep copy of start grid
        Grid newGrid = new Grid();
        //for deep copy you need to
        newGrid.setGridValues(this.gridValues, 16);

        ArrayList<Integer> startSpots = newGrid.findOccupiedSpots();
        //this updates mergeFlag and moveFlag for newGrid
        //record endSpots for animation
        newGrid.endSpots = newGrid.findEndSpots(startSpots, dir);

        return newGrid;
    }

    public void testIntermediateGrid()
    {
        Arrays.fill(this.gridValues, 0);
        this.gridValues[3] = 2;
        Grid newGrid = makeIntermediateGrid('L');
        int[] testResult = {2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        if (Arrays.equals(newGrid.gridValues, testResult))
        {
            System.out.println("success intermediateGrid1");
        }

        Arrays.fill(this.gridValues, 0);
        this.gridValues[3] = 2;
        this.gridValues[2] = 4;
        newGrid = makeIntermediateGrid('L');
        testResult = new int[]{4, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        if (Arrays.equals(newGrid.gridValues, testResult))
        {
            System.out.println("success intermediateGrid2");
        }

        Arrays.fill(this.gridValues, 0);
        this.gridValues[3] = 2;
        this.gridValues[0] = 4;
        newGrid = makeIntermediateGrid('L');
        testResult = new int[]{4, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        if (Arrays.equals(newGrid.gridValues, testResult))
        {
            System.out.println("success intermediateGrid3");
        }

        Arrays.fill(this.gridValues, 0);
        this.gridValues[15] = 2;
        this.gridValues[14] = 2;
        this.gridValues[13] = 4;
        this.gridValues[12] = 2;
        newGrid = makeIntermediateGrid('R');
        testResult = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 2};
        if (Arrays.equals(newGrid.gridValues, testResult))
        {
            System.out.println("success intermediateGrid4");
        }

        Arrays.fill(this.gridValues, 0);
        this.gridValues[3] = 4;
        this.gridValues[7] = 2;
        this.gridValues[12] = 4;
        this.gridValues[13] = 2;
        this.gridValues[15] = 8;
        newGrid = makeIntermediateGrid('R');
        testResult = new int[]{0, 0, 0, 4, 0, 0, 0, 2, 0, 0, 0, 0, 0, 4, 2, 8};
        if (Arrays.equals(newGrid.gridValues, testResult))
        {
            System.out.println("success intermediateGrid5");
        }
    }

    public void setGridValues(int array[], int size)
    {
        for (int i = 0; i < size; i++)
        {
            this.gridValues[i] = array[i];
        }
    }

    public boolean isWinner()
    {
        for (int val : gridValues)
        {
            if (val == 2048)
            {
                return true;
            }
        }

        return false;
    }

    //check for loser
    //if we are at an intermediate grid and all sites are occupied and no moves can be made,
    // then game is over
    public boolean isLoser()
    {
        //assume no movement possible
        boolean cantMove = true;

        //loop through moveFlag array
        //until we hit a non-zero value
        //which means a move is possible
        for (int i = 0; i < 16; i++)
        {
            if (moveFlag[i] > 0)
            {
                cantMove = false;
                break;
            }
        }

        return (this.numOccupiedSites == 16 && !cantMove);
    }

    //occupied spots should always deliver least to greatest
    public ArrayList<Integer> findOccupiedSpots()
    {
        ArrayList<Integer> occupiedSpots = new ArrayList<>();
        for (int i = 0; i < this.gridValues.length; i++)
        {
            if (this.gridValues[i] != 0)
            {
                occupiedSpots.add(i);
            }
        }

        this.numOccupiedSites = occupiedSpots.size();
        return occupiedSpots;
    }

    public ArrayList<Integer> findOpenSpots()
    {
        ArrayList<Integer> openSpots = new ArrayList<>();
        for (int i = 0; i < this.gridValues.length; i++)
        {
            if (this.gridValues[i] == 0)
            {
                openSpots.add(i);
            }
        }
        return openSpots;
    }

    // occupied spots,  movement dir -> ending spots
    //ending spots needs to map exactly to occupied spots
    public ArrayList<Integer> findEndSpots(ArrayList<Integer> startSpots, char dir)
    {
        ArrayList<Integer> endSpots = new ArrayList<>();

        //initialize endSpots with all 0s equal in size to startSpots
        for (int s : startSpots)
        {
            endSpots.add(0);
        }

        int newIndex = 0;
        int swapTemp = 0;

        //based on movement direction, set which tiles we're checking for movement
        //if right, need to try moving tiles in col 2, col 1, then col 0
        //if left, try moving col 1, col 2, then col 3
        int [] moveTileCheckOrder = switch (dir)
                {
                    case 'L' -> leftTileCheckOrder;
                    case 'R' -> rightTileCheckOrder;
                    case 'U' -> upTileCheckOrder;
                    case 'D' -> downTileCheckOrder;
                    default -> new int[12];
                };

        for (int location : moveTileCheckOrder)
        {
            if (startSpots.contains(location))
            {
                newIndex = this.findNewIndex(location, dir);
                //need location position in startSpots
                int startSpotIndex = startSpots.indexOf(location);
                endSpots.set(startSpotIndex, newIndex);

                //if newIndex has shifted,
                // update gridValues
                // and update moveFlag
                if (newIndex != location)
                {
                    swapTemp = this.gridValues[location];
                    this.gridValues[location] = 0;
                    this.gridValues[newIndex] = swapTemp;

                    //location was the original position
                    moveFlag[location] = 1;
                }
            }
        }

        return endSpots;
    }


    public void testFindEndSpots()
    {
        Arrays.fill(this.gridValues, 0);
        ArrayList<Integer> endSites = new ArrayList<>();

        // {3} left -> {0}
        this.gridValues[3] = 2;
        ArrayList<Integer> occupiedSites = new ArrayList<>();
        occupiedSites.add(3);

        endSites = this.findEndSpots(occupiedSites , 'L');
        ArrayList<Integer> testResult = new ArrayList<>();
        testResult.add(0);

        if (endSites.equals(testResult))
        {
            System.out.println("success findEnd1");
        }
        else
        {
            System.out.println("failure findEnd1");
        }

        // {2, 3} left -> {0, 1}
        Arrays.fill(this.gridValues, 0);
        Arrays.fill(this.moveFlag, 0);
        this.gridValues[3] = 2;
        this.gridValues[2] = 4;
        occupiedSites.clear();
        occupiedSites.add(2);
        occupiedSites.add(3);
        //clear mergeFlag
        //clear moveFlag
        endSites = this.findEndSpots(occupiedSites , 'L');
        testResult.clear();
        testResult.add(0);
        testResult.add(1);

        if (endSites.equals(testResult))
        {
            System.out.println("success findEnd2");
        }
        else
        {
            System.out.println("failure findEnd2");
        }

        // {2, 3} right -> {2, 3}
        Arrays.fill(this.gridValues, 0);
        Arrays.fill(this.moveFlag, 0);
        this.gridValues[3] = 2;
        this.gridValues[2] = 4;
        occupiedSites.clear();
        occupiedSites.add(2);
        occupiedSites.add(3);
        //clear mergeFlag
        //clear moveFlag
        endSites = this.findEndSpots(occupiedSites , 'R');
        testResult.clear();
        testResult.add(2);
        testResult.add(3);

        if (endSites.equals(testResult))
        {
            System.out.println("success findEnd3");
        }
        else
        {
            System.out.println("failure findEnd3");
        }

        // {2, 3} right -> {3} merged
        Arrays.fill(this.gridValues, 0);
        Arrays.fill(this.moveFlag, 0);
        this.gridValues[3] = 2;
        this.gridValues[2] = 2;
        occupiedSites.clear();
        occupiedSites.add(2);
        occupiedSites.add(3);
        //clear mergeFlag
        //clear moveFlag
        endSites = this.findEndSpots(occupiedSites , 'R');
        testResult.clear();
        testResult.add(3);
        testResult.add(3);

        if (endSites.equals(testResult))
        {
            System.out.println("success findEnd4");
        }
        else
        {
            System.out.println("failure findEnd4");
        }

        // {2, 3} right -> {3} merged
        Arrays.fill(this.gridValues, 0);
        Arrays.fill(this.moveFlag, 0);
        this.gridValues[11] = 2;
        this.gridValues[12] = 2;
        this.gridValues[13] = 2;
        this.gridValues[15] = 4;
        occupiedSites.clear();
        occupiedSites.add(11);
        occupiedSites.add(12);
        occupiedSites.add(13);
        occupiedSites.add(15);
        //clear mergeFlag
        //clear moveFlag
        endSites = this.findEndSpots(occupiedSites , 'R');
        testResult.clear();
        testResult.add(11);
        testResult.add(14);
        testResult.add(14);
        testResult.add(15);

        if (endSites.equals(testResult))
        {
            System.out.println("success findEnd5");
        }
        else
        {
            System.out.println("failure findEnd5");
        }

    }

    //this function can only be called in a particular order defined above
    //Example: if move direction is left, you have to move tiles that are further left first
    //otherwise more right tiles won't be moved to the correct position
    //it's possible for this to return the same as current index
    //Grid, int gridIndex, char Direction -> int newGridIndex
    private int findNewIndex(int currentGridIndex, char dir)
    {
        int newGridIndex = currentGridIndex;
        int row = currentGridIndex / 4;
        int col = currentGridIndex % 4;

        //this is for safety
        if ((dir == 'L' && col == 0) || (dir == 'R' && col == 3) ||
                (dir == 'U' && row == 0) || (dir == 'D' && row == 3))
        {
            ;
        }
        else
        {
            if (dir == 'L')
            {
                int newCol = 0;

                //update gridIndex in direction of travel
                //stop at boundary
                do
                {
                    newGridIndex -= 1;
                    newCol = newGridIndex % 4;
                } while (this.gridValues[newGridIndex] == 0 && newCol > 0);

                //leave while loop because we hit non-zero value in grid:
                //newCol = 0, 1, 2
                //leave while loop because we hit boundary
                //newCol = 0

                //check if newGridIndex is occupied, if occupied we need to check for merge
                if (this.gridValues[newGridIndex] > 0)
                {
                    //see if values are the same and another merge isn't already happening here
                    if (this.gridValues[newGridIndex] == this.gridValues[currentGridIndex] && mergeFlag[newGridIndex] != 1)
                    {
                        mergeFlag[newGridIndex] = 1;
                    }
                    //if values aren't the same or merge already happened we need to move back an index
                    else
                    {
                        newGridIndex += 1;
                    }
                }
            }

            else if (dir == 'R')
            {
                int newCol = 0;

                do
                {
                    newGridIndex += 1;
                    newCol = newGridIndex % 4;
                } while (this.gridValues[newGridIndex] == 0 && newCol < 3);

                if (this.gridValues[newGridIndex] > 0)
                {
                    if (this.gridValues[newGridIndex] == this.gridValues[currentGridIndex] && mergeFlag[newGridIndex] != 1)
                    {
                        mergeFlag[newGridIndex] = 1;
                    }
                    else
                    {
                        newGridIndex -= 1;
                    }
                }
            }

            else if (dir == 'D')
            {
                int newRow = 0;

                do
                {
                    newGridIndex += 4;
                    newRow = newGridIndex / 4;
                } while (this.gridValues[newGridIndex] == 0 && newRow < 3);

                if (this.gridValues[newGridIndex] > 0)
                {
                    if (this.gridValues[newGridIndex] == this.gridValues[currentGridIndex] && mergeFlag[newGridIndex] != 1)
                    {
                        mergeFlag[newGridIndex] = 1;
                    }
                    else
                    {
                        newGridIndex -= 4;
                    }
                }
            }

            else if (dir == 'U')
            {
                int newRow = 0;

                do
                {
                    newGridIndex -= 4;
                    newRow = newGridIndex / 4;
                } while (this.gridValues[newGridIndex] == 0 && newRow > 0);

                if (this.gridValues[newGridIndex] > 0)
                {
                    if (this.gridValues[newGridIndex] == this.gridValues[currentGridIndex] && mergeFlag[newGridIndex] != 1)
                    {
                        mergeFlag[newGridIndex] = 1;
                    }
                    else
                    {
                        newGridIndex += 4;
                    }
                }
            }
        }

        return newGridIndex;
    }

    public void testFindNewIndex()
    {
        Arrays.fill(this.gridValues, 0);

        //3 only tile, check 3 -> 0
        this.gridValues[3] = 2;
        int testResult = this.findNewIndex(3, 'L');
        if (testResult == 0)
        {
            System.out.println("success findNewIndex1");
        }
        else
        {
            System.out.println("failure findNewIndex1");
        }

        // 3 and 2 occupied, check 2, move left -> 0
        //move tile at 2 to tile at 0
        // 3 and 0 occupied, check 3, move left -> 1
        this.gridValues[2] = 4;
        testResult = this.findNewIndex(2, 'L');
        if (testResult == 0)
        {
            System.out.println("success findNewIndex3");
        }
        else
        {
            System.out.println("failure findNewIndex3");
        }
        testResult = this.findNewIndex(2, 'D');
        if (testResult == 14)
        {
            System.out.println("success findNewIndex3");
        }
        else
        {
            System.out.println("failure findNewIndex3");
        }

        this.gridValues[2] = 0;
        this.gridValues[0] = 4;

        testResult = this.findNewIndex(3, 'L');
        if (testResult == 1)
        {
            System.out.println("success findNewIndex2");
        }
        else
        {
            System.out.println("failure findNewIndex2");
        }


        this.gridValues[3] = 0;
        this.gridValues[2] = 4;
        this.gridValues[15] = 4;
        //2 and 15 occupied, check 2, move left -> 0
        //2 and 15 occupied, check 15, move left -> 12
        //2 and 15 occupied, check 2, move right -> 3
        testResult = this.findNewIndex(2, 'L');
        if (testResult == 0)
        {
            System.out.println("success findNewIndex4");
        }
        else
        {
            System.out.println("failure findNewIndex4");
        }

        testResult = this.findNewIndex(15, 'L');
        if (testResult == 12)
        {
            System.out.println("success findNewIndex5");
        }
        else
        {
            System.out.println("failure findNewIndex5");
        }

        Arrays.fill(this.gridValues, 0);
        this.gridValues[0] = 4;
        this.gridValues[4] = 2;
        this.gridValues[5] = 4;
        this.gridValues[8] = 2;

        testResult = this.findNewIndex(0, 'L');
        if (testResult == 0)
        {
            System.out.println("success findNewIndex6");
        }
        else
        {
            System.out.println("failure findNewIndex6");
        }

        testResult = this.findNewIndex(4, 'L');
        if (testResult == 4)
        {
            System.out.println("success findNewIndex7");
        }
        else
        {
            System.out.println("failure findNewIndex7");
        }

        testResult = this.findNewIndex(5, 'L');
        if (testResult == 5)
        {
            System.out.println("success findNewIndex8");
        }
        else
        {
            System.out.println("failure findNewIndex8");
        }

        testResult = this.findNewIndex(8, 'L');
        if (testResult == 8)
        {
            System.out.println("success findNewIndex9");
        }
        else
        {
            System.out.println("failure findNewIndex9");
        }

        //check for good merging
        Arrays.fill(this.gridValues, 0);
        Arrays.fill(this.mergeFlag, 0);
        this.gridValues[4] = 2;
        this.gridValues[5] = 2;
        this.gridValues[6] = 2;
        this.gridValues[7] = 2;

        testResult = this.findNewIndex(4, 'L');
        if (testResult == 4)
        {
            System.out.println("success findNewIndex10");
        }
        else
        {
            System.out.println("failure findNewIndex10");
        }
        testResult = this.findNewIndex(5, 'L');
        if (testResult == 4 && mergeFlag[4] == 1)
        {
            System.out.println("success findNewIndex11");
        }
        else
        {
            System.out.println("failure findNewIndex11");
        }

        this.gridValues[5] = 0;
        testResult = this.findNewIndex(6, 'L');
        if (testResult == 5)
        {
            System.out.println("success findNewIndex12");
        }
        else
        {
            System.out.println("failure findNewIndex12");
        }
        this.gridValues[6] = 0;
        this.gridValues[5] = 2;
        testResult = this.findNewIndex(7, 'L');
        if (testResult == 5 && mergeFlag[5] == 1)
        {
            System.out.println("success findNewIndex13");
        }
        else
        {
            System.out.println("failure findNewIndex13");
        }

    }


    //IntermediateGrid -> ListOfMovedTilesIndex
    public ArrayList<Integer> findMovingTiles()
    {
        ArrayList<Integer> movingIndices = new ArrayList<>();

        for (int i = 0; i < moveFlag.length; i++)
        {
            if (moveFlag[i] != 0)
            {
                movingIndices.add(i);
            }
        }

        return movingIndices;
    }

}
