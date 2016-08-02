package utils;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import java.awt.*;

/**
 * Created by Иван on 31.07.2016.
 */
public class MidiPlayer {

    public static MidiEvent makeEvent(int comd, int chan, int one, int two, int tick){
        MidiEvent event = null;
        try {
            ShortMessage message = new ShortMessage(comd, chan, one, two);
            event = new MidiEvent(message,tick);
        }catch (Exception e)
        {}
            return  event;

    }

}
