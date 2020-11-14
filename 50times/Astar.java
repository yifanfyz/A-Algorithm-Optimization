//package Astar;
import java.lang.Math;
import java.util.*;
//import Astar.Point;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import java.io.FileWriter;
import java.nio.file.*;
import java.nio.charset.*;

class Point {

    private int x;
    private int y;
    private int value;
    private Point father;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public void setValue(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point getFather() {
        return father;
    }

    public void setFather(Point father) {
        this.father = father;
    }

    public int getValue() {
        return value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}


public class Astar {

    Point[][] map;
    Point Sstart;
    Point Sgoal;
    int[][]intmap; //added so map can be used for different algorithm

    Queue<Point> openQueue;
    Queue<Point> closedQueue;
    double[][] FList;
    double[][] GList;
    double[][] HList;
    double weight = 1; 

    public Astar(){
        this.openQueue = new LinkedList<Point>();
        this.closedQueue = new LinkedList<Point>();
        int[][] intmap = generate();
        this.intmap=intmap;

        this.map = new Point[intmap.length][intmap[0].length];
        for (int i = 0; i < this.map.length; i++) {
            for (int j = 0; j < this.map[0].length; j++) {
                this.map[i][j] = new Point(i, j, intmap[i][j]);
            }
        }
        this.FList = new double[intmap.length][intmap[0].length];
        this.GList = new double[intmap.length][intmap[0].length];
        this.HList = new double[intmap.length][intmap[0].length];
        for(int i=0; i<map.length; i++) {
            for(int j=0; j<map[0].length;j++) {
                FList[i][j] = Integer.MAX_VALUE;
                HList[i][j] = Integer.MAX_VALUE;
                GList[i][j] = Integer.MAX_VALUE;
            }
        }
        init();
    }

    public Astar(int[][] intmap, Double weight){
        this.openQueue = new LinkedList<Point>();
        this.closedQueue = new LinkedList<Point>();
        this.map = new Point[intmap.length][intmap[0].length];
        this.weight = weight;
        this.intmap = intmap;
        setInitial(this.intmap);
        for (int i = 0; i < this.map.length; i++) {
            for (int j = 0; j < this.map[0].length; j++) {
                this.map[i][j] = new Point(i, j, intmap[i][j]);
            }
        }
        this.FList = new double[intmap.length][intmap[0].length];
        this.GList = new double[intmap.length][intmap[0].length];
        this.HList = new double[intmap.length][intmap[0].length];
        for(int i=0; i<map.length; i++) {
            for(int j=0; j<map[0].length;j++) {
                FList[i][j] = Integer.MAX_VALUE;
                HList[i][j] = Integer.MAX_VALUE;
                GList[i][j] = Integer.MAX_VALUE;
            }
        }
        init();
    }
    public Astar(int[][] intmap, int sx, int sy, int gx, int gy, double weight){
    	this.weight = weight;
        this.openQueue = new LinkedList<Point>();
        this.closedQueue = new LinkedList<Point>();
        this.map = new Point[intmap.length][intmap[0].length];
        this.intmap = intmap;

        this.Sstart = new Point(sx, sy);
        this.Sgoal = new Point(gx, gy);
        for (int i = 0; i < this.map.length; i++) {
            for (int j = 0; j < this.map[0].length; j++) {
                this.map[i][j] = new Point(i, j, intmap[i][j]);
            }
        }
        this.FList = new double[intmap.length][intmap[0].length];
        this.GList = new double[intmap.length][intmap[0].length];
        this.HList = new double[intmap.length][intmap[0].length];
        for(int i=0; i<map.length; i++) {
            for(int j=0; j<map[0].length;j++) {
                FList[i][j] = Integer.MAX_VALUE;
                HList[i][j] = Integer.MAX_VALUE;
                GList[i][j] = Integer.MAX_VALUE;
            }
        }
        init();
    }

    private void init() {
        openQueue.offer(Sstart);
        int Sstart_x = Sstart.getX();
        int Sstart_y = Sstart.getY();
        int Sgoal_x = Sgoal.getX();
        int Sgoal_y = Sgoal.getY();

        this.GList[Sstart_x][Sstart_y] = 0;
        this.HList[Sstart_x][Sstart_y] = getDistance(Sstart_x, Sstart_y, Sgoal_x, Sgoal_y);
        this.FList[Sstart_x][Sstart_y] = GList[Sstart_x][Sstart_y] + HList[Sstart_x][Sstart_y];
    }


    public int [][]generate(){//task1

        int[][] map =new int [120][160];
        int [][] hardToTravreseZone=new int[8][2];
        for(int i=0;i<120;i++){     // 一开始都是1
            for(int j=0;j<160;j++){
                map[i][j]=1;
            }
        }

        Random random=new Random();
        int k=0;
        while(k<8){    //8 31*31, 50% blocked, 50% unblocked
            int row= random.nextInt(120);
            int col= random.nextInt(160);
            hardToTravreseZone[k][0]=row;
            hardToTravreseZone[k][1]=col;

            int rowstart=row-15<=0?0:row-15 ;
            int colstart=col-15<=0?0:col-15 ;

            int rowend=row+15<119?row+15:119;
            int colend=col+15<159?col+15:159;
            for(int i=rowstart;i<=rowend;i++){
                for( int j=colstart;j<=colend;j++){
                    int l=random.nextInt(2);    // set a 50% probability to be 2
                    if( l==1){
                        map[i][j]=2;
                    }
                }
            }
            k++;
        }
        map=setHighway(map);
        for(int i=0;i<120;i++){
            for(int j=0;j<159;j++){
                if(random.nextInt(5)==0&&map[i][j]!=3&&map[i][j]!=4)map[i][j]=0;
            }
        }

        setInitial(map);
        return map;

    }

    public int [][]setHighway(int [][]map){//task1
        int paths=0;
        HashSet<Integer>path1=new HashSet<>();
        HashSet<Integer>path2=new HashSet<>();
        HashSet<Integer>path3=new HashSet<>();
        HashSet<Integer>path4=new HashSet<>();

        while(paths<4){
            HashSet<Integer>curpath=new HashSet<>();

            Random random=new Random();
            int set=random.nextInt(4);
            int row= random.nextInt(120);
            int col= random.nextInt(160);
            boolean restart=false;
            boolean bondearyhit=false;
            int setsetcount=0;
            int varitaing=-1;
            int by=1;
            if(set==0){
                varitaing=0;
                row=0;
            }else if(set==1){
                col=0;
                varitaing=1;
            }else if(set==2){
                row=120;
                varitaing=0;
                by=-1;

            }else if(set==3){
                col=160;
                varitaing=1;
                by=-1;
            }
            int curloc=row*1000+col;

            boolean start=true;
            while(!restart&&!bondearyhit) {
                int headto=random.nextInt(5);
                if(start){
                    headto=0;start=false;
                    curpath.add(curloc);
                }
                if(headto<3) {}
                else if(headto==3) {varitaing= varitaing==1?0:1; }
                else if(headto==4){varitaing= varitaing==1?0:1; by*=-1; }
                int count=0;

                while(count<20&&!bondearyhit&&!restart){
                    if(varitaing==0&&by==1){
                        row++;
                    }else if(varitaing==0&&by==-1){
                        row--;
                    }else if(varitaing==1&&by==1){
                        col++;
                    }else {
                        col--;
                    }
                    curloc = row * 1000 + col;
                    curpath.add(curloc);
                    if(path1.contains(curloc)||path2.contains(curloc)||path3.contains(curloc)) {
                        restart=true;
                    }
                    if(row==0||col==0||row==119||col==159){
                        bondearyhit=true;
                    }
                    count++;
                }


            }
            if(restart){
                setsetcount++;
                if(setsetcount==10){
                    path1.clear();
                    path2.clear();
                    path3.clear();
                    path4.clear();
                }
            }else{
                if(curpath.size()<100){

                }else{
                    if(paths==0){
                        path1=curpath;
                    }else if(paths==1){
                        path2=curpath;
                    }else if(paths==2){
                        path3=curpath;
                    }else {
                        path4=curpath;
                    }
                    paths++;
                }


            }

        }
        for(int set:path1){
            int row=set/1000;
            int col=set%1000;
            map[row][col]= map[row][col]==1?3:4;
        }
        for(int set:path2){
            int row=set/1000;
            int col=set%1000;
            map[row][col]= map[row][col]==1?3:4;
        }
        for(int set:path3){
            int row=set/1000;
            int col=set%1000;
            map[row][col]= map[row][col]==1?3:4;
        }
        for(int set:path4){
            int row=set/1000;
            int col=set%1000;
            map[row][col]= map[row][col]==1?3:4;
        }

        return map;
    }

    public void printmap(){//task1
        for(int i=0;i<this.map.length;i++){
            String p = "";
            for(int j=0;j<this.map[0].length;j++){
                if(map[i][j].getValue() ==3){
                    p += "a ";
                }else if(map[i][j].getValue()==4){
                    p += "b ";
                }else {
                    p += (this.map[i][j].getValue()+" ");
                }

            }
            System.out.println(p);
        }

    }

    public Point getStart() {
        return Sstart;
    }

    public Point getGoal() {
        return Sgoal;
    }

    public void setInitial (int [][]intmap){//task1

        //set Sstart and Sgoal
        Random initial = new Random();

        while(true){

            int rowOfSstart = initial.nextInt(20);
            int colOfSstart = initial.nextInt(20);
            int rowOfSgoal = intmap.length - initial.nextInt(20)-1;
            int colOfSgoal = intmap[0].length - initial.nextInt(20)-1;
            int distance = getDistance(rowOfSstart,colOfSstart,rowOfSgoal,colOfSgoal );


            if (intmap[rowOfSstart][colOfSstart]!=0 && distance >=100){

                this.Sstart = new Point(rowOfSstart, colOfSstart);
                this.Sgoal= new Point(rowOfSgoal, colOfSgoal);

                //	System.out.println("Successfully set Start and Goal");
            //    System.out.println(Sstart.getX()+","+Sstart.getY());
             //   System.out.println(Sgoal.getX()+","+Sgoal.getY());

                break;
            }

        }

    }

    public int printPath() {
    //    System.out.println("================ printPath ================");
        Point father_point = null;
        char[][] result = new char[120][160];
        for (int i = 0; i < 120; i++) {
            for (int j = 0; j < 160; j++) {
                result[i][j] = '.';
            }
        }

        int step = 0;
        father_point = map[Sgoal.getX()][Sgoal.getY()];
        while (father_point != null) {
            if(father_point.equals(Sstart))
                result[father_point.getX()][father_point.getY()] = 'r';
            else if(father_point.equals(Sgoal)) {
                result[father_point.getX()][father_point.getY()] = 'a';
                step++;
            }
            else {
                result[father_point.getX()][father_point.getY()] = '*';
                step++;
            }
            father_point = father_point.getFather();
        }
        // 打印行走步数
        System.out.println("step is : " + step);
        return step;
        // for (int i = 0; i < 120; i++) {
        //     for (int j = 0; j < 160; j++) {
        //         System.out.print(result[i][j] + " ");
        //     }
        //     System.out.println();
        // }
    }

    public void start() {

        Point currentPoint;

        while((currentPoint = findShortestFPoint())!= null) {
            if (currentPoint.getX() == Sgoal.getX()
                    && currentPoint.getY() == Sgoal.getY())
                return;
            updateNeighborPoints(currentPoint, map);
        }
    }


    private Point findShortestFPoint() {
        Point currentPoint = null;
        Point shortestFPoint = null;
        double shortestFValue = Double.MAX_VALUE;

        Iterator<Point> it = openQueue.iterator();
        while (it.hasNext()) {
            currentPoint = it.next();
            if (FList[ currentPoint.getX()][currentPoint.getY()] <= shortestFValue) {
                shortestFPoint = currentPoint;
                shortestFValue = FList[ currentPoint.getX()][ currentPoint.getY()];
            }
        }

        if (shortestFValue != Integer.MAX_VALUE) {
            openQueue.remove(shortestFPoint);
            closedQueue.offer(shortestFPoint);
        }

        return shortestFPoint;
    }

    private int getDistance(int current_x, int current_y, int goal_x, int goal_y) {
        return Math.abs(current_x - goal_x)+ Math.abs(current_y - goal_y);
    }

    private void updateNeighborPoints(Point currentPoint, Point[][]map) {
        int current_x = (int) currentPoint.getX();
        int current_y = (int) currentPoint.getY();
        // 左
        if (checkPosValid(current_x, current_y - 1)) {
            updatePoint(map[current_x][current_y],
                    map[current_x][current_y - 1]);
        }
        // 右
        if (checkPosValid(current_x, current_y + 1)) {
            updatePoint(map[current_x][current_y],
                    map[current_x][current_y + 1]);
        }
        // 上
        if (checkPosValid( current_x - 1, current_y)) {
            updatePoint(map[current_x][current_y],
                    map[current_x - 1][current_y]);
        }
        //左上
        if (checkPosValid( current_x - 1, current_y-1)) {
            updatePoint(map[current_x][current_y],
                    map[current_x - 1][current_y-1]);
        }
        //右上
        if (checkPosValid( current_x - 1, current_y+1)) {
            updatePoint(map[current_x][current_y],
                    map[current_x - 1][current_y+1]);
        }
        // 下
        if (checkPosValid(current_x + 1, current_y)) {
            updatePoint(map[current_x][current_y],
                    map[current_x + 1][current_y]);
        }
        // 左下
        if (checkPosValid(current_x + 1, current_y-1)) {
            updatePoint(map[current_x][current_y],
                    map[current_x + 1][current_y-1]);
        }
        // 右下
        if (checkPosValid(current_x + 1, current_y+1)) {
            updatePoint(map[current_x][current_y],
                    map[current_x + 1][current_y+1]);
        }

    }


    public void uniformcostsearch(){ //uniform cost search
        //bascially G list
       // this.intmap=new int [160][120];
        this.GList[this.openQueue.peek().getX()][this.openQueue.peek().getY()]=0; //set start to 0
        this.openQueue=new PriorityQueue<Point>((n1,n2)->((int)(this.GList[n1.getX()][n1.getY()]-this.GList[n2.getX()][n2.getY()])));
        this.openQueue.offer(this.Sstart);
        //travel every simgle posibility from the start
        while(!this.openQueue.isEmpty()){
            Point temp=this.openQueue.poll();
            int current_x=temp.getX();
            int current_y=temp.getY();
            if(current_x==this.Sgoal.getX()&&current_y==this.Sgoal.getY())return;
            // 左
            if (checkPosValid(current_x, current_y - 1)) {
                updatePointforuniform(map[current_x][current_y],
                        map[current_x][current_y - 1]);
            }
            // 右
            if (checkPosValid(current_x, current_y + 1)) {
                updatePointforuniform(map[current_x][current_y],
                        map[current_x][current_y + 1]);
            }
            // 上
            if (checkPosValid( current_x - 1, current_y)) {
                updatePointforuniform(map[current_x][current_y],
                        map[current_x - 1][current_y]);
            }
            //左上
            if (checkPosValid( current_x - 1, current_y-1)) {
                updatePointforuniform(map[current_x][current_y],
                        map[current_x - 1][current_y-1]);
            }
            //右上
            if (checkPosValid( current_x - 1, current_y+1)) {
                updatePointforuniform(map[current_x][current_y],
                        map[current_x - 1][current_y+1]);
            }
            // 下
            if (checkPosValid(current_x + 1, current_y)) {
                updatePointforuniform(map[current_x][current_y],
                        map[current_x + 1][current_y]);
            }
            // 左下
            if (checkPosValid(current_x + 1, current_y-1)) {
                updatePointforuniform(map[current_x][current_y],
                        map[current_x + 1][current_y-1]);
            }
            // 右下
            if (checkPosValid(current_x + 1, current_y+1)) {
                updatePointforuniform(map[current_x][current_y],
                        map[current_x + 1][current_y+1]);
            }

        }








    }

    private void generatePic(String picName){
        BufferedImage pic0 = new BufferedImage(6, 6, BufferedImage.TYPE_INT_RGB);
        BufferedImage pic1 = new BufferedImage(6, 6, BufferedImage.TYPE_INT_RGB);
        BufferedImage pic2 = new BufferedImage(6, 6, BufferedImage.TYPE_INT_RGB);
        BufferedImage pic3 = new BufferedImage(6, 6, BufferedImage.TYPE_INT_RGB);
        BufferedImage pic4 = new BufferedImage(6, 6, BufferedImage.TYPE_INT_RGB);
        BufferedImage v = new BufferedImage(6, 6, BufferedImage.TYPE_INT_RGB);
        BufferedImage h = new BufferedImage(6, 6, BufferedImage.TYPE_INT_RGB);
        BufferedImage l2r = new BufferedImage(6, 6, BufferedImage.TYPE_INT_RGB);
        BufferedImage r2l = new BufferedImage(6, 6, BufferedImage.TYPE_INT_RGB);
        BufferedImage start = new BufferedImage(6, 6, BufferedImage.TYPE_INT_RGB);
        BufferedImage end = new BufferedImage(6, 6, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = new BufferedImage(map.length*6, map[0].length*6, BufferedImage.TYPE_INT_RGB);
        BufferedImage rev = new BufferedImage(map[0].length*6, map.length*6, BufferedImage.TYPE_INT_RGB);

        try {
            //通过ImageIO类读取文件
            pic0 = ImageIO.read(new FileInputStream("blocked.png"));
            pic1 = ImageIO.read(new FileInputStream("normal.png"));
            pic2 = ImageIO.read(new FileInputStream("hard2cross.png"));
            pic3 = ImageIO.read(new FileInputStream("highway.png"));
            pic4 = ImageIO.read(new FileInputStream("hardhighway.png"));
            v = ImageIO.read(new FileInputStream("vertical.png"));
            h = ImageIO.read(new FileInputStream("horizontal.png"));
            l2r = ImageIO.read(new FileInputStream("left2right.png"));
            r2l = ImageIO.read(new FileInputStream("right2left.png"));
            start = ImageIO.read(new FileInputStream("start.png"));
            end = ImageIO.read(new FileInputStream("end.png"));
            for (int i = 0; i < map.length; i ++) {
                for (int j = 0; j < map[0].length; j ++) {
                    if (this.map[i][j].getValue() == 0) {
                        for (int k = 0; k < 6; k ++) {
                            for (int l = 0; l < 6; l ++) {
                                result.setRGB(i*6+k,j*6+l,pic0.getRGB(k,l));
                            }
                        }
                    }else if(this.map[i][j].getValue() == 1){
                        for (int k = 0; k < 6; k ++) {
                            for (int l = 0; l < 6; l ++) {
                                result.setRGB(i*6+k,j*6+l,pic1.getRGB(k,l));
                            }
                        }
                    }else if(this.map[i][j].getValue() == 2){
                        for (int k = 0; k < 6; k ++) {
                            for (int l = 0; l < 6; l ++) {
                                result.setRGB(i*6+k,j*6+l,pic2.getRGB(k,l));
                            }
                        }
                    }else if(this.map[i][j].getValue() == 3){
                        for (int k = 0; k < 6; k ++) {
                            for (int l = 0; l < 6; l ++) {
                                result.setRGB(i*6+k,j*6+l,pic3.getRGB(k,l));
                            }
                        }
                    }else if(this.map[i][j].getValue() == 4){
                        for (int k = 0; k < 6; k ++) {
                            for (int l = 0; l < 6; l ++) {
                                result.setRGB(i*6+k,j*6+l,pic4.getRGB(k,l));
                            }
                        }
                    }else{
                        System.out.println("Unexpected Error at map["+i+"]["+j+"]");
                    }
                }
            }
            Point father_point = null;

            int step = 0;
            father_point = map[Sgoal.getX()][Sgoal.getY()];
            int temp_x = -1;
            int temp_y = -1;
            while (father_point != null) {
                if(father_point.equals(Sstart)){
                    for (int k = 0; k < 6; k ++) {
                        for (int l = 0; l < 6; l ++) {
                            if (start.getRGB(k,l) != 0) {
                                result.setRGB(father_point.getX()*6+k,father_point.getY()*6+l,start.getRGB(k,l));
                            }
                        }
                    }
                }else if(father_point.equals(Sgoal)) {
                    for (int k = 0; k < 6; k ++) {
                        for (int l = 0; l < 6; l ++) {
                            if (end.getRGB(k,l) != 0) {
                                result.setRGB(father_point.getX()*6+k,father_point.getY()*6+l,end.getRGB(k,l));
                            }
                        }
                    }
                    step++;
                }
                else {
                    //普通的点到点
                    if (temp_x != -1) {
                        //四个对角
                        if (temp_x != father_point.getX() && temp_y != father_point.getY()) {
                            if (temp_x < father_point.getX()) {
                                if (temp_y < father_point.getY()) {
                                    for (int k = 0; k < 6; k ++) {
                                        for (int l = 0; l < 6; l ++) {
                                            if (l2r.getRGB(k,l) != 0)
                                                result.setRGB(father_point.getX()*6+k-3,father_point.getY()*6+l-3,l2r.getRGB(k,l));
                                        }
                                    }
                                }
                                else{
                                    for (int k = 0; k < 6; k ++) {
                                        for (int l = 0; l < 6; l ++) {
                                            if (r2l.getRGB(k,l) != 0)
                                                result.setRGB(father_point.getX()*6+k-3,father_point.getY()*6+l+3,r2l.getRGB(k,l));
                                        }
                                    }
                                }
                            }else{
                                if (temp_y < father_point.getY()) {
                                    for (int k = 0; k < 6; k ++) {
                                        for (int l = 0; l < 6; l ++) {
                                            if (r2l.getRGB(k,l) != 0)
                                                result.setRGB(father_point.getX()*6+k+3,father_point.getY()*6+l-3,r2l.getRGB(k,l));
                                        }
                                    }
                                }else{
                                    for (int k = 0; k < 6; k ++) {
                                        for (int l = 0; l < 6; l ++) {
                                            if (l2r.getRGB(k,l) != 0)
                                                result.setRGB(father_point.getX()*6+k+3,father_point.getY()*6+l+3,l2r.getRGB(k,l));
                                        }
                                    }
                                }
                            }
                        }else{
                            //上下左右正四个方位
                            if (temp_x < father_point.getX()) {
                                for (int k = 0; k < 6; k ++) {
                                    for (int l = 0; l < 6; l ++) {
                                        if (h.getRGB(k,l) != 0)
                                            result.setRGB(father_point.getX()*6+k-3,father_point.getY()*6+l,h.getRGB(k,l));
                                    }
                                }
                            }else if(temp_x > father_point.getX()){
                                for (int k = 0; k < 6; k ++) {
                                    for (int l = 0; l < 6; l ++) {
                                        if (h.getRGB(k,l) != 0)
                                            result.setRGB(father_point.getX()*6+k+3,father_point.getY()*6+l,h.getRGB(k,l));
                                    }
                                }
                            }else if(temp_y < father_point.getY()){
                                for (int k = 0; k < 6; k ++) {
                                    for (int l = 0; l < 6; l ++) {
                                        if (v.getRGB(k,l) != 0)
                                            result.setRGB(father_point.getX()*6+k,father_point.getY()*6+l-3,v.getRGB(k,l));
                                    }
                                }
                            }else if(temp_y > father_point.getY()){
                                for (int k = 0; k < 6; k ++) {
                                    for (int l = 0; l < 6; l ++) {
                                        if (v.getRGB(k,l) != 0)
                                            result.setRGB(father_point.getX()*6+k,father_point.getY()*6+l+3,v.getRGB(k,l));
                                    }
                                }
                            }
                        }
                    }
                    step++;
                }
                for (int k = 0; k < 6; k ++) {
                    for (int l = 0; l < 6; l ++) {
                        if (start.getRGB(k,l) != 0)
                            result.setRGB(Sstart.getX()*6+k,Sstart.getY()*6+l,start.getRGB(k,l));
                    }
                }
                for (int k = 0; k < 6; k ++) {
                    for (int l = 0; l < 6; l ++) {
                        if (end.getRGB(k,l) != 0)
                            result.setRGB(Sgoal.getX()*6+k,Sgoal.getY()*6+l,end.getRGB(k,l));
                    }
                }
                temp_x = father_point.getX();
                temp_y = father_point.getY();
                father_point = father_point.getFather();
            }

            // 打印行走步数
            System.out.println("Total step is : " + step);
            for (int i = 0; i < map[0].length*6; i ++) {
                for (int j = 0; j < map.length*6; j ++) {
                    rev.setRGB(i,j,result.getRGB(j,i));
                }

            }
            File outputfile = new File(picName);
            ImageIO.write(rev, "png", outputfile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkPosValid( int x, int y) {
        // 检查x,y是否越界， 并且当前节点不是墙
        if ((x >= 0 && x < map.length) && (y >= 0 && y < map[0].length) && (map[x][y].getValue()!=0) ) {
            // 检查当前节点是否已在关闭队列中，若存在，则返回 "false"
            Iterator<Point> it = closedQueue.iterator();
            Point point = null;
            while (it.hasNext()) {
                if ((point = it.next()) != null) {
                    if (point.getX() == x && point.getY() == y)
                        return false;
                }
            }
            return true;
        }
        return false;
    }
    private void updatePointforuniform(Point lastPoint, Point currentPoint){
        int last_x = lastPoint.getX();
        int last_y = lastPoint.getY();
        int current_x = currentPoint.getX();
        int current_y = currentPoint.getY();
        double pre_g= 0;//历史遗留问题。。 别在意他的名字！！！
        int last = this.map[last_x][last_y].getValue();
        int current = this.map[current_x][current_y].getValue();
        boolean sp = true;// 如果是上下左右的情况下 true，否则false
        int mark = 0;//1 - 6 六种情况
        if (last_x == current_x || last_y == current_y) {//上下左右
            sp = true;
        }else{
            sp = false;
        }
        int sum = last + current;
        //这里处理的麻烦了一点。。如果map不是0-4存起来的话会好说很多QAQ
        switch(sum) {
            case 2:
                mark = 1;
                break;
            case 3:
                mark = 2;
                break;
            case 4:
                if (last == 2) {
                    mark = 3;
                }else{
                    mark = 1;
                }
                break;
            case 5:
                mark = 2;
                break;
            case 6:
                if (last == 3) {
                    mark = 4;
                }else{
                    mark = 3;
                }
                break;
            case 7:
                mark = 5;
                break;
            case 8:
                mark = 6;
                break;
            default:
                System.out.println("ERROR: wrong sum number! Check setValue()!");
        }
        switch(mark){
            case 1:
                if(sp){
                    pre_g ++;
                }else{
                    pre_g += Math.sqrt(2);
                }
                break;
            case 2:
                if(sp){
                    pre_g += 1.5;
                }else{
                    pre_g += (3*(Math.sqrt(2))/2);
                }
                break;
            case 3:
                if(sp){
                    pre_g += 2;
                }else{
                    pre_g += (2*Math.sqrt(2));
                }
                break;
            case 4:
                if(sp){
                    pre_g += 0.25;
                }else{
                    pre_g += Math.sqrt(8);
                }
                break;
            case 5:
                if(sp){
                    pre_g += 0.375;
                }else{
                    pre_g += (3*(Math.sqrt(2))/8);
                }
                break;
            case 6:
                if(sp){
                    pre_g += 0.5;
                }else{
                    pre_g += (Math.sqrt(8)/4);
                }
                break;
            default:
                System.out.println("Error: Wrong case!! Check calculation!");
        }

        // 起始节点到当前节点的距离
        double temp_g = this.GList[last_x][last_y] + pre_g;
        if(this.GList[current_x][current_y]>temp_g){
            this.GList[current_x][current_y]=temp_g;
            this.map[current_x][current_y].setFather(lastPoint);
            this.openQueue.offer(currentPoint);
        }


    }

    private void updatePoint(Point lastPoint, Point currentPoint) {
        int last_x = lastPoint.getX();
        int last_y = lastPoint.getY();
        int current_x = currentPoint.getX();
        int current_y = currentPoint.getY();
        double pre_g= 0;//历史遗留问题。。 别在意他的名字！！！
        int last = this.map[last_x][last_y].getValue();
        int current = this.map[current_x][current_y].getValue();
        boolean sp = true;// 如果是上下左右的情况下 true，否则false
        int mark = 0;//1 - 6 六种情况

        if (last_x == current_x || last_y == current_y) {//上下左右
            sp = true;
        }else{
            sp = false;
        }
        int sum = last + current;
        //这里处理的麻烦了一点。。如果map不是0-4存起来的话会好说很多QAQ
        switch(sum) {
            case 2:
                mark = 1;
                break;
            case 3:
                mark = 2;
                break;
            case 4:
                if (last == 2) {
                    mark = 3;
                }else{
                    mark = 1;
                }
                break;
            case 5:
                mark = 2;
                break;
            case 6:
                if (last == 3) {
                    mark = 4;
                }else{
                    mark = 3;
                }
                break;
            case 7:
                mark = 5;
                break;
            case 8:
                mark = 6;
                break;
            default:
                System.out.println("ERROR: wrong sum number! Check setValue()!");
        }
        switch(mark){
            case 1:
                if(sp){
                    pre_g ++;
                }else{
                    pre_g += Math.sqrt(2);
                }
                break;
            case 2:
                if(sp){
                    pre_g += 1.5;
                }else{
                    pre_g += (3*(Math.sqrt(2))/2);
                }
                break;
            case 3:
                if(sp){
                    pre_g += 2;
                }else{
                    pre_g += (2*Math.sqrt(2));
                }
                break;
            case 4:
                if(sp){
                    pre_g += 0.25;
                }else{
                    pre_g += Math.sqrt(8);
                }
                break;
            case 5:
                if(sp){
                    pre_g += 0.375;
                }else{
                    pre_g += (3*(Math.sqrt(2))/8);
                }
                break;
            case 6:
                if(sp){
                    pre_g += 0.5;
                }else{
                    pre_g += (Math.sqrt(8)/4);
                }
                break;
            default:
                System.out.println("Error: Wrong case!! Check calculation!");
        }

        // 起始节点到当前节点的距离
        double temp_g = GList[last_x][last_y] + pre_g;
        // 当前节点到目的位置的距离
        double temp_h = getDistance(current_x, current_y, Sgoal.getX(), Sgoal.getY());
        // f(x) = g(x) + h(x)
        double temp_f = temp_g + weight* temp_h;

        // 如果当前节点在开启列表中不存在，则：置入开启列表，并且“设置”
        // 1) 起始节点到当前节点距离
        // 2) 当前节点到目的节点的距离
        // 3) 起始节点到目的节点距离
        if (!openQueue.contains(currentPoint)) {
            openQueue.offer(currentPoint);
            currentPoint.setFather(lastPoint);

            // 起始节点到当前节点的距离
            GList[current_x][current_y] = temp_g;
            // 当前节点到目的节点的距离
            HList[current_x][current_y] = temp_h;
            // f(x) = g(x) + h(x)
            FList[current_x][current_y] = temp_f;
        } else {

            // 如果当前节点在开启列表中存在，并且，
            // 从起始节点、经过上一节点到当前节点、至目的地的距离 < 上一次记录的从起始节点、到当前节点、至目的地的距离，
            // 则：“更新”
            // 1) 起始节点到当前节点距离
            // 2) 当前节点到目的节点的距离
            // 3) 起始节点到目的节点距离
            if (temp_f < FList[current_x][current_y]) {
                // 起始节点到当前节点的距离
                GList[current_x][current_y] = temp_g;
                // 当前节点到目的位置的距离
                HList[current_x][current_y] = temp_h;
                // f(x) = g(x) + h(x)
                FList[current_x][current_y] = temp_f;
                // 更新当前节点的父节点
                currentPoint.setFather(lastPoint);
            }
        }
    }

    public static void main(String[] args) {
        Astar astar;
        if (args.length == 0) {
            System.out.println("No file specified, generate random map...");
            astar = new Astar();
        }else{
            try{
                int sx = -1;
                int sy = -1;
                int gx = -1;
                int gy = -1;
                if (args.length != 1) {
                    sx = Integer.parseInt(args[1]);
                    sy = Integer.parseInt(args[2]);
                    gx = Integer.parseInt(args[3]);
                    gy = Integer.parseInt(args[4]);
                }
                Path path = Paths.get("./"+args[0]);
                //byte[] bytes = Files.readAllBytes(path);
                List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
                int x = allLines.size();
                int y = allLines.get(0).length();
                int[][] intmap = new int[x][y/2];
                for (int i = 0; i < x; i ++) {
                    int m = 0;
                    for (int j = 0; j < y; j ++) {
                        if (allLines.get(i).charAt(j) == '0') {
                            intmap[i][m] = 0;
                            m ++;
                        }else if(allLines.get(i).charAt(j) == '1'){
                            intmap[i][m] = 1;
                            m ++;
                        }else if(allLines.get(i).charAt(j) == '2'){
                            intmap[i][m] = 2;
                            m ++;
                        }else if(allLines.get(i).charAt(j) == 'a'){
                            intmap[i][m] = 3;
                            m ++;
                        }else if(allLines.get(i).charAt(j) == 'b'){
                            intmap[i][m] = 4;
                            m ++;
                        }
                    }
                }
        //        System.out.println("intmap x: "+intmap.length);
        //       System.out.println("intmap y: "+intmap[0].length);
                // for(int i=0;i<intmap.length;i++){
                //     String p = "";
                //     for(int j=0;j<intmap[0].length;j++){
                //         p += (intmap[i][j]+" ");
                //     }
                //     System.out.println(p);
                // }
                if (sx == -1) {
                    astar = new Astar(intmap, 1.0);
                }else if (sx < x && sy < y/2 && gx < x && gy < y/2) {
                    astar = new Astar(intmap, sx,sy,gx,gy,1);
                }else{
                    System.out.println("Error on input of Sstart and Sgoal, randomize start and goal...");
                    astar = new Astar(intmap, 1.0);
                }


            }catch(Exception e){
                System.out.println("No such file found, generate random map...");
                astar = new Astar();


            }
        }
        int accStepO = 0;
        int accStepU = 0;
        int accStepWa = 0;
        int accStepWb = 0;
        int accStepWc = 0;
        Double accCostO = 0.0;
        Double accCostU = 0.0;
        Double accCostWa = 0.0;
        Double accCostWb = 0.0;
        Double accCostWc = 0.0;
        long accOriginal = 0;
        long accUniform = 0;
        long accWa = 0; //w = 1.5
        long accWb = 0; //w = 2.0
        long accWc = 0; //w = 2.5
        Double accMemoryA = 0.0;
        Double accMemoryUSH = 0.0;
        Double accMemoryWa = 0.0;
        Double accMemoryWb = 0.0;
        Double accMemoryWc = 0.0;
        Double oldMemory = 0.0; //old
        Double currMemory = 0.0;  // current

        for (int i = 0; i < 50; i ++) {


            Double startloop= (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
            //System.out.println("startloop:" +startloop);
            //System.out.println("oldMemory:" + oldMemory);
            long startTime;
            long endTime;
            long res = 0;
            double cost = 0.0;
            if (i == 0) {
                startTime = System.currentTimeMillis(); 

                astar.start();
                accMemoryA = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
                currMemory = accMemoryA;
                //System.out.println("current memory:" + (currMemory - oldMemory));
                oldMemory = currMemory;
                endTime = System.currentTimeMillis(); 
               
                cost = astar.GList[astar.Sgoal.getX()][astar.Sgoal.getY()];
                //System.out.println("cost: "+astar.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
                accCostO += cost;
                
                res = res+(endTime - startTime);
                accStepO += astar.printPath();
                //System.out.println("runtime: " + (res) + "ms");
                accOriginal += res;
            }else{
                astar.openQueue = new LinkedList<Point>();
                astar.closedQueue = new LinkedList<Point>();
                astar.init();
                startTime = System.currentTimeMillis(); 
                astar.start();
                currMemory = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
                endTime = System.currentTimeMillis();

                accMemoryA += (currMemory - startloop);
                //System.out.println("current memory:" + (currMemory - startloop));
                oldMemory = currMemory;
                cost = astar.GList[astar.Sgoal.getX()][astar.Sgoal.getY()];
                //System.out.println("cost: "+astar.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
                accCostO += cost;
                 
                res = res+(endTime - startTime);
                accStepO += astar.printPath();
                //System.out.println("runtime: " + (res) + "ms");
                accOriginal += res;
            }

        res = 0;
        startTime = System.currentTimeMillis(); 
        oldMemory=(double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;

        Astar astaruniform =  new Astar(astar.intmap, astar.Sstart.getX(),astar.Sstart.getY(),astar.Sgoal.getX(),astar.Sgoal.getY(),1.0);
        astaruniform.uniformcostsearch();
        currMemory = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
        endTime = System.currentTimeMillis(); 
        accMemoryUSH += (currMemory - oldMemory);
        System.out.println("current memory:" + (currMemory - oldMemory));
       // oldMemory = currMemory;
      
        cost = astaruniform.GList[astar.Sgoal.getX()][astar.Sgoal.getY()];
        //System.out.println("cost: "+astaruniform.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        accCostU += cost;
        
        res = res+(endTime - startTime);
        accStepU += astaruniform.printPath();
        //System.out.println("runtime: " + (res) + "ms");
        accUniform += res;


        res = 0;
        startTime = System.currentTimeMillis(); 
        oldMemory=(double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
        Astar weightedAstar15 = new Astar(astar.intmap, astar.Sstart.getX(),astar.Sstart.getY(),astar.Sgoal.getX(),astar.Sgoal.getY(),1.5);
        weightedAstar15.start();
        currMemory = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
        endTime = System.currentTimeMillis(); 
        accMemoryWa += (currMemory - oldMemory);
        System.out.println("current memory:" + (currMemory - oldMemory));
      //  oldMemory = currMemory;
        cost = weightedAstar15.GList[astar.Sgoal.getX()][astar.Sgoal.getY()];
        //System.out.println("cost: "+weightedAstar15.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        accCostWa += cost;
        
        res = res+(endTime - startTime);
        accStepWa += weightedAstar15.printPath();
        //System.out.println("runtime: " + (res) + "ms");
        accWa += res;


        res = 0;
        startTime = System.currentTimeMillis(); 
        oldMemory=(double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
        Astar weightedAstar20 = new Astar(astar.intmap, astar.Sstart.getX(),astar.Sstart.getY(),astar.Sgoal.getX(),astar.Sgoal.getY(),2.0);
        weightedAstar20.start();
        currMemory = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
        endTime = System.currentTimeMillis();
        accMemoryWb += (currMemory - oldMemory);
        System.out.println("current memory:" + (currMemory - oldMemory));
     //   oldMemory = currMemory;
        cost = weightedAstar20.GList[astar.Sgoal.getX()][astar.Sgoal.getY()];
        //System.out.println("cost: "+weightedAstar20.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        accCostWb += cost;
         
        res = res+(endTime - startTime);
        accStepWb += weightedAstar20.printPath();
        //System.out.println("runtime: " + (res) + "ms");
        accWb += res;



        res = 0;
        startTime = System.currentTimeMillis(); 
        oldMemory=(double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
        Astar weightedAstar25 = new Astar(astar.intmap, astar.Sstart.getX(),astar.Sstart.getY(),astar.Sgoal.getX(),astar.Sgoal.getY(),2.5);
        weightedAstar25.start();
        currMemory = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
        endTime = System.currentTimeMillis(); 
        accMemoryWc += (currMemory - oldMemory);
        System.out.println("current memory:" + (currMemory - oldMemory));
       // oldMemory = currMemory;
        cost = weightedAstar25.GList[astar.Sgoal.getX()][astar.Sgoal.getY()];
        //System.out.println("cost: "+weightedAstar25.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        accCostWc += cost;
        
        res = res+(endTime - startTime);
        accStepWc += weightedAstar25.printPath();
        //System.out.println("runtime: " + (res) + "ms");
        accWc += res;
        System.out.println();
        System.out.println("i:"+i);


    }
        System.out.println("runtime for A*: " + (accOriginal/50) + "ms");
        System.out.println("runtime for Uniform Cost Search: " + (accUniform/50) + "ms");
        System.out.println("runtime for A* with w = 1.5: " + (accWa/50) + "ms");
        System.out.println("runtime for A* with w = 2.0: " + (accWb/50) + "ms");
        System.out.println("runtime for A* with w = 2.5: " + (accWc/50) + "ms");
        System.out.println();

        System.out.println("cost for A*: " + (accCostO/50));
        System.out.println("cost for for Uniform Cost Search: " + (accCostU/50));
        System.out.println("cost for A* with w = 1.5: " + (accCostWa/50));
        System.out.println("cost for A* with w = 2.0: " + (accCostWb/50));
        System.out.println("cost for A* with w = 2.5: " + (accCostWc/50));
        System.out.println();

        System.out.println("step for A*: " + (accStepO/50));
        System.out.println("step for for Uniform Cost Search: " + (accStepU/50));
        System.out.println("step for A* with w = 1.5: " + (accStepWa/50));
        System.out.println("step for A* with w = 2.0: " + (accStepWb/50));
        System.out.println("step for A* with w = 2.5: " + (accStepWc/50));
        System.out.println();
        System.out.println("memory for A*: " + (accMemoryA/50) + "kb");
        System.out.println("memory for for Uniform Cost Search: " + (accMemoryUSH/50)+ "kb");
        System.out.println("memory for A* with w = 1.5: " + (accMemoryWa/50)+ "kb");
        System.out.println("memory for A* with w = 2.0: " + (accMemoryWb/50)+ "kb");
        System.out.println("memory for A* with w = 2.5: " + (accMemoryWc/50)+ "kb");



        // System.out.println("------------Original A*------------");
        // long startTime;
        // long endTime;
        // long res = 0;
        //  startTime = System.currentTimeMillis(); 
        // astar.start();
        // System.out.println("cost: "+astar.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        // endTime = System.currentTimeMillis(); 
        // res = res+(endTime - startTime);
        // System.out.println("runtime: " + (res) + "ms");


        // System.out.println("------------UniformCostSearch------------");
        // res = 0;
        // startTime = System.currentTimeMillis(); 
        // Astar astaruniform =  new Astar(astar.intmap, astar.Sstart.getX(),astar.Sstart.getY(),astar.Sgoal.getX(),astar.Sgoal.getY(),1.0);
        // astaruniform.uniformcostsearch();
        // System.out.println("cost: "+astaruniform.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        // endTime = System.currentTimeMillis(); 
        // res = res+(endTime - startTime);
        // System.out.println("runtime: " + (res) + "ms");


        // System.out.println("------------Weighted Astar for w=1.5------------");
        // res = 0;
        // startTime = System.currentTimeMillis(); 
        // Astar weightedAstar15 = new Astar(astar.intmap, astar.Sstart.getX(),astar.Sstart.getY(),astar.Sgoal.getX(),astar.Sgoal.getY(),1.5);
        // weightedAstar15.start();
        // System.out.println("cost: "+weightedAstar15.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        // endTime = System.currentTimeMillis(); 
        // res = res+(endTime - startTime);
        // System.out.println("runtime: " + (res) + "ms");


        // System.out.println("------------Weighted Astar for w=2.0------------");
        // res = 0;
        // startTime = System.currentTimeMillis(); 
        // Astar weightedAstar20 = new Astar(astar.intmap, astar.Sstart.getX(),astar.Sstart.getY(),astar.Sgoal.getX(),astar.Sgoal.getY(),2.0);
        // weightedAstar20.start();
        // System.out.println("cost: "+weightedAstar20.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        // endTime = System.currentTimeMillis(); 
        // res = res+(endTime - startTime);
        // System.out.println("runtime: " + (res) + "ms");


        // System.out.println("------------Weighted Astar for w=2.5------------");
        // res = 0;
        // startTime = System.currentTimeMillis(); 
        // Astar weightedAstar25 = new Astar(astar.intmap, astar.Sstart.getX(),astar.Sstart.getY(),astar.Sgoal.getX(),astar.Sgoal.getY(),2.5);
        // weightedAstar25.start();
        // System.out.println("cost: "+weightedAstar25.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        // endTime = System.currentTimeMillis(); 
        // res = res+(endTime - startTime);
        // System.out.println("runtime: " + (res) + "ms");
	
		
		
    }

}
