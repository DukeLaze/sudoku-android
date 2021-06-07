package com.example.oving8.internal;

import android.content.Context;
import android.util.Log;

import com.example.oving8.datatypes.Difficulty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;

public class FileIO {
    Context context;
    String rootFolder;
    String easyFolder;
    String mediumFolder;
    String hardFolder;

    public FileIO(Context context){
        this.context = context;
        rootFolder = context.getFilesDir().getPath();
        easyFolder = "boards/easy";
        mediumFolder = "boards/medium";
        hardFolder = "boards/hard";
        init();
        Log.d("MDT", "FileIO: easyFolder path: " + easyFolder);
    }
    void init(){
        try{
            File appDirectory = context.getFilesDir();
            if(!appDirectory.isDirectory())
            {
                Log.d("MDT", "getBoard: appDirectory not directory.");
            }
            File easyDir = new File(appDirectory+easyFolder);
            File mediumDir = new File(appDirectory+mediumFolder);
            File hardDir = new File(appDirectory+hardFolder);

            if(!easyDir.exists()){
                if(!easyDir.mkdirs()){
                    Log.d("MDT", "init: failed to create easyDir");
                }
            }

            if(!mediumDir.exists()){
                if(!mediumDir.mkdirs()){
                    Log.d("MDT", "init: failed to create mediumDir");
                }
            }

            if(!hardDir.exists()){
                if(!hardDir.mkdirs()){
                    Log.d("MDT", "init: failed to create hardDir");
                }
            }

            //If folders are empty, add sample boards.
            if(easyDir.listFiles() == null || easyDir.listFiles().length == 0){
                addSampleBoards();
            }
        }catch(Exception e){
            Log.e("MET", "getBoard: ", e);
        }
    }

    void addSampleBoards(){
        Log.d("MDT", "addSampleBoards: ");
        int[][] easy =
                {
                        {
                            1, 0, 0,
                            0, 0, 7,
                            0, 6, 0
                        },
                        {
                            0, 0, 0,
                            0, 4, 0,
                            0, 2, 8
                        },
                        {
                            7, 0, 2,
                            0, 0, 0,
                            0, 0, 3
                        },
                        {
                            6, 0, 1,
                            0, 5, 0,
                            0, 0, 0
                        },
                        {
                            3, 9, 0,
                            2, 0, 4,
                            0, 1, 7
                          },
                        {
                            0, 0, 0,
                            0, 3, 0,
                            2, 0, 5
                        },
                        {
                            3, 0, 0,
                            0, 0, 0,
                            4, 0, 5
                        },
                        {
                            6, 7, 0,
                            0, 5, 0,
                            0, 0, 0
                        },
                        {
                            0, 4, 0,
                            3, 0, 0,
                            0, 0, 7
                        }
                };
        int[][] medium =
                {
                        {
                            0, 5, 0,
                            0, 0, 1,
                            0, 4, 0
                        },
                        {
                            9, 4, 0,
                            0, 0, 8,
                            0, 5, 0
                        },
                        {
                            0, 0, 6,
                            0, 0, 4,
                            3, 8, 0
                        },
                        {
                            9, 2, 6,
                            0, 0, 0,
                            0, 3, 0
                        },
                        {
                            0, 0, 0,
                            0, 3, 0,
                            0, 0, 0
                        },
                        {
                            0, 4, 0,
                            0, 0, 0,
                            5, 6, 1
                        },
                        {
                            0, 8, 7,
                            2, 0, 0,
                            1, 0, 0
                        },
                        {
                            0, 9, 0,
                            8, 0, 0,
                            0, 7, 2
                        },
                        {
                            0, 1, 0,
                            6, 0, 0,
                            0, 9, 0
                        }
                };
        int[][] hard =
                {
                        {
                            1, 0, 0,
                            0, 0, 4,
                            8, 0, 0
                        },
                        {
                            0, 5, 0,
                            2, 6, 0,
                            3, 0, 9
                        },
                        {
                            6, 7, 0,
                            0, 0, 0,
                            2, 0, 0
                        },
                        {
                            7, 0, 0,
                            6, 0, 0,
                            0, 4, 8
                        },
                        {
                            0, 0, 0,
                            0, 0, 0,
                            0, 0, 0
                        },
                        {
                            9, 3, 0,
                            0, 0, 2,
                            0, 0, 6
                        },
                        {
                            0, 0, 1,
                            0, 0, 0,
                            0, 8, 9
                        },
                        {
                            9, 0, 7,
                            0, 8, 4,
                            0, 3, 0
                        },
                        {
                            0, 0, 5,
                            3, 0, 0,
                            0, 0, 7
                        }
                };
        saveBoard(Difficulty.EASY, easy);
        saveBoard(Difficulty.MEDIUM, medium);
        saveBoard(Difficulty.HARD, hard);
    }

    public int[][] getBoard(Difficulty difficulty){
        File appDirectory = context.getFilesDir();
        File targetDir = null;
        switch (difficulty){
            case EASY:
                targetDir = new File(appDirectory+easyFolder);
                break;
            case MEDIUM:
                targetDir = new File(appDirectory+mediumFolder);
                break;

            case HARD:
                targetDir = new File(appDirectory+hardFolder);
                break;
            default:
                Log.d("MDT", "getBoard: unknown difficulty");
        }
        try{
            File[] files = targetDir.listFiles();
            int fileCount = files.length;
            Random r = new Random();
            int toLoad = r.nextInt(fileCount);
            File f = files[toLoad];
            Log.d("MDT", "getBoard: " + f.getPath());
            FileInputStream fis = new FileInputStream(f);
            byte[] data = new byte[9*9*4]; //assumes 9x9 grid of ints (of size 4) was stored
            int amountRead = fis.read(data);
            if(amountRead != (9*9*4)){
                Log.d("MDT", "getBoard: didn't read entire board. Read: " + amountRead + " bytes.");
            }
            else{
                int[][] board = new int[9][9];
                for(int i = 0; i < 9; i++){
                    for(int j = 0; j < 9; j++){
                        for(int k = 0; k < 4; k++){
                            board[i][j] = (board[i][j] << 8) | data[(i*36)+(j*4)+k];
                        }
                        Log.d("MDT", "getBoard: " + board[i][j]);
                    }
                }
                return board;
            }

        }catch(Exception e){
            Log.e("MET", "getBoard: ", e);
        }
        return null;
    }

    public boolean saveBoard(Difficulty difficulty, int[][] board){
        File appDirectory = context.getFilesDir();
        File targetDir = null;
        switch (difficulty){
            case EASY:
                targetDir = new File(appDirectory+easyFolder);
                break;
            case MEDIUM:
                targetDir = new File(appDirectory+mediumFolder);
                break;

            case HARD:
                targetDir = new File(appDirectory+hardFolder);
                break;
            default:
                Log.d("MDT", "getBoard: unknown difficulty");
        }

        FileOutputStream fis = null;
        try{
            File[] files = targetDir.listFiles();
            int fileCount = 0;
            if(files != null){
                fileCount = files.length;
                Log.d("MDT", "saveBoard: files count: " + files.length);
            }
            File f = new File(targetDir, Integer.toString(fileCount));
            fis = new FileOutputStream(f);
            byte[] data = new byte[9*9*4]; //assumes 9x9 grid of ints (of size 4) was stored
            for(int i = 0; i < 9; i++){
                for(int j = 0; j < 9; j++){
                    for(int k = 0; k < 4; k++){
                        //store the int from left to right, >> 24, >> 16, >> 8 >> 0
                        Log.d("MDT", "saveBoard: " + ((i*36)+(j*4)+k));
                        data[(i*36)+(j*4)+k] = (byte)(board[i][j] >> ((3-k)*8));
                    }
                }
            }
            fis.write(data);
            if(fis != null){
                fis.close();
            }
            return true;
        }catch(Exception e){
            Log.e("MET", "getBoard: ", e);
        }
        return false;
    }
}
