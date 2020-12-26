package com.rappelr.bloodmoon.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.rappelr.bloodmoon.Bloodmoon;

import lombok.NonNull;
import lombok.val;

public class YamlUtil {
	
	public void copy(@NonNull final String source, @NonNull final String destination) {
		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(Bloodmoon.getInstance().getResource("default_world.yml")));
			
			val lines = new ArrayList<String>();
			
			String line = new String();
			
			while((line = reader.readLine()) != null)
				lines.add(line);
			
			reader.close();
			
			writeTo(destination, lines.toArray(new String[0]));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeTo(@NonNull final String file, @NonNull final String... lines) {
		writeTo(new File(file), lines);
	}
	
	public void writeTo(@NonNull final File file, @NonNull final String... lines) {
		try {
			if(!file.exists())
				file.createNewFile();
			
			final FileWriter writer = new FileWriter(file);
			
			for(String line : lines)
				writer.write(line + "\n");
			
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
