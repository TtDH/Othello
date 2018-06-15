
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class MyOthelloClient extends JFrame{
    final static int BLACK = 1;
    final static int WHITE = -1;

    private JTextField tf;
    private JTextArea ta;
    private JLabel label;
    private OthelloCanvas canvas;
    private String userName;

    Socket socket;
    InputStream sIn;
    OutputStream sOut;
    BufferedReader br;
    PrintWriter pw;

    public MyOthelloClient(String server, String portNum, String userName) {
	    try{
            socket = new Socket(server, Integer.parseInt(portNum)); // mainの引数を受け取れるように
            pw = new PrintWriter(socket.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(IOException e){
            System.err.println("Caught IOException");
            System.exit(1);
        }
        this.userName = userName;
        
        this.setSize(640,320);
	    this.addWindowListener(
            new WindowAdapter() {
		        public void windowClosing(WindowEvent e)  {
		            /* ウインドウが閉じられた時の処理 */
		            System.exit(0);
		        }
	        }
        );	 
	    tf = new JTextField(40);
	    tf.addActionListener(
        new ActionListener(){
		        public void actionPerformed(ActionEvent e){
		            /* テキストフィールドに文字が入力された時の処理 */
                    String command = tf.getText();
		            System.out.println(command);

		            if(command.equals("quit")){
		        	    System.exit(0);
		            }

		            //ここに送信部分追加(NICK,PUT,SAY)
                    if(command.equals("NICK")){
                        System.out.println("Nick confirm!");
                    }

		            tf.setText(""); //テキストフィールドの文字を初期化
		        }
	        }
	    );
    	ta = new JTextArea(18,40);
    	ta.setLineWrap(true);
    	ta.setEditable(false);
    	label = new JLabel();
    
    	JPanel mainp = (JPanel)getContentPane();
    	JPanel ep = new JPanel();
    	JPanel wp = new JPanel();
    	canvas = new OthelloCanvas();
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
    	this.setVisible(true);

    	//受信部分追加
    }

    public static void main(String args[]) {
        switch(args.length){
            case 0:
                System.out.println("No hostname given");
                System.exit(1);    
                break;
            case 1:
                new MyOthelloClient(args[0], "25033", null);
                break;
            case 2:
                new MyOthelloClient(args[0], args[1], null);
                break;
            case 3:
                new MyOthelloClient(args[0], args[1], args[2]);
                break;     
            default:
                System.out.println("Too many value");
                System.exit(1);
                break;
        }
    }
}

class OthelloCanvas extends JPanel {
    private final static int startx = 20;
    private final static int starty = 10;
    private final static int gap = 30;
    private byte[][] board ={
	    {0,0,0,0,0,0,0,0},
	    {0,0,0,0,0,0,0,0},
	    {0,0,0,0,0,0,0,0},
	    {0,0,0,1,-1,0,0,0},
	    {0,0,0,-1,1,0,0,0},
	    {0,0,0,0,0,0,0,0},
	    {0,0,0,0,0,0,0,0},
	    {0,0,0,0,0,0,0,0}
    };  //サンプルデータ

    public OthelloCanvas(){
	    this.addMouseListener(
            new MouseAdapter() {
		        public void mousePressed(MouseEvent e) {
		            /* 盤目上でマウスがクリックされた時の処理 */
		            Point p = e.getPoint();
		            System.out.println(""+p); //デバッグ用表示

		            //ここに送信部分追加(クリックからPUTへの変換)
		        }
	        }
        );
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
	        	if(board[i][j]==MyOthelloClient.BLACK){
	        	    g.setColor(Color.BLACK);
	        	    g.fillOval(startx+gap*i,starty+gap*j,gap,gap);
	        	}else if(board[i][j]==MyOthelloClient.WHITE){
	        	    g.setColor(Color.WHITE);
	        	    g.fillOval(startx+gap*i,starty+gap*j,gap,gap);
	        	}
	        }
	    }
    }
}