package byog.Phase1;

import byog.Core.Game;
import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import byog.lab5.Position;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MapGenerator {
       public static int width;
       public static int height;
       private static Random RANDOM;
       private static Random RANDOM2;
       private static final TERenderer ter = new TERenderer();
       public static TETile[][] Tiles;
       private static int[][] isFull; /*0 for nothing,1 for floors and 2 for walls*/
       private static List<Position> ListOfFloor=new ArrayList<>();
       private static List<Position> FloorToBeAdded=new ArrayList<>();

       public MapGenerator(int WIDTH, int HEIGHT,long seed){
          width=WIDTH;
          height=HEIGHT;
          Tiles=new TETile[WIDTH][HEIGHT];
          isFull=new int[WIDTH][HEIGHT];
          RANDOM=new Random(seed);
          RANDOM2=new Random(seed);
       }

       private static void RenderInitial(){
           ter.initialize(width,height);
       }

       private static void TilesClear(){
           for(int i=0;i<width;i+=1){
               for(int j=0;j<height;j+=1){
                   Tiles[i][j]=Tileset.SAND;
                   isFull[i][j]=0;
               }
           }
       }

       private static boolean isAnything(Position p){
           return isFull[p.px][p.py]>0;
       }

       private static boolean isOnBoundary(Position p){
           return p.px * p.py == 0 || p.px == width - 1 || p.py == height - 1;
       }

       private static boolean insert(Position p,TETile t){
           /**Return True if the insert is correct and insert the tile
            * otherwise return False*/
           if(p.px<0||p.px>=width||p.py<0||p.py>=height) return false;
           if(isAnything(p)){
               return false;
           }
           Tiles[p.px][p.py]=t;
           if(t==Tileset.FLOOR)isFull[p.px][p.py]=1;
           if(t==Tileset.WALL)isFull[p.px][p.py]=2;
           return true;
       }

       private static void RandomFloorExpand(Position p) {
           int direction = RANDOM.nextInt(10);
           Position ExpandPositon = new Position(0, 0);

           if (direction < 4) {
               ExpandPositon.px = p.px - 1;
               ExpandPositon.py = p.py;
           } else if (direction < 5) {
               ExpandPositon.px = p.px;
               ExpandPositon.py = p.py + 1;
           } else if (direction < 9){
               ExpandPositon.px = p.px + 1;
               ExpandPositon.py = p.py;
           }else {
               ExpandPositon.px = p.px;
               ExpandPositon.py = p.py - 1;
           }

           if(!isOnBoundary(ExpandPositon)){
               boolean jud=insert(ExpandPositon,Tileset.FLOOR);
               if(jud){
                   FloorToBeAdded.add(ExpandPositon);
                   ExpandPositon.timesofvisit=0;
               }
           }
           p.timesofvisit+=1;
       }

       private static void BuildWalls(Position p){
           insert(new Position(p.px-1,p.py),Tileset.WALL);
           insert(new Position(p.px-1,p.py+1),Tileset.WALL);
           insert(new Position(p.px,p.py+1),Tileset.WALL);
           insert(new Position(p.px+1,p.py+1),Tileset.WALL);
           insert(new Position(p.px+1,p.py),Tileset.WALL);
           insert(new Position(p.px+1,p.py-1),Tileset.WALL);
           insert(new Position(p.px,p.py-1),Tileset.WALL);
           insert(new Position(p.px-1,p.py-1),Tileset.WALL);
       }

       public static void Skeleton(){
           for(int i=0;i<width/2;i++){
               Position beginpoint1=new Position(width/4+i,height/2);
               Tiles[width/4+i][height/2]=Tileset.FLOOR;
               isFull[width/4+i][height/2]=1;
               ListOfFloor.add(beginpoint1);
           }
           for(int i=0;i<height/2;i++){
               Position beginpoint1=new Position(width/2,height/4+i);
               Tiles[width/2][height/4+i]=Tileset.FLOOR;
               isFull[width/2][height/4+i]=1;
               ListOfFloor.add(beginpoint1);
           }
       }

       public static void EndCleaning(){
           for(int i=0;i<width;i+=1){
               for(int j=0;j<height;j+=1){
                   isFull[i][j]=0;
               }
           }
           ListOfFloor.clear();
       }

       public void TileArrayProcessing(){
           TilesClear();
           Skeleton();
           for(int i=0;i<width*height/8;i++){
               for(Position p:ListOfFloor){
                   if(RANDOM2.nextInt(100)>30&&p.timesofvisit<2){
                       RandomFloorExpand(p);
                   }
               }
               ListOfFloor.addAll(FloorToBeAdded);
               FloorToBeAdded.clear();
           }

           for(Position p:ListOfFloor){
               BuildWalls(p);
           }
       }
       public void TilesProcessing(){
           TileArrayProcessing();
           EndCleaning();
       }

       public void GenerateMap(){
           RenderInitial();
           TileArrayProcessing();
           ter.renderFrame(Tiles);
           EndCleaning();
       }
}
