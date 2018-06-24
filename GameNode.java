import java.util.ArrayList;

class GameNode{
    final int MIDDLE_NUM = 10;
    final int FINISH_NUM = 48;

    final static byte BLACK = 1;
    final static byte WHITE = -1;
    private byte color;
    private ArrayList<GameNode> array;
    private int putPosC;
    private int putPosR;
    private int numOfMoves;

    private byte[][] board;
    private int depth;
    private int eval;
	static final byte[][] evalBoard = {
		{45,-12,0,-1,-1,0,-12,45},
		{-12,-15,-3,-3,-3,-3,-15,-12},
		{0,-3,0,-1,-1,0,-3,0},
		{-1,-3,-1,-1,-1,-1,-3,-1},
		{-1,-3,-1,-1,-1,-1,-3,-1},
		{0,-3,0,-1,-1,0,-3,0},
		{-12,-15,-3,-3,-3,-3,-15,-12},
		{45,-12,0,-1,-1,0,-12,45},
	};

    // 空生成用コンストラクタ
    GameNode(){};
    
    // root生成時に使うコンストラクタ
    GameNode(byte[][] b, int d, byte c, int n){
        board = b;  // ShallowCopy
        depth = d;
        color = c;
        numOfMoves = n;
        setEval();
        array = new ArrayList<GameNode>();
        createNode();
        for(GameNode gn : array){
            System.out.println("\teval: " + gn.getEval());
        }
        if(returnGameNode()!=null)System.out.println("return Node's eval: " + returnGameNode().getEval());
    }

    // root以外の生成時に使うコンストラクタ
    GameNode(byte[][] b, int d, byte c, int ppr, int ppc, int n){
        board = b;
        depth = d;
        color = c;
        putPosR = ppr;
        putPosC = ppc;
        numOfMoves = n;
        array = new ArrayList<GameNode>();
        if(depth < OthelloClient.limitDepth){
            createNode();
            if(array.isEmpty()){
                depth++;
                color = (byte)(-1*color);
                createNode();
            }
        }
        setEval();
    }

	void setEval(){
        // 盤面評価
        int myBoardEval = 0;
        int enemyBoardEval = 0;
        // 自分の石の数
        int myStoneNum = 0;
        int enemyStoneNum = 0;
        // 着手可能数
        int myCanPutNum = 0;
        int enemyCanPutNum = 0;

		for(int r=0; r<8; r++){
			for(int c=0; c<8; c++){
                if(board[r][c] == color){
                    myStoneNum++;
                    myBoardEval += evalBoard[r][c];
                }else if(board[r][c] == -1 * color){
                    enemyStoneNum++;
                    enemyBoardEval += evalBoard[r][c];
                }
                if(canPut(r, c, color))myCanPutNum++;
                else if(canPut(r,c,(byte)(-1*color)))enemyCanPutNum++;
			}
        }
        double x0, x1, x2;
        if(numOfMoves < MIDDLE_NUM){
            x0=0.5; x1=1; x2=3;
        }else if(numOfMoves >= MIDDLE_NUM  && numOfMoves < FINISH_NUM){
            x0=2; x1=1; x2=1;
        }else{
            x0=0.5; x1=3; x2=1;
        }
        // myBoardEvalだけでよいのでは？
        eval = (int)x0*(myBoardEval - enemyBoardEval)
                + (int)x1*(myStoneNum - enemyStoneNum) 
                + (int)x2*(myCanPutNum - enemyCanPutNum);
    }
    
    int getEval(){
        return eval;
    }

    byte[][] getBoard(){
        return board;
    }

    int getDepth(){
        return depth;
    }

    ArrayList<GameNode> getArray(){
        return array;
    }

    int getPPC(){
        return putPosC;
    }

    int getPPR(){
        return putPosR;
    }

    boolean canPut(int r, int c, byte putColor){
		boolean res = false;
		if(!(board[r][c]==0))return false;
		int dr,dc;
		for(int i=0; i<9; i++){
            if(res)break;
			dr = (int)((Math.sqrt(2)+0.1) * Math.sin(Math.toRadians(i*45)));
            dc = (int)((Math.sqrt(2)+0.1) * Math.cos(Math.toRadians(i*45)));
            // r+dr,c+dcがOutOfBoundsあるいは
            // r+dr,c+dcの位置のコマが相手の色でないならばcontinue
			if(!(0<=r+dr && r+dr<8 && 0<=c+dc && c+dc<8) || !(board[r+dr][c+dc] == -1*putColor))continue;
			for(int j=2; j<8; j++){
                if(!(0<=r+j*dr && r+j*dr<8 && 0<=c+j*dc && c+j*dc<8))break;
                if(board[r+j*dr][c+j*dc] == 0)break;    // OutOfBouds避け
				else if(board[r+j*dr][c+j*dc] == putColor){
                    res = true;
                    break;
				}
			}
		}
        // if(res)System.out.println("can put: " + r + " " + c);
        return res;
	}

    /**
     * x,yに石を置いた時の盤面をディープコピーして返す関数
     */
    byte[][] put(int r, int c, byte putColor){
        byte[][] boardCopy = new byte[8][];
        for(int i=0; i<8; i++){
            boardCopy[i] = board[i].clone();
        }
        boardCopy[r][c] = putColor;
        int dr,dc;
		for(int i=0; i<9; i++){
			dr = (int)((Math.sqrt(2)+0.1) * Math.sin(Math.toRadians(i*45)));
            dc = (int)((Math.sqrt(2)+0.1) * Math.cos(Math.toRadians(i*45)));
            // r+dr,c+dcがOutOfBoundsあるいは
            // r+dr,c+dcの位置のコマが相手の色でないならばcontinue
			if(!(0<=r+dr && r+dr<8 && 0<=c+dc && c+dc<8) || !(boardCopy[r+dr][c+dc] == -1*putColor))continue;
			for(int j=2; j<8; j++){
                if(!(0<=r+j*dr && r+j*dr<8 && 0<=c+j*dc && c+j*dc<8))break;
                if(boardCopy[r+j*dr][c+j*dc] == 0)break;    // OutOfBouds避けに条件文分割
				else if(boardCopy[r+j*dr][c+j*dc] == putColor){
                    for(int k=1; k<=j; k++)boardCopy[r+k*dr][c+k*dc] = putColor;
                    break;
				}
			}
		}
        return boardCopy;
    }

    void printBoard(){
        for(byte[] bytes : board){
            for(byte b : bytes)System.out.printf("%2d ", b);
            System.out.println("");            
        }
        System.out.println("eval: "+eval);
    }

    void createNode(){
        byte turn = (byte)(color*Math.pow(-1,depth));
        for(int r=0; r<8; r++){
            for(int c=0; c<8; c++){
                if(canPut(r, c, turn))
                    array.add(new GameNode(put(r, c, turn), depth+1, (byte)(-1*color), r, c, numOfMoves+1));
            }
        }
    }

    public GameNode returnGameNode(){
        if(array.size() == 0)return null;
        int minI=0, maxI=0;
        double minEval=0, maxEval=0;
        for(int i=0; i<array.size(); i++){
            double eval=array.get(i).getEval();
            if(i==0){
                minI=i; maxI=i;
                minEval=eval; maxEval=eval;
            }else{
                if(eval > maxEval){
                    maxEval = eval;
                    maxI = i;
                }
                else if(eval < minEval){
                    minEval = eval;
                    minI = i;
                }
            }
        }
        if(color == (byte)(color*Math.pow(-1,depth)))return array.get(maxI);
        else return array.get(minI);
    }

    public static void main(String args[]){
        byte[][] firstBoard = {
            {0,0,0,0,0,0,0,-1},
            {0,0,0,0,0,1,0,-1},
            {0,1,0,0,1,1,1,-1},
            {0,-1,1,-1,-1,-1,0,1},
            {0,0,-1,1,-1,1,1,1},
            {0,0,1,-1,1,1,0,0},
            {0,1,1,0,0,1,0,0},
            {0,-1,1,0,0,0,0,0},
        };
        GameNode root = new GameNode(firstBoard, Integer.parseInt(args[0]), Byte.parseByte(args[1]), 0);
        GameNode next = root.returnGameNode();
        System.out.println("c: "+next.getPPC()+" r: "+next.getPPR());
    }
}