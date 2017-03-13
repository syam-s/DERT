package gov.nasa.arc.dert.view.mapelement;

import gov.nasa.arc.dert.landscape.Landscape;
import gov.nasa.arc.dert.scene.MapElement;
import gov.nasa.arc.dert.scene.featureset.Feature;
import gov.nasa.arc.dert.scene.featureset.FeatureSet;
import gov.nasa.arc.dert.ui.DoubleTextField;
import gov.nasa.arc.dert.ui.FieldPanel;
import gov.nasa.arc.dert.ui.GroupPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Provides controls for setting options for FeatureSets.
 *
 */
public class FeatureSetPanel extends MapElementBasePanel {

	// Controls
	private JTextField fileText;
	private JTextArea propText;
	private DoubleTextField lineWidthText;
	private DoubleTextField sizeText;

	// FeatureSet
	private FeatureSet featureSet;

	/**
	 * Constructor
	 * 
	 * @param parent
	 */
	public FeatureSetPanel() {
		super();
		icon = FeatureSet.icon;
		type = "FeatureSet";
		build(true, false);
	}

	@Override
	protected void build(boolean addNotes, boolean addLoc) {
		super.build(addNotes, addLoc);
		
		ArrayList<Component> compList = new ArrayList<Component>();
		
		compList.add(new JLabel("File", SwingConstants.RIGHT));
		fileText = new JTextField();
		fileText.setEditable(false);
		fileText.setBorder(BorderFactory.createEmptyBorder());
		fileText.setToolTipText("path to GeoJSON file");
		compList.add(fileText);
		
		compList.add(new JLabel("Line Width", SwingConstants.RIGHT));
		lineWidthText = new DoubleTextField(8, FeatureSet.defaultLineWidth, true, Landscape.format) {
			@Override
			protected void handleChange(double value) {
				if (Double.isNaN(value)) {
					return;
				}
				featureSet.setLineWidth((float) value);
			}
		};
		compList.add(lineWidthText);

		compList.add(new JLabel("Point Size", SwingConstants.RIGHT));
		sizeText = new DoubleTextField(8, FeatureSet.defaultSize, true, "0.00") {
			@Override
			protected void handleChange(double value) {
				if (Double.isNaN(value)) {
					return;
				}
				featureSet.setPointSize((float)value);
			}
		};
		compList.add(sizeText);
		
		contents.add(new FieldPanel(compList), BorderLayout.CENTER);
		
		GroupPanel groupPanel = new GroupPanel("Properties");
		groupPanel.setLayout(new BorderLayout());
		propText = new JTextArea();
		propText.setEditable(false);
		propText.setRows(4);
		groupPanel.add(new JScrollPane(propText), BorderLayout.CENTER);
		contents.add(groupPanel, BorderLayout.SOUTH);
		
	}

	@Override
	public void setMapElement(MapElement mapElement) {
		this.mapElement = mapElement;
		featureSet = null;
		if (mapElement instanceof FeatureSet) {
			featureSet = (FeatureSet) mapElement;
			propText.setText("");
			nameLabel.setText(featureSet.getName());
			fileText.setText("File: "+featureSet.getFilePath());
			noteText.setText(featureSet.getState().getAnnotation());
			lineWidthText.setValue(featureSet.getLineWidth());
			lineWidthText.setEnabled(true);
			sizeText.setValue(featureSet.getSize());
			sizeText.setEnabled(true);
		}
		else if (mapElement instanceof Feature) {
			Feature feature = (Feature)mapElement;
			FeatureSet fs = (FeatureSet)feature.getParent();
			if (fs != null)
				fileText.setText("File: "+fs.getFilePath());
			nameLabel.setText(feature.getName());
			HashMap<String,Object> properties = feature.getProperties();
			Object[] key = properties.keySet().toArray();
			String str = "";
			for (int i=0; i<key.length; ++i) {
				str += key[i]+" = "+properties.get((String)key[i])+"\n";
			}
			propText.setText(str);
			propText.setCaretPosition(0);
			lineWidthText.setValue(fs.getLineWidth());
			lineWidthText.setEnabled(false);
			sizeText.setValue(fs.getSize());
			sizeText.setEnabled(false);
		}
	}
}
