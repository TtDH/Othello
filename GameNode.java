class GameNode{
    final static int BLACK = 1;
    final static int WHITE = -1;
    private int color = BLACK;

    private byte[][] board = {
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,0,-1,1,0,0,0},
            {0,0,0,1,-1,0,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0},
        };;
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

    GameNode(){
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++)canPut(i,j);
        }
    }
    
    GameNode(byte[][] b, int d){
        board = b;
        depth = d;
        evalBoard();
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

    boolean canPut(int x, int y){
		boolean res = false;
		if(!(board[x][y]==0))return false;
		int dx,dy;
		for(int i=0; i<8; i++){
            if(res)break;
			dx = (int)(Math.sqrt(2) * Math.cos(i*Math.toRadians(45)));
			dy = (int)(Math.sqrt(2) * Math.sin(i*Math.toRadians(45)));
			if(0<=x+dx && x+dx<8 && 0<=y+dy && y+dy<8 && !(board[x+dx][y+dy] == -1*color))continue;
			for(int j=2; j<8; j++){
				if(!(0<=x+dx && x+dx<8 && 0<=y+dy && y+dy<8) || board[x+j*dx][y+j*dy] == 0)break;
				else if(board[x+j*dx][y+j*dy] == color){
                    res = true;
                    break;
				}
			}
		}
        if(res)System.out.println("can put: " + x + " " + y);
        return res;
	}

    void put(int x, int y){
        
    }

    GameNode createNode(){
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(canPut(i,j))
            }
        }
    }

    public static void main(String args[]){
        new GameNode();
    }
}