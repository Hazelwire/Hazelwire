package org.hazelwire.gui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
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
import org.eclipse.wb.swt.SWTResourceManager;
import org.hazelwire.main.Generator;
import org.hazelwire.modules.Option;

public class GUIBuilder implements Observer
{

	private Display display;
	private Label label, modName, lblTotalAmountOf;
	private Composite wrapper2, composite_7;
	private Tree challenges, tree;
	private Combo combo;
	private PointsListener pl;
	private Text packages;
	private GridData gd_1;
	private GridData gd_2;
	private GridData gd_3;
	private GridData gd_4;
	private Text text_1;
	private Text text_2;
	private Text txtHazelwireVm;
	private Text text_3;
	private Text text_output;
	private Canvas canvas;
	private GridData gd_5;
	private static GUIBuilder instance;
	private Shell shell;
	
	public static void main(String[] args)
	{
		GUIBuilder builder = GUIBuilder.getInstance();
		builder.init();
	}
	
	public synchronized static GUIBuilder getInstance()
	{
		if(!(instance instanceof GUIBuilder))
		{
			instance = new GUIBuilder();
		}
		
		return instance;
	}
	
	private GUIBuilder()
	{

	}
	
	public void init()
	{
		/*
		 * Maak ModsBookkeeper aan en voegt zichzelf toe als observer.
		 */
		
		Generator.getInstance();
		ModsBookkeeper.getInstance().initMods(GUIBridge.getModulesForGUI());
		ModsBookkeeper.getInstance().addObserver(this);
		display = new Display();
		shell = new Shell(display);
		this.addGUIElements(shell);
		shell.pack();
		shell.open();
		shell.setText("Hazelwire Alpha v0.1");
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		Generator.getInstance().shutDown(); //Nice way to shut down
	}

	private void addGUIElements(Shell shell)
	{
		GridLayout gl_shell = new GridLayout(1, false);
		gl_shell.marginHeight = 0;
		gl_shell.marginWidth = 0;
		shell.setLayout(gl_shell);

		// FIXME: resizen gaat scheel en de titel valt weg :(
		Composite all = new Composite(shell, SWT.BORDER);
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

		Composite title = new Composite(all, SWT.NONE);
		title.setLayout(new GridLayout(4, false));
		gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		title.setLayoutData(gd);
		Label name = new Label(title, SWT.LEFT);
		name.setForeground(SWTResourceManager.getColor(255, 255, 255));
		name.setBackground(SWTResourceManager.getColor(139, 69, 19));
		name.setText("Hazelwire Maker");
		gd_5 = new GridData();
		gd_5.horizontalSpan = 4;
		gd_5.verticalAlignment = SWT.FILL;
		gd_5.horizontalAlignment = SWT.FILL;
		gd_5.grabExcessHorizontalSpace = true;
		gd_5.grabExcessVerticalSpace = true;
		gd_5.widthHint = 600;
		name.setLayoutData(gd_5);

		CTabFolder tabFolder = new CTabFolder(all, SWT.TOP);
		gd = new GridData();
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		tabFolder.setLayoutData(gd);

		CTabItem modselection = new CTabItem(tabFolder, SWT.NULL);
		modselection.setText("Module Selection");

		SashForm sash = new SashForm(tabFolder, SWT.VERTICAL);
		sash.setBackground(tabFolder.getDisplay().getSystemColor(
				SWT.COLOR_DARK_GRAY));

		GridLayout gl = new GridLayout();

		sash.setLayout(gl);

		Composite wrapper = new Composite(sash, SWT.None);
		GridLayout grid = new GridLayout(3, false);
		grid.marginHeight = 2;
		grid.horizontalSpacing = 2;
		grid.marginWidth = 2;
		grid.verticalSpacing = 2;
		wrapper.setLayout(grid);
		TagTree tt = new TagTree();
		Display display = wrapper.getDisplay();
		GridLayout rightGrid = new GridLayout(1, true);
		ModListPainter p = new ModListPainter(this);
		tree = new Tree(wrapper, SWT.NONE);
		tree.addMouseListener(tt);
		tree.setBackground(new Color(display, 0, 0, 0));
		tree.setForeground(new Color(display, 255, 255, 255));

		TagTree.populateTree(tree);
		gd_1 = new GridData();
		gd_1.horizontalSpan = 2;
		gd_1.grabExcessHorizontalSpace = true;
		gd_1.grabExcessVerticalSpace = true;
		gd_1.horizontalAlignment = SWT.FILL;
		gd_1.verticalAlignment = SWT.FILL;
		gd_1.widthHint = 200;
		tree.setLayoutData(gd_1);

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
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		selectedComp.setLayoutData(gd);

		Button importB = new Button(wrapper, SWT.PUSH | SWT.CAP_ROUND);
		importB.setText("Import");
		importB.setLocation(0, 370);
		importB.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		importB.addMouseListener(new ImportMouseListener());
		gd = new GridData();
		gd.horizontalAlignment = SWT.LEFT;
		importB.setLayoutData(gd);

		Text search = new Text(wrapper, SWT.BORDER);
		search.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.CENTER;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		search.setText("search...");
		search.setLayoutData(gd);
		search.addKeyListener(new Searcher(this));
		search
				.setToolTipText("Type search phrase and hit return.\nIn order to see all tags, enter nothing and hit return.");

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
		modselection.setControl(sash);

		// RANDOM CANVAS. ONDERSTE DEEL SASHFORM
		Composite mod = new Composite(sash, SWT.NONE);
		mod.setLayout(new GridLayout(1, false));
		mod.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

		modName = new Label(mod, SWT.CENTER);
		modName.setBackground(new Color(shell.getDisplay(), 0, 0, 0));
		modName.setForeground(new Color(shell.getDisplay(), 255, 255, 255));
		int selected = ModsBookkeeper.getInstance().getSelected();
		if (selected != -1)
		{
			modName.setText("Module: "
					+ ModsBookkeeper.getInstance().getSelectedMods().get(
							selected).getName());
		}
		else
		{
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

		wrapper2 = new Composite(moddetails, SWT.NONE);
		GridLayout gl_wrapper2 = new GridLayout(3, false);
		gl_wrapper2.verticalSpacing = 2;
		gl_wrapper2.marginWidth = 2;
		gl_wrapper2.marginHeight = 2;
		gl_wrapper2.horizontalSpacing = 2;
		wrapper2.setLayout(gl_wrapper2);

		Label tagt = new Label(wrapper2, SWT.LEFT);
		tagt.setText("Tags");
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.CENTER;
		gd.verticalSpan = 1;
		tagt.setLayoutData(gd);

		Label chals = new Label(wrapper2, SWT.LEFT);
		chals.setText("Challenges");
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.CENTER;
		gd.verticalSpan = 1;
		chals.setLayoutData(gd);

		Label packs = new Label(wrapper2, SWT.LEFT);
		packs.setText("Packages");
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.CENTER;
		gd.verticalSpan = 1;
		packs.setLayoutData(gd);

		ScrolledComposite tagswrap = new ScrolledComposite(wrapper2,
				SWT.V_SCROLL | SWT.BORDER);
		tagswrap.setAlwaysShowScrollBars(true);
		tagswrap.setMinWidth(125);
		tagswrap.setExpandHorizontal(true);
		tagswrap.setExpandVertical(true);
		tagswrap.getVerticalBar().setIncrement(15);
		tagswrap.setLayout(new GridLayout(1, true));

		gd_4 = new GridData();
		gd_4.verticalSpan = 2;

		gd_4.widthHint = 125;
		gd_4.grabExcessHorizontalSpace = true;
		gd_4.grabExcessVerticalSpace = true;
		gd_4.horizontalAlignment = SWT.FILL;
		gd_4.verticalAlignment = SWT.FILL;
		tagswrap.setLayoutData(gd_4);

		Canvas tags = new Canvas(tagswrap, SWT.NONE);
		TagListPainter tlp = new TagListPainter(this);

		tags.addPaintListener(tlp);
		tagswrap.setContent(tags);

		challenges = new Tree(wrapper2, SWT.MULTI);
		ChallengesTree.populateTree(challenges);
		gd_2 = new GridData();
		gd_2.verticalSpan = 2;

		gd_2.widthHint = 250;
		gd_2.grabExcessHorizontalSpace = true;
		gd_2.grabExcessVerticalSpace = true;
		gd_2.horizontalAlignment = SWT.FILL;
		gd_2.verticalAlignment = SWT.FILL;
		challenges.setLayoutData(gd_2);

		packages = new Text(wrapper2, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL
				| SWT.MULTI);
		packages.setBackground(new Color(display, 0, 0, 255));
		gd_3 = new GridData();
		gd_3.verticalSpan = 2;
		gd_3.widthHint = 250;
		gd_3.grabExcessHorizontalSpace = true;
		gd_3.grabExcessVerticalSpace = true;
		gd_3.horizontalAlignment = SWT.FILL;
		gd_3.verticalAlignment = SWT.FILL;
		packages.setLayoutData(gd_3);
		packages
				.setText("Select a module and the packages associated with it will be displayed here");

		description.setControl(wrapper2);
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

		composite_7 = new Composite(optionsWrapper, SWT.NONE);
		composite_7.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_DARK_GREEN));

		composite_7.setLayout(new GridLayout(1, true));
		Mod selectedMod = ModsBookkeeper.getInstance().getSelectedMod();
		if (selectedMod != null)
		{
			HashMap<String, Option> options = selectedMod.getOptions();
			for (String s : options.keySet())
			{
				new OptionsComposite(composite_7, SWT.NONE, s, selectedMod,
						this);
			}
		}

		optionsWrapper.setContent(composite_7);
		optionsWrapper.setMinSize(composite_7.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));

		Composite pointsComp = new Composite(wrapper3, SWT.NONE);
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
				.addMouseListener(new DefaultMouseListener(this, composite_7));

		CTabItem tbtmVmGeneration = new CTabItem(tabFolder, SWT.NONE);
		tbtmVmGeneration.setText("VM Generation");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmVmGeneration.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		Composite composite_4 = new Composite(composite, SWT.NONE);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
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

		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(3, false));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true,
				false, 1, 1));

		Button btnDownload = new Button(composite_1, SWT.NONE);
		btnDownload.setText("Download");
		btnDownload.addMouseListener(new DownLoadListener(this));

		ProgressBar progressBar = new ProgressBar(composite_1, SWT.SMOOTH);
		GridData gd_progressBar = new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 2, 1);
		gd_progressBar.widthHint = 222;
		progressBar.setLayoutData(gd_progressBar);

		text_1 = new Text(composite_1, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		text_1.setEnabled(false);
		text_1
				.setText("Press the download button to download the default Hazelwire VM.\nYou can also locate a VM on your hard drive, for example if you have already downloaded the default VM. Usage of a non-default VM is not supported and may not work.");
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, false, false,
				3, 1);
		gd_text_1.widthHint = 262;
		gd_text_1.heightHint = 150;
		text_1.setLayoutData(gd_text_1);

		text_2 = new Text(composite_1, SWT.BORDER);
		text_2.setText("");
		GridData gd_text_2 = new GridData(SWT.FILL, SWT.CENTER, false, false,
				2, 1);
		gd_text_2.widthHint = 200;
		text_2.setLayoutData(gd_text_2);
		text_2.addMouseListener(new BrowseMouseListener(text_2));

		Button btnBrowse = new Button(composite_1, SWT.NONE);
		btnBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnBrowse.setText("Browse");
		btnBrowse.addMouseListener(new BrowseMouseListener(text_2));

		Composite composite_3 = new Composite(composite, SWT.NONE);
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

		Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayout(new GridLayout(3, false));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));

		Composite composite_5 = new Composite(composite_2, SWT.NONE);
		composite_5.setLayout(new GridLayout(2, false));
		GridData gd_composite_5 = new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 2);
		gd_composite_5.widthHint = 124;
		composite_5.setLayoutData(gd_composite_5);

		Label lblNewLabel_1 = new Label(composite_5, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel_1.setText("VM Base");

		txtHazelwireVm = new Text(composite_5, SWT.BORDER);
		txtHazelwireVm.setEnabled(false);
		txtHazelwireVm.setText("Hazelwire VM");
		GridData gd_txtHazelwireVm = new GridData(SWT.RIGHT, SWT.CENTER, true,
				false, 1, 1);
		gd_txtHazelwireVm.widthHint = 100;
		txtHazelwireVm.setLayoutData(gd_txtHazelwireVm);

		Label lblNewLabel_2 = new Label(composite_5, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel_2.setText("Main Server IP");

		text_3 = new Text(composite_5, SWT.BORDER);
		text_3.setEditable(false);
		text_3.setToolTipText("Not implemented yet, I guess... ");
		GridData gd_text_3 = new GridData(SWT.RIGHT, SWT.CENTER, true, false,
				1, 1);
		gd_text_3.widthHint = 100;
		text_3.setLayoutData(gd_text_3);

		text_output = new Text(composite_5, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP
				| SWT.MULTI);
		//text_4.setEnabled(true);
		GridData gd_text_4 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_text_4.widthHint = 426;
		gd_text_4.heightHint = 266;
		text_output.setLayoutData(gd_text_4);

		Label lblDerpsep = new Label(composite_2, SWT.SEPARATOR | SWT.VERTICAL);
		lblDerpsep.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false,
				false, 1, 2));
		lblDerpsep.setText("derpsep");

		ScrolledComposite scrolledComposite = new ScrolledComposite(
				composite_2, SWT.V_SCROLL);
		scrolledComposite.setAlwaysShowScrollBars(true);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.getVerticalBar().setIncrement(15);

		Canvas canvas_1 = new Canvas(scrolledComposite, SWT.NONE);
		canvas_1.setSize(canvas_1.computeSize(200, 300));
		OverviewPainter op = new OverviewPainter(this);
		canvas_1.addPaintListener(op);
		canvas_1.addMouseListener(op);
		scrolledComposite.setContent(canvas_1);
		scrolledComposite.setMinSize(canvas_1.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));

		lblTotalAmountOf = new Label(composite_2, SWT.NONE);
		lblTotalAmountOf.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		lblTotalAmountOf.setText("Total amount of points: 0");

		Label label_2 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Composite composite_6 = new Composite(composite, SWT.NONE);
		composite_6.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true,
				false, 1, 1));
		GridLayout gl_composite_6 = new GridLayout(3, false);
		gl_composite_6.horizontalSpacing = 10;
		gl_composite_6.marginWidth = 0;
		gl_composite_6.verticalSpacing = 0;
		gl_composite_6.marginHeight = 0;
		composite_6.setLayout(gl_composite_6);

		Button btnImport = new Button(composite_6, SWT.NONE);
		btnImport.setText("Import");
		btnImport.addMouseListener(new ConfigImportMouseListener(this));

		Button btnGenerate = new Button(composite_6, SWT.NONE);
		GridData gd_btnGenerate = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_btnGenerate.widthHint = 70;
		btnGenerate.setLayoutData(gd_btnGenerate);
		btnGenerate.setText("Generate");
		btnGenerate.addMouseListener(new GenerateListener(this));

		Button btnExport = new Button(composite_6, SWT.NONE);
		btnExport.setText("Export");
		btnExport.addMouseListener(new ConfigExportListener(this));
	}

	/*
	 * Deze methode update de Tree met daarin alle modules geselecteerd op tag
	 */
	public void updateTagtree(String search)
	{
		TagTree.updateTagtree(tree, search);
	}

	/*
	 * Deze methode update de combobox waar alle challenges instaan
	 */
	public void updateCombo(Mod selected)
	{
		combo.removeAll();
		if (selected != null)
		{
			ArrayList<Challenge> selChals = selected.getChallenges();
			for (Challenge c : selChals)
			{
				combo.add(c.getIdString());
			}
			if (combo.getItemCount() > 0)
			{
				combo.select(0);
				pl.widgetSelected(null);
			}
		}
		else
		{
			pl.widgetSelected(null);
		}

	}

	/*
	 * Deze methode update het vlak waarin alle packages staan
	 */
	public void updatePackages(Mod selected)
	{
		if (selected != null)
		{
			ArrayList<String> packs = selected.getPackages();
			String text = "";
			for (String s : packs)
			{
				text += s + "\n";
			}
			packages.setText(text);
		}
		else
		{
			packages
					.setText("Select a module and the packages associated with it will be displayed here");
		}
	}

	/*
	 * Deze methode update de lijst met geselecteerde modules
	 */
	public void updateModList()
	{
		canvas.redraw();
	}

	/*
	 * Deze methode update de labels die de totale score bijhouden (op allebei
	 * de tabs)
	 */
	public void updateScoreLabels()
	{
		if (label != null)
		{
			label.setText("Total amount of points: "
					+ ModsBookkeeper.getInstance().getTotalPoints());
			label.redraw();
		}
		if (lblTotalAmountOf != null)
		{
			lblTotalAmountOf.setText("Total amount of points: "
					+ ModsBookkeeper.getInstance().getTotalPoints());
			label.redraw();
		}
	}

	/*
	 * Deze methode is om de Challenges boom bij te werken.
	 */
	public void updateChallengesTree()
	{
		ChallengesTree.populateTree(challenges);
		// Doet populate nog iets anders dan update?
	}

	/*
	 * Deze methode update het options veld.
	 */
	public void updateOptions()
	{
		Control[] kids = composite_7.getChildren();
		for (Control c : kids)
		{
			c.dispose();
		}
		Mod selectedMod = ModsBookkeeper.getInstance().getSelectedMod();
		if (selectedMod != null)
		{
			HashMap<String, Option> options = selectedMod.getOptions();
			for (String s : options.keySet())
			{
				new OptionsComposite(composite_7, SWT.NONE, s, selectedMod,
						this);
			}
		}
		composite_7.redraw();
		((ScrolledComposite) composite_7.getParent()).setContent(composite_7);
	}

	/*
	 * De update die luistert naar ModsBookkeeper.
	 */
	public void update(Observable obs, Object obj)
	{
		updateScoreLabels();
		int selected = ModsBookkeeper.getInstance().getSelected();
		if (modName != null)
		{
			if (selected != -1)
			{
				modName.setText("Module: "
						+ ModsBookkeeper.getInstance().getSelectedMods().get(
								selected - 1).getName());
			}
			else
			{
				modName.setText("No module selected");
			}
		}
		if (combo != null && selected != -1)
		{
			updateCombo(ModsBookkeeper.getInstance().getSelectedMod());
		}
		label.getShell().redraw();
		updateChallengesTree();
		updatePackages(ModsBookkeeper.getInstance().getSelectedMod());
		updateOptions();
	}

	public Text getTextOutput()
	{		
		return text_output;
	}

	public Display getDisplay()
	{
		return display;
	}
}
