/**
 * Copyright (C) 2017 Lukas Wiest
 * v1.2.1
 */
package de.siling.jaad;

import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.adts.ADTSDemultiplexer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This library plays AAC audio files, using the JAAD decoder. To use this lib,
 * you need to have the JAAD library jar, too.
 *
 * @author Lukas Wiest
 */
public class AACPlayer
{

    static boolean isPlaying = true,stop=false;
    static private void decodeAndPlayAAC() {
        SourceDataLine line = null;
        byte[] b;
        try {
            isPlaying = true;
            final ADTSDemultiplexer adts = new ADTSDemultiplexer(new FileInputStream("/Users/alex.zhu/Downloads/aac测试音频文件.aac"));
            final Decoder dec = new Decoder(adts.getDecoderSpecificInfo());
            final SampleBuffer buf = new SampleBuffer();
            while(!stop) {
                b = adts.readNextFrame();
                dec.decodeFrame(b, buf);

                if(line==null) {
                    final AudioFormat aufmt = new AudioFormat(buf.getSampleRate(), buf.getBitsPerSample(), buf.getChannels(), true, true);
                    line = AudioSystem.getSourceDataLine(aufmt);
                    line.open();
                    line.start();
                }
                b = buf.getData();
                line.write(b, 0, b.length);
            }
        }
        catch (EOFException e){
            e.printStackTrace();
        }
        catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (AACException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(line!=null) {
                line.stop();
                line.close();
                isPlaying = false;
            }
        }
    }

    public static void main(String[] args){
        //File[] files = {new File("/Users/alex.zhu/Downloads/aac测试音频文件.aac")};
        decodeAndPlayAAC();
        //player.play();
    }
}
