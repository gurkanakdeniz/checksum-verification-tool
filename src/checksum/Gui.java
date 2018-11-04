package checksum;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

public class Gui extends JFrame {
	private static final long	serialVersionUID	= 63924900336217723L;

	private JPanel				contentPane;
	private JTextField			fileLocationName;
	private JButton				compareButton;
	private JTextArea			inputSums;
	private JTextArea			fileSums;
	private JScrollPane			fileSumsScroll;
	private JScrollPane			inputSumsScroll;
	private JTextField			compareResult;
	private JButton				fileButton;
	private JComboBox<Hash>		sumsList;
	private GroupLayout			groupLayoutContentPane;
	private Vector<Hash>		hashItems;
	private Font				fontButton;
	private Font				fontText;
	private Color				successColor;
	private Color				defaultColor;
	private final String		calculating			= "Calculating...";

	private static File			selectedFile;
	private static Hash			selectedHash;
	private static String		hashResult;

	public Gui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 435, 290);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		hashListInit();
		elementsInit();

		try {
			elementActionsInit();
		} catch (Exception e) {
			e.printStackTrace();
		}

		layoutInit();
	}

	private void hashListInit() {
		hashItems = new Vector<Hash>(Arrays.asList(Hash.values()));
	}

	private void elementsInit() {
		fontButton = new Font(Font.SANS_SERIF, Font.BOLD, 13);
		fontText = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		successColor = new Color(140, 255, 102);
		defaultColor = new Color(204, 204, 255);

		fileButton = new JButton("File Button");
		fileButton.setFocusPainted(false);
		fileButton.setBackground(Color.ORANGE);
		fileButton.setFont(fontButton);

		compareButton = new JButton("Compare");
		compareButton.setFocusPainted(false);
		compareButton.setBackground(Color.GRAY);
		compareButton.setFont(fontButton);

		sumsList = new JComboBox<Hash>(hashItems);
		sumsList.setBackground(Color.LIGHT_GRAY);
		sumsList.setFont(fontText);

		fileLocationName = new JTextField("File Name");
		fileLocationName.setBorder(null);
		fileLocationName.setEditable(false);

		fileSums = new JTextArea(5, 4);
		fileSums.setLineWrap(true);
		fileSums.setText("file not selected");
		fileSums.setEditable(false);
		fileSums.setFont(fontText);
		fileSums.setBackground(defaultColor);
		fileSumsScroll = new JScrollPane(fileSums);

		inputSums = new JTextArea(10, 4);
		inputSums.setLineWrap(true);
		inputSums.setText("input sums");
		inputSums.setFont(fontText);
		inputSums.setBackground(Color.WHITE);
		inputSumsScroll = new JScrollPane(inputSums);

		compareResult = new JTextField("");
		compareResult.setEditable(false);
		compareResult.setFont(fontText);
		compareResult.setBorder(null);

		this.setFont(fontText);
		contentPane.setFont(fontText);
	}

	private void elementActionsInit() {

		fileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser openFile = new JFileChooser();
				openFile.setCurrentDirectory(new File(System.getProperty("user.dir")));
				openFile.setPreferredSize(new Dimension(400, 300));

				int fileSelect = openFile.showSaveDialog(null);
				if (fileSelect == JFileChooser.APPROVE_OPTION) {
					selectedFile = null;

					Thread fileThread = (new Thread() {
						public void run() {
							fileButton.setEnabled(false);
							selectedFile = openFile.getSelectedFile();

							if (selectedFile != null) {
								fileLocationName.setText(selectedFile.getName());
								selectedHash = null;
								selectedHash = (Hash) sumsList.getSelectedItem();
								calculate();

								fileSums.setBackground(successColor);
							}
							fileButton.setEnabled(true);
						}
					});
					fileThread.start();

					fileSums.setText(calculating);
				}
			}
		});

		sumsList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Hash selectItem = (Hash) sumsList.getSelectedItem();

				if (selectedHash == null || (selectedHash.getId() != selectItem.getId())) {
					selectedHash = selectItem;

					if (selectedFile != null) {
						Thread calculateThread = (new Thread() {
							public void run() {
								fileButton.setEnabled(false);
								compareResult.setText("");
								compareResult.setBackground(getBackground());
								fileSums.setBackground(defaultColor);
								calculate();
								fileSums.setBackground(successColor);
								fileButton.setEnabled(true);
							}
						});
						calculateThread.start();

						fileSums.setText(calculating);
					}
				}

			}
		});

		compareButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (!fileSums.getText().equals(calculating)) {
					if (selectedHash != null && selectedFile != null && inputSums.getText() != null
							&& !inputSums.getText().equals("")) {

						if (fileSums.getText().equals(inputSums.getText())) {
							compareResult.setText("match :)");
							compareResult.setBackground(Color.GREEN);
						} else if (fileSums.getText().toLowerCase().equals(inputSums.getText().toLowerCase())) {
							compareResult.setText("match :/");
							compareResult.setBackground(Color.ORANGE);
							JOptionPane.showMessageDialog(contentPane, "Strings match but case insensitive :/", ":/",
									JOptionPane.WARNING_MESSAGE);
						} else {
							compareResult.setText("don't match :(");
							compareResult.setBackground(new Color(255, 51, 51));
						}

						return;
					}
				}

				JOptionPane.showMessageDialog(contentPane, "Something not right :(", ":(", JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	public void calculate() {
		hashResult = CalculationUtil.calculate(selectedHash, selectedFile);
		fileSums.setText(hashResult);
	}

	private void layoutInit() {
		groupLayoutContentPane = new GroupLayout(contentPane);
		groupLayoutContentPane.setHorizontalGroup(groupLayoutContentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayoutContentPane.createSequentialGroup().addGap(21).addGroup(groupLayoutContentPane
						.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayoutContentPane.createSequentialGroup()
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(sumsList, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
								.addGap(18).addComponent(compareButton).addGap(18).addComponent(compareResult))
						.addGroup(groupLayoutContentPane.createSequentialGroup().addComponent(fileButton).addGap(27)
								.addComponent(fileLocationName))
						.addComponent(inputSumsScroll, GroupLayout.PREFERRED_SIZE, 383, GroupLayout.PREFERRED_SIZE)
						.addComponent(fileSumsScroll, GroupLayout.PREFERRED_SIZE, 383, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(36, Short.MAX_VALUE)));
		groupLayoutContentPane.setVerticalGroup(groupLayoutContentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayoutContentPane.createSequentialGroup().addGap(23)
						.addGroup(groupLayoutContentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(fileLocationName, GroupLayout.PREFERRED_SIZE, 29,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(fileButton, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
						.addGap(18)
						.addComponent(fileSumsScroll, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(inputSumsScroll, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addGroup(groupLayoutContentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(compareResult)
								.addGroup(groupLayoutContentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(compareButton, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
										.addComponent(sumsList, GroupLayout.PREFERRED_SIZE, 29,
												GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		contentPane.setLayout(groupLayoutContentPane);
	}

}
