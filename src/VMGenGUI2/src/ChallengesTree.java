import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;


public class ChallengesTree implements MouseListener{
	
	public static void populateTree(Tree tree){
		updateTree(tree, ModsBookkeeper.getInstance().getSelected());
	}
	
	public static void updateTree(Tree challenges, int selected){
		ChallengesTree ct = new ChallengesTree();
		challenges.addMouseListener(ct);
		Display display = challenges.getDisplay();
		challenges.setBackground(new Color(display, 0,0,0));
		challenges.setForeground(new Color(display, 255, 255, 255));
		challenges.removeAll();
		if(selected != -1){
			Mod selectedMod = ModsBookkeeper.getInstance().getSelectedMods().get(selected-1);
			for (Challenge c : selectedMod.getChallenges()) {
				  TreeItem item = new TreeItem(challenges, SWT.NONE);
			      item.setText("Challenge " + c.getId());
			      TreeItem subItem1 = new TreeItem(item, SWT.CHECK);
			      subItem1.setText(selectedMod.getTags().toString()); // Apparently not
			      
			      TreeItem pointsSub = new TreeItem(item, SWT.NONE);
			      pointsSub.setText("Nr. of points: "+ c.getPoints());
			      
			      String d = c.getDescription();
			      //String d = "kjersg i eghw woegih ewg egkhewjegjg kjdjjvdjnmsd kjdjkds gejgsejhkgoirv jiej srvlkjv nl ejn esvjn ,sdnfkjeee";
				  int width = challenges.getBounds().width;
				  int length = width /6;
				  String[] split = d.split(" ");
				  
				  for(int k = 0; k < split.length; k++){
					  String zin = "";
					  int j;
					  for(j = k ; j < split.length && (zin + split[j]).length() <= length ; j++){
						  zin+= " "+split[j];
					  }
					  TreeItem subItem2 = new TreeItem(item, SWT.WRAP);
				      subItem2.setText(zin);
				      k=j-1;
				  }
			}
		}
		challenges.getShell().redraw();
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDown(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseUp(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
