import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
//import org.eclipse.ui.forms.widgets.ExpandableComposite;

public class Test implements MouseListener{
	static int[] circle(int r, int offsetX, int offsetY) {
	    int[] polygon = new int[8 * r + 4];
	    // x^2 + y^2 = r^2
	    for (int i = 0; i < 2 * r + 1; i++) {
	      int x = i - r;
	      int y = (int) Math.sqrt(r * r - x * x);
	      polygon[2 * i] = offsetX + x;
	      polygon[2 * i + 1] = offsetY + y;
	      polygon[8 * r - 2 * i - 2] = offsetX + x;
	      polygon[8 * r - 2 * i - 1] = offsetY - y;
	    }
	    return polygon;
	  }

	  public static void main(String[] args) {
		  new Test();
	  }
	  
	  public Test(){
		 dostuff();
	  }
	  
	  private void dostuff(){
		 /* final Display display = new Display();
		    // Shell must be created with style SWT.NO_TRIM
		    final Shell shell = new Shell(display);
		    shell.setBackground(display.getSystemColor(SWT.COLOR_RED));

		    ArrayList<String> keywords = new ArrayList<String>();
		    keywords.add("Ik");
		    keywords.add("Haat");
		    keywords.add("Het");
		    keywords.add("Ontwerpproject");
		    keywords.add("!");
		    
		    for(int i=0; i<keywords.size(); i++){
		    	String k = keywords.get(i);
		    	Button b = new Button(shell,SWT.ARROW_LEFT|SWT.CENTER);
		    	b.setText("> "+k);
		    	b.addMouseListener(this);
		    	b.pack();
		    	b.setLocation(150, 150 + 30*i);
		    }
		    
		    Tree tree = new Tree(shell, SWT.MULTI);
		    tree.setLocation(50, 50);
		    
		    // add ability to move shell around
		    Listener l = new Listener() {
		      Point origin;

		      public void handleEvent(Event e) {
		        switch (e.type) {
		        case SWT.MouseDown:
		          origin = new Point(e.x, e.y);
		          break;
		        case SWT.MouseUp:
		          origin = null;
		          break;
		        case SWT.MouseMove:
		          if (origin != null) {
		            Point p = display.map(shell, null, e.x, e.y);
		            shell.setLocation(p.x - origin.x, p.y - origin.y);
		          }
		          break;
		        }
		      }
		    };
		    shell.addListener(SWT.MouseDown, l);
		    shell.addListener(SWT.MouseUp, l);
		    shell.addListener(SWT.MouseMove, l);
		    // add ability to close shell
		    Button b = new Button(shell, SWT.PUSH);
		    b.setBackground(shell.getBackground());
		    b.setText("close");
		    b.pack();
		    b.setLocation(10, 68);
		    b.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event e) {
		        shell.close();
		      }
		    });
		    shell.open();
		    while (!shell.isDisposed()) {
		      if (!display.readAndDispatch())
		        display.sleep();
		    }
		   // region.dispose();
		    display.dispose();*/
		  Display display = new Display ();
	      Color red = display.getSystemColor(SWT.COLOR_RED);
	      Color blue = display.getSystemColor(SWT.COLOR_BLUE);
	      Shell shell = new Shell (display);
	      shell.setLayout(new FillLayout());
	        
	      // set the size of the scrolled content - method 1
	      final ScrolledComposite sc1 = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	      final Composite c1 = new Composite(sc1, SWT.NONE);
	      sc1.setContent(c1);
	      c1.setBackground(red);
	      GridLayout layout = new GridLayout();
	      layout.numColumns = 4;
	      c1.setLayout(layout);
	      Button b1 = new Button (c1, SWT.PUSH);
	      b1.setText("first button");
	      c1.setSize(c1.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	      
	      // set the minimum width and height of the scrolled content - method 2
	      final ScrolledComposite sc2 = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	      sc2.setExpandHorizontal(true);
	      sc2.setExpandVertical(true);
	      final Composite c2 = new Composite(sc2, SWT.NONE);
	      sc2.setContent(c2);
	      c2.setBackground(blue);
	      layout = new GridLayout();
	      layout.numColumns = 4;
	      c2.setLayout(layout);
	      Button b2 = new Button (c2, SWT.PUSH);
	      b2.setText("first button");
	      sc2.setMinSize(c2.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	      
	      Button add = new Button (shell, SWT.PUSH);
	      add.setText("add children");
	      final int[] index = new int[]{0};
	      add.addListener(SWT.Selection, new Listener() {
	          public void handleEvent(Event e) {
	              index[0]++;
	              Button button = new Button(c1, SWT.PUSH);
	              button.setText("button "+index[0]);
	              // reset size of content so children can be seen - method 1
	              c1.setSize(c1.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	              c1.layout();
	              
	              button = new Button(c2, SWT.PUSH);
	              button.setText("button "+index[0]);
	              // reset the minimum width and height so children can be seen - method 2
	              sc2.setMinSize(c2.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	              c2.layout();
	          }
	      });
	 
	      shell.open ();
	      while (!shell.isDisposed ()) {
	          if (!display.readAndDispatch ()) display.sleep ();
	      }
	      display.dispose ();

		  }

	@Override
	public void mouseDoubleClick(MouseEvent arg0) {}

	@Override
	public void mouseDown(MouseEvent arg0) {}

	@Override
	public void mouseUp(MouseEvent m) {
		System.out.println("Faggot");
		if(m.getSource() instanceof Button){
			Button b = (Button)m.getSource();
			if(b.getText().startsWith("> ")){
				b.setText("v " + (b.getText()).substring(2, b.getText().length()));
			}
			else if(b.getText().startsWith("v ")){
				b.setText("> " + (b.getText()).substring(2, b.getText().length()));
			}
		}
	}
	  }


