/*
 * c2017-2023 Courtney Brown 
 * Class: Project 2 Template
 * Lucie Pettit
 * CRCP3315 001
 * 9/26/23
 * Description: This is a template for the project 2 code, which is an implementation of a Markov chain of order 1
 */

package com.example;

//importing the JMusic stuff
import jm.JMC;
import jm.music.data.*;
import jm.util.*;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;

//make sure this class name matches your file name, if not fix.
public class App implements JMC{

	static MelodyPlayer player; // play a midi sequence
	static MidiFileToNotes midiNotes; // read a midi file
	static int noteCount = 0;

	//make cross-platform
	static FileSystem sys = FileSystems.getDefault();

	//the getSeperator() creates the appropriate back or forward slash based on the OS in which it is running -- OS X & Windows use same code :) 
	static String filePath = "mid"  + sys.getSeparator() +  "MaryHadALittleLamb.mid"; // path to the midi file -- you can change this to your file
															// location/name

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// run the unit tests
		int test = Integer.parseInt(args[0]);

		// setup the melody player
		// uncomment below when you are ready to test or present sound output
		// make sure that it is commented out for your final submit to github (eg. when
		// pushing)
		setup();

		if (test == 0){
			testAndTrainProbGen();
		}
		else if (test == 1){
			testAndGenProbGen();
		}
		else if (test == 2){
			both1();
		}
		else if (test == 3){
			testAndTrainMarkovGen();
		}
		else if (test == 4){
			testAndGenMarkovGen();
		}
		else if (test == 5){
			both2();
		}

		playMelody();

	}

	// doing all the setup stuff
	public static void setup() {

		// playMidiFile(filePath); //use to debug -- this will play the ENTIRE file --
		// use ONLY to check if you have a valid path & file & it plays
		// it will NOT let you know whether you have opened file to get the data in the
		// form you need for the assignment

		midiSetup(filePath);
	}

	public static void testAndTrainProbGen(){
		ProbabilityGenerator<Integer> pitchgen = new ProbabilityGenerator<Integer>();
		ProbabilityGenerator<Double> rhythmgen = new ProbabilityGenerator<Double>();

		pitchgen.train(midiNotes.getPitchArray());
		rhythmgen.train(midiNotes.getRhythmArray());

		pitchgen.printProbabilityDistribution(false);
		rhythmgen.printProbabilityDistribution(false);
	}
	public static void testAndGenProbGen(){
		ProbabilityGenerator<Integer> melpitch = new ProbabilityGenerator<Integer>();
		ProbabilityGenerator<Integer> probpitch = new ProbabilityGenerator<>();
		ProbabilityGenerator<Double> melrhythm = new ProbabilityGenerator<>();
		ProbabilityGenerator<Double> probrhythm = new ProbabilityGenerator<Double>();

		melpitch.train(midiNotes.getPitchArray());
		for (int i = 0; i < 10000; i++){
			ArrayList<Integer> song = melpitch.generate(20);
			probpitch.train(song);
		}
		probpitch.printProbabilityDistribution(true);

		melrhythm.train(midiNotes.getRhythmArray());
		for (int i = 0; i < 10000; i++){
			ArrayList<Double> song = melrhythm.generate(20);
			probrhythm.train(song);
		}
		probrhythm.printProbabilityDistribution(true);

	}
	public static void both1(){
		testAndTrainProbGen();
		testAndGenProbGen();
	}

	public static void testAndTrainMarkovGen(){
		MarkovChainGenerator<Integer> pitchgen = new MarkovChainGenerator<Integer>();
		MarkovChainGenerator<Double> rhythmgen = new MarkovChainGenerator<Double>();

		pitchgen.train(midiNotes.getPitchArray());
		rhythmgen.train(midiNotes.getRhythmArray());

		pitchgen.printProbabilityDistribution(false);
		rhythmgen.printProbabilityDistribution(false);
	}

	public static void testAndGenMarkovGen(){
		MarkovChainGenerator<Integer> melpitch = new MarkovChainGenerator<Integer>();
		MarkovChainGenerator<Double> melrhythm = new MarkovChainGenerator<>();
		MarkovChainGenerator<Integer> ttpitch = new MarkovChainGenerator<>();
		MarkovChainGenerator<Double> ttrhythm = new MarkovChainGenerator<Double>();

		melpitch.train(midiNotes.getPitchArray()); //melgen output correct
        for (int i = 0; i < 100000; i++){
                 
			ArrayList<Integer> song = melpitch.generate(20);
			ttpitch.train(song);

        }
        ttpitch.printProbabilityDistribution(true);

		melrhythm.train(midiNotes.getRhythmArray());
		for (int i = 0; i < 100000; i++){
                 
			ArrayList<Double> song = melrhythm.generate(20);
			ttrhythm.train(song);

        }
        ttrhythm.printProbabilityDistribution(true);
	}

	public static void both2(){
		testAndTrainMarkovGen();
		testAndGenMarkovGen();
	}

	public static void saveToFile(){
		Score s = new Score("JMDemo1 - Scale");	    
		Part p = new Part("Flute", FLUTE, 0);
		Phrase phr = new Phrase("Chromatic Scale", 0.0);
		File newMidi = new File("newMidi.mid");
		for(int i = 0; i < 12; i++){
			float chance = (float) ((float) -1 + Math.random() + 1); 
			Note n = new Note(C4 + chance, CROTCHET);
			phr.addNote(n);
		}


	}
	// plays the midi file using the player -- so sends the midi to an external
	// synth such as Kontakt or a DAW like Ableton or Logic
	static public void playMelody() {

		assert (player != null); // this will throw an error if player is null -- eg. if you haven't called
									// setup() first

		while (!player.atEndOfMelody()) {
			player.play(); // play each note in the sequence -- the player will determine whether is time
							// for a note onset
		}

	}

	// opens the midi file, extracts a voice, then initializes a melody player with
	// that midi voice (e.g. the melody)
	// filePath -- the name of the midi file to play
	static void midiSetup(String filePath) {

		// Change the bus to the relevant port -- if you have named it something
		// different OR you are using Windows
		player = new MelodyPlayer(100, "Microsoft GS Wavetable Synth"); // sets up the player with your bus.

		midiNotes = new MidiFileToNotes(filePath); // creates a new MidiFileToNotes -- reminder -- ALL objects in Java
													// must
													// be created with "new". Note how every object is a pointer or
													// reference. Every. single. one.

		// // which line to read in --> this object only reads one line (or ie, voice or
		// ie, one instrument)'s worth of data from the file
		midiNotes.setWhichLine(0); // this assumes the melody is midi channel 0 -- this is usually but not ALWAYS
									// the case, so you can try other channels as well, if 0 is not working out for
									// you.

		noteCount = midiNotes.getPitchArray().size(); // get the number of notes in the midi file

		assert (noteCount > 0); // make sure it got some notes (throw an error to alert you, the coder, if not)

		// sets the player to the melody to play the voice grabbed from the midi file
		// above
		player.setMelody(midiNotes.getPitchArray());
		player.setRhythm(midiNotes.getRhythmArray());
	}

	static void resetMelody() {
		player.reset();

	}

	// this function is not currently called. you may call this from setup() if you
	// want to test
	// this just plays the midi file -- all of it via your software synth. You will
	// not use this function in upcoming projects
	// but it could be a good debug tool.
	// filename -- the name of the midi file to play
	static void playMidiFileDebugTest(String filename) {
		Score theScore = new Score("Temporary score");
		Read.midi(theScore, filename);
		Play.midi(theScore);
	}

}
