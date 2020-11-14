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
import java.util.Scanner;


class Point {

    private int x;
    private int y;
    private int value;
    private Point father;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        this.father = null;
    }

    public Point(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.father = null;
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
    int totalStep=0;

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

    public void printPath() {
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
        System.out.println("Total step (not cost): " + step);
     /*   for (int i = 0; i < 120; i++) {
            for (int j = 0; j < 160; j++) {
                System.out.print(result[i][j] + " ");
            }
            System.out.println();
        }*/

       
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

    // public double getMinFPointQueue(Queue<Point> q){
    //     Iterator<Point> iterator = queue.iterator();
    //     double min = Double.MAX_VALUE;
    //     while(iterator.hasNext()){
    //         if (this.FList[iterator.x][iterator.y] < min) {
    //             min = this.FList[iterator.x][iterator.y];
    //         }
    //     }
    //     return min;
    // }

    // public void sequential(double w2){
    //     int n = this.printPath();
    //         Queue<Point>[] open = new LinkedList<Point>[n];
    //         Queue<Point>[] closed = new LinkedList<Point>[n];
    //         Point[n+1][3] p; //p[i][0] == Sstart, p[i][1] == s, p[i][2] == Sgoal
    //         Point[n+1][3] bp;//bp[i][0] == g[Sstart], bp[i][1] == g[s], bbp[i][2] == g[Sgoal]
    //         point ptr = this.Sgoal.father;

    //     for (int i = 0; i <= n; i ++) {
    //         open[i] = null;
    //         closed[i] = null;
    //         p[i][0] = ptr;
    //         p[i][2] = this.Sgoal;
    //         bp[i][0] = null;
    //         bp[i][2] = null;
    //         ptr = ptr.father;
    //         open[i].offer(this.Sgoal);
    //     }
    //     while(getMinPointQueue(open[0]) < Double.MAX_VALUE){
    //         for (int i = 0; i <= n; i ++) {
    //             if (getMinPointQueue(open[i]) <= (w2*getMinPointQueue(open[0]))) {
    //                 double valueg = this.GList[g[i][0].getX()][g[i][0].getY()];
    //                 if (valueg <= getMinPointQueue(open[i])) {
    //                     if (valueg< Double.MAX_VALUE) {
    //                         return bp[i][0];
    //                     }
    //                 }else{
    //                     Point s = open[i].poll();
    //                     Point ss = s.parent;
    //                     while(){

    //                     }
    //                 }
    //             }
    //         }
    //     }

    // }

    public void sequential(double w2){
        Point mark = Sgoal;
        while(mark.getFather() != null){
            mark = mark.getFather();
            Astar newastar = new Astar(this.intmap, mark.getX(), mark.getY(), this.Sgoal.getX(),this.Sgoal.getY(), this.weight);
            if ((newastar.FList[mark.getX()][mark.getY()]*w2) < (this.FList[this.Sgoal.getX()][this.Sgoal.getY()] - this.FList[mark.getX()][mark.getY()])) {
                this.Sgoal.setFather(this.map[newastar.Sgoal.getFather().getX()][newastar.Sgoal.getFather().getY()]);
                Point thisptr = this.Sgoal.getFather();
                Point temp = newastar.Sgoal.getFather();
                while(temp != null){
                    thisptr.setFather(this.map[temp.getFather().getX()][temp.getFather().getY()]);
                    thisptr = thisptr.getFather();
                    temp = temp.getFather();
                }
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
		Double memoryStart = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
     //   int[][] map =new int [120][160];  // 干啥用的


        System.out.println("------------Original A*------------");
        long startTime;
        long endTime;
        long res = 0;
    // for(int i = 0; i <100; i++){
         startTime = System.currentTimeMillis(); 

         
         System.out.println("start memory usage: "+ memoryStart);
        astar.start();
     //   System.out.println("Weight: "+astar.weight);
     //   System.out.println("Sstart: "+astar.Sstart.getX()+","+astar.Sstart.getY());
        System.out.println("Sgoal: "+astar.Sgoal.getX()+","+astar.Sgoal.getY());


        System.out.println("cost: "+astar.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        endTime = System.currentTimeMillis(); 
        res = res+(endTime - startTime);
        Double memoryA = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
        System.out.println("runtime: " + (res) + "ms");
        System.out.println("KB: "+ memoryA);
        astar.printPath();


        boolean check_value = true;

        Scanner v_astart = new Scanner(System.in);
        System.out.print( "do you wan to check more: " );
                check_value = v_astart.nextBoolean( );

        while( check_value){
            System.out.print( "Please enter the x value of point you want to check : " );

            if (v_astart.hasNextInt()) {

                int x = v_astart.nextInt( );
                System.out.print( "Please enter the y value of point you want to check : " );
                int y = v_astart.nextInt( );

                if (astar.GList[x][y] != Integer.MAX_VALUE){
                    System.out.println("the g value is: "+astar.GList[x][y]);
                    System.out.println("the h value is: "+astar.HList[x][y]);
                    System.out.println("the h value is: "+astar.FList[x][y]);
                }else
                    System.out.println("the point did not be visited");
                
                
                System.out.print( "do you wan to check more: " );
                check_value = v_astart.nextBoolean( );
           
        }else{
            System.out.println("no input");
            break;

        }
        }


    //}
   // System.out.println("runtime: " + (res/100) + "ms");

      //  astar.printPath();
     //   astar.generatePic("generatedPicA*.png");


        System.out.println("------------UniformCostSearch------------");
        res = 0;
    // for(int i = 0; i <100; i++){
        startTime = System.currentTimeMillis(); 
        Astar astaruniform =  new Astar(astar.intmap, astar.Sstart.getX(),astar.Sstart.getY(),astar.Sgoal.getX(),astar.Sgoal.getY(),1.0);
        astaruniform.uniformcostsearch();
     //   System.out.println("Weight: "+astar.weight);
        System.out.println("cost: "+astaruniform.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        endTime = System.currentTimeMillis(); 
        res = res+(endTime - startTime);
        Double memoryUSH = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024 - memoryA;
        System.out.println("runtime: " + (res) + "ms");
        System.out.println("KB: "+ memoryUSH);
        astaruniform.printPath();



        boolean un_check_value= true;
        
        Scanner v_astaruniform = new Scanner(System.in);
        System.out.print( "do you wan to check more: " );
        un_check_value = v_astaruniform.nextBoolean( );
        while( un_check_value){
            System.out.print( "Please enter the x value of the  point you want to check : " );

            if (v_astaruniform.hasNextInt()) {

                int x = v_astaruniform.nextInt( );
                System.out.print( "Please enter the y value of the point you want to check : " );
                int y = v_astaruniform.nextInt( );

                if (astaruniform.GList[x][y] != Integer.MAX_VALUE){
                    System.out.println("the g value is: "+astaruniform.GList[x][y]);
                    System.out.println("the h value is: "+ 0);
                    System.out.println("the h value is: "+astaruniform.GList[x][y]);
                }else
                    System.out.println("the point did not be visited");
                
                
                System.out.print( "do you wan to check more: " );
                un_check_value = v_astaruniform.nextBoolean( );
           
        }else{
            System.out.println("no input");
            break;

        }
        }




   // }
   // System.out.println("runtime: " + (res/100) + "ms");

      //  astaruniform.printPath();
      //  astaruniform.generatePic("generatedPicUCS.png");


        System.out.println("------------Weighted Astar for w=1.5------------");
        res = 0;
   //  for(int i = 0; i <100; i++){
        startTime = System.currentTimeMillis(); 
        Astar weightedAstar15 = new Astar(astar.intmap, astar.Sstart.getX(),astar.Sstart.getY(),astar.Sgoal.getX(),astar.Sgoal.getY(),1.5);
	//	System.out.println("Weight: "+weightedAstar15.weight);
        weightedAstar15.start();
        System.out.println("cost: "+weightedAstar15.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        endTime = System.currentTimeMillis(); 
        res = res+(endTime - startTime);
        Double memoryW15 = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024 - memoryUSH - memoryA;
        System.out.println("runtime: " + (res) + "ms");
        System.out.println("KB: "+ memoryW15);
        weightedAstar15.printPath();
   // }
    //System.out.println("runtime: " + (res/100) + "ms");

       //	weightedAstar15.printPath();
       //	weightedAstar15.generatePic("generatedPicW15.png");


        check_value = true;

       System.out.print( "do you wan to check more: " );
               check_value = v_astart.nextBoolean( );

       while( check_value){
           System.out.print( "Please enter the x value point you want to check : " );

           if (v_astart.hasNextInt()) {

               int x = v_astart.nextInt( );
               System.out.print( "Please enter the y value point you want to check : " );
               int y = v_astart.nextInt( );

               if (astar.GList[x][y] != Integer.MAX_VALUE){
                   System.out.println("the g value is: "+weightedAstar15.GList[x][y]);
                   System.out.println("the h value is: "+weightedAstar15.HList[x][y]);
                   System.out.println("the f value is: "+weightedAstar15.FList[x][y]);
               }else
                   System.out.println("the point did not be visited");
               
               
               System.out.print( "do you wan to check more: " );
               check_value = v_astart.nextBoolean( );
          
       }else{
           System.out.println("no input");
           break;

       }
       }


        System.out.println("------------Weighted Astar for w=2.0------------");
        res = 0;
   //  for(int i = 0; i <100; i++){
        startTime = System.currentTimeMillis(); 
        Astar weightedAstar20 = new Astar(astar.intmap, astar.Sstart.getX(),astar.Sstart.getY(),astar.Sgoal.getX(),astar.Sgoal.getY(),2.0);
	//	System.out.println("Weight: "+weightedAstar20.weight);
        weightedAstar20.start();
        System.out.println("cost: "+weightedAstar20.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        endTime = System.currentTimeMillis(); 
        res = res+(endTime - startTime);
        Double memoryW20 = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024 - memoryA- memoryW15 - memoryUSH;
        System.out.println("runtime: " + (res) + "ms");
        System.out.println("KB: "+ memoryW20);
        weightedAstar20.printPath();

    //}       
   // System.out.println("runtime: " + (res/100) + "ms");

    //	weightedAstar20.printPath();
      // 	weightedAstar20.generatePic("generatedPicW20.png");

      check_value = true;

      System.out.print( "do you wan to check more: " );
              check_value = v_astart.nextBoolean( );

      while( check_value){
          System.out.print( "Please enter the x value point you want to check : " );

          if (v_astart.hasNextInt()) {

              int x = v_astart.nextInt( );
              System.out.print( "Please enter the y value point you want to check : " );
              int y = v_astart.nextInt( );

              if (astar.GList[x][y] != Integer.MAX_VALUE){
                  System.out.println("the g value is: "+weightedAstar20.GList[x][y]);
                  System.out.println("the h value is: "+weightedAstar20.HList[x][y]);
                  System.out.println("the f value is: "+weightedAstar20.FList[x][y]);
              }else
                  System.out.println("the point did not be visited");
              
              
              System.out.print( "do you wan to check more: " );
              check_value = v_astart.nextBoolean( );
         
      }else{
          System.out.println("no input");
          break;

      }
      }





        System.out.println("------------Weighted Astar for w=2.5------------");
        res = 0;
  //   for(int i = 0; i <100; i++){
        startTime = System.currentTimeMillis(); 
        Astar weightedAstar25 = new Astar(astar.intmap, astar.Sstart.getX(),astar.Sstart.getY(),astar.Sgoal.getX(),astar.Sgoal.getY(),2.5);
		//System.out.println("Weight: "+weightedAstar25.weight);
        weightedAstar25.start();
        System.out.println("cost: "+weightedAstar25.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        endTime = System.currentTimeMillis(); 
        res = res+(endTime - startTime);
        Double memoryW25 = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024 - memoryA - memoryW15 - memoryW20 - memoryUSH;
        System.out.println("runtime: " + (res) + "ms");
        System.out.println("KB: "+ memoryW25);
        
        System.out.println("Total memory usage: "+(double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024);
        System.out.println("Total memory calculated: "+ (memoryW15+memoryW20+memoryW25+memoryA+memoryUSH));
        weightedAstar25.printPath();



        check_value = true;

       System.out.print( "do you wan to check more: " );
               check_value = v_astart.nextBoolean( );

       while( check_value){
           System.out.print( "Please enter the x vlue point you want to check : " );

           if (v_astart.hasNextInt()) {

               int x = v_astart.nextInt( );
               System.out.print( "Please enter the y vlue point you want to check : " );
               int y = v_astart.nextInt( );

               if (astar.GList[x][y] != Integer.MAX_VALUE){
                   System.out.println("the g value is: "+weightedAstar25.GList[x][y]);
                   System.out.println("the h value is: "+weightedAstar25.HList[x][y]);
                   System.out.println("the f value is: "+weightedAstar25.FList[x][y]);
               }else
                   System.out.println("the point did not be visited");
               
               
               System.out.print( "do you wan to check more: " );
               check_value = v_astart.nextBoolean( );
          
       }else{
           System.out.println("no input");
           break;
       }




       }

        System.out.println("------------Sequential heuristic Astar with w1 = 1.5 and w2 = 1.5------------");
        res = 0;
        startTime = System.currentTimeMillis(); 
        weightedAstar15.sequential(1.5);
        System.out.println("cost: "+weightedAstar15.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        endTime = System.currentTimeMillis(); 
        res = res+(endTime - startTime);
        memoryW15 = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024 - memoryUSH - memoryA;
        System.out.println("runtime: " + (res) + "ms");
        System.out.println("KB: "+ memoryW15);
        weightedAstar15.printPath();

        System.out.println("------------Sequential heuristic Astar with w1 = 1.5 and w2 = 2------------");
        res = 0;
        startTime = System.currentTimeMillis(); 
        weightedAstar15.sequential(2);
        System.out.println("cost: "+weightedAstar15.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        endTime = System.currentTimeMillis(); 
        res = res+(endTime - startTime);
        memoryW15 = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024 - memoryUSH - memoryA;
        System.out.println("runtime: " + (res) + "ms");
        System.out.println("KB: "+ memoryW15);
        weightedAstar15.printPath();

        System.out.println("------------Sequential heuristic Astar with w1 = 2 and w2 = 1.5------------");
        res = 0;
        startTime = System.currentTimeMillis(); 
        weightedAstar20.sequential(1.5);
        System.out.println("cost: "+weightedAstar15.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        endTime = System.currentTimeMillis(); 
        res = res+(endTime - startTime);
        memoryW20 = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024 - memoryUSH - memoryA;
        System.out.println("runtime: " + (res) + "ms");
        System.out.println("KB: "+ memoryW15);
        weightedAstar15.printPath();

        System.out.println("------------Sequential heuristic Astar with w1 = 2 and w2 = 2------------");
        res = 0;
        startTime = System.currentTimeMillis(); 
        weightedAstar20.sequential(2);
        System.out.println("cost: "+weightedAstar15.GList[astar.Sgoal.getX()][astar.Sgoal.getY()]);
        endTime = System.currentTimeMillis(); 
        res = res+(endTime - startTime);
        memoryW20 = (double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024 - memoryUSH - memoryA;
        System.out.println("runtime: " + (res) + "ms");
        System.out.println("KB: "+ memoryW15);
        weightedAstar15.printPath();






        v_astart.close();
        v_astaruniform.close();


   // }   
    //System.out.println("runtime: " + (res/100) + "ms");

    	
	//	weightedAstar25.generatePic("generatedPicW25.png");

	//	astar.printmap();

	

   // Double accMemoryA = 0;
   // Double accMemoryUSH = 0;
   // Double accMemoryWa = 0;
   // Double accMemoryWb = 0;
   // Double accMemoryWc = 0;


		
		
    }

}
