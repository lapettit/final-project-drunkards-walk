/*
 * c2017-2023 Lucie Pettit (NOTE: you'll have to change the name and give me a bit of credit!)
 * 
 * Class: ProbabliityGenerator
 * 
 */


package com.example;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ProbabilityGenerator <E>
{
	ArrayList<E> alphabet = new ArrayList<E>();
	ArrayList<Float> tokenCounts = new ArrayList<Float>();
	float
	 tokenCount = 0;
	ArrayList<Float> normalCounts = new ArrayList<Float>();


	void train(ArrayList<E> data){
		for (int i = 0; i < data.size(); i++)
		{
			int index = alphabet.indexOf(data.get(i));
			//find index of data[i] in alphabet
			
			if (index == -1)
			{
				index = alphabet.size();
				//add data[i] to alphabet container/array
				alphabet.add(data.get(i));
				//add a 0 to your alphabet counts array  
				tokenCounts.add((float) 0.0);
			}      

			float last = tokenCounts.get(index)+ 1; //note – syntax will look different – eg. if using ArrayList
			tokenCounts.set(index, last);
		}
		tokenCount += data.size();
	}


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

	public ArrayList<E> generate(int sizeOfGeneration){
		ArrayList <E> tokenCounts2 = new ArrayList <E>();
		
		for (int i = 0; i < sizeOfGeneration; i++){
			tokenCounts2.add(generate());
		}

		return tokenCounts2;
	}
	//nested convenience class to return two arrays from sortArrays() method
	//students do not need to use this class
	protected class SortArraysOutput
	{
		public ArrayList<E> symbolsListSorted;
		public ArrayList<Float> symbolsCountSorted;
	}

	//sort the symbols list and the counts list, so that we can easily print the probability distribution for testing
	//symbols -- your alphabet or list of symbols (input)
	//counts -- the number of times each symbol occurs (input)
	//symbolsListSorted -- your SORTED alphabet or list of symbols (output)
	//symbolsCountSorted -- list of the number of times each symbol occurs inorder of symbolsListSorted  (output)
	public SortArraysOutput sortArrays(ArrayList<E> symbols, ArrayList<Float> counts)	{

		SortArraysOutput sortArraysOutput = new SortArraysOutput(); 
		
		sortArraysOutput.symbolsListSorted = new ArrayList<E>(symbols);
		sortArraysOutput.symbolsCountSorted = new ArrayList<Float>();
	
		//sort the symbols list
		Collections.sort(sortArraysOutput.symbolsListSorted, new Comparator<E>() {
			@Override
			public int compare(E o1, E o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		//use the current sorted list to reference the counts and get the sorted counts
		for(int i=0; i<sortArraysOutput.symbolsListSorted.size(); i++)
		{
			int index = symbols.indexOf(sortArraysOutput.symbolsListSorted.get(i));
			sortArraysOutput.symbolsCountSorted.add(counts.get(index));
		}

		return sortArraysOutput;

	}
	
	//Students should USE this method in your unit tests to print the probability distribution
	//HINT: you can overload this function so that it uses your class variables instead of taking in parameters
	//boolean is FALSE to test train() method & TRUE to test generate() method
	//symbols -- your alphabet or list of symbols (input)
	//counts -- the number of times each symbol occurs (input)
	//sumSymbols -- the count of how many tokens we have encountered (input)
	public void printProbabilityDistribution(boolean round, ArrayList<E> symbols, ArrayList<Float> counts, double sumSymbols)
	{
		//sort the arrays so that elements appear in the same order every time and it is easy to test.
		SortArraysOutput sortResult = sortArrays(symbols, counts);
		ArrayList<E> symbolsListSorted = sortResult.symbolsListSorted;
		ArrayList<Float> symbolsCountSorted = sortResult.symbolsCountSorted;

		System.out.println("-----Probability Distribution-----");
		
		for (int i = 0; i < symbols.size(); i++)
		{
			if (round){
				DecimalFormat df = new DecimalFormat("#.##");
				System.out.println("Data: " + symbolsListSorted.get(i) + " | Probability: " + df.format((double)symbolsCountSorted.get(i) / sumSymbols));
			}
			else
			{
				System.out.println("Data: " + symbolsListSorted.get(i) + " | Probability: " + (double)symbolsCountSorted.get(i) / sumSymbols);
			}
		}
		
		System.out.println("------------");
	}

	public void printProbabilityDistribution(boolean round){
		printProbabilityDistribution(round, alphabet, tokenCounts, tokenCount);
	}

	
}
