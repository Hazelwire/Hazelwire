package org.hazelwire.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.wb.swt.SWTResourceManager;
import org.hazelwire.main.Configuration;
import org.hazelwire.main.Generator;
import org.hazelwire.modules.Option;

/**
 * This class is responsible for drawing the VM generation GUI. It
 * consists of a large method to draw the entire GUI and a collection of
 * smaller methods to update parts of it. In order to be able to do this,
 * it is a subclass of both {@link Observer} and {@link ControlListener}.
 */
public class GUIBuilder implements Observer, ControlListener {

	//Heb CONFIGTEXTWIDTH 20 gemaakt, omdat 10 een beetje klein was.
	private static int CONFIGTEXTWIDTH = 20;
	private Display display;
	private Label label, modName, lblTotalAmountOf;
	private Composite composite_7;
	private Tree challenges, tree;
	private Combo combo;
	private PointsListener pl;
	private Text packages;
	private Text text_1;
	private Text text_filepath;
	private Text txtHazelwireVm;
	private Text text_output;
	private Canvas canvas, tags, canvas_1;
	private static GUIBuilder instance;
	private Shell shell;
	private GridData gd_8;
	private HashMap<String, Text> textMap = new HashMap<String, Text>();
	private ProgressBar progressBar;

	/**
	 * The main method that starts the VM generation system by getting the
	 * GUIBuilder instance and calling the init method to add all {@link Widget}s
	 * to it.
	 * @param args Standard array of {@link String}s parameter. This array is not 
	 * used in our system.
	 */
	public static void main(String[] args) {
		GUIBuilder builder = GUIBuilder.getInstance();
		builder.init();
	}
	
	/**
	 * This method checks whether an instance of GUIBuilder exists. Be that the case,
	 * it returns this instance. Otherwise, it calls the private constructor method,
	 * saves the newly created GUIBuilder and returns it.
	 * @return the instance of GUIBuilder.
	 */
	public synchronized static GUIBuilder getInstance() {
		if (!(instance instanceof GUIBuilder)) {
			instance = new GUIBuilder();
		}
		return instance;
	}
	
	/**
	 * Private GUIBuilder constructor that instantiates GUIBuilder. The constructor is
	 * private because the Singleton pattern is being used so as to make sure that only 
	 * one instance of the GUI is created and used.
	 */
	private GUIBuilder() {}

	/**
	 * The init method first gets the {@link ModsBookkeeper} instance and adds itself
	 * as an {@link Observer}. Next, it creates a {@link Shell}, adds itself as a 
	 * {@link ControlListener}, calls addGUIElements to add all necessary {@link Widget}s 
	 * and displays the shell until the user closes it, or until the generation process
	 * is completed.
	 */
	public void init() {
		Generator.getInstance();
		
		try
		{
			ModsBookkeeper.getInstance().initMods(GUIBridge.getModulesForGUI());
			ModsBookkeeper.getInstance().addObserver(this);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		display = new Display();
		shell = new Shell(display, SWT.SHELL_TRIM);
		this.addGUIElements(shell);
		shell.addControlListener(this);
		shell.pack();
		shell.open();
		shell.setText("Hazelwire Beta v1.0");
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		try
		{
			Generator.getInstance().setKeepGenerating(false);
			Generator.getInstance().shutDown(false,false);
			System.exit(0);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * This method adds all GUI {@link Widget}s to the provided {@link Shell} and,
	 * where necessary, adds Listeners to elements.
	 * @param shell the {@link Shell} that the elements will be added to.
	 * @requires shell != null
	 */
	private void addGUIElements(Shell shell) {
		GridLayout gl_shell = new GridLayout(1, false);
		gl_shell.horizontalSpacing = 0;
		gl_shell.verticalSpacing = 0;
		gl_shell.marginHeight = 0;
		gl_shell.marginWidth = 0;
		shell.setLayout(gl_shell);

		Composite all = new Composite(shell, SWT.NONE);
		all.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		GridLayout gl_all = new GridLayout(1, false);
		gl_all.horizontalSpacing = 0;
		gl_all.verticalSpacing = 0;
		gl_all.marginHeight = 0;
		gl_all.marginWidth = 0;
		all.setLayout(gl_all);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		all.setLayoutData(gd);

		TabFolder tabFolder = new TabFolder(all, SWT.TOP);
		tabFolder.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		gd = new GridData();
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		tabFolder.setLayoutData(gd);

		TabItem modselection = new TabItem(tabFolder, SWT.NULL);
		modselection.setText("Module Selection");

		SashForm sash = new SashForm(tabFolder, SWT.VERTICAL);
		sash.setSashWidth(2);
		sash.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));

		GridLayout gl = new GridLayout();

		sash.setLayout(gl);
		TagTree tt = new TagTree();
		GridLayout rightGrid = new GridLayout(1, true);
		ModListPainter p = new ModListPainter(this);
		Searcher searcher = new Searcher(this);
		modselection.setControl(sash);

		SashForm sashForm = new SashForm(sash, SWT.NONE);
		sashForm.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		sashForm.setSashWidth(2);

		Composite composite_10 = new Composite(sashForm, SWT.NONE);
		composite_10.setLayout(new GridLayout(2, false));
		tree = new Tree(composite_10, SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		tree.setSize(353, 293);
		tree.addMouseListener(tt);
		tree.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		tree.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_FOREGROUND));

		TagTree.populateTree(tree);

		Button importB = new Button(composite_10, SWT.PUSH | SWT.CAP_ROUND);
		importB.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		importB.setSize(86, 23);
		importB.setText("Import modules");
		importB.setLocation(0, 370);
		importB.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));

		Text search = new Text(composite_10, SWT.BORDER);
		search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		search.setSize(270, 19);
		search.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		search.setText("search...");
		search.addKeyListener(searcher);
		search.setToolTipText("Type search phrase and hit return.\nIn order to see all tags, enter nothing and hit return.");
		search.addFocusListener(searcher);

		Composite wrapper = new Composite(sashForm, SWT.None);
		wrapper.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		wrapper.addControlListener(this);
		GridLayout grid = new GridLayout(1, false);
		grid.marginHeight = 2;
		grid.horizontalSpacing = 2;
		grid.marginWidth = 2;
		grid.verticalSpacing = 2;
		wrapper.setLayout(grid);
		Display display = wrapper.getDisplay();

		ScrolledComposite selectedComp = new ScrolledComposite(wrapper,
				SWT.V_SCROLL);
		selectedComp.setLayout(rightGrid);
		canvas = new Canvas(selectedComp, SWT.NONE);
		canvas.addPaintListener(p);
		canvas.addMouseListener(p);
		selectedComp.setContent(canvas);
		canvas.setSize(200, 300);
		selectedComp.setMinWidth(200);
		selectedComp.setExpandHorizontal(true);
		selectedComp.setAlwaysShowScrollBars(true);
		selectedComp.getVerticalBar().setIncrement(15);
		gd_8 = new GridData();
		gd_8.grabExcessVerticalSpace = true;
		gd_8.grabExcessHorizontalSpace = true;
		gd_8.horizontalAlignment = SWT.FILL;
		gd_8.verticalAlignment = SWT.FILL;
		selectedComp.setLayoutData(gd_8);

		label = new Label(wrapper, SWT.CENTER);
		label.setText("Total amount of points: "
				+ ModsBookkeeper.getInstance().getTotalPoints() + "\t");
		label.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		label.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		label.setAlignment(SWT.LEFT);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.CENTER;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		label.setLayoutData(gd);
		importB.addMouseListener(new ImportMouseListener());
		sashForm.setWeights(new int[] { 1, 1 });

		// RANDOM CANVAS. ONDERSTE DEEL SASHFORM
		Composite mod = new Composite(sash, SWT.NONE);
		mod.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		mod.setLayout(new GridLayout(1, false));
		mod.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		mod.addControlListener(this);

		modName = new Label(mod, SWT.CENTER);
		modName.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		modName.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		int selected = ModsBookkeeper.getInstance().getSelected();
		if (selected != -1) {
			modName.setText("Module: "
					+ ModsBookkeeper.getInstance().getSelectedMods()
							.get(selected).getName());
		} else {
			modName.setText("No Module selected");
		}
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalSpan = 1;
		modName.setLayoutData(gd);

		TabFolder moddetails = new TabFolder(mod, SWT.TOP);
		moddetails.setSize(moddetails.computeSize(200, 250));
		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		moddetails.setLayoutData(gd);

		TabItem description = new TabItem(moddetails, SWT.NULL);
		description.setText("Description");

		SashForm sashForm_1 = new SashForm(moddetails, SWT.NONE);
		description.setControl(sashForm_1);
		sashForm_1.setSashWidth(2);
		sashForm_1.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));

		Composite composite_11 = new Composite(sashForm_1, SWT.NONE);
		composite_11.setLayout(new GridLayout(1, false));

		Label tagt = new Label(composite_11, SWT.LEFT);
		tagt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tagt.setSize(135, 13);
		tagt.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		tagt.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		tagt.setText("Tags");

		ScrolledComposite tagswrap = new ScrolledComposite(composite_11,
				SWT.V_SCROLL);
		GridData gd_tagswrap = new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1);
		gd_tagswrap.widthHint = 125;
		tagswrap.setLayoutData(gd_tagswrap);
		tagswrap.setSize(135, 250);
		tagswrap.setAlwaysShowScrollBars(true);
		tagswrap.setMinWidth(110);
		tagswrap.setExpandHorizontal(true);
		tagswrap.setExpandVertical(false);
		tagswrap.getVerticalBar().setIncrement(15);
		tagswrap.setBackground(display.getSystemColor(SWT.COLOR_LIST_SELECTION));
		tagswrap.setLayout(new GridLayout(1, true));

		tags = new Canvas(tagswrap, SWT.NONE);
		tags.setSize(tags.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		TagListPainter tlp = new TagListPainter();

		tags.addPaintListener(tlp);
		tagswrap.setContent(tags);

		Composite composite_12 = new Composite(sashForm_1, SWT.NONE);
		composite_12.setLayout(new GridLayout(1, false));

		Label chals = new Label(composite_12, SWT.LEFT);
		chals.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		chals.setSize(342, 13);
		chals.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		chals.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		chals.setText("Challenges");

		challenges = new Tree(composite_12, SWT.BORDER | SWT.MULTI);
		GridData gd_challenges = new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1);
		gd_challenges.widthHint = 250;
		challenges.setLayoutData(gd_challenges);
		challenges.setSize(342, 250);
		challenges.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		ChallengesTree.populateTree(challenges);

		Composite composite_13 = new Composite(sashForm_1, SWT.NONE);
		composite_13.setLayout(new GridLayout(1, false));

		Label packs = new Label(composite_13, SWT.LEFT);
		packs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		packs.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		packs.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		packs.setText("Packages");

		packages = new Text(composite_13, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP
				| SWT.V_SCROLL | SWT.MULTI);
		GridData gd_packages = new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1);
		gd_packages.widthHint = 250;
		packages.setLayoutData(gd_packages);
		packages.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		packages.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		packages.setText("Select a module and the packages associated with it will be displayed here");
		sashForm_1.setWeights(new int[] { 147, 264, 275 });

		gd = new GridData();

		TabItem tbtmSettings = new TabItem(moddetails, SWT.NONE);
		tbtmSettings.setText("Settings");

		Composite wrapper3 = new Composite(moddetails, SWT.NONE);
		tbtmSettings.setControl(wrapper3);
		wrapper3.setLayout(new GridLayout(2, false));
		// FIXME make this say module name
		combo = new Combo(wrapper3, SWT.NONE);
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.widthHint = 125;
		gd.verticalSpan = 1;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.LEFT;
		combo.setLayoutData(gd);

		ScrolledComposite optionsWrapper = new ScrolledComposite(wrapper3,
				SWT.V_SCROLL | SWT.BORDER);
		optionsWrapper.setAlwaysShowScrollBars(true);
		optionsWrapper.setMinWidth(500);
		optionsWrapper.setExpandHorizontal(true);
		optionsWrapper.setExpandVertical(true);
		optionsWrapper.getVerticalBar().setIncrement(15);
		optionsWrapper.setLayout(new GridLayout(1, true));
		gd = new GridData();
		gd.widthHint = 500;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.verticalSpan = 3;
		optionsWrapper.setLayoutData(gd);

		composite_7 = new Composite(optionsWrapper, SWT.BORDER);
		composite_7.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		composite_7.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));

		composite_7.setLayout(new GridLayout(1, true));
		Mod selectedMod = ModsBookkeeper.getInstance().getSelectedMod();
		if (selectedMod != null) {
			HashMap<String, Option> options = selectedMod.getOptions();
			for (Option s : options.values()) {
				new OptionsComposite(composite_7, SWT.NONE, s, selectedMod);
			}
		}

		optionsWrapper.setContent(composite_7);
		optionsWrapper.setMinSize(composite_7.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));

		Composite pointsComp = new Composite(wrapper3, SWT.NONE);
		pointsComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_pointsComp = new GridLayout(3, false);
		gl_pointsComp.horizontalSpacing = 0;
		gl_pointsComp.marginHeight = 0;
		gl_pointsComp.verticalSpacing = 0;
		gl_pointsComp.marginWidth = 0;
		pointsComp.setLayout(gl_pointsComp);

		Label points = new Label(pointsComp, SWT.CENTER | SWT.CENTER);
		points.setText("Points");
		gd = new GridData();
		gd.widthHint = 70;
		gd.verticalAlignment = SWT.CENTER;
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.verticalSpan = 2;
		points.setLayoutData(gd);

		Text pointsText = new Text(pointsComp, SWT.BORDER | SWT.CENTER);
		pointsText.setText("");
		pointsText.setData("pointsText");
		pointsText.setSize(points.computeSize(50, 20));
		gd = new GridData();
		gd.widthHint = 50;
		gd.verticalAlignment = SWT.CENTER;
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.verticalSpan = 2;
		pointsText.setLayoutData(gd);

		pl = new PointsListener(pointsText, combo, this);
		pointsText.addKeyListener(pl);
		combo.addSelectionListener(pl);

		Button up = new Button(pointsComp, SWT.FLAT | SWT.ARROW);
		up.setData("upArrow");
		up.addMouseListener(pl);
		gd = new GridData();
		gd.verticalSpan = 1;
		up.setLayoutData(gd);

		Button down = new Button(pointsComp, SWT.ARROW | SWT.DOWN);
		down.setData("downArrow");
		down.addMouseListener(pl);
		gd = new GridData();
		gd.verticalSpan = 1;
		down.setLayoutData(gd);

		Button defaultB = new Button(wrapper3, SWT.PUSH | SWT.CAP_ROUND);
		defaultB.setText("Defaults");
		defaultB.setData("defaultB");
		defaultB.addMouseListener(pl);
		gd = new GridData();
		gd.verticalAlignment = SWT.DOWN;
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalSpan = 2;
		defaultB.setLayoutData(gd);

		Button btnDefaults = new Button(wrapper3, SWT.NONE);
		btnDefaults.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnDefaults.setText("Defaults");
		btnDefaults
				.addMouseListener(new DefaultMouseListener(composite_7));
		sash.setWeights(new int[] { 1, 1 });

		TabItem tbtmConfiguration_1 = new TabItem(tabFolder, SWT.NONE);
		tbtmConfiguration_1.setText("Configuration");

		Composite composite_9 = new Composite(tabFolder, SWT.NONE);
		composite_9.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		tbtmConfiguration_1.setControl(composite_9);
		composite_9.setLayout(new GridLayout(1, false));

		Composite composite_8 = new Composite(composite_9, SWT.BORDER);
		composite_8.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true,
				true, 1, 1));
		composite_8.setLayout(new GridLayout(3, true));

		ConfigListener cl = new ConfigListener(this);
		
		try
		{
			Enumeration<?> propertyEnum = Configuration.getInstance().getRawProperties().propertyNames();
			
			while(propertyEnum.hasMoreElements())
			{
				String key = (String) propertyEnum.nextElement();
				
				Label lblNewLabel_3 = new Label(composite_8, SWT.NONE);
				lblNewLabel_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true,
						false, 1, 1));
				lblNewLabel_3.setText(key);
				
				Text tempText = new Text(composite_8, SWT.BORDER);
				//Set the fixed maximum width so longer values don't screw up the interface
				GC gc = new GC(tempText);
			    FontMetrics fm = gc.getFontMetrics();
			    int width = CONFIGTEXTWIDTH * fm.getAverageCharWidth();
			    int height = fm.getHeight();
			    gc.dispose();
			    tempText.setSize(tempText.computeSize(width, height));
			    
			    //FIXME: Niks, maar hier staat de oplossing: Ik heb de layoutdata van de Text aangepast
			    //en de breedte op je ding gezet.
			    gd = new GridData();
			    gd.widthHint = width;
			    tempText.setLayoutData(gd);
			    
			    
				//tempText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				//		1));
				tempText.setData(key);
				tempText.addFocusListener(cl);
				textMap.put((String) tempText.getData(), tempText);		    

				Button tempBtnDefault = new Button(composite_8, SWT.NONE);
				tempBtnDefault.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
						false, 1, 1));
				tempBtnDefault.setText("Default");
				tempBtnDefault.setData(key);
				tempBtnDefault.addMouseListener(cl);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		Button btnDefaults_1 = new Button(composite_8, SWT.NONE);
		btnDefaults_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 3, 1));
		btnDefaults_1.setText("Defaults");
		btnDefaults_1.setData("Defaults");
		btnDefaults_1.addMouseListener(cl);

		for (String key : textMap.keySet()) {
			this.updateConfig(key);
		}

		TabItem tbtmVmGeneration = new TabItem(tabFolder, SWT.NONE);
		tbtmVmGeneration.setText("VM Generation");

		SashForm sashForm_2 = new SashForm(tabFolder, SWT.NONE);
		sashForm_2.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		tbtmVmGeneration.setControl(sashForm_2);
		sashForm_2.setSashWidth(2);
		sashForm_2.setOrientation(SWT.VERTICAL);

		Composite composite_14 = new Composite(sashForm_2, SWT.NONE);
		composite_14.setLayout(new GridLayout(1, false));

		Composite composite_4 = new Composite(composite_14, SWT.NONE);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		composite_4.setSize(732, 13);
		GridLayout gl_composite_4 = new GridLayout(3, false);
		gl_composite_4.horizontalSpacing = 10;
		gl_composite_4.verticalSpacing = 0;
		gl_composite_4.marginWidth = 0;
		gl_composite_4.marginHeight = 0;
		composite_4.setLayout(gl_composite_4);

		Label label_1 = new Label(composite_4, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		label_1.setText("Overview1");

		Label lblBaseVm = new Label(composite_4, SWT.NONE);
		lblBaseVm.setText("Base VM");

		Label label_3 = new Label(composite_4, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		label_3.setText("Overview2");

		Composite composite_1 = new Composite(composite_14, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true,
				true, 1, 1));
		composite_1.setSize(296, 222);
		composite_1.setLayout(new GridLayout(3, false));

		Button btnDownload = new Button(composite_1, SWT.NONE);
		btnDownload.setText("Download");
		btnDownload.addMouseListener(new DownLoadListener());

		progressBar = new ProgressBar(composite_1, SWT.SMOOTH);
		GridData gd_progressBar = new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 2, 1);
		gd_progressBar.widthHint = 222;
		progressBar.setLayoutData(gd_progressBar);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);

		text_1 = new Text(composite_1, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		text_1.setEnabled(false);
		text_1.setText("Press the download button to download the default Hazelwire VM.\nYou can also locate a VM on your hard drive, for example if you have already downloaded the default VM. Usage of a non-default VM is not supported and may not work.");
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, false, false,
				3, 1);
		gd_text_1.widthHint = 262;
		gd_text_1.heightHint = 150;
		text_1.setLayoutData(gd_text_1);

		text_filepath = new Text(composite_1, SWT.BORDER);
		text_filepath.setText("");
		GridData gd_text_2 = new GridData(SWT.FILL, SWT.CENTER, false, false,
				2, 1);
		gd_text_2.widthHint = 200;
		text_filepath.setLayoutData(gd_text_2);
		text_filepath.addMouseListener(new BrowseMouseListener(text_filepath));

		Button btnBrowse = new Button(composite_1, SWT.NONE);
		btnBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnBrowse.setText("Browse");
		btnBrowse.addMouseListener(new BrowseMouseListener(text_filepath));
		
		composite_1.layout();
		
		Composite composite_15 = new Composite(sashForm_2, SWT.NONE);
		composite_15.setLayout(new GridLayout(1, false));

		Composite composite_3 = new Composite(composite_15, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		GridLayout gl_composite_3 = new GridLayout(3, false);
		gl_composite_3.horizontalSpacing = 10;
		gl_composite_3.verticalSpacing = 0;
		gl_composite_3.marginWidth = 0;
		gl_composite_3.marginHeight = 0;
		composite_3.setLayout(gl_composite_3);

		Label lblOverview = new Label(composite_3, SWT.SEPARATOR
				| SWT.HORIZONTAL);
		lblOverview.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		lblOverview.setText("Overview1");

		Label lblNewLabel = new Label(composite_3, SWT.NONE);
		lblNewLabel.setText("Overview");

		Label lblOverview_1 = new Label(composite_3, SWT.SEPARATOR
				| SWT.HORIZONTAL);
		lblOverview_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		lblOverview_1.setText("Overview2");

		SashForm sashForm_3 = new SashForm(composite_15, SWT.NONE);
		sashForm_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		sashForm_3.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		sashForm_3.setSashWidth(2);
		sashForm_3.setBounds(0, 0, 64, 64);

		Composite composite_5 = new Composite(sashForm_3, SWT.NONE);
		composite_5.setLayout(new GridLayout(2, false));

		Label lblNewLabel_1 = new Label(composite_5, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel_1.setText("VM Base Name");

		txtHazelwireVm = new Text(composite_5, SWT.BORDER);
		txtHazelwireVm.setEnabled(true);
		txtHazelwireVm.setText("HazelwireTest");
		GridData gd_txtHazelwireVm = new GridData(SWT.RIGHT, SWT.CENTER, true,
				false, 1, 1);
		gd_txtHazelwireVm.widthHint = 100;
		txtHazelwireVm.setLayoutData(gd_txtHazelwireVm);
		txtHazelwireVm.addMouseListener(new VMNameListener());

		text_output = new Text(composite_5, SWT.BORDER | SWT.READ_ONLY
				| SWT.WRAP | SWT.MULTI);
		GridData gd_text_4 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_text_4.widthHint = 300;
		gd_text_4.heightHint = 266;
		text_output.setLayoutData(gd_text_4);

		Composite composite_2 = new Composite(sashForm_3, SWT.NONE);
		composite_2.setLayout(new GridLayout(1, false));
		composite_2.addControlListener(this);

		ScrolledComposite scrolledComposite = new ScrolledComposite(
				composite_2, SWT.V_SCROLL);
		scrolledComposite.setAlwaysShowScrollBars(true);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.getVerticalBar().setIncrement(15);

		canvas_1 = new Canvas(scrolledComposite, SWT.NONE);
		canvas_1.setSize(canvas_1.computeSize(200, 300));
		OverviewPainter op = new OverviewPainter();
		canvas_1.addPaintListener(op);
		canvas_1.addMouseListener(op);
		scrolledComposite.setContent(canvas_1);
		scrolledComposite.setMinSize(canvas_1.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));

		lblTotalAmountOf = new Label(composite_2, SWT.NONE);
		lblTotalAmountOf.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		lblTotalAmountOf.setText("Total amount of points: 0");
		sashForm_3.setWeights(new int[] { 1, 1 });

		Label label_2 = new Label(composite_15, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		label_2.setSize(899, 2);

		Composite composite_6 = new Composite(composite_15, SWT.NONE);
		composite_6.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true,
				false, 1, 1));
		composite_6.setSize(260, 23);
		GridLayout gl_composite_6 = new GridLayout(3, false);
		gl_composite_6.horizontalSpacing = 10;
		gl_composite_6.marginWidth = 0;
		gl_composite_6.verticalSpacing = 0;
		gl_composite_6.marginHeight = 0;
		composite_6.setLayout(gl_composite_6);

		Button btnImport = new Button(composite_6, SWT.NONE);
		btnImport.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnImport.setText("Import from XML");
		btnImport.addMouseListener(new ConfigImportMouseListener());

		Button btnGenerate = new Button(composite_6, SWT.NONE);
		GridData gd_btnGenerate = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		gd_btnGenerate.widthHint = 70;
		btnGenerate.setLayoutData(gd_btnGenerate);
		btnGenerate.setText("Generate");
		btnGenerate.addMouseListener(new GenerateListener(this));

		Button btnExport = new Button(composite_6, SWT.NONE);
		btnExport.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnExport.setText("Export to XML");
		btnExport.addMouseListener(new ConfigExportListener());
		sashForm_2.setWeights(new int[] { 150, 200 });
		
		composite_6.layout();
	}

	/**
	 * This method takes a search phrase and updates the {@link Tree} of
	 * {@link Tag}s, leaving only those {@link Tag}s and {@link Mod}s that
	 * contain the search phrase in their name or description. Entering an
	 * empty {@link String} as a search phrase, restores the tree to its
	 * original settings.
	 * @param search the {@link String} search phrase.
	 * @requires search != null
	 */
	public void updateTagtree(String search) {
		TagTree.updateTagtree(tree, search);
	}

	/**
	 * This method updates the {@link Combo} containing all {@link Challenge}s in
	 * {@link Mod} selected. It shows an option for each of the {@link Challenge}s 
	 * in selected and by default selects the first one in the list.
	 * @param selected the currently selected {@link Mod}, or null, if no {@link Mod}
	 * is selected.
	 */
	public void updateCombo(Mod selected) {
		combo.removeAll();
		if (selected != null) {
			ArrayList<Challenge> selChals = selected.getChallenges();
			for (Challenge c : selChals) {
				combo.add(c.getIdString());
			}
			if (combo.getItemCount() > 0) {
				combo.select(0);
				pl.widgetSelected(null);
			}
		} else {
			pl.widgetSelected(null);
		}

	}

	/**
	 * This method updates the {@link Text} that shows all packages belonging
	 * to the {@link Mod} selected. It shows a new package on each line and if
	 * no {@link Mod} is selected, it shows a default informational text.
	 * @param selected the currently selected {@link Mod}, or null, if no {@link Mod}
	 * is selected.
	 */
	public void updatePackages(Mod selected) {
		if (selected != null) {
			ArrayList<String> packs = selected.getPackages();
			String text = "";
			for (String s : packs) {
				text += s + "\n";
			}
			packages.setText(text);
		} else {
			packages.setText("Select a module and the packages associated with it will be displayed here");
		}
	}

	/**
	 * This method invokes the redraw method on the list of selected {@link Mod}s.
	 * @requires canvas != null
	 */
	public void updateModList() {
		canvas.redraw();
	}

	/**
	 * This method updates both the labels that display the total amount of points
	 * that can be scored per Virtual Machine, with the current configurations.
	 * The total amount of points is the value of all challenges of all selected
	 * {@link Mod}s.
	 * @requires label != null
	 * @requires lblTotalAmountOf != null
	 */
	public void updateScoreLabels() {
		if (label != null) {
			label.setText("Total amount of points: "
					+ ModsBookkeeper.getInstance().getTotalPoints());
			label.redraw();
		}
		if (lblTotalAmountOf != null) {
			lblTotalAmountOf.setText("Total amount of points: "
					+ ModsBookkeeper.getInstance().getTotalPoints());
			label.redraw();
		}
	}

	/**
	 * This method makes sure that the {@link Tree} of {@link Challenge}s is updated
	 * according to the currently selected {@link Mod}. It will show all 
	 * {@link ChallengesTree} if a {@link Mod} is selected and it will show an
	 * empty {@link Tree} if no {@link Mod} is selected.
	 */
	public void updateChallengesTree() {
		ChallengesTree.populateTree(challenges);
	}

	/**
	 * This method updates the {@link Text} to contain the current value of the 
	 * specified configuration option.
	 * @param key {@link String} consisting of the name of the configuration option
	 * that needs to be updated.
	 * @requires key != null
	 * @requires textMap != null
	 */
	public void updateConfig(String key) {
		if (textMap.get(key) != null) {
			try
			{				
				textMap.get(key)
						.setText(
								ModsBookkeeper.getInstance().getConfigFile()
										.getActual(key));
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method is used for drawing {@link OptionsComposite}s for each of the
	 * {@link Option}s the selected {@link Mod} has. No {@link OptionsComposite}s
	 * are drawn when no {@link Mod} is currently selected. Finally, the GUI is 
	 * redrawn to display the changes.
	 * @requires composite_7 != null
	 */
	public void updateOptions() {
		Control[] kids = composite_7.getChildren();
		for (Control c : kids) {
			c.dispose();
		}
		Mod selectedMod = ModsBookkeeper.getInstance().getSelectedMod();
		if (selectedMod != null) {
			HashMap<String, Option> options = selectedMod.getOptions();
			for (Option o : options.values()) {
				new OptionsComposite(composite_7, SWT.NONE, o, selectedMod);
			}
		}
		composite_7.redraw();
		((ScrolledComposite) composite_7.getParent()).setContent(composite_7);
	}

	/**
	 * This method is called by the {@link Observable} {@link ModsBookkeeper}, whenever
	 * the collection of selected {@link Mod} or the selected {@link Mod} itself changes.
	 * This method calls updateScoreLabels, sets the name of the selected module, calls
	 * updateCombo, updateChallengesTree, updatePackages and updateOptions.
	 */
	public void update(Observable obs, Object obj) {
		updateScoreLabels();
		int selected = ModsBookkeeper.getInstance().getSelected();
		if (modName != null) {
			if (selected != -1) {
				// FIXME: rare nullpointer als je de laatste in de lijst
				// probeert weg te doen.
				// FIXME: module verschijnt niet bij dubbelklik
				modName.setText("Module: "
						+ ModsBookkeeper.getInstance().getSelectedMods()
								.get(selected - 1).getName());
			} else {
				modName.setText("No module selected");
			}
		}
		if (combo != null && selected != -1) {
			updateCombo(ModsBookkeeper.getInstance().getSelectedMod());
		}
		label.getShell().redraw();
		updateChallengesTree();
		updatePackages(ModsBookkeeper.getInstance().getSelectedMod());
		updateOptions();
	}

	/**
	 * @return the large {@link Text} on the VM Generation tab 
	 */
	public Text getTextOutput() {
		return text_output;
	}
	
	public Text getTextFilePath()
	{
		return text_filepath;
	}

	/**
	 * @return the {@link Display}
	 */
	public Display getDisplay() {
		return display;
	}

	@Override
	public void controlMoved(ControlEvent arg0) {}

	@Override
	/**
	 * This method is called whenever the {@link Shell}, the ModList, 
	 * the TagList or the Overview is resized. It redraws the {@link Composite} 
	 * that was resized to make it match the new size.
	 * @requires canvas != null
	 * @requires tags != null
	 * @requires canvas_1 != null
	 */
	public void controlResized(ControlEvent c) {
		if (c.getSource() instanceof Shell) {
			((Shell) c.getSource()).layout();
			((Shell) c.getSource()).redraw();
		} else if (c.getSource() instanceof Composite) {
			canvas.redraw();
			tags.redraw();
			canvas_1.redraw();
		}
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}
}
