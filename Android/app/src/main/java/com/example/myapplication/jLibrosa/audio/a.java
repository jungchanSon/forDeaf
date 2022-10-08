package audio;

import audio.exception.FileFormatNotSupportedException;
import audio.wavFile.WavFileException;
import java.io.IOException;

public class a {

    public static void main(String[] args) throws FileFormatNotSupportedException, IOException, WavFileException {
        System.out.println("hello");
        JLibrosa jLibrosa = new JLibrosa();


        String path = "audioFiles/1995-1826-0003.wav";
        int sampleRate = -1;
        int duration = -1;
        int samplerate = jLibrosa.getSampleRate();

        float audioFeat []= jLibrosa.loadAndRead(path, sampleRate, duration);

        float mfccValues[][] = jLibrosa.generateMFCCFeatures(audioFeat, sampleRate, 40);


        System.out.println("Size of MFCC Feature Values: (" + mfccValues.length + " , " + mfccValues[0].length + " )");
        System.out.println("Size of MFCC Feature Values: (" + mfccValues.length + " , " + mfccValues[0].length + " )");

        for(int i=0;i<1;i++) {
            for(int j=0;j<10;j++) {
                System.out.printf("%.6f%n", mfccValues[i][j]);
            }
        }

    }

}
