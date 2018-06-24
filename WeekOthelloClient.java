import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class WeekOthelloClient extends OthelloClient{

    WeekOthelloClient(String host, int port, String username){
        super(host, port, username);
    }

    @Override
    protected void mainLoop(){
		Random rand = new Random();
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
		    while(true){
				String message = br.readLine();
				stn = new StringTokenizer(message," ",false);
				String com = stn.nextToken();
                
				// put randomly
				int x = rand.nextInt(8);
				int y = rand.nextInt(8);
				putPiece(x,y);

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
				    canvas.repaint();
				    continue;
				}
				if(com.equals("END")){
				    //System.out.println(message);
				    label.setText(message);
				    //setMessage("==System==:"+message);
				    break;
				}if(com.equals("CLOSE")){
				    label.setText(message);
				    return;
				}if(com.equals("TURN")){
				    byte c = Byte.parseByte(stn.nextToken());
				    if(c==color){
						label.setText("Your Turn");
				    }else{
						label.setText("Enemy Turn");						
				    }
				    continue;
				}

				System.out.println(message);
		    }
		}catch(IOException e){
		    System.exit(0);
		}
		System.exit(0);
    }

    public static void main(String args[]) {
		new WeekOthelloClient(args[0],Integer.parseInt(args[1]),args[2]);
    }
}