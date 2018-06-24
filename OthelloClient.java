import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class OthelloClient extends JFrame{
    final static int BLACK = 1;
    final static int WHITE = -1;

    protected Socket socket;
    protected BufferedReader br;
    protected PrintWriter pw;    
    protected byte color;
    protected byte[][] board = new byte[8][8];
    protected JTextField tf;
    protected JTextArea ta;
    protected JLabel label;
    protected OthelloCanvas canvas;
	protected String username = "";
	private int numOfMoves;
	public static int limitDepth=1;
	
	public ArrayList<GameNode> array = new ArrayList<GameNode>();

    public OthelloClient(String host, int port, String username) {
		numOfMoves=0;
		this.username = username;
		//setTitle(username);
		try{
		    socket = new Socket(host,port);
		    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    pw = new PrintWriter(socket.getOutputStream());
		}catch(Exception e){
		    e.printStackTrace();
		    System.exit(1);
		}
		addWindowListener(
			new WindowAdapter() {
    	        public void windowClosing(WindowEvent e)  {
    	            System.exit(0);
    	        }
    	    }
		);	    
		tf = new JTextField(40);
		ta = new JTextArea(18,40);
		ta.setLineWrap(true);
		ta.setEditable(false);
		label = new JLabel();

		this.setSize(640,320);

		tf.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
				    if(tf.getText().equals("quit")){
						System.exit(0);
				    }
				    //sayMessage(tf.getText());
				    pw.println("SAY "+tf.getText());
				    pw.flush();
				    tf.setText("");
				}
		    }
		);

		JPanel mainp = (JPanel)getContentPane();
		JPanel ep = new JPanel();
		JPanel wp = new JPanel();
		canvas = new OthelloCanvas(this);

		GridLayout gl = new GridLayout(1,2);
		gl.setHgap(5);
		mainp.setLayout(gl);
		ep.setLayout(new BorderLayout());
		ep.add(new JScrollPane(ta),BorderLayout.CENTER);
		ep.add(tf,BorderLayout.SOUTH);
		wp.setLayout(new BorderLayout());
		wp.add(label,BorderLayout.SOUTH);
		wp.add(canvas,BorderLayout.CENTER);
		mainp.add(wp);
		mainp.add(ep);

		setVisible(true);

		mainLoop();
    }

    void putPiece(int x, int y){
		pw.println("PUT "+x+" "+y);
		pw.flush();
    }

    byte[][] getBoard(){
		return board;
	}

	int miniMax(GameNode gn){
		if(gn.getDepth() == limitDepth)return gn.getEval();
		int best=0;
		int index=0;
		for(int i=0; i<gn.getArray().size(); i++){
			int val;
			val = miniMax(gn.getArray().get(i));
			if(i==0)best=val;
			if(gn.getDepth()%2==0 && best<val){
				best=val;
				index = i;
			}
			else if(gn.getDepth()%2==1 && best>val){
				best=val;
				index = i;
			}
		}
		// rootNodeならそのインデックスを返す
		if(gn.getDepth() == 0)return index;
		else return best;
	}

    protected void mainLoop(){
		try{
		    pw.println("NICK "+username);
		    pw.flush();
		    StringTokenizer stn = new StringTokenizer(br.readLine()," ",false);
		    stn.nextToken(); //START message
		    color = Byte.parseByte(stn.nextToken());
		    if(color==BLACK){
				setTitle(username+"(BLACK)");
		    }else{
				setTitle(username+"(WHITE)");
			}
			GameNode root;
			// 初期化してないエラー避け
			root = new GameNode();
		    while(true){
				String message = br.readLine();
				stn = new StringTokenizer(message," ",false);
				String com = stn.nextToken();
				
				if(com.equals("SAY")){
				    setMessage(message.substring(4));
				    continue;
				}
				if(com.equals("BOARD")){
				    for(int i=0;i<8;i++){
						for(int j=0;j<8;j++){
						    board[i][j] = Byte.parseByte(stn.nextToken());
						}
					}

					root = new GameNode(board, 0, color, numOfMoves);

				    canvas.repaint();
				    continue;
				}
				if(com.equals("END")){
				    //System.out.println(message);
				    label.setText(message);
				    //setMessage("==System==:"+message);
				    break;
				}
				if(com.equals("CLOSE")){
				    label.setText(message);
				    return;
				}
				if(com.equals("TURN")){
				    byte c = Byte.parseByte(stn.nextToken());
					numOfMoves++;
				    if(c==color){
						label.setText("Your Turn");
						int index = miniMax(root);
						GameNode next = root.getArray().get(index);
						if(next == null)continue;
						int pr = next.getPPR();
						int pc = next.getPPC();
						putPiece(pr, pc);

						// System.out.println("numOfMoves: "+numOfMoves);
				    }else{
						label.setText("Enemy Turn");
				    }
				    continue;
				}
				System.out.println(message);
		    }
		}catch(IOException e){
		    System.exit(1);
		}
		System.exit(0);
    }

    protected void setMessage(String str){
		ta.append(str+"\n");
		int len = ta.getText().length();
		ta.setCaretPosition(len); 
    }

    public static void main(String args[]) {
		new OthelloClient(args[0],Integer.parseInt(args[1]),args[2]);
    }
}


class OthelloCanvas extends JPanel {
    private final static int startx = 20;
    private final static int starty = 10;
    private final static int gap = 30;
    private OthelloClient oc;
    private byte[][] board;

    OthelloCanvas(OthelloClient oc){
		this.oc = oc;
		this.board=oc.getBoard();
		this.addMouseListener(
			new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
				    Point p = e.getPoint();
				    putPiece((p.x-startx)/gap,(p.y-starty)/gap);
				}
		    }
		);
    }

    private void putPiece(int x, int y){
		oc.putPiece(x,y);
    }


    public void paintComponent(Graphics g){
		g.setColor(new Color(0,180,0));
		g.fillRect(startx,starty,gap*8,gap*8);

		g.setColor(Color.BLACK);
		for(int i=0;i<9;i++){
		    g.drawLine(startx,starty+i*gap,startx+8*gap,starty+i*gap);
		    g.drawLine(startx+i*gap,starty,startx+i*gap,starty+8*gap);
		}
		for(int i=0;i<8;i++){
		    for(int j=0;j<8;j++){
				if(board[i][j]==OthelloClient.BLACK){
				    g.setColor(Color.BLACK);
				    g.fillOval(startx+gap*i,starty+gap*j,gap,gap);
				}else if(board[i][j]==OthelloClient.WHITE){
				    g.setColor(Color.WHITE);
				    g.fillOval(startx+gap*i,starty+gap*j,gap,gap);
				}
		    }
		}
    }
}
