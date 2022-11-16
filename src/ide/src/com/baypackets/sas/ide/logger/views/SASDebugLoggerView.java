package com.baypackets.sas.ide.logger.views;

import java.io.FileReader;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.w3c.dom.Element;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.logger.actions.ClearDisplayAction;
import com.baypackets.sas.ide.logger.actions.CopyAction;
import com.baypackets.sas.ide.logger.actions.EditLoggerSettingsAction;
import com.baypackets.sas.ide.logger.actions.FindAction;
import com.baypackets.sas.ide.logger.actions.RefreshDisplayAction;
import com.baypackets.sas.ide.logger.actions.ToggleScrollingAction;
import com.baypackets.sas.ide.logger.filters.Filter;
import com.baypackets.sas.ide.logger.loader.LogFilesListener;
import com.baypackets.sas.ide.logger.loader.LogFilesLoader;
import com.baypackets.sas.ide.logger.loader.LogFilesUpdateListener;
import com.baypackets.sas.ide.logger.util.BoundedList;
import com.baypackets.sas.ide.logger.util.XmlUtils;

/**
 * The main view for LogWatcher. Still has lots of code generated from the PDE
 * wizard that is not used.
 */
public class SASDebugLoggerView extends ViewPart {

	private static Class jmxmpConnectorClass = null;

	static {
		try {
			jmxmpConnectorClass = Class
					.forName("javax.management.remote.jmxmp.JMXMPConnector");
		} catch (ClassNotFoundException e) {
			SasPlugin.getDefault().log("The JMXMPConnector class not found");
		}
	}
	private Action m_clearAction = null;
	private Action m_findAction = null;
	private Action m_scrollAction = null;
	private Action m_editAction = null;
	private Action m_copyAction = null;

	LogFilesListener watcher;
	Document doc;

	private Action m_refreshAction = null;
	TextViewer viewer = null;
	private Vector m_watchers = new Vector();
	private Composite composite = null;
	private Composite parentComposite = null;
	private WatcherData entry = null;
	private static final String SIPDEBUG_LOGGER_STATE_FILENAME = "sipDebugLoggerState.xml";
//	public static final String DEBUG_LOG = "debuglog";
	private final String SIP_DEBUG_LOG = "sipDebug.log";

	/**
	 * Listen for changes to Logwatcher preferences. Currently, only changes to
	 * the font are noticed.
	 */
	private IPropertyChangeListener m_propListener = new IPropertyChangeListener() {
		public void propertyChange(
				org.eclipse.jface.util.PropertyChangeEvent event) {
			if (event.getProperty().equals("logwatcherFont")) {
				SasPlugin plugin = SasPlugin.getDefault();
				plugin.putFont("logwatcherFont", PreferenceConverter
						.getFontDataArray(plugin.getPreferenceStore(),
								"logwatcherFont"));
				entry.getViewer().getTextWidget().setFont(
						plugin.getFont("logwatcherFont"));
			}
		}
	};

	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		// Register a property change listener for the preferences page.
		SasPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(
				m_propListener);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		composite = parent;
		setViewTitle();
		makeActions();
		contributeToActionBars();
		setGlobalActionHandlers();
		loadWatcherState();
	}

	public void refreshView() {
		this.loadWatcherState();
	}

	/**
	 * Load the watcher state from a previous instance.
	 */
	public void loadWatcherState() {
		LogFilesLoader loader = new LogFilesLoader(this);
		IPath path = SasPlugin.getDefault().getStateLocation();
		path = path.addTrailingSeparator();
		path = path.append(SIPDEBUG_LOGGER_STATE_FILENAME);

		if (!path.toFile().exists()) {
			SasPlugin
					.getDefault()
					.log(
							"SASDebugLoggerView: sipDebugLoggerState.xml file donot exist so calling addWatcher()........................");
		
			this.addWatcher(1, 1, null, true);
		} else {
			try {
				loader.loadWatchers(new FileReader(path.toFile()));
			} catch (Exception e) {
				SasPlugin.getDefault().log("Error loading watcher state", e);
			}
		}
	}

	private void setGlobalActionHandlers() {
		getViewSite().getActionBars().setGlobalActionHandler(
				ActionFactory.FIND.getId(), m_findAction);

		getViewSite().getActionBars().setGlobalActionHandler(
				ActionFactory.COPY.getId(), m_copyAction);
	}

	private void setViewTitle() {
		String title = "sipDebug.log";
		// Set the inner title name.
		setContentDescription(title);

		// Set the title of the entire view.
		setPartName(title);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(m_copyAction);
		manager.add(m_findAction);
		manager.add(m_clearAction);
		manager.add(new Separator("other"));
		manager.add(m_editAction);
		manager.add(m_scrollAction);
		manager.add(m_refreshAction); 
		manager.add(new Separator("Additions"));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(m_editAction);
		manager.add(m_clearAction);
		manager.add(m_refreshAction);
		manager.add(m_scrollAction);
	}

	/**
	 * Create the actions and set their default states.
	 */
	private void makeActions() {
		// Edit the logger settings
		m_editAction = new EditLoggerSettingsAction(this);
		m_editAction.setEnabled(false);

		//Refresh the logger view
		m_refreshAction = new RefreshDisplayAction(this);
		m_refreshAction.setEnabled(false);

		// Clear the display
		m_clearAction = new ClearDisplayAction(this);
		m_clearAction.setEnabled(false);

		// Find in log file
		m_findAction = new FindAction(this);

		// Copy
		m_copyAction = new CopyAction(this);

		// Toggle scrolling
		m_scrollAction = new ToggleScrollingAction(this);
		m_scrollAction.setChecked(false);
		m_scrollAction.setEnabled(false);
	}

	/**
	 * Add a Watcher for the specified File. Will set up the UI components as
	 * well as the actual LogFilesListener.
	 */
	public void addWatcher(int interval, int numLines, Vector filters,
			boolean saveState) {
		SasPlugin.getDefault().log(
				"SASDebugLoggerView: addWatcher()........................");
		// Create the text viewer and associated document

		if (viewer == null) {
			viewer = new TextViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL);
			doc = new Document();
			viewer.setDocument(doc);
			viewer.setEditable(false);

			// Add a context menu to the text viewer
			MenuManager menuMgr = new MenuManager("#PopupMenu");
			menuMgr.setRemoveAllWhenShown(true);
			menuMgr.addMenuListener(new IMenuListener() {
				public void menuAboutToShow(IMenuManager manager) {
					SASDebugLoggerView.this.fillContextMenu(manager);
				}
			});
			Menu menu = menuMgr.createContextMenu(viewer.getControl());
			viewer.getControl().setMenu(menu);
			getSite().registerContextMenu(menuMgr, viewer);

		}

		// Add the watcher

		try {
			if(watcher!=null){
				watcher.stopListener();
			}
			watcher = new LogFilesListener(interval, numLines, SIP_DEBUG_LOG);
			watcher.setFilters(filters);
		} catch (Exception e) {
			// Shouldn't happen!
			e.printStackTrace();
			return;
		}
		entry = new WatcherData(viewer, watcher, filters);
		addWatcherListener();

		// Allow filters to set line styles.
		viewer.getTextWidget().addLineStyleListener(new LineStyleListener() {
			public void lineGetStyle(LineStyleEvent event) {
				Vector filters = entry.getFilters();
				if (filters != null) {
					for (Iterator iter = filters.iterator(); iter.hasNext();) {
						Filter f = (Filter) iter.next();
						if (f.matches(event.lineText)) {
							f.handleViewerMatch(event);
						}
					}
				}
			}
		});

		// Set the font.
		Font f = SasPlugin.getDefault().getFont("logwatcherFont");
		viewer.getTextWidget().setFont(f);

		// Start the watcher and enable all actions.
		watcher.start();
		m_clearAction.setEnabled(true);
		m_scrollAction.setEnabled(true);
		m_editAction.setEnabled(true);
		m_refreshAction.setEnabled(true);
		if (saveState) {
			saveWatcherState();
		}
	}


	/**
	 * Add a LogFilesUpdateListener to the given watcher.
	 */
	private void addWatcherListener() {
		final Display display = Display.getCurrent();
		watcher.addListener(new LogFilesUpdateListener() {
			// On every update, add the updated content to the text viewer.
			public void update(BoundedList list) {
				final BoundedList flist = list;
				display.asyncExec(new Runnable() {
					public void run() {
						if(entry.getViewer().getTextWidget()!=null){
							
							if(entry.getWatcher().isFirstUpdate()||entry.getWatcher().isError()) {  //if notify listenrer is invoked before first update then donot append Text
							     entry.getViewer().getTextWidget().append(                              // but in error case show it
								 flist.getFormattedText());
							     entry.getWatcher().clear();
							      }
							if (entry.isScroll()) {
								// Scroll to the bottom
								entry.getViewer().setTopIndex(
										doc.getNumberOfLines());
							}
					    }else{
					    	SasPlugin.getDefault().log("Stopping the SAS Server Logs Listener!!!!!!!!!!!!");
						    entry.getWatcher().stopListener();
					    }
					}
				});
			}
		});
	}

	/**
	 * Change the properties of an active active watcher.
	 */
	public void editWatcher(WatcherData entry, int interval, int numLines,
			Vector filters) {
		entry.getWatcher().setInterval(interval);
		entry.getWatcher().setNumLines(numLines);
		entry.getWatcher().setFilters(filters);
		entry.setFilters(filters);
		saveWatcherState();
	}

	/**
	 * Write the current set of watchers to a config file.
	 */
	private void saveWatcherState() {
		IPath path = SasPlugin.getDefault().getStateLocation();
		path = path.addTrailingSeparator();
		path = path.append(SIPDEBUG_LOGGER_STATE_FILENAME);
		try {
			org.w3c.dom.Document doc = XmlUtils.createDocument();
			Element watcher = doc.createElement("watchers");

			doc.appendChild(watcher);
			entry.toXML(doc, watcher);
			// Write to the file
			Source source = new DOMSource(doc);
			Result result = new StreamResult(path.toFile());
			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();
			xformer.transform(source, result);
		} catch (Exception e) {
			SasPlugin.getDefault().log("Error saving watcher state", e);
		}
	}

	public void setFocus() {
		composite.setFocus();
	}

	/**
	 * Clean up after ourselves.
	 */
	public void dispose() {
		super.dispose();

		if (entry != null)
			entry.dispose();

		SasPlugin.getDefault().getPreferenceStore()
				.removePropertyChangeListener(m_propListener);
	}


	

	public Composite getComposite() {
		return composite;
	}

	/**
	 * Returns the WatcherEntry for the currently selected watcher
	 */
	public WatcherData getSelectedEntry() {
		return entry;
	}
	
	
	
	private BoundedList flist;
}