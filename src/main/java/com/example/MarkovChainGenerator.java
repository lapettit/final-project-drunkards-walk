package com.example;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;




public class MarkovChainGenerator<E> extends ProbabilityGenerator <E>{

	ArrayList<ArrayList<Float>> transitionTable = new ArrayList<>();
	ArrayList<Float> normalCounts = new ArrayList<Float>();

	ProbabilityGenerator<E> newgen = new ProbabilityGenerator<E>();

	void train (ArrayList<E> data){
		int  lastIndex = -1;
		for (int i = 0; i < data.size(); i++)
		{
			//tokenIndex = the index of the token in the alphabet
			int tokenIndex = alphabet.indexOf(data.get(i));
			if (tokenIndex == -1)
			{
				tokenIndex = alphabet.size();
                //   2. add a new row to the transition table (expand vertically)
				ArrayList<Float> newRow = new ArrayList<Float>();
				for (int j = 0; j < alphabet.size(); j++){
					newRow.add((float) 0.0);
				}
				transitionTable.add(newRow);
                //   3. add a new column (expand horizontally)
				for (int k = 0; k < transitionTable.size(); k++){
					transitionTable.get(k).add((float)0);
				}
				// 4. add the token
				alphabet.add(data.get(i));
			}
            //ok, now add the counts to the transition table
			if(lastIndex > -1) //that is, we have a previous token so its not the 1st time thru
			{
                //   1.	Use lastIndex to get the correct row (array) in your transition table.
				float last = transitionTable.get(lastIndex).get(tokenIndex);
				last = last + 1;
				transitionTable.get(lastIndex).set(tokenIndex, last);
                //   2.	Use the tokenIndex to index the correct column (value of the row you accessed)]
                //   3.	Add 1 to that value.
			}
			lastIndex = tokenIndex; //setting current to previous for next round
		}
		newgen.train(data);

	}

	private float adders(ArrayList<Float> a){
		float sum = 0;
		for (int i = 0; i < a.size(); i++){
			sum = sum + (float)a.get(i);
		}
		return sum;
	}

	public ArrayList<ArrayList<Float>> createProbDist(){
		// Then, in the new createProbDistributionFromCounts function, iterate through each row of the transitionTable to  
		ArrayList<ArrayList<Float>> newTTable = new ArrayList<ArrayList<Float>>();
		for (int i = 0; i < transitionTable.size(); i++){
			float s = adders(transitionTable.get(i));
			float val;
			ArrayList<Float> newRow = new ArrayList<Float>();
			for (int j = 0; j < transitionTable.get(i).size(); j++){
				if (s == 0){
					val = 0;
				}
				else{
					val = transitionTable.get(i).get(j) / s;					
				}
				newRow.add(val);
			}
			newTTable.add(newRow);
		}
		return newTTable;
	}
	
	public E generate(E initToken){
		ArrayList<Float> firstRow = transitionTable.get(alphabet.indexOf(initToken));
		tokenCounts = firstRow;
		tokenCount = adders(tokenCounts);
		if (tokenCount == 0){
			initToken = newgen.generate();
		}
		else{
			initToken = super.generate();
		}
		return initToken;
	}                          

    public ArrayList<E> generate(E initToken, int numberOfTokensToGenerate){
		ArrayList<E> result = new ArrayList<E>();

		for (int k = 0; k < numberOfTokensToGenerate; k++){
			E token = generate(initToken);
			result.add(token);
			initToken = token;	
		}
		
		return result;
	} //this calls the above.

    public ArrayList<E> generate(int numberOfTokensToGenerate){
		E init = newgen.generate(1).get(0);
		return generate(init, numberOfTokensToGenerate);
	} //this calls the above with a random initToken

  	//nested convenience class to return two arrays from sortTransitionTable() method
	//students do not need to use this class
	protected class SortTTOutput
	{
		public ArrayList<E> symbolsListSorted;
		ArrayList<ArrayList<Float>> ttSorted;
	}

	//sort the symbols list and the counts list, so that we can easily print the probability distribution for testing
	//symbols -- your alphabet or list of symbols (input)
	//tt -- the unsorted transition table (input)
	//symbolsListSorted -- your SORTED alphabet or list of symbols (output)
	//ttSorted -- the transition table that changes reflecting the symbols sorting to remain accurate  (output)
	public SortTTOutput sortTT(ArrayList<E> symbols, ArrayList<ArrayList<Float>> tt)	{

		SortTTOutput sortArraysOutput = new SortTTOutput(); 
		
		sortArraysOutput.symbolsListSorted = new ArrayList<E>(symbols);
		sortArraysOutput.ttSorted = new ArrayList<ArrayList<Float>>();
	
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
			sortArraysOutput.ttSorted.add(new ArrayList<Float>());
			for( int j=0; j<tt.get(index).size(); j++)
			{
				int index2 = symbols.indexOf(sortArraysOutput.symbolsListSorted.get(j));
				sortArraysOutput.ttSorted.get(i).add(tt.get(index).get(index2));
			}
		}

		return sortArraysOutput;

	}
	
	//this prints the transition table
	//symbols - the alphabet or list of symbols found in the data
	//tt -- the transition table of probabilities (not COUNTS!) for each symbol coming after another
	public void printProbabilityDistribution(boolean round, ArrayList<E> symbols, ArrayList<ArrayList<Float>> tt)
	{
		//sort the transition table
		SortTTOutput sorted = sortTT(symbols, tt);
		symbols = sorted.symbolsListSorted;
		tt = sorted.ttSorted;

		System.out.println("-----Transition Table -----");
		
		System.out.println(symbols);
		
		for (int i=0; i<tt.size(); i++)
		{
			System.out.print("["+symbols.get(i) + "] ");
			for(int j=0; j<tt.get(i).size(); j++)
			{
				if(round)
				{
					DecimalFormat df = new DecimalFormat("#.##");
					System.out.print(df.format((double)tt.get(i).get(j)) + " ");
				}
				else
				{
					System.out.print((double)tt.get(i).get(j) + " ");
				}
			
			}
			System.out.println();


		}
		System.out.println();
		
		System.out.println("------------");
    }

	public void printProbabilityDistribution(boolean round){
		printProbabilityDistribution(round, alphabet, createProbDist());
	}
}
