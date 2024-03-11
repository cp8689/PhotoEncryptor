package com.example.helloworld;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class MainController implements Initializable {

    public String path;
    public static String name, hash; //This must be static due to restrictions in JavaFX
    public static int iterations = 10000, keyLength = 512;
    public static byte[] saltBytesFromFile;
    private boolean hasname;
    private static File f;

    FileWriter out  = new FileWriter("passwords.txt", true);
    private Fileparser fileParser = new Fileparser("passwords.txt");
    OnStart onstart;
    BufferedImage image;
    AES aes = new AES(); // this means for every new instance of MainController also create a new instance of Aes for it which will never be null
    int widthofimage, heightofimage, numberofpixils;


    @FXML
    private PasswordField passwordtextbox;

    @FXML
    private PasswordField decryptpasswordtextbox;

    @FXML
    private AnchorPane rootpane;
    //initiates the fileparser
    public MainController() throws IOException {
        fileParser.parse();
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }


    @FXML
    private void encryptpress(ActionEvent event) throws Exception {
        //File chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"));
        f = fileChooser.showOpenDialog(null);    //f is the image
        //Checks if a jpg file was chosen
        if (f == null) {//if a file wasn't chosen...
            AlertBox.display("Error", "No file was chosen");
            return;
        }
        path = f.getAbsolutePath();//gets absolute path of file
        name = f.getName();//gets name of file
        if (fileParser.checkforfile(name)){
            AlertBox.display("Error", "This filename has already been encrypted.");
            return;
        }
        if (fileParser.checkforfile(name)) { //checks to see if the image is in the folder, goes back to parent node if false
            if (fileParser.hasHash(name) || fileParser.hasHash(name)) {
                AlertBox.display("Error", "This filename already has a password.");
                //if true, then go back to main menu and display: "This filename already has a password."
                Parent parent = onstart.getRoot(); //Not sure if this gets the pointer to the root or just a copy of the root, but it might be used to go backwards in scenes
                Scene parentscene = onstart.getScene();
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(parentscene);
                window.show();
                return;
            }
            AlertBox.display("Error", "This filename was added without a password: Delete the file reference.");
            return;
            //hasname = true; //maybe close this automatically
            //skips the addname process if hasname is true
        }
        //out.write(name);

        // I used 1 main controller with static variables instead of one controller per fxml. In the future, using multiple controllers is more efficient

        onstart.changeScene("/com/example/helloworld/password.fxml");
    }


    @FXML
    private void Createpassword(ActionEvent event) throws Exception {

        name = name.toLowerCase().replace(".jpg", "") + ".encrypted";
        FileWriter writetoencoded = new FileWriter(name);
        //This copies the file and places it in the same directory
        //gets inputted password
        String passwordPlainText = passwordtextbox.getText();
        //converts that string to a char array
        char[] password = passwordPlainText.toCharArray();

        //saltBytesFromFile for the password is created
        saltBytesFromFile = aes.getSalt();
        //hash is created with test password

        System.out.println("password: " + passwordPlainText);
        byte[] hashbyte = aes.hashPassword(password, saltBytesFromFile, iterations, keyLength);
        hash = javax.xml.bind.DatatypeConverter.printBase64Binary(hashbyte);

        //converted to a string to compare
        //stores the hash and string inside the passwords.txt document
        storeInFile();

        //this stores the image into a buffer
        image = ImageIO.read(f);
        name = name.toLowerCase().replace(".jpg", "") + ".encrypted";
        String newfile = f.getName();
        File EncryptedImage = new File(newfile);
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        widthofimage = image.getWidth();
        heightofimage = image.getHeight();
        numberofpixils = widthofimage * heightofimage;
        int add = 0;
        byte[] bytes = new byte[numberofpixils];
        //this is the process of storing the buffer
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                bytes[add] = (byte) image.getRGB(x, y);
                add++;
            }
        }

        byte[] encryptedbytes = new byte[numberofpixils];
        //then it encrypts that buffer
        AES.EncryptedJpgInfo ecryptedInfo = aes.encrypt(bytes, passwordPlainText); //passwordPlainText is real password holder
        String encryptedContentBase64 = javax.xml.bind.DatatypeConverter.printBase64Binary(ecryptedInfo.ecryptedBytes);
        String ivBase64 = javax.xml.bind.DatatypeConverter.printBase64Binary(ecryptedInfo.ivBytes);

        writetoencoded.write(encryptedContentBase64 + "|" + ivBase64 + "|" + image.getWidth()+ "x" + image.getHeight());
        writetoencoded.close();
        //byte[] newimageencryptedbytes = newimageencryptedbytes[encryptedbytes]
        //then it stores that encrypted buffer into a new image
        //passwordPlainText = passwordtextbox.getText();
        //System.out.println(passwordPlainText);
        AlertBox.display("Success", "This file " + name + " has been encrypted");
        //onstart.changeScene("/com/example/helloworld/mainmenu.fxml");  WORK ON THIS

        //image = ImageIO.fileParser(f);

        // doAESEncryption(passwordPlainText);
    }


    @FXML
    private void decryptpress(ActionEvent event) throws Exception {
        //if passwords.txt is not empty: AnchorPane pane = FXMLLoader.load(getClass().getResource("password2.fxml"));
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(".encrypted Files", "*.encrypted"));
        File f = fileChooser.showOpenDialog(null);
        if (f == null) {//if a file wasn't chosen...
            AlertBox.display("Error", "No file was chosen");
            return;
        }
        fileParser = new Fileparser("passwords.txt");
        out = new FileWriter("passwords.txt", true);
        path = f.getAbsolutePath();//gets absolute path of file
        name = f.getName();//gets name of file

        onstart.changeScene("/com/example/helloworld/password2.fxml");
    }


    @FXML
    private void usePassword(ActionEvent event) throws Exception {
        //gets inputted password
        String passwordPlainText = decryptpasswordtextbox.getText();
        //gets the saltBytesFromFile from the passwords.txt
        String saltBase64FromFile = fileParser.getSalt(name);

        saltBytesFromFile = javax.xml.bind.DatatypeConverter.parseBase64Binary(saltBase64FromFile);

        char[] password = passwordPlainText.toCharArray();
        //hashes the entered password and saltBytesFromFile stored for the specific image

        byte[] hashbyte = aes.hashPassword(password, saltBytesFromFile, iterations, keyLength);
        String hashcompare = javax.xml.bind.DatatypeConverter.printBase64Binary(hashbyte);
        hash = fileParser.getHash(name);


        if( hash.equals(hashcompare)){ //this compares to see if the hash is that same as te hash that was stored
            System.out.println("Test");
            f = new File(name);
            Scanner scanner = new Scanner(f);
            String fileContent = scanner.useDelimiter("\\A").next();
            scanner.close();

            String[] fileSplit = fileContent.split("\\|");

            String encryptedBase64 = fileSplit[0];
            String ivdBase64 = fileSplit[1];
            String dimensions = fileSplit[2];
            String[] dimenSplit = dimensions.split("x");

            byte[] encryptedBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(encryptedBase64);
            byte[] ivBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(ivdBase64);
            int width = Integer.parseInt(dimenSplit[0]);
            int height = Integer.parseInt(dimenSplit[1]);

            //
            //image = ImageIO.read(f);
            String newfile = "Decrypted" + f.getName().toLowerCase().replace(".encrypted", "") + ".jpg";
            File DecryptedImage = new File(newfile);
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

            byte[] output = aes.decrypt(encryptedBytes, ivBytes, passwordPlainText); //passwordPlainText is real password holde;

            int add = 0;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    newImage.setRGB(x, y, output[add]);
                    add++;
                }
            }
            ImageIO.write(newImage, "JPG", DecryptedImage);
            //passwordPlainText = passwordtextbox.getText();
            AlertBox.display("Success", "This file " + name + " has been decrypted");
        }else{
          AlertBox.display("Failure", "The password in file " + name + " has does not match");
        }


    }


    public void storeInFile() throws IOException {
        String saltstring = javax.xml.bind.DatatypeConverter.printBase64Binary(saltBytesFromFile);

        out.write(name);
        out.write(" ");
        out.write(hash);
        out.write(" ");
        out.write(saltstring);
        out.write("\n");
        out.close();
    }


    public void setOnStartReference(OnStart onStartReference) {
        onstart = onStartReference;
    }

}

/*
///////////////////////////////////////////////////////////////////////////////////////////////////////


This opens a new scene - doesn't work now
        Parent parent = FXMLLoader.load(getClass().getResource("password.fxml"));
        Scene enterpasswordscene = new Scene(parent);

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(enterpasswordscene);
        window.show();


 This prints to passwords.txt - works
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"));
        File f = fileChooser.showOpenDialog(null);
        if(f != null){
            path = f.getAbsolutePath();//gets absolute path of file
            name = f.getName();//gets name of file
            try {
                FileWriter out = new FileWriter("passwords.txt", true);
                out.write(name);
                out.write("-");
                out.write(path);
                out.write("-");
                out.write("\n");
                out.close();
                System.out.println(name);
            }catch(IOException e){
                e.printStackTrace();
            }
        }


        //XMLLoader.load(getClass().getResource("mainmenu.fxml"));  This loads xml - doesn't work
 */
