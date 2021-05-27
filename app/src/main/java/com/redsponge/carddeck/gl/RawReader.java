package com.redsponge.carddeck.gl;

import android.content.res.Resources;

import androidx.annotation.RawRes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RawReader {

    public static Resources resources;

    public static String readRawFile(@RawRes int resource) {
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(resources.openRawResource(resource)));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                output.append(line);
            }
            return output.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
