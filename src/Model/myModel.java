
package Model;

    import Client.Client;
    import IO.MyDecompressorInputStream;
    import Client.*;
    import Server.*;
    import algorithms.mazeGenerators.Maze;
    import algorithms.mazeGenerators.MyMazeGenerator;
    import algorithms.search.AState;
    import algorithms.search.Solution;
    import javafx.scene.input.KeyCode;

    import java.awt.*;
    import java.awt.event.KeyEvent;
    import java.io.*;
    import java.net.InetAddress;
    import java.net.UnknownHostException;
    import java.util.ArrayList;
    import java.util.Observable;
    import java.util.Random;
    import java.util.concurrent.ExecutorService;
    import java.util.concurrent.Executors;
    import java.util.concurrent.ThreadPoolExecutor;

public class myModel extends Observable implements IModel {

    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;
    private Maze maze;
    private Solution solution;
    private int characterPositionRow ;
    private int characterPositionColumn ;


    public myModel(){
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
    }

    public void startServers() {
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();
    }

    public void stopServersAndThreadPool() {
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
        threadPool.shutdown();
    }

    public void generateMaze(int width, int height) {
        //Generate maze
        threadPool.execute(() -> {
            generateRandomMaze(width,height);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setChanged();
            notifyObservers("New maze was generated!");
        });
    }

    public void generateRandomMaze(int width, int height) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{width, height};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[height*width +12 ]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressed Maze with bytes
                        maze = new Maze(decompressedMaze);
                        //update start position
                        characterPositionRow = maze.getStartPosition().getRowIndex();
                        characterPositionColumn = maze.getStartPosition().getColumnIndex();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void hideSolution(){

    }

    public void solveMaze(){
        //solve maze
        threadPool.execute(() -> {
            solveMazeConcurrently();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
    public void solveMazeConcurrently(){
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        solution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        //Print Maze Solution retrieved from the server
                        //print of solution steps should be removed!!!
//                        System.out.println(String.format("Solution steps: %s", solution));
//                        ArrayList<AState> mazeSolutionSteps = solution.getSolutionPath();
//                        for (int i = 0; i < mazeSolutionSteps.size(); i++) {
//                            System.out.println(String.format("%s. %s", i,
//                                    mazeSolutionSteps.get(i).toString()));
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
            setChanged();
            notifyObservers("Solved maze!");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }



//    //notice that we changed it from int[][] as the value return- maybe will cause damage!
//    // before was: private int[][] generateRandomMaze
//    private void generateRandomMaze(int width, int height) {
//
//    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    public Solution getSolution() {return solution;}

    public boolean isMovementValid(int row,int column){
        return maze.isPositionInMazeBoundaries(row,column) && maze.getPositionValue(row,column)==0;
    }

    @Override
    public void moveCharacter(KeyCode movement) {
        switch (movement) {
            case UP :
            case NUMPAD8 : //up
                if(isMovementValid(characterPositionRow-1,characterPositionColumn))
                    characterPositionRow--;
                break;
            case DOWN:
            case NUMPAD2://down
                if(isMovementValid(characterPositionRow+1,characterPositionColumn))
                    characterPositionRow++;
                break;
            case RIGHT:
            case NUMPAD6://right
                if(isMovementValid(characterPositionRow,characterPositionColumn+1))
                    characterPositionColumn++;
                break;
            case LEFT:
            case NUMPAD4://left
                if(isMovementValid(characterPositionRow,characterPositionColumn-1))
                    characterPositionColumn--;
                break;
            case NUMPAD9://up-right
                if(isMovementValid(characterPositionRow-1,characterPositionColumn+1) &&
                        (isMovementValid(characterPositionRow-1,characterPositionColumn) ||
                        isMovementValid(characterPositionRow,characterPositionColumn+1))) {
                    characterPositionRow--;
                    characterPositionColumn++;
                }
                break;
            case NUMPAD3://down-right
                if(isMovementValid(characterPositionRow+1,characterPositionColumn+1) &&
                        (isMovementValid(characterPositionRow+1,characterPositionColumn) ||
                                isMovementValid(characterPositionRow,characterPositionColumn+1))) {
                    characterPositionRow++;
                    characterPositionColumn++;
                }
                break;
            case NUMPAD1://down-left
                if(isMovementValid(characterPositionRow+1,characterPositionColumn-1) &&
                        (isMovementValid(characterPositionRow,characterPositionColumn-1) ||
                                isMovementValid(characterPositionRow+1,characterPositionColumn))) {
                    characterPositionRow++;
                    characterPositionColumn--;
                }
                break;
            case NUMPAD7://up-left
                if(isMovementValid(characterPositionRow-1,characterPositionColumn-1) &&
                        (isMovementValid(characterPositionRow-1,characterPositionColumn) ||
                                isMovementValid(characterPositionRow,characterPositionColumn-1))) {
                    characterPositionRow--;
                    characterPositionColumn--;
                }
                break;
        }
        setChanged();
        if(characterPositionRow == maze.getGoalPosition().getRowIndex() &&
                characterPositionColumn == maze.getGoalPosition().getColumnIndex())
            notifyObservers("Character reached to end");
        else
            notifyObservers("Character moved!");
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    public void save(File file){
        try{
            ObjectOutputStream writeToFile = new ObjectOutputStream(new FileOutputStream(file));
            writeToFile.writeObject(maze);
            writeToFile.writeObject(characterPositionRow);
            writeToFile.writeObject(characterPositionColumn);
            // writeToFile.flush();
            writeToFile.close();
        }catch (Exception e){

        }
    }

    public void load (File file){
        if (!file.getName().endsWith(".maze")){
            setChanged();
            //not dealing with this because user cant choose non - maze files
            notifyObservers("Could not open maze!\nFile with unknown extension was chosen.");
        }
        try{
            ObjectInputStream readFromFile = new ObjectInputStream(new FileInputStream(file));
            maze = (Maze) readFromFile.readObject();
            characterPositionRow = (int) readFromFile.readObject();
            characterPositionColumn = (int) readFromFile.readObject();
            setChanged();
            notifyObservers("New maze was loaded!");
        }catch (Exception e){

        }
    }
}
