import java.util.ArrayList;

class GameNode{
    private static OthelloClient oc;
    final static byte BLACK = 1;
    final static byte WHITE = -1;
    private static byte color = BLACK;
    private ArrayList<GameNode> array;

    private byte[][] board = {
        {0,0,0,0,0,0,0,-1},
        {0,0,0,0,0,1,0,-1},
        {0,1,0,0,1,1,1,-1},
        {0,-1,1,-1,-1,-1,0,1},
        {0,0,-1,1,-1,1,1,1},
        {0,0,1,-1,1,1,0,0},
        {0,1,1,0,0,1,0,0},
        {0,-1,1,0,0,0,0,0},
    };
    private int depth;
    private int eval;
	static final byte[][] evalBoard = {
		{30,-12,0,-1,-1,0,-12,30},
		{-12,-15,-3,-3,-3,-3,-15,-12},
		{0,-3,0,-1,-1,0,-3,0},
		{-1,-3,-1,-1,-1,-1,-3,-1},
		{-1,-3,-1,-1,-1,-1,-3,-1},
		{0,-3,0,-1,-1,0,-3,0},
		{-12,-15,-3,-3,-3,-3,-15,-12},
		{30,-12,0,-1,-1,0,-12,30},
	};

    GameNode(int d){
        depth = d;
        // System.out.println("color: "+ color);
        // System.out.println("byte: " + (byte)(color*Math.pow(-1, depth)));
        
        for(int r=0; r<8; r++){
            for(int c=0; c<8; c++){
                if(canPut(r, c, (byte)(color*Math.pow(-1, depth)))){
                    printBoard(put(r, c, (byte)(color*Math.pow(-1, depth))));
                }
            }
        }
    }
    
    // 初回生成時のみ
    GameNode(byte[][] b, int d, byte c){
        board = b;  // ShallowCopy
        depth = d;
        color = c;
        evalBoard();
        array = new ArrayList<GameNode>();
        createNode();
    }

	void evalBoard(){
		int res = 0;
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				if(board[i][j] == color)res += board[i][j];
			}
		}
		eval = res;
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
        if(res)System.out.println("can put: " + r + " " + c);
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

    void printBoard(byte[][] board){
        for(byte[] bytes : board){
            for(byte b : bytes)System.out.printf("%2d ", b);
            System.out.println("");            
        }
    }

    void createNode(){
        for(int r=0; r<8; r++){
            for(int c=0; c<8; c++){
                if(canPut(r, c, (byte)(color*Math.pow(-1,depth))))
                    array.add(new GameNode(put(r, c, (byte)(color*Math.pow(-1,depth))), depth+1, color));
            }
        }
    }

    public static void main(String args[]){
        new GameNode(Integer.parseInt(args[0]));
    }
}