package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.DirItem;
import model.Disk;
import model.FileLabel;
import model.FileSystem;

public class FileSystemController implements Initializable {

	private Stage stage;

	private FileSystem fileSystem;

	@FXML
	private FlowPane flowPane;

	@FXML
	private PieChart diskUsingPieChart;
	
	private ObservableList<PieChart.Data> pieChartData;
	private PieChart.Data usedDisk;
	private PieChart.Data noUsedDisk;
	private static int uesd;
	private static int noUesd;

	@FXML
	private GridPane diskUsingTable;
	
	private List<StackPane> diskUsingTableBlocks = new ArrayList<StackPane>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.initDiskUsingTable();
		this.initDiskUsingPieChart();

		byte[] values = { 'a', 0, 0, 0, 0, DirItem.FILE, 0, 0 };
		try {
			FileLabel f1 = new FileLabel(new DirItem(values, "123"));
			flowPane.getChildren().add(f1);
			FileLabel f2 = new FileLabel(new DirItem(values, "123"));
			flowPane.getChildren().add(f2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initDiskUsingTable() {
		this.diskUsingTable.setPadding(new Insets(3.0, 3.0, 3.0, 3.0));
		this.diskUsingTable.setHgap(3.0);
		this.diskUsingTable.setVgap(3.0);
		for (int i = 0; i < Disk.totalBlock; i++) {
			Text diskBlock = new Text(String.valueOf(i));
			StackPane stackPane = new StackPane();
			stackPane.getChildren().add(diskBlock);
			stackPane.setStyle("-fx-background-color: #c8c8c8");
			this.diskUsingTableBlocks.add(stackPane);
			this.diskUsingTable.add(stackPane, i%8, i/8);
		}
	}
	
	private void upDateDiskUsingTable() {
		//TODO
	}

	private void initDiskUsingPieChart() {
		this.usedDisk = new PieChart.Data("已使用", this.uesd);
		this.noUsedDisk = new PieChart.Data("未使用", this.noUesd);
		this.pieChartData = FXCollections.observableArrayList(this.usedDisk, this.noUsedDisk);
		
		this.diskUsingPieChart.setData(this.pieChartData);
	}
	
	private void upDateDiskUsingPieChart() {
		//TODO
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
