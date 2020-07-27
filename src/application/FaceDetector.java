package application;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvSize;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_INTER_AREA;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvResize;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;
import org.bytedeco.javacpp.helper.opencv_core.AbstractCvMemStorage;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import org.bytedeco.javacpp.opencv_core.IplImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Path;
import net.sourceforge.tess4j.*;

public class FaceDetector implements Runnable {
	
	static int compteur=1;

	Database database = new Database();
	ArrayList<String> user;

	FaceRecognizer faceRecognizer = new FaceRecognizer();

	MotionDetector motionDetector = new MotionDetector();
	OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
	Java2DFrameConverter paintConverter = new Java2DFrameConverter();
	ArrayList<String> output = new ArrayList<String>();
	File file = new File("C:/Users/asmae el arrassi/JEE-workspace/ensahVision/ID/789.jpg");
	Image image1 = new Image(file.toURI().toString());

	@FXML
	public Label ll;
	private Exception exception = null;
	
	private int count = 15;
	public String classiferName;
	public File classifierFile;
	String path=new File("").getAbsolutePath();
	
	public boolean saveF = false;
	public boolean saveFace = false;
	public boolean isRecFace = false;
	public boolean isOutput = false;
	public boolean isOcrMode = false;
	public boolean isMotion = false;
	public boolean isEyeDetection = false;
	public boolean isSmile = false;
	public boolean isID = false;
	public boolean showId = false;
	public boolean saveId = false;
	/*public boolean isUpperBody = false;
	public boolean isFullBody = false;*/
	private boolean stop = false;

	private CvHaarClassifierCascade classifier = null;
	private CvHaarClassifierCascade classifierEye = null;
	private CvHaarClassifierCascade classifierSideFace = null;
	/*private CvHaarClassifierCascade classifierUpperBody = null;
	private CvHaarClassifierCascade classifierFullBody = null;*/
	private CvHaarClassifierCascade classifierSmile = null;
	private CvHaarClassifierCascade classifierEyeglass = null;
	
	
	public CvMemStorage storage = null;
	private FrameGrabber grabber = null;
	private IplImage grabbedImage = null, temp, temp2, grayImage = null, smallImage = null;
	public ImageView frames2;
	public ImageView frames;
    private ImageView imageId=new ImageView();
    private BufferedImage image2;
    public IplImage iplImage=null;
	
	
	private CvSeq faces = null;
	private CvSeq eyes = null;
	private CvSeq smile = null;
	/*private CvSeq upperBody = null;
	private CvSeq fullBody = null;*/
	private CvSeq sideface = null;

	

	int recogniseCode;
	int recogniseId;
	public int code;
	public int reg;
	public int age;
	public int Code1;
	
	public String fname; //first name
	public String Lname; //last name
	public String sec; //section
	public String name; 
	public String Prenom;
	public String Nom;

	public void init() {
		faceRecognizer.init();

		setClassifier("haar/haarcascade_frontalface_alt.xml");
		setClassifierEye("haar/haarcascade_eye.xml");
		setClassifierSideFace("haar/haarcascade_profileface.xml");
		/*setClassifierUpperBody("haar/haarcascade_upperbody.xml");
		setClassifierFullBody("haar/haarcascade_fullbody.xml");*/
		setClassifierSmile("haar/haarcascade_smile.xml");
		setClassifierEyeGlass("haar/haarcascade_eye_tree_eyeglasses.xml");

	}

	public void start() {
		try {
			new Thread(this).start();
		} catch (Exception e) {
			if (exception == null) {
				exception = e;

			}
		}
	}

	@Override
	public void run() {
		try {
			try {
				grabber = OpenCVFrameGrabber.createDefault(0); //parameter 0 default camera , 1 for secondary

				grabber.setImageWidth(700);
				grabber.setImageHeight(700);
				grabber.start();

				grabbedImage = grabberConverter.convert(grabber.grab());

				storage = AbstractCvMemStorage.create();
			} catch (Exception e) {
				if (grabber != null)
					grabber.release();
				grabber = new OpenCVFrameGrabber(0);
				grabber.setImageWidth(700);
				grabber.setImageHeight(700);
				grabber.start();
				grabbedImage = grabberConverter.convert(grabber.grab());

			}
			
			grayImage =   cvCreateImage(cvGetSize(grabbedImage), 8, 1); ; //converting image to grayscale
			
			//reducing the size of the image to speed up the processing
			smallImage = cvCreateImage(cvSize(grabbedImage.width() / 4, grabbedImage.height() / 4), 8, 1); 

			stop = false;

			while (!stop && (grabbedImage = grabberConverter.convert(grabber.grab())) != null) {

				Frame frame = grabberConverter.convert(grabbedImage);
				BufferedImage image = paintConverter.getBufferedImage(frame, 2.2 / grabber.getGamma());
				Graphics2D g2 = image.createGraphics();

				if (faces == null) {
					cvClearMemStorage(storage);
					
					//creating a temporary image
					temp = cvCreateImage(cvGetSize(grabbedImage), grabbedImage.depth(), grabbedImage.nChannels());

					cvCopy(grabbedImage, temp);

					cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
					cvResize(grayImage, smallImage, CV_INTER_AREA);
					
					//cvHaarDetectObjects(image, cascade, storage, scale_factor, min_neighbors, flags, min_size, max_size)
					faces = cvHaarDetectObjects(smallImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
					//face detection
					
					CvPoint org = null;
					if (grabbedImage != null) {

						if (isEyeDetection) { 		//eye detection logic
							eyes = cvHaarDetectObjects(smallImage, classifierEye, storage, 1.1, 3,
									CV_HAAR_DO_CANNY_PRUNING);

							if (eyes.total() == 0) {
								eyes = cvHaarDetectObjects(smallImage, classifierEyeglass, storage, 1.1, 3,
										CV_HAAR_DO_CANNY_PRUNING);

							}

							printResult(eyes, eyes.total(), g2);

						}

					

						if (isSmile) {
							try {
								smile = cvHaarDetectObjects(smallImage, classifierSmile, storage, 1.1, 3,
										CV_HAAR_DO_CANNY_PRUNING);

								if (smile != null) {
									printResult(smile, smile.total(), g2);
									System.out.println("pas smile!");
								}else {
									System.out.println("avec smile!");
								}
							} catch (Exception e) {
								
								e.printStackTrace();
							}

						}

						if (isOcrMode) {
							try {

								OutputStream os = new FileOutputStream("captures.png");
								ImageIO.write(image, "PNG", os);
								System.out.println("Capture!!");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						isOcrMode = false;
						if (isID) {
							String fName = "ID/" + code+".jpg";
							cvSaveImage(fName, temp);
						}


						if (faces.total() == 0) {
							faces = cvHaarDetectObjects(smallImage, classifierSideFace, storage, 1.1, 3,
									CV_HAAR_DO_CANNY_PRUNING);

						}

						if (faces != null) {
							g2.setColor(Color.green);
							g2.setStroke(new BasicStroke(2));
							int total = faces.total();

							for (int i = 0; i < total; i++) {
								
								//printing rectange box where face detected frame by frame
								CvRect r = new CvRect(cvGetSeqElem(faces, i));
								g2.drawRect((r.x() * 4), (r.y() * 4), (r.width() * 4), (r.height() * 4));

								CvRect re = new CvRect((r.x() * 4), r.y() * 4, (r.width() * 4), r.height() * 4);

								cvSetImageROI(temp, re);

								// File f = new File("captures.png");

								org = new CvPoint(r.x(), r.y());

								if (isRecFace) {
									String names="Personne inconnue!";
									this.recogniseCode = faceRecognizer.recognize(temp);

									//getting recognised user from the database
									
									if(recogniseCode != -1)
									{
										database.init();
										user = new ArrayList<String>();
										user = database.getUser(this.recogniseCode);
										this.output = user;
										

										names = user.get(1) + " " + user.get(2);
										if (showId) {
											/*try {
												
												 file = new File(path+"\\ID\\id"+recogniseCode+".jpg");
												  image2 = ImageIO.read(file);
												
												  SampleController.imageID.setFitHeight(240);
												SampleController.imageID.setFitWidth(251);
												
												//imageId.setImage(image2);
												
												WritableImage showFrame1 = SwingFXUtils.toFXImage(image2, null);
												
												SampleController.imageID.setImage(showFrame1);
												SampleController.imageID.setVisible(true);
												showId=false;
												
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
												showId=false;
												System.out.println("tu n'a pas une carte national disponible");
											}*/
											showId=false;

										}
										
									
									}
								
									//printing recognised person name into the frame
									g2.setColor(Color.WHITE);
									g2.setFont(new Font("Arial Black", Font.BOLD, 20));
									
									g2.drawString(names, (int) (r.x() * 6.5), r.y() * 4);

								}

								
								

							}
							if(saveFace) {
								String fName = "faces/" + code + "-" + fname + "_" + Lname + "_" + count + ".jpg";
								cvSaveImage(fName, temp);
								count++;}
							saveFace=false;
							
							if (saveId) {
								
                                
								//extraire données de la carte national 
								File file =new File(path+"\\ID\\id"+SampleController.cpt+".jpg");
								
								 BufferedImage img1=ImageIO.read(file);
								ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
							    Java2DFrameConverter java2dConverter = new Java2DFrameConverter();
							     iplImage = iplConverter.convert(java2dConverter.convert(img1));
							    
							    IplImage grabId=cvCreateImage(cvGetSize(iplImage), 8, 1);;
							    
							    cvCvtColor(iplImage,grabId,CV_BGR2GRAY);
							  
							   
								cvSaveImage("IDgray/idgray.jpg",grabId);
                  		    	String[] dRecuperer=OCR.init(path+"\\IDgray\\idgray.jpg").split("\n");
                  		    	//Recuperer nom et prenom
								String[] dRecupererPrenom=dRecuperer[3].split(" ");
								String[] dRecupererNom=dRecuperer[5].split("[ \\W]");
								String dRecupererNom1 = dRecupererNom[1].replaceAll("[^A-Za-z]+", "");
							     Prenom=dRecupererPrenom[0];
								  Nom="";
								//comparer nom et prenom
								if(dRecupererNom[1].equals(dRecupererNom1)) {
									Nom=dRecupererNom[0]+" "+dRecupererNom[1];
								//System.out.println(dRecupererNom[0]+","+dRecupererNom[1]+","+dRecupererPrenom[0]);
								}
								else {
									Nom=dRecupererNom[0];
									//System.out.println(dRecupererNom[0]+","+dRecupererPrenom[0]);
								}
								System.out.println(fname+","+Lname);
								System.out.println(Nom+","+Prenom);
								
								//comparer le visage
								
		                               IplImage grayImage1 =   cvCreateImage(cvGetSize(iplImage), 8, 1); ; //converting image to grayscale
										
										//reducing the size of the image to speed up the processing
										IplImage smallImage1 = cvCreateImage(cvSize(iplImage.width() / 4, iplImage.height() / 4), 8, 1); 
										IplImage temp7 = cvCreateImage(cvGetSize(iplImage), iplImage.depth(), iplImage.nChannels());

										cvCopy(iplImage, temp7);

										cvCvtColor(iplImage, grayImage1, CV_BGR2GRAY);
										cvResize(grayImage1, smallImage1, CV_INTER_AREA);
										
										//cvHaarDetectObjects(image, cascade, storage, scale_factor, min_neighbors, flags, min_size, max_size)
										 CvSeq faces1 =null;
												faces1=cvHaarDetectObjects(smallImage1, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
										if(faces1!=null) {
											for (int i = 0; i < total; i++) {
												
												//printing rectange box where face detected frame by frame
												CvRect r = new CvRect(cvGetSeqElem(faces1, i));
											

												CvRect re = new CvRect((r.x() * 4), r.y() * 4, (r.width() * 4), r.height() * 4);

												cvSetImageROI(temp7, re);
										}
										}
									
										cvSaveImage("ID/idtest1.jpg", temp7);
									    Code1 = faceRecognizer.recognize(temp7);
										System.out.println(Code1);
										
									
									
								
									
								
								compteur++;
							
		
							}
							
							this.saveId = false;
							faces = null;
						}

						WritableImage showFrame = SwingFXUtils.toFXImage(image, null);

						javafx.application.Platform.runLater(new Runnable(){
							
							
							WritableImage showFrame = SwingFXUtils.toFXImage(image, null);
							
							
							@Override
							 public void run() {
							frames.setImage(showFrame);
							
							 }
							 });

						if (isMotion) {
							new Thread(() -> {

								try {

									motionDetector.init(grabbedImage, g2);

								} catch (InterruptedException ex) {
								} catch (Exception e) {
									
									e.printStackTrace();
								}

							}).start();

						}
						isMotion = false;

					}
					cvReleaseImage(temp);
				}

				}
			
		} catch (Exception e) {
			if (exception == null) {
				exception = e;

			}
		}
	}

	public ImageView getImageId() {
		return imageId;
	}

	public void setImageId(ImageView imageId) {
		this.imageId = imageId;
	}

	public void stop() {
		stop = true;

		grabbedImage = grayImage = smallImage = null;
		try {
			grabber.stop();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			
			e.printStackTrace();
		}
		try {
			grabber.release();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
		
			e.printStackTrace();
		}
		grabber = null;
	}

	public void setClassifier(String name) {

		try {

			setClassiferName(name);
			classifierFile = Loader.extractResource(classiferName, null, "classifier", ".xml");

			if (classifierFile == null || classifierFile.length() <= 0) {
				throw new IOException("Could not extract \"" + classiferName + "\" from Java resources.");
			}

			// Preload the opencv_objdetect module to work around a known bug.
			Loader.load(opencv_objdetect.class);
			classifier = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
			classifierFile.delete();
			if (classifier.isNull()) {
				throw new IOException("Could not load the classifier file.");
			}

		} catch (Exception e) {
			if (exception == null) {
				exception = e;

			}
		}

	}

	public void setClassifierEye(String name) {

		try {

			classiferName = name;
			classifierFile = Loader.extractResource(classiferName, null, "classifier", ".xml");

			if (classifierFile == null || classifierFile.length() <= 0) {
				throw new IOException("Could not extract \"" + classiferName + "\" from Java resources.");
			}

			// Preload the opencv_objdetect module to work around a known bug.
			Loader.load(opencv_objdetect.class);
			classifierEye = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
			classifierFile.delete();
			if (classifier.isNull()) {
				throw new IOException("Could not load the classifier file.");
			}

		} catch (Exception e) {
			if (exception == null) {
				exception = e;

			}
		}

	}

	public void setClassifierSmile(String name) {

		try {

			setClassiferName(name);
			classifierFile = Loader.extractResource(classiferName, null, "classifier", ".xml");

			if (classifierFile == null || classifierFile.length() <= 0) {
				throw new IOException("Could not extract \"" + classiferName + "\" from Java resources.");
			}

			// Preload the opencv_objdetect module to work around a known bug.
			Loader.load(opencv_objdetect.class);
			classifierSmile = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
			classifierFile.delete();
			if (classifier.isNull()) {
				throw new IOException("Could not load the classifier file.");
			}

		} catch (Exception e) {
			if (exception == null) {
				exception = e;

			}
		}

	}

	public void printResult(CvSeq data, int total, Graphics2D g2) {
		for (int j = 0; j < total; j++) {
			CvRect eye = new CvRect(cvGetSeqElem(eyes, j));

			g2.drawOval((eye.x() * 4), (eye.y() * 4), (eye.width() * 4), (eye.height() * 4));

		}
	}

	public void setClassifierSideFace(String name) {

		try {

			classiferName = name;
			classifierFile = Loader.extractResource(classiferName, null, "classifier", ".xml");

			if (classifierFile == null || classifierFile.length() <= 0) {
				throw new IOException("Could not extract \"" + classiferName + "\" from Java resources.");
			}

			// Preload the opencv_objdetect module to work around a known bug.
			Loader.load(opencv_objdetect.class);
			classifierSideFace = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
			classifierFile.delete();
			if (classifier.isNull()) {
				throw new IOException("Could not load the classifier file.");
			}

		} catch (Exception e) {
			if (exception == null) {
				exception = e;

			}
		}

	}

	
	public void setClassifierEyeGlass(String name) {

		try {

			setClassiferName(name);
			classifierFile = Loader.extractResource(classiferName, null, "classifier", ".xml");

			if (classifierFile == null || classifierFile.length() <= 0) {
				throw new IOException("Could not extract \"" + classiferName + "\" from Java resources.");
			}

			// Preload the opencv_objdetect module to work around a known bug.
			Loader.load(opencv_objdetect.class);
			classifierEyeglass = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
			classifierFile.delete();
			if (classifier.isNull()) {
				throw new IOException("Could not load the classifier file.");
			}

		} catch (Exception e) {
			if (exception == null) {
				exception = e;

			}
		}

	}


	public String getClassiferName() {
		return classiferName;
	}

	public void setClassiferName(String classiferName) {
		this.classiferName = classiferName;
	}

	public void setFrames2(ImageView frames2) {
		this.frames2 = frames2;
	}


	public boolean isEyeDetection() {

		return isEyeDetection;
	}

	public void setEyeDetection(boolean isEyeDetection) {
		this.isEyeDetection = isEyeDetection;
	}

	public boolean getOcrMode() {
		return isOcrMode;
	}

	public void setOcrMode(boolean isOcrMode) {
		this.isOcrMode = isOcrMode;
	}

	public void destroy() {
	}

	public boolean isMotion() {
		return isMotion;
	}

	public void setMotion(boolean isMotion) {
		this.isMotion = isMotion;
	}

	public ArrayList<String> getOutput() {
		return output;
	}

	public void clearOutput() {
		this.output.clear();
	}

	public void setOutput(ArrayList<String> output) {
		this.output = output;
	}

	public int getRecogniseCode() {
		return recogniseCode;
	}

	public void setRecogniseCode(int recogniseCode) {
		this.recogniseCode = recogniseCode;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return Lname;
	}

	public void setLname(String lname) {
		Lname = lname;
	}

	public int getReg() {
		return reg;
	}

	public void setReg(int reg) {
		this.reg = reg;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getSec() {
		return sec;
	}

	public void setSec(String sec) {
		this.sec = sec;
	}

	public void setFrame(ImageView frame) {
		this.frames = frame;
	}

	public void setSaveFace(Boolean f) {
		this.saveFace = f;
	}

	public Boolean getIsRecFace() {
		return isRecFace;
	}

	public void setIsRecFace(Boolean isRecFace) {
		this.isRecFace = isRecFace;
	}
	public boolean isSaveId() {
		return saveId;
	}

	public void setSaveId(boolean saveId) {
		this.saveId = saveId;
	}

	public int getRecogniseId() {
		return recogniseId;
	}

	public void setRecogniseId(int recogniseId) {
		this.recogniseId = recogniseId;
	}

	public boolean isShowId() {
		return showId;
	}

	public void setShowId(boolean showId) {
		this.showId = showId;
		
	}

	public boolean isSaveF() {
		return saveF;
	}

	public void setSaveF(boolean saveF) {
		this.saveF = saveF;
	}

	public int getCode1() {
		return Code1;
	}

	public void setCode1(int code1) {
		Code1 = code1;
	}

	public String getPrenom() {
		return Prenom;
	}

	public void setPrenom(String prenom) {
		Prenom = prenom;
	}

	public String getNom() {
		return Nom;
	}

	public void setNom(String nom) {
		Nom = nom;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	



}
