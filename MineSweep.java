package simpleGames;
import java.awt.*;
import java.awt.event.*;
import java.time.Duration;
import java.time.Instant;
import javax.swing.*;
public class MineSweep {//one of the first things i made, thats why its kinda spaghetti and unlabeled before I recently went in and labeled+added some things
	public static void main( String args[] ){
		new MineSweep(12, 60, 20); //grid side length, button size, approximate bomb count
	}
boolean[][] boo;//stores spaces that have been filled by fill()
JFrame window;
JButton restart;
Font mono,medium,big;
boolean first=true;
int winsize,butsize,flags;
double bombs;
String thewinner;
boolean[][] grid;//stores bombs
JButton[][] buttons;
boolean[][] flagsGrid;
JLabel info,time,points;
Timer timer;
public MineSweep(int size, int but, double bom){
	bombs=0.5/(bom/(size*size));
	flagsGrid=new boolean[size][size];
	grid=new boolean[size][size];
	boo = new boolean[size][size];
	buttons=new JButton[size][size];
	winsize=size;butsize=but;
	time = new JLabel();
	mono = new Font(Font.MONOSPACED,Font.PLAIN,butsize/3);
	big = new Font(Font.MONOSPACED,Font.PLAIN,5+winsize*3*(butsize/30));
	medium = new Font(Font.MONOSPACED,Font.PLAIN,(butsize/3+5+winsize*3*(butsize/30))/2);
	window = new JFrame("MineSweep");
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.setBounds(980,0,(int)((winsize+1*(double)2/(butsize/15))*butsize-14),(int)(1.25*((winsize+1*(double)2/(butsize/15))*butsize+9)));//-14,+9
	window.setLayout(null);
	time.setBounds(0,0,size*but,butsize);
	time.setFont(medium);
	info=new JLabel("Aproximate flags: "+bom, SwingConstants.CENTER);
	info.setBounds(0,0,size*but,(int)(0.25*((winsize+1*(double)2/(butsize/15))*butsize+9)));
	info.setFont(mono);
	MS();
	window.setVisible(true);
}
public void MS(){
	flags=0;first=true;
	for(int i=0;i<winsize;i++){
		for(int j=0;j<winsize;j++){
			switch ((int)(Math.round(Math.random()*bombs))){//1=50%;2=25%;3=1/6;4=12.5%
			case 0: grid[i][j] = true;break;
			default: grid[i][j] = false;
			}
		}
	}
	for (int i=0;i<grid.length;i++){
		for(int j=0;j<grid.length;j++){
			buttons[i][j]=initiateButton(i,j);
		}
	}
	for (int i=0;i<grid.length;i++){
		for(int j=0;j<grid.length;j++){
			boo[i][j]=false;
		}
	}	
}
public void updateText() {
	window.add(time);
	time.setFont(medium);
	Instant start;
	start = Instant.now();
	timer = new Timer(1, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int theTime = (int)Duration.between(start, Instant.now()).getSeconds();
			time.setText(String.format("%02d",theTime/60)+":"+String.format("%02d",theTime%60));
		}
	});
	timer.start();
	window.add(info);
	info.setFont(mono);
	info.setText("Flags remaining: " + (bombsCount()-flags));
}
private JButton initiateButton(int x,int y){
	JButton n = new JButton();
	n.setBounds(x*butsize,(int)(0.25*((winsize+1*(double)2/(butsize/15))*butsize+9))+y*butsize, butsize, butsize);
	window.add(n);
	n.addMouseListener(new MouseListener(){
		@Override
		public void mouseClicked(MouseEvent e){}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				if(first&&getCount(x,y)!=0) {
					window.getContentPane().removeAll();
					window.remove(time);
					window.repaint();
					while(getCount(x,y)!=0) {
						for(int i=0;i<winsize;i++){
							for(int j=0;j<winsize;j++){
								switch ((int)(Math.round(Math.random()*bombs))){//1=50%;2=25%;3=1/6;4=12.5%
								case 0: grid[i][j] = true;break;
								default: grid[i][j] = false;
								}
							}
						}
					}
					for (int i=0;i<grid.length;i++){
						for(int j=0;j<grid.length;j++){
							buttons[i][j]=initiateButton(i,j);
						}
					}
					window.remove(time);
					updateText();
					first=false;
					
				}
				else if(first) {first=false;updateText();}
				else {first=false;}
				window.remove(n);
				//buttons[x][y]=null;
				JTextField k=initiateText(x,y);
				int count = getCount(x,y);
				if(count==0) {fill(x,y);}
				else if(count==9) {
					end();
				}
				else{
					k.setText(""+count);
				}	
			}
			else if (SwingUtilities.isRightMouseButton(e)&&!first) {
				flags++;
				info.setText("Flags remaining: " + (bombsCount()-flags));
				window.remove(n);flagsGrid[x][y]=true;
				if(bombsRemaining()==0) {
					win();
				}
				JButton m=new JButton("X");
				m.setBounds(x*butsize,(int)(0.25*((winsize+1*(double)2/(butsize/15))*butsize+9))+y*butsize, butsize, butsize);
				window.add(m);
				m.addMouseListener(new MouseListener(){
					@Override
					public void mouseClicked(MouseEvent e) {}
					@Override
					public void mousePressed(MouseEvent e) {}
					@Override
					public void mouseReleased(MouseEvent e) {if(!first) {
						flags--;
						boo[x][y]=true;
						info.setText("Flags remaining: " + (bombsCount()-flags));
						if (SwingUtilities.isRightMouseButton(e)) {
							window.remove(m);
							buttons[x][y]=initiateButton(x,y);
						}
					}}
					@Override
					public void mouseEntered(MouseEvent e) {}
					@Override
					public void mouseExited(MouseEvent e) {}
				});
			}
		}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	});
	return n;
}
public void fill(int x,int y) {
	if(!boo[x][y]) {
		for(int i=x-1;i<=x+1;i++) {
			for(int j=y-1;j<=y+1;j++) {
				if(inBounds(i,j)&&buttons[i][j].getParent()!=null) {
					window.remove(buttons[i][j]);
					JTextField k=initiateText(i,j);
					k.setText(""+((getCount(i,j)==0)?"":getCount(i,j)));
					if(getCount(i,j)==0) {fill(i,j);}
				}
			}
		}
	boo[x][y]=true;
	if(inBounds(x-1,y)&& getCount(x-1,y)==0){
		fill(x-1,y);
	}
	if(inBounds(x+1,y)&&getCount(x+1,y)==0){
		fill(x+1,y);
	}
	if(inBounds(x,y-1)&&getCount(x,y-1)==0){
		fill(x,y-1);
	}
	if(inBounds(x,y+1)&&getCount(x,y+1)==0)
	{
		fill(x,y+1);
	}}
}
private void leftClick(int x, int y) {
	JTextField j=initiateText(x,y);
	int count = getCount(x,y);
	if(count==0) {}
	else if(count==9) {
		j.setText("x");
	}
	else{
		j.setText(""+count);
	}
}
private JTextField initiateText(int x,int y){
	JTextField k=new JTextField("");
	k.setBounds(x*butsize,(int)(0.25*((winsize+1*(double)2/(butsize/15))*butsize+9))+ y*butsize, butsize, butsize);
	window.add(k);
	k.setFont(mono);
	k.setHorizontalAlignment(JTextField.CENTER);
	k.setEditable(false);
	return k;
}
public void end(){
//	System.out.print(toString()+"\n");
//	for(int i=0;i<buttons.length;++i){
//		for(int j=0;j<buttons[i].length;j++){
//			System.out.print((buttons[j][i].getParent()==null)+" ");
//		}
//		System.out.println();
//	}
	printGrid();
	//System.out.println();
	window.getContentPane().removeAll();
	window.remove(time);
	timer.stop();
	window.repaint();
	for (int i=0;i<grid.length;i++) {
		for(int j=0;j<grid.length;j++) {
			leftClick(i,j);
		}
	}
	restart=new JButton("Again?");
	restart.setBounds((int)(((winsize+1*(double)2/(butsize/15))*butsize-14)/3),(int)(0.25*((winsize+1*(double)2/(butsize/15))*butsize-14)/3),(int)(((winsize+1*(double)2/(butsize/15))*butsize-14)/3),(int)(0.25*((winsize+1*(double)2/(butsize/15))*butsize-14)/3));
	restart.setFont(medium);
	window.add(restart);
	window.add(time);
	restart.addActionListener( new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			window.getContentPane().removeAll();
			window.repaint();
			window.remove(time);
			MS();
		}
	});
}
public void win() {
	//System.out.println(toString()+"\n");
	printGrid();
	//System.out.println();
	window.getContentPane().removeAll();
	timer.stop();
	window.repaint();
	for (int i=0;i<grid.length;i++) {
		for(int j=0;j<grid.length;j++) {
			leftClick(i,j);
		}
	}
	restart=new JButton("You win! Again?");
	restart.setHorizontalTextPosition(SwingConstants.CENTER);
	restart.setBounds(butsize,butsize,butsize*(winsize-2),butsize);
	restart.setFont(mono);
	window.add(restart);
	window.add(time);
	restart.addActionListener( new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			window.getContentPane().removeAll();
			window.repaint();
			MS();
		}
	});

}
public void printGrid(){
	int [][]mat=getGrid();
	for(int i=0;i<grid.length;++i){
		for(int j=0;j<grid[i].length;j++){
			System.out.print((mat[j][i])+" ");
		}
		System.out.println();
	}
	System.out.println();
}
private int[][] getGrid(){
	int[][] mat=new int[grid.length][grid[0].length];
	for(int i=0;i<grid.length;i++){
		for(int j=0;j<grid[i].length;j++){
			mat[i][j]=getCount(i,j);
		}
	}
	return mat;
}
public int getCount( int r, int c){
	int near=0;
	if (grid[r][c]){
		return 9;
	}
	for (int i=-1;i<=1;i++){
		for (int j=-1;j<=1;j++){
			if (inBounds(r+i,c+j) && grid[r+i][c+j]){
				near++;
			}
		}
	}
	return near;
	}
private boolean inBounds( int r, int c){
	if (r>-1 && c>-1 && r<grid.length && c<grid[0].length){
		return true;
	}
	return false;
	}
public int bombsCount(){
	int count=0;
	for(int i=0;i<grid.length;i++){
		for(int j=0;j<grid[i].length;j++){
			if (grid[i][j]){
				count++;
			}
		}
	}
	return count;
}
public int bombsRemaining() {
	int count=0;
	for(int i=0;i<grid.length;i++){
		for(int j=0;j<grid[i].length;j++){
			if (grid[i][j]&&!flagsGrid[i][j]){ //There is bomb there and no flag there
				count++;
			}
		}
	}
	return count;
}
public String toString(){
	String output="";
	for(int i=0;i<grid.length;i++){
		for(int j=0;j<grid[i].length;j++){
			if(grid[i][j]){
				output+=1+" ";
			}
			else{
				output+=0+" ";
			}
		}
		output+="\n";
	}
	return output.trim();
}
}