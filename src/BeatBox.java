import javafx.stage.FileChooser;
import utils.MidiPlayer;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by Иван on 01.08.2016.
 */
public class BeatBox {
    JPanel mMainPanel;
    ArrayList<JCheckBox> mCheckBoxList;
    Sequencer mSequencer;
    Sequence mSequence;
    Track mTrack;
    JFrame mJFrame;
    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat",
            "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap",
            "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga",
            "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo",
            "Open Hi Conga"};
    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public static void main(String[] args) {
        new BeatBox().buildGui();
    }

    private void buildGui() {
        mJFrame = new JFrame("Cyber BeatBox");
        mJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mCheckBoxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        JButton saveTrack = new JButton("Save Track");
        saveTrack.addActionListener(new MySendListener());
        buttonBox.add(saveTrack);

        JButton loadTrack = new JButton("Load Track");
        loadTrack.addActionListener(new MyReadInListener());
        buttonBox.add(loadTrack);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        mJFrame.getContentPane().add(background);
        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mMainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mMainPanel);
        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            mCheckBoxList.add(c);
            mMainPanel.add(c);
        }

        setUpMidi();
        mJFrame.setBounds(50, 50, 300, 300);
        mJFrame.pack();
        mJFrame.setVisible(true);
    }


    private void setUpMidi() {
        try {
            mSequencer = MidiSystem.getSequencer();
            mSequencer.open();
            mSequence = new Sequence(Sequence.PPQ, 4);
            mTrack = mSequence.createTrack();
            mSequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildTrackAndStart() {
        int[] trackList = null;
        mSequence.deleteTrack(mTrack);
        mTrack = mSequence.createTrack();
        for (int i = 0; i < 16; i++) {
            trackList = new int[16];
            int key = instruments[i];
            for (int j = 0; j < 16; j++) {

                JCheckBox jc = (JCheckBox) mCheckBoxList.get(j + 16 * i);
                if (jc.isSelected()) {
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            }

            makeTracks(trackList);
            mTrack.add(MidiPlayer.makeEvent(176, 1, 127, 0, 16));

        }

        mTrack.add(MidiPlayer.makeEvent(192, 9, 1, 0, 15));
        try {
            mSequencer.setSequence(mSequence);
            mSequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            mSequencer.start();
            mSequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void makeTracks(int[] trackList) {
        for (int i = 0; i < 16; i++) {
            int key = trackList[i];

            if (key != 0) {
                mTrack.add(MidiPlayer.makeEvent(144, 9, key, 100, i));
                mTrack.add(MidiPlayer.makeEvent(128, 9, key, 100, i + 1));
            }
        }
    }

    private class MyStartListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            buildTrackAndStart();
        }
    }

    private class MyStopListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            mSequencer.stop();
        }
    }

    private class MyUpTempoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = mSequencer.getTempoFactor();
            mSequencer.setTempoFactor((float)(tempoFactor * 1.03));
        }
    }

    private class MyDownTempoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = mSequencer.getTempoFactor();
            mSequencer.setTempoFactor((float)(tempoFactor * 0.97));
        }
    }
    public class MySendListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean[] checkboxState = new boolean[256];
            for (int i = 0; i < checkboxState.length; i++) {
                JCheckBox check = mCheckBoxList.get(i);
                if(check.isSelected()){
                    checkboxState[i] = true;
                }
            }
            try{
                JFileChooser choose = new JFileChooser();
                choose.showSaveDialog(mJFrame);
                File file = choose.getSelectedFile();
                FileOutputStream fileStream = new FileOutputStream(file);
                ObjectOutputStream os = new ObjectOutputStream(fileStream);
                os.writeObject(checkboxState);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    public class MyReadInListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean[] checkboxState = null;

            try{
                JFileChooser choose = new JFileChooser();
                choose.showOpenDialog(mJFrame);
                File file = choose.getSelectedFile();
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream is = new ObjectInputStream(fileIn);
                checkboxState=(boolean[])is.readObject();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            for (int i = 0; i < checkboxState.length; i++) {
                JCheckBox check =  mCheckBoxList.get(i);
                check.setSelected(checkboxState[i]);
            }
            mSequencer.stop();
            buildTrackAndStart();
        }

    }

}
