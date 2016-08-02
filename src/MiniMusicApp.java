/**
 * Created by Иван on 29.07.2016.
 */

import utils.MidiPlayer;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MiniMusicApp implements ControllerEventListener {


    private static final int NOTE_ON = 144, NOTE_OFF = 128, CONTROLLER_EVENT = 176;
    static JFrame frame = new JFrame("Музыкальный клип");
    static MyDrawPanel ml;
    static JButton button= new JButton("start");
    public static void main(String[] args) {
        MiniMusicApp mini = new MiniMusicApp();
        mini.play();
    }

    public void play() {
        setUpGui();
        try {
            Sequencer player = MidiSystem.getSequencer();
            player.open();

            int[] eventsIWant = {127};
            player.addControllerEventListener(ml, eventsIWant);
            Sequence seq = new Sequence(Sequence.PPQ, 4);
            Track track = seq.createTrack();
            for (int i = 5; i < 61; i += 4) {
                track.add(MidiPlayer.makeEvent(NOTE_ON, 1, i, 100, i));
                track.add(MidiPlayer.makeEvent(CONTROLLER_EVENT, 1, 127, 0, i));
                track.add(MidiPlayer.makeEvent(NOTE_OFF, 1, i, 100, i + 2));
            }
            player.setSequence(seq);
            player.setTempoInBPM(220);
            player.start();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }
    public void setUpGui(){
        ml = new MyDrawPanel();
        frame.setContentPane(ml);

        frame.setBounds(30,30,300,300);
        frame.setVisible(true);
    }
    @Override
    public void controlChange(ShortMessage event) {
        System.out.println("ля");
    }

    class MyDrawPanel extends JPanel implements ControllerEventListener {
        boolean mMsg = false;

        public void paintComponent(Graphics graphics) {
            if (mMsg) {
                Graphics2D graphics2D = (Graphics2D) graphics;
                int r = (int) (Math.random() * 250);
                int g = (int) (Math.random() * 250);
                int b = (int) (Math.random() * 250);
                graphics.setColor(new Color(r, g, b));
                int height = (int) (Math.random() * 120 + 10);
                int width = (int) (Math.random() * 120 + 10);
                int x = (int) (Math.random() * 40 + 10);
                int y = (int) (Math.random() * 40 + 10);
                graphics.fillRect(x, y, height, width);
                mMsg = false;
            }

        }

        @Override
        public void controlChange(ShortMessage event) {
            mMsg = true;
            repaint();
        }
    }
}
