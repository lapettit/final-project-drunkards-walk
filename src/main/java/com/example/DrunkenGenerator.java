package com.example;

import java.util.ArrayList;

public class DrunkenGenerator<E> extends MarkovChainGenerator<E>{
    public E generate(){
        double normalCount = 0.0;
		normalCounts = new ArrayList<>();
		for (int i = 0; i < tokenCounts.size(); i++){
			normalCount = normalCount + (float) ((float) tokenCounts.get(i) / tokenCount);
			normalCounts.add((float)normalCount);
		}
		
        float chance = (float) ((float) -1 + Math.random() + 1);
		float index = 0;
		boolean found = false;
		float rIndex = (float) Math.random(); //actual java code

		float dist;
        if (chance == -1){
            dist = normalCounts.get((int) index - 1);
        }
        else if (chance == 1){
            normalCounts.get((int) index + 1);
        }
        else {
            dist = normalCounts.get((int) index);
        }
		while ((!found) && (index < alphabet.size())){
			dist = normalCounts.get((int) index);
			found = (dist >= rIndex);
			index++;
		}
		return alphabet.get((int) (index-1));
    }
}
