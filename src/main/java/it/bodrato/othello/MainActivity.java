package it.bodrato.othello;

import java.util.Arrays;
//import java.util.Comparator;
import java.util.Random;


import android.os.Bundle;
import android.os.Vibrator;
//import android.provider.MediaStore.Images;
import android.app.Activity;
//import android.util.DisplayMetrics;
//import android.text.style.LineHeightSpan.WithDensity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private TextView output;
	
	private boolean gamelock = false;
	
	
	private int x;
	private int mx,my;
	

	
	public char board[] = new char[64];

	//private int handicap;
	//private char selfplay;		/* true if computer playing with itself */

	//private int h[] = new int[8];		/* handicap position table */

	private char mine, his;		/* who has black (*) and white (@) in current game */
//	private char mefirst;		/* true if computer goes first in current game */

	private char BLACK = '*';
	private char WHITE = 'O';
	private char EMPTY = '-';

	private Random rand = new Random(); 

	public class  mt implements Comparable<mt> {
	//public class  mt  {

		int x;
		int y;
		int c;
		int s;
		
		//@Override
		public int compareTo(mt other) {
			//System.out.println ("Comparing");
			if (this.s > other.s) return -1;
			if (this.s < other.s) return 1;
			if (this.c > other.c) return -1;
			if (this.c < other.c) return 1;
			return 0;
		}
		
	 }

/*	
	public static Comparator<mt> mtComparator = new Comparator<mt>() {

		public int compare(mt mt1, mt mt2) {
			if (mt1.s > mt2.s) return -1;
			if (mt1.s < mt2.s) return 1;
			if (mt1.c > mt2.c) return -1;
			if (mt1.c < mt2.c) return 1;
			return 0;
			
		}
	};
*/
	
	private int chkmv1( char b[], char p, int x, int y, int m, int n )
	{
		int k;
		k=0;
		while ((x += m) >= 0 && x < 8 && (y += n) >= 0 && y<8)
		{
			if (b[x*8+y]==EMPTY) return 0;
			if (b[x*8+y]== p ) return k;
			if (x==0 || x==7 || y==0 || y==7)
				k += 10;
			 else k++;
		}
		return 0;
	}
	
	private int chkmov( char b[], char p, int x, int y )
	{
		if (b[x*8+y] != EMPTY) return 0;
		return	chkmv1(b,p,x,y,0,1) + chkmv1(b,p,x,y,1,0) +
			chkmv1(b,p,x,y,0,-1)+ chkmv1(b,p,x,y,-1,0)+
			chkmv1(b,p,x,y,1,1) + chkmv1(b,p,x,y,1,-1)+
			chkmv1(b,p,x,y,-1,1)+ chkmv1(b,p,x,y,-1,-1);
	}


	private int chkmvs( char b[], char p )
	{
		int i,j,k;
		k=0;
		for (i=0; i<8; i++) for (j=0; j<8; j++)
			k += chkmov(b,p,i,j);
		return k;
	}
	
	
	private void cpybrd(char a[], char b[])
	{
		int x;
		for (x=0; x<64; x++)
			a[x]=b[x];
	}
	

	private int cntbrd(char b[], char p)
	{
		int i,k;
		i=64;k=0;
		while (i-->0)
			if (b[i] == p) ++k;
		return (k);
	}


	private void clrbrd(char b[])
	{
		int i,j;
		for (i=0; i<8; i++)
			for (j=0; j<8; j++)
				b[i*8+j]= EMPTY;
		b[27] = b[36] = BLACK;
		b[28] = b[35] = WHITE;
	}
	
	private void prtbrd(char b[])
	{
		ImageButton abutton;
		for (x=0 ; x!=64; x++) {
	        abutton = (ImageButton) (super.findViewById(R.id.imageButton1+x));
	        switch (b[x]) {
		        case '*':
		        	abutton.setImageResource(R.drawable.blue);
		        	break;
		        case 'O':
		        	abutton.setImageResource(R.drawable.red);
		        	break;
		        default:
		        	abutton.setImageResource(R.drawable.blank);
		        	break;
		        }
	        }
	}
	
	
	private void putmv1(char b[], char p,int x, int y, int m, int n)
	{
		while ((x += m) >= 0 && x<8 && (y += n)>=0 && y<8) {
			if (b[x*8+y] == EMPTY || b[x*8+y] == p) return;
			b[x*8+y] = p;
		 }
	}

	private void putmov(char b[], char p, int x, int y)
	{
		int i,j;
		b[x*8+y] = p;
		for (i= -1; i<=1; i++) for (j= -1; j<=1; j++) {
			if ((i != 0 || j!=0)&&chkmv1(b,p,x,y,i,j)>0)
				putmv1(b,p,x,y,i,j);
		 }
	}
	
	private char notak2(char b[], char p,char o,char e, int x, int y,int m,int n)
	{
		x += m; y +=n;
		if (x>=0 && x<=7 && y>=0 && y<=7)
			while(b[x*8+y] == 0) {
			 x += m; y+=n;
			 if (x<0 || x>7 || y<0 || y>7 || b[x*8+y]==e)
				return o;
			 }
		while (x>=0 && x<=7 && y>=0 && y<=7 && b[x*8+y]==p)
				{ x +=m; y+=n; }
		if (x<0 || x>7 || y<0 || y>7) return p;
		return b[x*8+y];
	}
	
	private boolean notak1(char b[], char p,char o,char e,int x,int y,int m,int n)
	{
		int c1,c2;
		c1 = notak2(b,p,o,e,x,y,m,n);
		c2 = notak2(b,p,o,e,x,y,-m,-n);
		return !(c1==o && c2==e || c1==e && c2==o);
	}

	private boolean notake(char b[],char p,char o,char e,int x,int y)
	{
		return notak1(b,p,o,e,x,y,0,1) && notak1(b,p,o,e,x,y,1,1) && notak1(b,p,o,e,x,y,1,0) && notak1(b,p,o,e,x,y,1,-1);
	}
	
	private int s_move(char b[], char p, char o, char e, int i, int j)
	{
		char a[] = new char[64];
		int ok,s,k,l,side,oside;
		int c,dkl;
		cpybrd(a,b);
		putmov(a,p,i,j);
		side = 0;
		if (i==0 || i==7) side++;
		if (j==0 || j==7) side++;
		s = 0;
		ok = 0;
		if (side==2 || notake(b,p,o,e,i,j)) ok++;
		oside = 0;
		for (k=0; k<8; k++) for(l=0; l<8; l++)
		 {
			c=chkmov(a,o,k,l);
			if (c==0) continue;
			dkl = 1;
			if (k==0 || k==7) { dkl+=2; oside|=4;}
			if (l==0 || l==7) {dkl+=2; oside|=4; }
			if (dkl==5) {dkl = 10; oside |= 16; }
				else if (!notake(a,o,p,e,k,l))
						continue;
			oside |= 1;
			s -= dkl;
			if (c>=10) { s -= 4; oside |= 8; }
			}
		if (s< -oside) s= -oside;
		if (side>0) return s+side-7+10*ok;
		if (i==1 || i==6) {s--; side++;}
		if (j==1 || j==6) {s--; side++;}
		if (side>0) return s;
		if (i==2 || i==5) s++;
		if (j==2 || j==5) s++;
		return s;
	}
	
	//private int fillmt(char b[64], char p, char o, char e, struct mt t[64])
	private int fillmt(char b[], char p, char o, char e, mt t[])
	{
		int i,j,k,c;
		//i=0; j=0;
		//chkmov(b,p,i,j);
		
		//System.out.println (t.length);
		//System.out.println (t[0].x);
		//System.out.println (t.length);
		for (i=0; i<64; i++) {
			t[i] = new mt();
			t[i].s = -2;
			t[i].c = -2;
		}
		
		k = 0;
		
		for (i=0; i<8; i++) for(j=0; j<8; j++) {
			c=chkmov(b,p,i,j);
			if (c!=0) {
				t[k].c = c;
				t[k].x = i;
				t[k].y = j;
				t[k].s = s_move(b,p,o,e,i,j);
				++k;
				/*
				System.out.print("---");
				System.out.print(k);
				System.out.print(":");
				System.out.print(t[k].x);
				System.out.print(",");
				System.out.print(t[k].y);
				System.out.print(" -s- ");
				System.out.print(t[k].s);
				System.out.print(" -c- ");
				System.out.println(t[k].c);
				*/
			}
		}
		return k;
	}
	
/*	
	private void analyze(char b[], char p, char o, char e)
	{
		mt[] t = new mt[64];
		char[] a = new char[64];
		int i,k,c;
		k = fillmt(b,p,o,e,t);
		cpybrd(a,b);
		for (i=0; i<k; i++) {
			c = 'F' - t[i].s;
			if (c > 'Z') c= 'Z';
			a[t[i].x*8+t[i].y]=(char)c;
		}
		prtbrd(a);
	}
*/

	/*
	private int cmpmov(mt a, mt b)
	{
		if (a.s > b.s) return -1;
		if (a.s < b.s) return 1;
		if (a.c > b.c) return -1;
		if (a.c < b.c) return 1;
		return 0;
	}
	*/

	
	//private int my_mov(char b[], char p,char o,char e,int *m,int *n)
	//private int my_mov(char b[], char p,char o,char e,int m,int n)
	private int my_mov(char b[], char p,char o,char e)
	{
		mt t[] = new mt[64];
		int i,k;

		k = fillmt(b,p,o,e,t);
		if (k==0) return 0;
		
		Arrays.sort(t);
		//Arrays.sort(t, mtComparator);
		//qsort (t, k);
		
		for (i=0; i<=k; i++) {
			/*if (t[k].s!= -2) {
			System.out.print(i);
			System.out.print(":");
			System.out.print(t[i].x);
			System.out.print(",");
			System.out.print(t[i].y);
			System.out.print(" -s- ");
			System.out.print(t[i].s);
			System.out.print(" -c- ");
			System.out.println(t[i].c);
			}*/
			if (t[i].s != t[0].s || t[i].c != t[0].c)
							break;
		}
		
		k = rand.nextInt(i);
/*
		*m = t[k].x;
		*n = t[k].y;
*/
/*
		System.out.print(k);
		System.out.print("!");
		System.out.print(t[k].x);
		System.out.print(",");
		System.out.println(t[k].y);
		System.out.print(t[k+1].x);
		System.out.print(",");
		System.out.println(t[k+1].y);
	*/	
		if (t[k].c == -2 || t[k].s == -2)
			return 0;

		mx = t[k].x;
		my = t[k].y;

		return 1;
	}

	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        this.output = (TextView) super.findViewById(R.id.output);

		ImageButton abutton;
		for (x=0 ; x!=64; x++) {
	        abutton = (ImageButton) (super.findViewById(R.id.imageButton1+x));
	        abutton.setOnClickListener(this);
		}

        Button button = (Button) super.findViewById(R.id.button);
        button.setOnClickListener(this);
        Button button2 = (Button) super.findViewById(R.id.button2);
        button2.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {

        ImageButton abutton;
        int x, y;
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        x = 3;
        y = 3;
        if (arg0 == super.findViewById(R.id.button2)) {
            v.vibrate(50);
        }

        if (arg0 == super.findViewById(R.id.button)) {
            MainActivity.this.output.setText(R.string.no_winner);

            //	ImageButton abutton;
            //	 DisplayMetrics metrics = new DisplayMetrics();
            //	 getWindowManager().getDefaultDisplay().getMetrics(metrics);

            //h[0] = h[1] = h[4] = h[7] = 0;
            //h[2] = h[3] = h[5] = h[6] = 7;

            clrbrd(board);

            //for (x=0 ; x!=64; x++) board[x] = EMPTY;

            // I go first
            mine = BLACK;
            his = WHITE;

            prtbrd(board);
            //int i=0;
            //int j=0;

            //my_mov(board,his,mine,EMPTY);
            //putmov(board,his,mx,my);


            //prtbrd(board);
            //putmov(board,his,4,5);
            //prtbrd(board);


            gamelock = false;
            //printboard();
        } else {
/*
			if (chkmvs(board,his)==0 && chkmvs(board,mine)==0) {
				gamelock=true;
				if (cntbrd(board,his) > cntbrd(board,mine))
					MainActivity.this.output.setText(R.string.me_win);
				else
					if (cntbrd(board,his) == cntbrd(board,mine))
							MainActivity.this.output.setText(R.string.no_winner);
						else
							MainActivity.this.output.setText(R.string.you_win);
			}
*/
            if (!gamelock) {
                /* PASS key ? */
                if (arg0 == super.findViewById(R.id.button2)) {
                    v.vibrate(40);
                    chkmov(board, mine, x, y);

                } else {
                    abutton = (ImageButton) arg0;
                    int pos = abutton.getId() - R.id.imageButton1;
                    //if (board[pos]==EMPTY) {
                    x = pos / 8;
                    y = pos - (8 * (pos / 8));
                }
                /* PASS key ? */
//                System.out.println (chkmov(board,mine,x,y));
//                System.out.println (x);
//                System.out.println (y);
                if ((arg0 == super.findViewById(R.id.button2)) || (chkmov(board, mine, x, y) == 0)) {
                    //if (arg0 == super.findViewById(R.id.button2)) {
                    //System.out.println ("putmov");
                    putmov(board, his, x, y);
                }
                prtbrd(board);

                v.vibrate(30);

                //cntbrd(b,his)
                if (cntbrd(board, his) > cntbrd(board, mine))
                    MainActivity.this.output.setText(R.string.me_winning);
                else if (cntbrd(board, his) == cntbrd(board, mine))
                    MainActivity.this.output.setText(R.string.no_winner);
                else
                    MainActivity.this.output.setText(R.string.you_winning);

                if (((chkmvs(board, his) == 0) && (chkmvs(board, mine) == 0)) && (arg0 == super.findViewById(R.id.button2)))
                    MainActivity.this.output.setText(R.string.forfeit);
                else {
                    if (my_mov(board, his, mine, EMPTY) == 1)
                        putmov(board, his, mx, my);
                    //else
                    //    MainActivity.this.output.setText(R.string.forfeit);
                }


            }

				/*while (chkmvs(board,mine)!=0)
				{
					if (chkmvs(board,his)==0)
						MainActivity.this.output.setText(R.string.forfeit);
					else {
						my_mov(board,his,mine,EMPTY);
						putmov(board,his,mx,my);
					}
				}*/

				/* Check for winners AFTER the last played move */
            if (chkmvs(board, his) == 0 && chkmvs(board, mine) == 0) {
                gamelock = true;
                if (cntbrd(board, his) > cntbrd(board, mine))
                    MainActivity.this.output.setText(R.string.me_win);
                else if (cntbrd(board, his) == cntbrd(board, mine))
                    MainActivity.this.output.setText(R.string.no_winner);
                else
                    MainActivity.this.output.setText(R.string.you_win);
            }

            //printboard();
            prtbrd(board);


        }
    }
}
		
		//if (arg0 == super.findViewById(R.id.imageButton1)) super.setContentView("@drawable/blank");
		//R.drawable.modeitemnormal
		//arg0.setImageResource();
		//this.setContentView("@drawable/blank", null)

		//if (arg0 == super.findViewById(R.id.imageButton1)) R.id.imageButton1.setImageResource(R.drawable.blue);

		
