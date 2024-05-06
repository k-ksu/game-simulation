
/**
 * The Main program implements an application that solves assignment4 (Game simulation)
 *
 * @author  Ksenia Korchagina
 * @version 1.1
 * @since   2023-12-03
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Main is the main class of the task that
 * contains an entry point for the program.
 */
public class Main {
    /**
     * Here gameBoard of Bord type created
     */
    private Board gameBoard;

    /**
     * NewInsect is used to check given type of Insect
     * @param insectType is one of 4 Insect types, which we determine in enum InsectType
     * @param insectColor is one of 4 Insect colors, which we determine in enum InsectColor
     * @param x first coordinate of entity location
     * @param y second coordinate of entity location
     * @return return null in nor suitable case
     * @throws InvalidInsectTypeException is a type of exception which is used to catch not right Insect Type
     * @throws InvalidInsectColorException is a type of exception which is used to catch not right Insect Color
     */
    private static Insect newInsect(String insectType, String insectColor, int x, int y)
            throws InvalidInsectTypeException, InvalidInsectColorException {
        InsectColor color = InsectColor.toColor(insectColor);
        InsectType type = InsectType.toType(insectType);
        EntityPosition entityPosition = new EntityPosition(x, y);
        if (type == InsectType.BUTTERFLY) {
            return new Butterfly(entityPosition, color);
        }
        if (type == InsectType.ANT) {
            return new Ant(entityPosition, color);
        }
        if (type == InsectType.SPIDER) {
            return new Spider(entityPosition, color);
        }
        if (type == InsectType.GRASSHOPPER) {
            return new Grasshopper(entityPosition, color);
        }
        return null;
    }

    /**
     * newInsect is the main function of Main class,
     * where we read input file, check all scanned data,
     * add new Insects and foodPoints on our board, call all
     * necessary functions to simulate a game and add results
     * to the output file.
     * @param args
     * @throws IOException is necessary because in this function
     * we check many Exceptions(IOException type), which may happened
     */
    //@SuppressWarnings("checkstyle:MagicNumber")
    public static void main(String[] args) throws IOException {
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        Scanner scanner = new Scanner(new File(inputFile));
        FileWriter fileWriter = new FileWriter(outputFile);
        PrintWriter writer = new PrintWriter(fileWriter);

        try {
            final int k4 = 4;
            final int k10 = 1000;
            final int k16 = 16;
            final int k200 = 200;
            int d = scanner.nextInt();
            if (d < k4 || d > k10) {
                throw new InvalidBoardSizeException();
            }
            Board board = new Board(d);
            int n = scanner.nextInt();
            if (n < 1 || n > k16) {
                throw new InvalidNumberOfInsectsException();
            }
            int m = scanner.nextInt();
            if (m < 1 || m > k200) {
                throw new InvalidNumberOfFoodPointsException();
            }
            Map<String, BoardEntity> boardData = board.getBoardData();
            for (int i = 0; i < n; i++) {
                String scannedColor = scanner.next();
                String scannedType = scanner.next();

                int scannedXCoordinate = scanner.nextInt();
                int scannedYCoordinate = scanner.nextInt();
                if (scannedXCoordinate > d || scannedYCoordinate > d || scannedXCoordinate < 1
                        || scannedYCoordinate < 1) {
                    throw new InvalidEntityPositionException();
                }

                Insect insect = newInsect(scannedType, scannedColor, scannedXCoordinate, scannedYCoordinate);

                for (int j = 0; j < i; j++) {
                    Insect insectPrev = (Insect) boardData.get(Integer.toString(j));
                    if (insect.color == insectPrev.color && insect.getClass() == insectPrev.getClass())  {
                        throw new DuplicateInsectException();
                    }
                }

                if (boardData.containsKey(scannedXCoordinate + "_" + scannedYCoordinate)) {
                    throw new TwoEntitiesOnSamePositionException();
                }

                board.addEntity(Integer.toString(i), insect);
                board.addEntity(scannedXCoordinate + "_" + scannedYCoordinate, insect);
            }

            for (int i = 0; i < m; i++) {
                int scannedAmountOfFood = scanner.nextInt();
                int scannedXCoordinate = scanner.nextInt();
                int scannedYCoordinate = scanner.nextInt();
                if (scannedXCoordinate > d || scannedYCoordinate > d || scannedXCoordinate < 1
                        || scannedYCoordinate < 1) {
                    throw new InvalidEntityPositionException();
                } else {
                    EntityPosition entityPosition = new EntityPosition(scannedXCoordinate, scannedYCoordinate);
                    FoodPoint foodPoint = new FoodPoint(entityPosition, scannedAmountOfFood);

                    if (boardData.containsKey(scannedXCoordinate + "_" + scannedYCoordinate)) {
                        throw new TwoEntitiesOnSamePositionException();
                    }

                    board.addEntity(scannedXCoordinate + "_" + scannedYCoordinate, foodPoint);
                }
            }

            for (int i = 0; i < n; i++) {
                Insect insect = (Insect) boardData.get(Integer.toString(i));
                Direction bestDirection = insect.getBestDirection(boardData, d);
                int eatenFood = insect.travelDirection(bestDirection, boardData, d);

                writer.println(insect.color + " " + insect + " "
                        + bestDirection + " " + eatenFood);
            }
        } catch (Exception e) {
            writer.println(e.getMessage());
        }
        writer.close();

    }
}

/**
 * InsectColor is an enum, which is used to save all colors
 * and check given colors
 */
enum InsectColor {
    RED {
        /**
         * case RED
         * @return "Red" in the right way according to the task
         */
        @Override
        public String toString() {
            return "Red";
        }
    },
    GREEN {
        /**
         * case GREEN
         * @return "Blue" in the right way according to the task
         */
        @Override
        public String toString() {
            return "Green";
        }
    },
    BLUE {
        /**
         * case BLUE
         * @return "Blue" in the right way according to the task
         */
        @Override
        public String toString() {
            return "Blue";
        }
    },
    YELLOW {
        /**
         * case YELLOW
         * @return "Yellow" in the right way according to the task
         */
        @Override
        public String toString() {
            return "Yellow";
        }
    };

    /**
     * function toColor make the given String one of 4 colors and check the possibility of the action
     * @param s is a String, which should be represented as a color
     * @return colors from enum InsectColor
     * @throws InvalidInsectColorException is a possible exception, which we check in this function
     */
    public static InsectColor toColor(String s) throws InvalidInsectColorException {
        if (s.equals("Red")) {
            return InsectColor.RED;
        }
        if (s.equals("Green")) {
            return InsectColor.GREEN;
        }
        if (s.equals("Blue")) {
            return InsectColor.BLUE;
        }
        if (s.equals("Yellow")) {
            return InsectColor.YELLOW;
        }

        throw new InvalidInsectColorException();
    }
}

/**
 * enum which contains all possible types of insects
 */
enum InsectType {
    BUTTERFLY,
    ANT,
    SPIDER,
    GRASSHOPPER;

    /**
     * function toType make the given String one of 4 insect types and check the possibility of the action
     * @param s is a String, which should be represented as a type
     * @return types from enum InsectType
     * @throws InvalidInsectTypeException is a possible exception, which we check in this function
     */
    public static InsectType toType(String s) throws InvalidInsectTypeException {
        if (s.equals("Butterfly")) {
            return InsectType.BUTTERFLY;
        }
        if (s.equals("Ant")) {
            return InsectType.ANT;
        }
        if (s.equals("Spider")) {
            return InsectType.SPIDER;
        }
        if (s.equals("Grasshopper")) {
            return InsectType.GRASSHOPPER;
        }

        throw new InvalidInsectTypeException();
    }
}

/**
 * enum direction contains all possible routes of moving
 */
enum Direction {
    N("North"),
    E("East"),
    S("South"),
    W("West"),
    NE("North-East"),
    SE("South-East"),
    SW("South-West"),
    NW("North-West");

    private String textRepresentation;

    private Direction(String text) {
        this.textRepresentation = text;
    }

    /**
     * we override our directions in a String way
     * @return String versions
     */
    @Override
    public String toString() {
        return this.textRepresentation;
    }
}

/**
 * class EntityPosition is necessary to store data about locations of different entities on the board
 */
class EntityPosition {
    private int x;
    private int y;

    /**
     * getter to private int x
     * @return private x
     */
    public int getX() {
        return x;
    }

    /**
     * setter to private int x
     * change original x to new x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * getter to private int y
     * @return private y
     */
    public int getY() {
        return y;
    }

    /**
     * setter to private int y
     * change original y to new y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * function which generate a position for new entity according to give x and y
     * @param x first coordinate of entity location
     * @param y second coordinate of entity location
     */
    public EntityPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

/**
 * BoardEntity is a super class for Insect and foodPoint classes
 */
abstract class BoardEntity {
    /**
     * here we create a new entityPosition of EntityPosition class,
     * where we'll store location of our entity
     */
    protected EntityPosition entityPosition;
}

/**
 * FoodPoint is a class representing one of two types of entities
 */
class FoodPoint extends BoardEntity {
    /**
     * value is an Integer, where we save amount of food in this FoodPoint
     */
    protected int value;

    /**
     * this function is a constructor, which create a new FoodPoint
     * @param position is a variable of EntityPosition type, where we store coordinates
     * @param value amount of food in this FoodPoint
     */
    public FoodPoint(EntityPosition position, int value) {
        this.entityPosition = position;
        this.value = value;
    }
}

/**
 * abstract class Insect represent one of BoardEntity entities
 */
abstract class Insect extends BoardEntity {
    protected InsectColor color;

    /**
     * constructor for new insects of Insect type
     * @param position position of a new insect
     * @param color color of a new insect
     */
    public Insect(EntityPosition position, InsectColor color) {
        this.entityPosition = position;
        this.color = color;
    }

    /**
     * function which choose best direction by counting the maximum amount of eaten food
     * @param board - our board, where we locate all food and insects
     * @param boardSize - size of our board
     * @return null because for this type of task I decide not to use this function
     * */
    public Direction getBestDirection(Map<String, BoardEntity> board, int boardSize) {
        return null;
    }

    /**
     * this function count amount of food, which Insect will eat while following chosen direction
     * @param dir chosen direction
     * @param boarData our board
     * @param boardSize size of our board
     * @return  null because for this type of task I decide not to use this function
     * */
    public int travelDirection(Direction dir, Map<String, BoardEntity> boarData, int boardSize) {
        return 0;
    }
}

/**
 * class Butterfly represent one of our Insect types.
 * Here we store all methods, which we need to work
 * with this type of Insect
 */
class Butterfly extends Insect implements OrthogonalMoving {
    /**
     * we create a new butterfly by using information from super class Insect
     * @param entityPosition position on the board
     * @param color color of this particular butterfly
     */
    public Butterfly(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }

    /**
     * here we override method toString to get a String
     * @return String version of BUTTERFLY
     */
    @Override
    public String toString() {
        return "Butterfly";
    }

    /**
     * here we override method getBestDirection to choose which direction should this butterfly follow
     * @param boardData - our board, where we locate all food and insects
     * @param boardSize - size of our board
     * @return direction to choose
     */
    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        Direction bestDirection = Direction.N;
        int maxScore = 0;

        int result1 = getOrthogonalDirectionVisibleValue(Direction.N, null, boardData, boardSize);
        int result2 = getOrthogonalDirectionVisibleValue(Direction.E, null, boardData, boardSize);
        int result3 = getOrthogonalDirectionVisibleValue(Direction.S, null, boardData, boardSize);
        int result4 = getOrthogonalDirectionVisibleValue(Direction.W, null, boardData, boardSize);

        if (result1 > maxScore) {
            maxScore = result1;
            bestDirection = Direction.N;
        }
        if (result2 > maxScore) {
            maxScore = result2;
            bestDirection = Direction.E;
        }
        if (result3 > maxScore) {
            maxScore = result3;
            bestDirection = Direction.S;
        }
        if (result4 > maxScore) {
            maxScore = result4;
            bestDirection = Direction.W;
        }

        return bestDirection;
    }

    /**
     * here we override method travelDirection to count maximum
     * amount of food, which our butterfly can eat
     * @param dir chosen direction
     * @param boardData our board
     * @param boardSize size of our board
     * @return amount of eaten food
     */
    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        int x = this.entityPosition.getX();
        int y = this.entityPosition.getY();

        int currentScore = 0;

        int dx = 0;
        int dy = 0;

        switch (dir) {
            case N:
                dy = 0;
                dx = -1;
                break;
            case E:
                dy = 1;
                dx = 0;
                break;
            case S:
                dy = 0;
                dx = 1;
                break;
            case W:
                dy = -1;
                dx = 0;
                break;
            default:
                break;
        }

        y += dy;
        x += dx;
        while (y >= 1 && x >= 1 && y <= boardSize && x <= boardSize) {
            BoardEntity entity = boardData.get(x + "_" + y);

            if (entity == null) {
                y += dy;
                x += dx;
                continue;
            }

            if (entity.getClass() == FoodPoint.class) {
                FoodPoint foodPoint = (FoodPoint) entity;

                currentScore += foodPoint.value;
                boardData.remove(x + "_" + y);
            }

            if (entity instanceof Insect) {
                Insect insect = (Insect) entity;

                if (this.color != insect.color) {
                    break;
                }
            }

            y += dy;
            x += dx;
        }

        x = this.entityPosition.getX();
        y = this.entityPosition.getY();
        boardData.remove(x + "_" + y);

        return currentScore;
    }

    /**
     * here we override getOrthogonalDirectionVisibleValue to
     * imitate moving orthogonally for butterfly
     * @param dir - direction
     * @param entityPosition - coordinates of a butterfly
     * @param boardData - our board
     * @param boardSize - size of our board
     * @return amount of food
     */
    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<String, BoardEntity> boardData, int boardSize) {
        int x = this.entityPosition.getX();
        int y = this.entityPosition.getY();

        int currentScore = 0;

        int dx = 0;
        int dy = 0;

        switch (dir) {
            case N:
                dy = 0;
                dx = -1;
                break;
            case E:
                dy = 1;
                dx = 0;
                break;
            case S:
                dy = 0;
                dx = 1;
                break;
            case W:
                dy = -1;
                dx = 0;
                break;
            default:
                break;
        }

        y += dy;
        x += dx;
        while (y >= 1 && x >= 1 && y <= boardSize && x <= boardSize) {
            BoardEntity entity = boardData.get(x + "_" + y);

            if (entity == null) {
                y += dy;
                x += dx;
                continue;
            }

            if (entity.getClass() == FoodPoint.class) {
                FoodPoint foodPoint = (FoodPoint) entity;

                currentScore += foodPoint.value;
            }

            y += dy;
            x += dx;
        }

        return currentScore;
    }

    /**
     * here we override method travelOrthogonally to count
     * specific amount of eaten food by travelling orthogonally
     * @param dir - direction
     * @param entityPosition - coordinates of a butterfly
     * @param color - color of a butterfly, which is important
     *              for case with eating other insects
     * @param boardData - our board
     * @param boardSize - size of our board
     * @return
     */
    @Override
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                  Map<String, BoardEntity> boardData, int boardSize) {
        int x = this.entityPosition.getX();
        int y = this.entityPosition.getY();

        int currentScore = 0;

        int dx = 0;
        int dy = 0;

        switch (dir) {
            case N:
                dy = 0;
                dx = -1;
                break;
            case E:
                dy = 1;
                dx = 0;
                break;
            case S:
                dy = 0;
                dx = 1;
                break;
            case W:
                dy = -1;
                dx = 0;
                break;
            default:
                break;
        }

        y += dy;
        x += dx;
        while (y >= 1 && x >= 1 && y <= boardSize && x <= boardSize) {
            BoardEntity entity = boardData.get(x + "_" + y);

            if (entity == null) {
                y += dy;
                x += dx;
                continue;
            }

            if (entity.getClass() == FoodPoint.class) {
                FoodPoint foodPoint = (FoodPoint) entity;

                currentScore += foodPoint.value;
                boardData.remove(x + "_" + y);
            }

            if (entity instanceof Insect) {
                Insect insect = (Insect) entity;

                if (this.color != insect.color) {
                    break;
                }
            }

            y += dy;
            x += dx;
        }

        x = this.entityPosition.getX();
        y = this.entityPosition.getY();
        boardData.remove(x + "_" + y);

        return currentScore;
    }
}

/**
 * class Ant represent one of 4 types of Insects
 */
class Ant extends Insect implements OrthogonalMoving, DiagonalMoving {
    /**
     * public Ant is used as a constructor to create new Insects of Ant type
     * @param entityPosition - coordinates of this Ant
     * @param color - color of this Ant
     */
    public Ant(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }

    /**
     * function getBestDirection is used to choose the best direction based on maximum possible amount os eaten food
     * @param boardData - our board, where we locate all food and insects
     * @param boardSize - size of our board
     * @return direction to choose
     */
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        Direction bestDirection = Direction.N;
        int maxScore = 0;

        int result1 = getOrthogonalDirectionVisibleValue(Direction.N, null, boardData, boardSize);
        int result2 = getOrthogonalDirectionVisibleValue(Direction.E, null, boardData, boardSize);
        int result3 = getOrthogonalDirectionVisibleValue(Direction.S, null, boardData, boardSize);
        int result4 = getOrthogonalDirectionVisibleValue(Direction.W, null, boardData, boardSize);

        int result5 = getDiagonalDirectionVisibleValue(Direction.NW, null, boardData, boardSize);
        int result6 = getDiagonalDirectionVisibleValue(Direction.NE, null, boardData, boardSize);
        int result7 = getDiagonalDirectionVisibleValue(Direction.SW, null, boardData, boardSize);
        int result8 = getDiagonalDirectionVisibleValue(Direction.SE, null, boardData, boardSize);

        if (result1 > maxScore) {
            maxScore = result1;
            bestDirection = Direction.N;
        }
        if (result2 > maxScore) {
            maxScore = result2;
            bestDirection = Direction.E;
        }
        if (result3 > maxScore) {
            maxScore = result3;
            bestDirection = Direction.S;
        }
        if (result4 > maxScore) {
            maxScore = result4;
            bestDirection = Direction.W;
        }
        if (result6 > maxScore) {
            maxScore = result6;
            bestDirection = Direction.NE;
        }
        if (result8 > maxScore) {
            maxScore = result8;
            bestDirection = Direction.SE;
        }
        if (result7 > maxScore) {
            maxScore = result7;
            bestDirection = Direction.SW;
        }
        if (result5 > maxScore) {
            maxScore = result5;
            bestDirection = Direction.NW;
        }

        return bestDirection;
    }


    /**
     * function travelDirection is used to count amount of eaten food on the chosen direction
     * @param dir chosen direction
     * @param boardData our board
     * @param boardSize size of our board
     * @return amount of food
     */
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        int x = this.entityPosition.getX();
        int y = this.entityPosition.getY();

        int currentScore = 0;

        int dx = 0;
        int dy = 0;

        switch (dir) {
            case N:
                dy = 0;
                dx = -1;
                break;
            case E:
                dy = 1;
                dx = 0;
                break;
            case S:
                dy = 0;
                dx = 1;
                break;
            case W:
                dy = -1;
                dx = 0;
                break;
            case NE:
                dy = 1;
                dx = -1;
                break;
            case SE:
                dy = 1;
                dx = 1;
                break;
            case SW:
                dy = -1;
                dx = 1;
                break;
            case NW:
                dy = -1;
                dx = -1;
                break;
            default:
                break;
        }

        y += dy;
        x += dx;
        while (y >= 1 && x >= 1 && y <= boardSize && x <= boardSize) {
            BoardEntity entity = boardData.get(x + "_" + y);

            if (entity == null) {
                y += dy;
                x += dx;
                continue;
            }

            if (entity.getClass() == FoodPoint.class) {
                FoodPoint foodPoint = (FoodPoint) entity;

                currentScore += foodPoint.value;
                boardData.remove(x + "_" + y);
            }

            if (entity instanceof Insect) {
                Insect insect = (Insect) entity;

                if (this.color != insect.color) {
                    break;
                }
            }

            y += dy;
            x += dx;
        }

        x = this.entityPosition.getX();
        y = this.entityPosition.getY();
        boardData.remove(x + "_" + y);

        return currentScore;
    }

    /**
     * here we override function which make ANT to String Ant
     * @return String "Ant"
     */
    @Override
    public String toString() {
        return "Ant";
    }

    /**
     * function getOrthogonalDirectionVisibleValue to
     * imitate moving orthogonally for ant
     * @param dir - current direction
     * @param entityPosition - coordinates of the ant
     * @param boardData - our board
     * @param boardSize - size of our board
     * @return amount of eaten food
     */
    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<String, BoardEntity> boardData, int boardSize) {
        int x = this.entityPosition.getX();
        int y = this.entityPosition.getY();

        int currentScore = 0;

        int dx = 0;
        int dy = 0;

        switch (dir) {
            case N:
                dy = 0;
                dx = -1;
                break;
            case E:
                dy = 1;
                dx = 0;
                break;
            case S:
                dy = 0;
                dx = 1;
                break;
            case W:
                dy = -1;
                dx = 0;
                break;
            default:
                break;
        }

        y += dy;
        x += dx;
        while (y >= 1 && x >= 1 && y <= boardSize && x <= boardSize) {
            BoardEntity entity = boardData.get(x + "_" + y);

            if (entity == null) {
                y += dy;
                x += dx;
                continue;
            }

            if (entity.getClass() == FoodPoint.class) {
                FoodPoint foodPoint = (FoodPoint) entity;

                currentScore += foodPoint.value;
            }

            y += dy;
            x += dx;
        }

        return currentScore;
    }

    /**
     * here we override method travelOrthogonally to count
     * specific amount of eaten food by travelling orthogonally
     * @param dir - current direction
     * @param entityPosition - coordinates of the ant
     * @param color - color of the anr, which will be necessary for
     *              case with facing other insects
     * @param boardData - our board
     * @param boardSize - size of our board
     * @return amount of eaten food
     */
    @Override
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                  Map<String, BoardEntity> boardData, int boardSize) {
        int x = this.entityPosition.getX();
        int y = this.entityPosition.getY();

        int currentScore = 0;

        int dx = 0;
        int dy = 0;

        switch (dir) {
            case N:
                dy = 0;
                dx = -1;
                break;
            case E:
                dy = 1;
                dx = 0;
                break;
            case S:
                dy = 0;
                dx = 1;
                break;
            case W:
                dy = -1;
                dx = 0;
                break;
            default:
                break;
        }

        y += dy;
        x += dx;
        while (y >= 1 && x >= 1 && y <= boardSize && x <= boardSize) {
            BoardEntity entity = boardData.get(x + "_" + y);

            if (entity == null) {
                y += dy;
                x += dx;
                continue;
            }

            if (entity.getClass() == FoodPoint.class) {
                FoodPoint foodPoint = (FoodPoint) entity;

                currentScore += foodPoint.value;
                boardData.remove(x + "_" + y);
            }

            if (entity instanceof Insect) {
                Insect insect = (Insect) entity;

                if (this.color != insect.color) {
                    break;
                }
            }

            y += dy;
            x += dx;
        }

        x = this.entityPosition.getX();
        y = this.entityPosition.getY();
        boardData.remove(x + "_" + y);

        return currentScore;
    }

    /**
     * function getDiagonalDirectionVisibleValue to
     * imitate moving orthogonally for ant
     * @param dir - current direction
     * @param entityPosition - coordinates of the ant
     * @param boardData - our board
     * @param boardSize - size of our board
     * @return - direction to choose
     */
    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                Map<String, BoardEntity> boardData, int boardSize) {
        int x = this.entityPosition.getX();
        int y = this.entityPosition.getY();

        int currentScore = 0;

        int dx = 0;
        int dy = 0;

        switch (dir) {
            case NE:
                dy = 1;
                dx = -1;
                break;
            case SE:
                dy = 1;
                dx = 1;
                break;
            case SW:
                dy = -1;
                dx = 1;
                break;
            case NW:
                dy = -1;
                dx = -1;
                break;
            default:
                break;
        }

        y += dy;
        x += dx;
        while (y >= 1 && x >= 1 && y <= boardSize && x <= boardSize) {
            BoardEntity entity = boardData.get(x + "_" + y);

            if (entity == null) {
                y += dy;
                x += dx;
                continue;
            }

            if (entity.getClass() == FoodPoint.class) {
                FoodPoint foodPoint = (FoodPoint) entity;

                currentScore += foodPoint.value;
            }

            y += dy;
            x += dx;
        }

        return currentScore;
    }

    /**
     * here we override method travelDiagonally to count
     * specific amount of eaten food by travelling orthogonally
     * @param dir - current direction
     * @param entityPosition - coordinates of the ant
     * @param color - color of the ant
     * @param boardData - our board
     * @param boardSize - size of our board
     * @return amount of eaten food
     */
    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                Map<String, BoardEntity> boardData, int boardSize) {
        int x = this.entityPosition.getX();
        int y = this.entityPosition.getY();

        int currentScore = 0;

        int dx = 0;
        int dy = 0;

        switch (dir) {
            case NE:
                dy = 1;
                dx = -1;
                break;
            case SE:
                dy = 1;
                dx = 1;
                break;
            case SW:
                dy = -1;
                dx = 1;
                break;
            case NW:
                dy = -1;
                dx = -1;
                break;
            default:
                break;
        }

        y += dy;
        x += dx;
        while (y >= 1 && x >= 1 && y <= boardSize && x <= boardSize) {
            BoardEntity entity = boardData.get(x + "_" + y);

            if (entity == null) {
                y += dy;
                x += dx;
                continue;
            }

            if (entity.getClass() == FoodPoint.class) {
                FoodPoint foodPoint = (FoodPoint) entity;

                currentScore += foodPoint.value;
                boardData.remove(x + "_" + y);
            }

            if (entity instanceof Insect) {
                Insect insect = (Insect) entity;

                if (this.color != insect.color) {
                    break;
                }
            }

            y += dy;
            x += dx;
        }

        x = this.entityPosition.getX();
        y = this.entityPosition.getY();
        boardData.remove(x + "_" + y);

        return currentScore;
    }
}

/**
 * class Spider represent one of 4 types of Insects
 */
class Spider extends Insect implements DiagonalMoving {
    /**
     * public Spider is a constructor to create new insects of Spider type
     * @param entityPosition - coordinates of a new spider
     * @param color - color of a new spider
     */
    public Spider(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }

    /**
     * function getBestDirection is used to choose the best direction based
     * on maximum possible amount os eaten food
     * @param boardData - our board, where we locate all food and insects
     * @param boardSize - size of our board
     * @return
     */
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        Direction bestDirection = Direction.NE;
        int maxScore = 0;

        int result1 = getDiagonalDirectionVisibleValue(Direction.NW, null, boardData, boardSize);
        int result2 = getDiagonalDirectionVisibleValue(Direction.NE, null, boardData, boardSize);
        int result3 = getDiagonalDirectionVisibleValue(Direction.SW, null, boardData, boardSize);
        int result4 = getDiagonalDirectionVisibleValue(Direction.SE, null, boardData, boardSize);

        if (result2 > maxScore) {
            maxScore = result2;
            bestDirection = Direction.NE;
        }
        if (result4 > maxScore) {
            maxScore = result4;
            bestDirection = Direction.SE;
        }
        if (result3 > maxScore) {
            maxScore = result3;
            bestDirection = Direction.SW;
        }
        if (result1 > maxScore) {
            maxScore = result1;
            bestDirection = Direction.NW;
        }

        return bestDirection;
    }

    /**
     * function travelDirection is used to count amount of eaten food on the chosen direction
     * @param dir chosen direction
     * @param boardData our board
     * @param boardSize size of our board
     * @return
     */
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        int x = this.entityPosition.getX();
        int y = this.entityPosition.getY();

        int currentScore = 0;

        int dx = 0;
        int dy = 0;

        switch (dir) {
            case NE:
                dy = 1;
                dx = -1;
                break;
            case SE:
                dy = 1;
                dx = 1;
                break;
            case SW:
                dy = -1;
                dx = 1;
                break;
            case NW:
                dy = -1;
                dx = -1;
                break;
            default:
                break;
        }

        y += dy;
        x += dx;
        while (y >= 1 && x >= 1 && y <= boardSize && x <= boardSize) {
            BoardEntity entity = boardData.get(x + "_" + y);

            if (entity == null) {
                y += dy;
                x += dx;
                continue;
            }

            if (entity.getClass() == FoodPoint.class) {
                FoodPoint foodPoint = (FoodPoint) entity;

                currentScore += foodPoint.value;
                boardData.remove(x + "_" + y);
            }

            if (entity instanceof Insect) {
                Insect insect = (Insect) entity;

                if (this.color != insect.color) {
                    break;
                }
            }

            y += dy;
            x += dx;
        }

        x = this.entityPosition.getX();
        y = this.entityPosition.getY();
        boardData.remove(x + "_" + y);

        return currentScore;
    }

    /**
     * here we override SPIDER to String "Spider"
     * @return String "Spider"
     */
    @Override
    public String toString() {
        return "Spider";
    }

    /**
     * function getOrthogonalDirectionVisibleValue to
     * imitate moving orthogonally for spider
     * @param dir - current direction
     * @param entityPosition - coordinates of the spider
     * @param boardData - our board
     * @param boardSize - size of our board
     * @return amount of eaten food
     */
    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                Map<String, BoardEntity> boardData, int boardSize) {
        int x = this.entityPosition.getX();
        int y = this.entityPosition.getY();

        int currentScore = 0;

        int dx = 0;
        int dy = 0;

        switch (dir) {
            case NE:
                dy = 1;
                dx = -1;
                break;
            case SE:
                dy = 1;
                dx = 1;
                break;
            case SW:
                dy = -1;
                dx = 1;
                break;
            case NW:
                dy = -1;
                dx = -1;
                break;
            default:
                break;
        }

        y += dy;
        x += dx;
        while (y >= 1 && x >= 1 && y <= boardSize && x <= boardSize) {
            BoardEntity entity = boardData.get(x + "_" + y);

            if (entity == null) {
                y += dy;
                x += dx;
                continue;
            }

            if (entity.getClass() == FoodPoint.class) {
                FoodPoint foodPoint = (FoodPoint) entity;

                currentScore += foodPoint.value;
            }

            y += dy;
            x += dx;
        }

        return currentScore;
    }

    /**
     * here we override method travelOrthogonally to count
     * specific amount of eaten food by travelling diagonally
     * @param dir - current direction
     * @param entityPosition - coordinates of the spider
     * @param color - color of the spider
     * @param boardData - our board
     * @param boardSize - size of our board
     * @return amount of eaten food
     */
    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                Map<String, BoardEntity> boardData, int boardSize) {
        int x = this.entityPosition.getX();
        int y = this.entityPosition.getY();

        int currentScore = 0;

        int dx = 0;
        int dy = 0;

        switch (dir) {
            case NE:
                dy = 1;
                dx = -1;
                break;
            case SE:
                dy = 1;
                dx = 1;
                break;
            case SW:
                dy = -1;
                dx = 1;
                break;
            case NW:
                dy = -1;
                dx = -1;
                break;
            default:
                break;
        }

        y += dy;
        x += dx;
        while (y >= 1 && x >= 1 && y <= boardSize && x <= boardSize) {
            BoardEntity entity = boardData.get(x + "_" + y);

            if (entity == null) {
                y += dy;
                x += dx;
                continue;
            }

            if (entity.getClass() == FoodPoint.class) {
                FoodPoint foodPoint = (FoodPoint) entity;

                currentScore += foodPoint.value;
                boardData.remove(x + "_" + y);
            }

            if (entity instanceof Insect) {
                Insect insect = (Insect) entity;

                if (this.color != insect.color) {
                    break;
                }
            }

            y += dy;
            x += dx;
        }

        x = this.entityPosition.getX();
        y = this.entityPosition.getY();
        boardData.remove(x + "_" + y);

        return currentScore;
    }
}

/**
 * class Grasshopper represent one of 4 types of Insects
 */
class Grasshopper extends Insect {

    /**
     * here we override GRASSHOPPER to String "Spider"
     * @return String "Grasshopper"
     */
    @Override
    public String toString() {
        return "Grasshopper";
    }

    /**
     * public Spider is a constructor to create new insects of Spider type
     * @param entityPosition - coordinates of a new spider
     * @param color - color of a new spider
     */
    public Grasshopper(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }

    /**
     * function tryDirection is used to choose the best direction for a grasshopper
     * @param direction - current direction
     * @param dx - how we'll change location of a grasshopper on Ox axis
     * @param dy - how we'll change location of a grasshopper on Oy axis
     * @param boardData - our board
     * @param boardSize - size of our board
     * @return amount of eaten food on this direction
     */
    private int tryDirection(Direction direction, int dx, int dy, Map<String, BoardEntity> boardData, int boardSize) {
        int x = this.entityPosition.getX();
        int y = this.entityPosition.getY();

        int currentScore = 0;

        y += dy;
        x += dx;
        while (y >= 1 && x >= 1 && y <= boardSize && x <= boardSize) {
            BoardEntity entity = boardData.get(x + "_" + y);

            if (entity == null) {
                y += dy;
                x += dx;
                continue;
            }

            if (entity.getClass() == FoodPoint.class) {
                FoodPoint foodPoint = (FoodPoint) entity;

                currentScore += foodPoint.value;
            }

            y += dy;
            x += dx;
        }

        return currentScore;
    }

    /**
     * in function getBestDirection we choose the best direction according to the maximum possible amount of eaten food
     * @param boardData - our board, where we locate all food and insects
     * @param boardSize - size of our board
     * @return
     */
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        Direction bestDirection = Direction.N;
        int maxScore = 0;
        final int km2 = -2;
        int westResult = tryDirection(Direction.W, 0, km2, boardData, boardSize);
        int eastResult = tryDirection(Direction.E, 0, 2, boardData, boardSize);
        int northResult = tryDirection(Direction.N, km2, 0, boardData, boardSize);
        int southResult = tryDirection(Direction.S, 2, 0, boardData, boardSize);

        if (northResult > maxScore) {
            maxScore = northResult;
            bestDirection = Direction.N;
        }
        if (eastResult > maxScore) {
            maxScore = eastResult;
            bestDirection = Direction.E;
        }
        if (southResult > maxScore) {
            maxScore = southResult;
            bestDirection = Direction.S;
        }
        if (westResult > maxScore) {
            maxScore = westResult;
            bestDirection = Direction.W;
        }

        return bestDirection;
    }

    /**
     * function travelDirection is used to count amount of eaten food on the chosen direction
     * @param dir chosen direction
     * @param boardData our board
     * @param boardSize size of our board
     * @return amount of eaten food
     */
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        int x = this.entityPosition.getX();
        int y = this.entityPosition.getY();

        int currentScore = 0;

        int dx = 0;
        int dy = 0;
        final int km2 = -2;

        switch (dir) {
            case E:
                dy = 2;
                dx = 0;
                break;
            case W:
                dy = km2;
                dx = 0;
                break;
            case N:
                dy = 0;
                dx = km2;
                break;
            case S:
                dy = 0;
                dx = 2;
                break;
            default:
                break;
        }

        y += dy;
        x += dx;
        while (y >= 1 && x >= 1 && y <= boardSize && x <= boardSize) {
            BoardEntity entity = boardData.get(x + "_" + y);

            if (entity == null) {
                y += dy;
                x += dx;
                continue;
            }

            if (entity.getClass() == FoodPoint.class) {
                FoodPoint foodPoint = (FoodPoint) entity;

                currentScore += foodPoint.value;
                boardData.remove(x + "_" + y);
            }

            if (entity instanceof Insect) {
                Insect insect = (Insect) entity;

                if (this.color != insect.color) {
                    break;
                }
            }

            y += dy;
            x += dx;
        }

        x = this.entityPosition.getX();
        y = this.entityPosition.getY();
        boardData.remove(x + "_" + y);

        return currentScore;
    }
}

/**
 * interface OrthogonalMoving might be used to imitate interface orthogonal moving of different insects
 */
interface OrthogonalMoving {
    /**
     * function getOrthogonalDirectionVisibleValue might
     * be used to count possible amount of food which will be eaten
     * in case following one of orthogonal directions
     * @param dir - current direction
     * @param entityPosition - coordinates of an insect
     * @param boarData - our board
     * @param boardSize - size of our board
     * @return amount of eaten food
     */
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<String, BoardEntity> boarData, int boardSize);

    /**
     * with the help of a function travelOrthogonally we might count amount of eaten
     * food while following chosen direction
     * @param dir - current direction
     * @param entityPosition - coordinates of an insect
     * @param color - color of an insect
     * @param boarData - our board
     * @param boardSize - size of our board
     * @return amount of eaten food
     */
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                  Map<String, BoardEntity> boarData, int boardSize);
}

/**
 * interface DiagonalMoving might be used to imitate interface diagonal moving of different insects
 */
interface DiagonalMoving {
    /**
     * function getDiagonalDirectionVisibleValue might
     * be used to count possible amount of food which will be eaten
     * in case following one of diagonal directions
     * @param dir - current direction
     * @param entityPosition - coordinates of an insect
     * @param boarData - our board
     * @param boardSize - size of our board
     * @return amount of eaten food
     */
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                Map<String, BoardEntity> boarData, int boardSize);

    /**
     * with the help of a function travelDiagonally we might count amount of eaten
     * food while following chosen direction
     * @param dir - current direction
     * @param entityPosition - coordinates of an insect
     * @param color - color of an insect
     * @param boarData - our board
     * @param boardSize - size of our board
     * @return amount of eaten food
     */
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                Map<String, BoardEntity> boarData, int boardSize);
}

/**
 * class Board represent our board, where we allocate all our insects and foodPoints
 */
class Board {
    /**
     * we store data on board in a Map</> vision
     * @return boardData which is a Map of all our entities
     */
    public Map<String, BoardEntity> getBoardData() {
        return boardData;
    }

    /**
     * here we create new Map
     */
    private Map<String, BoardEntity> boardData = new HashMap<>();
    private int size;

    /**
     * it is a constructor of a new board
     * @param boardSize - we create a board of a given size
     */
    public Board(int boardSize) {
        this.size = boardSize;
    }

    /**
     * with the help of this function we can add a new entity to the boardData
     * @param s - is a key for all entities
     * @param entity - coordinates of this entity
     */
    public void addEntity(String s, BoardEntity entity) {
        boardData.put(s, entity);
    }

    /**
     * it's a getter to get information about private entities
     * @param position - location on the board
     * @return null
     */
    public BoardEntity getEntity(EntityPosition position) {
        return null;
    }

    /**
     * it's a getter to get information about private entities
     * @param insect - insect, which direction we want to get
     * @return null
     */
    public Direction getDirection(Insect insect) {
        return null;
    }

    /**
     * here we count sum of eaten food on a direction
     * @param insect - insect foe which we use this function
     * @return 0
     */
    public int getDirectionSum(Insect insect) {
        return 0;
    }
}

/**
 * this class represent an exception for Invalid board size
 */
class InvalidBoardSizeException extends Exception {
    /**
     * call message
     * @return message about mistake
     */
    public String getMessage() {
        return "Invalid board size";
    }
}

/**
 * this class represent an exception for Invalid number of insects
 */
class InvalidNumberOfInsectsException extends Exception {
    /**
     * call message
     * @return message about mistake
     */
    public String getMessage() {
        return "Invalid number of insects";
    }
}

/**
 * this class represent an exception for Invalid number of food points
 */
class InvalidNumberOfFoodPointsException extends Exception {
    /**
     * call message
     * @return message about mistake
     */
    public String getMessage() {
        return "Invalid number of food points";
    }
}

/**
 * this class represent an exception for Invalid insect color
 */
class InvalidInsectColorException extends Exception {
    /**
     * call message
     * @return message about mistake
     */
    public String getMessage() {
        return "Invalid insect color";
    }
}

/**
 * this class represent an exception for Invalid insect type
 */
class InvalidInsectTypeException extends Exception {
    /**
     * call message
     * @return message about mistake
     */
    public String getMessage() {
        return "Invalid insect type";
    }
}

/**
 * this class represent an exception for Invalid entity position
 */
class InvalidEntityPositionException extends Exception {
    /**
     * call message
     * @return message about mistake
     */
    public String getMessage() {
        return "Invalid entity position";
    }
}

/**
 * this class represent an exception for case with Duplicate insects
 */
class DuplicateInsectException extends Exception {
    /**
     * call message
     * @return message about mistake
     */
    public String getMessage() {
        return "Duplicate insects";
    }
}

/**
 * this class represent an exception for situation when Two entities in the same position
 */
class TwoEntitiesOnSamePositionException extends Exception {
    /**
     * call message
     * @return message about mistake
     */
    public String getMessage() {
        return "Two entities in the same position";
    }
}