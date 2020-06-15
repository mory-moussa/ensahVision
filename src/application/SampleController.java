package application;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;

import javax.swing.text.View;

import application.FaceDetector;
import application.Database;
import application.OCR;
import application.Database;

public class SampleController {

	//**********************************************************************************************
	//Mention The file location path where the face will be saved & retrieved
	
	public String filePath="./faces";
	
	
	//**********************************************************************************************
	
	@FXML
	private Button startCam;
	@FXML
	private Button stopBtn;
	@FXML
	private Button motionBtn;
	@FXML
	private Button eyeBtn;
	@FXML
	private Button shapeBtn;
	@FXML
	private Button gitBtn;
	@FXML
	private Button facebookBtn;
	@FXML
	private Button gmailBtn;
	@FXML
	private Button exit1;
	@FXML
	private Button supprimeruser ;
	@FXML
	private Button saveBtn;
	@FXML
	private Button ocrBtn;
	@FXML
	private Button capBtn;
	@FXML
	private Button recogniseBtn;
	@FXML
	private Button stopRecBtn;
	@FXML
	private Button instaBtn;
	@FXML
	private Button googleBtn;
	@FXML
	private Button twitterBtn;
	@FXML
	private Button no_twitterBtn;
	@FXML
	private Button no_instaBtn;
	@FXML
	private Button no_faceBtn;
	@FXML
	private Button no_gmailBtn;
	@FXML
	private Button no_googleBtn;
	@FXML
	private Button no_gitBtn;
	@FXML
	private ImageView frame;
	@FXML
	private WebView webview;
	@FXML
	private ImageView motionView;
	@FXML
	private AnchorPane pdPane;
	@FXML
	private TitledPane dataPane;
	@FXML
	private TextField fname;
	@FXML
	private TextField lname;
	@FXML
	private TextField code;
	@FXML
	private TextField reg;
	@FXML
	private TextField sec;
	@FXML
	private TextField age;
	@FXML
	public ListView<String> logList;
	@FXML
	public ListView<String> output;
	@FXML
	public ProgressIndicator pb;
	@FXML
	public Label savedLabel;
	@FXML
	public Label warning;
	@FXML
	public Label title;
//	@FXML
//	public TilePane tile;
	@FXML
	public TextFlow ocr;
//**********************************************************************************************
	FaceDetector faceDetect = new FaceDetector();	//Creating Face detector object									
	ColoredObjectTracker cot = new ColoredObjectTracker(); //Creating Color Object Tracker object		
	Database database = new Database();		//Creating Database object

	OCR ocrObj = new OCR();
	ArrayList<String> user = new ArrayList<String>();
	ImageView imageView1;
	
	public static ObservableList<String> event = FXCollections.observableArrayList();
	public static ObservableList<String> outEvent = FXCollections.observableArrayList();

	public boolean enabled = false;
	public boolean isDBready = false;

	
	//**********************************************************************************************
	public void putOnLog(String data) {

		Instant now = Instant.now();

		String logs = now.toString() + ":\n" + data;

		event.add(logs);

		logList.setItems(event);

	}

	@FXML
	protected void startCamera() throws SQLException {

		//*******************************************************************************************
		//initializing objects from start camera button event
		faceDetect.init();
        frame.setVisible(true);
		faceDetect.setFrame(frame);
		supprimeruser.setVisible(false);
		faceDetect.start();

		if (!database.init()) {

			putOnLog("Error: Connexion à la base de données échouée ! ");

		} else {
			isDBready = true;
			putOnLog("Success: Connexion à la base de données réuissie ! ");
		}

		//*******************************************************************************************
		//Activater les boutons
		startCam.setVisible(false);
		eyeBtn.setDisable(false);
		stopBtn.setVisible(true);
		ocrBtn.setDisable(false);
		stopRecBtn.setDisable(true);
		capBtn.setDisable(false);
		motionBtn.setDisable(false);
		exit1.setDisable(false);
		saveBtn.setDisable(false);
	
		if (isDBready) {
			recogniseBtn.setDisable(false);
		}

		dataPane.setDisable(false);
		shapeBtn.setDisable(false);
        webview.setVisible(false);
		/*if (stopRecBtn.isDisable()) {
			stopRecBtn.setDisable(false);
		}*/
		//*******************************************************************************************
		
//		
//		tile.setPadding(new Insets(15, 15, 55, 15));
//		tile.setHgap(30);
//		
		//**********************************************************************************************
		//Picture Gallary
		
		putOnLog(" Real Time WebCam Stream Started !");
		
		//**********************************************************************************************
	}
	int count = 0;

	@FXML
	protected void faceRecognise() {

		
		faceDetect.setIsRecFace(true);
		// printOutput(faceDetect.getOutput());

		//recogniseBtn.setText("Voir les données");

		//Getting detected faces
		user = faceDetect.getOutput();
           if(!user.isEmpty()) {
        	   activeBtn();
           }
           
 
           
           
		if (count > 0 && !user.isEmpty()) {
			supprimeruser.setVisible(true);
            recogniseBtn.setDisable(true);
			//Retrieved data will be shown in Fetched Data pane
			System.out.println("ça marche pas");
			String t = "********* Face Data: " + user.get(1) + " " + user.get(2) + " *********";

			outEvent.add(t);

			String n1 = "First Name\t\t:\t" + user.get(1);

			outEvent.add(n1);

			output.setItems(outEvent);

			String n2 = "Last Name\t\t:\t" + user.get(2);

			outEvent.add(n2);

			output.setItems(outEvent);

			String fc = "Face Code\t\t:\t" + user.get(0);

			outEvent.add(fc);

			output.setItems(outEvent);

			String r = "Reg no\t\t\t:\t" + user.get(3);

			outEvent.add(r);

			output.setItems(outEvent);

			String a = "Age \t\t\t\t:\t" + user.get(4);

			outEvent.add(a);

			output.setItems(outEvent);
			String s = "Section\t\t\t:\t" + user.get(5);

			outEvent.add(s);

			output.setItems(outEvent);

		}

		count++;
		saveBtn.setDisable(true);

		putOnLog("Reconaissance faciale activée !");

		stopRecBtn.setDisable(false);

	}

	@FXML
	protected void stopRecognise() {
        count=0;
		faceDetect.setIsRecFace(false);
		faceDetect.clearOutput();
		disableBtn();
		this.user.clear();
		recogniseBtn.setDisable(false);
		supprimeruser.setVisible(false);
		//recogniseBtn.setText("Recognise Face");
		saveBtn.setDisable(false);
		stopRecBtn.setDisable(true);
		putOnLog("Face Recognition Deactivated !");

	}

	@FXML
	protected void startMotion() {
        
		supprimeruser.setVisible(false);
		faceDetect.setMotion(true);
		putOnLog("motion Detector Activated !");

	}

	@FXML
	protected void saveFace() throws SQLException {

		supprimeruser.setVisible(false);
		//Input Validation
		if (fname.getText().trim().isEmpty() || reg.getText().trim().isEmpty() || code.getText().trim().isEmpty()) {

			new Thread(() -> {

				try {
					warning.setVisible(true);

					Thread.sleep(2000);

					warning.setVisible(false);

				} catch (InterruptedException ex) {
				}

			}).start();

		} else {
			//Progressbar
			pb.setVisible(true);

			savedLabel.setVisible(true);

			new Thread(() -> {

				try {

					//faceDetect.setFname(fname.getText());

					faceDetect.setFname(fname.getText());
					faceDetect.setLname(lname.getText());
					faceDetect.setAge(Integer.parseInt(age.getText()));
					faceDetect.setCode(Integer.parseInt(code.getText()));
					faceDetect.setSec(sec.getText());
					faceDetect.setReg(Integer.parseInt(reg.getText()));

					database.setFname(fname.getText());
					database.setLname(lname.getText());
					database.setAge(Integer.parseInt(age.getText()));
					database.setCode(Integer.parseInt(code.getText()));
					database.setSec(sec.getText());
					database.setReg(Integer.parseInt(reg.getText()));

					database.insert();
					
					javafx.application.Platform.runLater(new Runnable(){
						
						@Override
						 public void run() {
							pb.setProgress(100);
						 }
						 });
					savedLabel.setVisible(true);
					Thread.sleep(2000);
					
					javafx.application.Platform.runLater(new Runnable(){
						
						@Override
						 public void run() {
							pb.setVisible(false);
						 }
						 });
					
					
					javafx.application.Platform.runLater(new Runnable(){
						
						@Override
						 public void run() {
					       savedLabel.setVisible(false);
						 }
						 });
					

				} catch (InterruptedException ex) {
				}

			}).start();
			
			faceDetect.setSaveFace(true);

		}
         
	}

	@FXML
	protected void stopCam() throws SQLException {

		faceDetect.stop();
		startCam.setVisible(true);
		stopBtn.setVisible(false);
		supprimeruser.setVisible(false);
        
		/* this.saveFace=true; */

		putOnLog("Cam Stream Stopped!");
        frame.setVisible(false);
		recogniseBtn.setDisable(true);
		saveBtn.setDisable(true);
		dataPane.setDisable(true);
		stopRecBtn.setDisable(true);
		disableBtn();
		eyeBtn.setDisable(true);
		shapeBtn.setDisable(true);
		exit1.setDisable(true);
		supprimeruser.setDisable(true);
		motionBtn.setDisable(true);
		ocrBtn.setDisable(true);
		capBtn.setDisable(true);
		database.db_close();
		putOnLog("Database Connection Closed");
		isDBready=false;
	}

	@FXML
	protected void ocrStart() {

		supprimeruser.setVisible(false);
		try {
			
			Text text1 = new Text(ocrObj.init());

			text1.setStyle("-fx-font-size: 14; -fx-fill: blue;");

			ocr.getChildren().add(text1);

		} catch (FontFormatException e) {

			e.printStackTrace();
		}

	}

	@FXML
	protected void capture() {

		supprimeruser.setVisible(false);
		faceDetect.setOcrMode(true);

	}

	@FXML
	protected void exitfenetre() {
       

		System.exit(0);
		

	}
    int eye_count=0;
	@FXML
	protected void startEyeDetect() {
		supprimeruser.setVisible(false);
          if(eye_count > 0) { 
           faceDetect.setEyeDetection(false);
           eye_count =0;
           }
          else {  
		 faceDetect.setEyeDetection(true);
         eye_count++;
         }
	}

	@FXML
	protected void gitStart() {

		
		    supprimeruser.setVisible(false);
		    activeBtn();
		    noVisibleBtn();
		    no_gitBtn.setVisible(true);
		    webview.setVisible(true);
		    frame.setVisible(false);
		    VisibleBtn();
		    gitBtn.setVisible(false); 

	        // Get WebEngine via WebView
	        WebEngine webEngine =webview.getEngine();
	         
	        // Load page
	        webEngine.load("https://github.com/");

	}
	@FXML
	protected void onInsta() {
		
		    supprimeruser.setVisible(false);
		    activeBtn();
		    noVisibleBtn();
		    no_instaBtn.setVisible(true);
		    webview.setVisible(true);
		    frame.setVisible(false);
		    VisibleBtn();
		    instaBtn.setVisible(false);

	        // Get WebEngine via WebView
	        WebEngine webEngine =webview.getEngine();
	         
	        // Load page
	        webEngine.load("https://www.instagram.com/");

	}
	@FXML
	protected void onTwitter() {

		
		    supprimeruser.setVisible(false);
		    activeBtn();
		    noVisibleBtn();
		    no_twitterBtn.setVisible(true);
		    webview.setVisible(true);
		    frame.setVisible(false);
		    VisibleBtn();
		    twitterBtn.setVisible(false);

	        // Get WebEngine via WebView
	        WebEngine webEngine =webview.getEngine();
	         
	        // Load page
	        webEngine.load("https://twitter.com/login?lang=fr");

	}
	@FXML
	protected void onGoogle() {
		    supprimeruser.setVisible(false);
            activeBtn();
		    noVisibleBtn();
		    no_googleBtn.setVisible(true);
		    webview.setVisible(true);
		    frame.setVisible(false);
		    VisibleBtn();
		    googleBtn.setVisible(false);
	        // Get WebEngine via WebView
	        WebEngine webEngine =webview.getEngine();
	        // Load page
	        webEngine.load("https://www.google.fr/");

	}
	@FXML
	protected void facebookStart() {
		
		supprimeruser.setVisible(false);
		activeBtn();
		noVisibleBtn();
		no_faceBtn.setVisible(true);
		webview.setVisible(true);
		frame.setVisible(false);
		VisibleBtn();
		facebookBtn.setVisible(false);
        // Get WebEngine via WebView
        WebEngine webEngine =webview.getEngine();    
        // Load page
        webEngine.load("https://www.facebook.com/");
	}

	@FXML
	protected void gmailStart() {
		
		
		    supprimeruser.setVisible(false);
		    activeBtn();
		    noVisibleBtn();
		    no_gmailBtn.setVisible(true);
		    webview.setVisible(true);
		    frame.setVisible(false);
		    VisibleBtn();
		    gmailBtn.setVisible(false);
        // Get WebEngine via WebView
        WebEngine webEngine =webview.getEngine();
         
        // Load page
        webEngine.load("https://mail.google.com/");

	}

	@FXML
	protected void supprimeruser() {

		if (user.get(1).isEmpty() || user.get(2).isEmpty() ) {

			new Thread(() -> {

				try {
					warning.setVisible(true);

					Thread.sleep(2000);

					warning.setVisible(false);

				} catch (InterruptedException ex) {
				}

			  }).start();

		 } else {
		new Thread(() -> {

			try {

				//faceDetect.setFname(fname.getText());


				

				database.userDelete(user.get(1) , user.get(2));
				File f= new File("C:\\Users\\asmae el arrassi\\ensahVision2\\faces\\"+user.get(0)+"-"+user.get(1)+"_"+user.get(2)+"_15.jpg");           //file to be delete  
				f.delete();
				System.out.println(f.delete());
				javafx.application.Platform.runLater(new Runnable(){
					
					@Override
					 public void run() {
						pb.setProgress(100);
					 }
					 });
				savedLabel.setVisible(true);
				Thread.sleep(2000);
				
				javafx.application.Platform.runLater(new Runnable(){
					
					@Override
					 public void run() {
						pb.setVisible(false);
					 }
					 });
				
				
				javafx.application.Platform.runLater(new Runnable(){
					
					@Override
					 public void run() {
				 savedLabel.setVisible(false);
					 }
					 });

			} catch (InterruptedException ex) {
			}

		}).start();}




	}

	@FXML
	protected void shapeStart() {

		faceDetect.stop();
		SquareDetector shapeFrame = new SquareDetector();
		shapeFrame.loop();
		

	}
	@FXML
	protected void onExitFace() {
		supprimeruser.setVisible(false);
		no_faceBtn.setVisible(false);
		facebookBtn.setVisible(true);
		frame.setVisible(true);
		webview.setVisible(false);
	}
	@FXML
	protected void onExitGit() {
		supprimeruser.setVisible(false);
		 no_gitBtn.setVisible(false);
		  gitBtn.setVisible(true);
		  frame.setVisible(true);
		  webview.setVisible(false);
		
	}
	@FXML
	protected void onExitGoogle() {
	  supprimeruser.setVisible(false);
      no_googleBtn.setVisible(false);
	  googleBtn.setVisible(true);
	  frame.setVisible(true);
	  webview.setVisible(false);
	}
	@FXML
	protected void onExitGmail() {

		 supprimeruser.setVisible(false);
		 no_gmailBtn.setVisible(false);
		  gmailBtn.setVisible(true);
		  frame.setVisible(true);
		  webview.setVisible(false);
	}
	@FXML
	protected void onExitTwitter() {
		 supprimeruser.setVisible(false);
		 no_twitterBtn.setVisible(false);
		  twitterBtn.setVisible(true);
		  frame.setVisible(true);
		  webview.setVisible(false);
		
	}

	@FXML
	protected void onExitInsta() {
		 supprimeruser.setVisible(false);
		 no_instaBtn.setVisible(false);
		  instaBtn.setVisible(true);
		  frame.setVisible(true);
		  webview.setVisible(false);
		
	}

	private ImageView createImageView(final File imageFile) {

		try {
			final Image img = new Image(new FileInputStream(imageFile), 120, 0, true, true);
			imageView1 = new ImageView(img);

			imageView1.setStyle("-fx-background-color: BLACK");
			imageView1.setFitHeight(120);

			imageView1.setPreserveRatio(true);
			imageView1.setSmooth(true);
			imageView1.setCache(true);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return imageView1;
	}
	/*public void loadPhotos() {
		String path = filePath;

		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		//Image reader from the mentioned folder
		for (final File file : listOfFiles) {

			imageView1 = createImageView(file);
			tile.getChildren().addAll(imageView1);
		}
	}*/
	protected void disableBtn() {
		googleBtn.setDisable(true);
		gmailBtn.setDisable(true);
		facebookBtn.setDisable(true);
	    gitBtn.setDisable(true);
	    instaBtn.setDisable(true);
	    twitterBtn.setDisable(true);
	}
	protected void activeBtn() {
		googleBtn.setDisable(false);
	    gmailBtn.setDisable(false);
	    facebookBtn.setDisable(false);
	    gitBtn.setDisable(false);
	    instaBtn.setDisable(false);
	    twitterBtn.setDisable(false);
	}
	protected void noVisibleBtn() {
		no_faceBtn.setVisible(false);
		no_gitBtn.setVisible(false);
		no_gmailBtn.setVisible(false);
		no_googleBtn.setVisible(false);
		no_instaBtn.setVisible(false);
		no_twitterBtn.setVisible(false);
	}
	protected void VisibleBtn() {
		facebookBtn.setVisible(true);
		gitBtn.setVisible(true);
		gmailBtn.setVisible(true);
		googleBtn.setVisible(true);
		instaBtn.setVisible(true);
		twitterBtn.setVisible(true);
	}

}
