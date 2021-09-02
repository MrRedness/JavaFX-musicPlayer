package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements Initializable {

    @FXML
    private Pane pane;
    @FXML
    private Label songLabel;
    @FXML
    private Button playButton, pauseButton, resetButton, previousButton, nextButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar songProgressBar;
    private Media media;
    private MediaPlayer mediaPlayer;

    private File directory;
    private File[] files;
    private ArrayList<File> songs;
    private int songNumber;
    private int[] speeds = {25,50,75,100,125,150,175,200};
    private Timer timer;
    private TimerTask task;
    private boolean running;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songs = new ArrayList<File>();
        directory = new File("music");
        files = directory.listFiles();
        if (files != null) {
            for(File file : files) {
                songs.add(file);
                System.out.println(file);
            }
            speedBox.setOnAction(this::changeSpeed);
        }
        media = new Media(songs.get(songNumber).toURI().toString());
        System.out.println(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(songs.get(songNumber).getName());

        for(int i=0; i < speeds.length; i++) {
            speedBox.getItems().add(String.valueOf(speeds[i]) + "%");
        }

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });
        songProgressBar.setStyle("-fx-accent: #ffc118;");
    }

    public void changeSpeed(ActionEvent actionEvent) {
        mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
    }

    public void playMedia() {
        beginTimer();
        mediaPlayer.play();
        mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
        if(speedBox.getValue() == null) {mediaPlayer.setRate(1);}
        else{changeSpeed(null);}
    }
    public void pauseMedia() {cancelTimer(); mediaPlayer.pause();}
    public void resetMedia() {songProgressBar.setProgress(0); mediaPlayer.seek(Duration.seconds(0));}
    private void changeSong() {
        mediaPlayer.stop();
        if(running) {cancelTimer();}
        media = new Media(songs.get(songNumber).toURI().toString());
        System.out.println(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(songs.get(songNumber).getName());
        playMedia();
    }
    public void nextMedia() {
        if(songNumber < songs.size() - 1) {
            songNumber++;
        }
        else {
            songNumber = 0;
        }
        changeSong();
    }
    public void previousMedia() {
        if(songNumber > 0) {
            songNumber--;
        }
        else {
            songNumber = songs.size() - 1;
        }
        changeSong();
    }
    public void beginTimer(){
       timer = new Timer();
       task = new TimerTask() {
           @Override
           public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
         //      System.out.println(current/end);
               songProgressBar.setProgress(current/end);

               if (current / end == 1) {
                   cancelTimer();
               }
           }
       };
       timer.scheduleAtFixedRate(task,0,1000);

    }

    public void cancelTimer(){
        running = false;
        timer.cancel();
    }
}
