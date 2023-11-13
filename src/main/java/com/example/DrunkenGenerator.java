package com.example;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DrunkenGenerator<E> extends MarkovChainGenerator<E>{
    public E generate(){
        double normalCount = 0.0;
		normalCounts = new ArrayList<>();
		for (int i = 0; i < tokenCounts.size(); i++){
			normalCount = normalCount + (float) ((float) tokenCounts.get(i) / tokenCount);
			normalCounts.add((float)normalCount);
		}
				
		float index = 0;
		boolean found = false;
		float rIndex = (float) Math.random(); //actual java code
		float dist;
		dist = normalCounts.get((int) index);
		while ((!found) && (index < alphabet.size())){
			dist = normalCounts.get((int) index);
			found = (dist >= rIndex);
			index++;
		}
		return alphabet.get((int) (index-1));
    }
}
