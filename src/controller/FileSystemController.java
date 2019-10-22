package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.DirItem;
import model.Disk;
import model.FAT;
import model.FileLabel;
import model.FileSystem;

public class FileSystemController extends RootController{

	private Stage stage;

	private FileSystem fileSystem;
	
	private NotepadController notepadController;

	@FXML
	private FlowPane flowPane;

	@FXML
	private PieChart diskUsingPieChart;
	
	private ObservableList<PieChart.Data> pieChartData;
	private PieChart.Data usedDisk;
	private PieChart.Data noUsedDisk;
	private double uesd = 0;
	private double noUesd = 0;

	@FXML
	private GridPane diskUsingTable;
	private List<StackPane> diskUsingTableBlocks = new ArrayList<StackPane>();
	
    @FXML
    private GridPane FATTable;
    private List<StackPane> FATTableBlocks = new ArrayList<StackPane>();
    private List<Text> FATTableTextBlocks = new ArrayList<Text>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.initDiskUsingTable();
		this.initDiskUsingPieChart();
		this.initFATTable();

//		byte[] values = { 'a', 0, 0, 0, 0, DirItem.FILE, 0, 0 };
//		try {
//			FileLabel f1 = new FileLabel(new DirItem(values, "123"));
//			flowPane.getChildren().add(f1);
//			FileLabel f2 = new FileLabel(new DirItem(values, "123"));
//			flowPane.getChildren().add(f2);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private void initDiskUsingTable() {
		for (int i = 0; i < Disk.totalBlock; i++) {
			Text diskBlock = new Text(String.valueOf(i));
			StackPane stackPane = new StackPane();
			stackPane.getChildren().add(diskBlock);
			stackPane.setStyle("-fx-background-color: #DDDDDD");
			this.diskUsingTableBlocks.add(stackPane);
			this.diskUsingTable.add(stackPane, i%8, i/8);
		}
	}
	
	public void upDateDiskUsingTable() {
		for (int i = 0; i < Disk.totalBlock; i++) {
			if(this.fileSystem.getFat().getLocation((byte) i) != FAT.EMPTY) {
				this.diskUsingTableBlocks.get(i).setStyle("-fx-background-color: #FF3333");
				this.uesd++;
			}else {
				this.diskUsingTableBlocks.get(i).setStyle("-fx-background-color: #DDDDDD");
				this.noUesd--;
			}
		}
	}

	private void initDiskUsingPieChart() {
		this.usedDisk = new PieChart.Data("已使用", this.uesd);
		this.noUsedDisk = new PieChart.Data("未使用", this.noUesd);
		this.pieChartData = FXCollections.observableArrayList(this.usedDisk, this.noUsedDisk);
		this.diskUsingPieChart.setData(this.pieChartData);
	}
	
	public void upDateDiskUsingPieChart() {
		this.usedDisk.setPieValue(this.uesd);
		this.noUsedDisk.setPieValue(this.noUesd);
	}
	
	private void initFATTable() {
		for (byte i = 0; i < 3; i++) {
			Text diskBlock = new Text("-1");
			StackPane stackPane = new StackPane();
			stackPane.getChildren().add(diskBlock);
			stackPane.setStyle("-fx-background-color: #DDDDDD");
			this.FATTableTextBlocks.add(diskBlock);
			this.FATTableBlocks.add(stackPane);
			this.FATTable.add(stackPane, i/64, i%64);
		}
		for (int i = 3; i < Disk.totalBlock; i++) {
			//TODO get true FAT
//			Text diskBlock = new Text(String.valueOf(this.fileSystem.getFat().getLocation(i)));
			Text diskBlock = new Text("0");
			StackPane stackPane = new StackPane();
			stackPane.getChildren().add(diskBlock);
			stackPane.setStyle("-fx-background-color: #DDDDDD");
			this.FATTableTextBlocks.add(diskBlock);
			this.FATTableBlocks.add(stackPane);
			this.FATTable.add(stackPane, i/64, i%64);
		}
	}
	
	public void upDateFATTable() {
		for (int i = 0; i < Disk.totalBlock; i++) {
			if(this.fileSystem.getFat().getLocation((byte) i) != FAT.EMPTY) {
				this.FATTableBlocks.get(i).setStyle("-fx-background-color: #FF3333");
				String string = String.valueOf(this.fileSystem.getFat().getLocation((byte) i));
				this.FATTableTextBlocks.get(i).setText(string);
			}else {
				this.FATTableBlocks.get(i).setStyle("-fx-background-color: #DDDDDD");
				String string = String.valueOf(this.fileSystem.getFat().getLocation((byte) i));
				this.FATTableTextBlocks.get(i).setText(string);
			}
		}
	}
	
	@FXML
	void createDir() { 
		
	}

	public FlowPane getFlowPane() {
		return flowPane;
	}

	public void setFlowPane(FlowPane flowPane) {
		this.flowPane = flowPane;
	}

	public Stage getStage() {
		return this.stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
