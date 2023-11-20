/*
 * c2017-2023 Courtney Brown 
 * Class: Final Project - Drunkards Walk
 * Lucie Pettit
 * CRCP3315 001
 * 11/14/23
 * Description: This is a drunken's walk prototype, which progresses the notes of a melody by a skip amount chosen by the user 
 * or not at all randomly
 */

package com.example;

//importing the JMusic stuff
import jm.JMC;
import jm.music.data.*;
import jm.util.*;
import java.util.*;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;

public class App implements JMC{

	static MelodyPlayer player; // play a midi sequence
	static MidiFileToNotes midiNotes; // read a midi file
	static int noteCount = 0;

	//make cross-platform
	static FileSystem sys = FileSystems.getDefault();

	//the getSeperator() creates the appropriate back or forward slash based on the OS in which it is running 
	static String filePath = "mid"  + sys.getSeparator() +  "MaryHadALittleLamb.mid"; // path to the midi file

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// run the unit tests
		int test = Integer.parseInt(args[0]);

		// setup the melody player
		setup();

		if (test == 0){ //prob train test
			testAndTrainProbGen();
		}
		else if (test == 1){ //prob generate test
			testAndGenProbGen();
		}
		else if (test == 2){ //both prob unit tests
			both1();
		}
		else if (test == 3){ //markov train test
			testAndTrainMarkovGen();
		}
		else if (test == 4){ //markov generate test
			testAndGenMarkovGen();
		}
		else if (test == 5){ //both markov unit tests
			both2();
		}

		playMelody();
		saveToFile();

	}

	public static void saveToFile(){
		ArrayList<Integer> pitches = midiNotes.getPitchArray(); 
		ArrayList<Double> rhythms = midiNotes.getRhythmArray(); 
		Score s = new Score("JMDemo1 - Scale"); // new scale
		Part p = new Part("Piano", PIANO, 0); // instrument
		Phrase phr = new Phrase("Chromatic Scale", 0.0); // new phrase for scale
		Scanner input = new Scanner(System.in); // user input
		int i = 0;

		System.out.println("Enter chance of randomness (float): "); // chance of skip happening
		float chance = input.nextFloat();
		System.out.println("Enter the skip amount (int): "); // how much skip?
		int skip = input.nextInt();
		
		while ((i != pitches.size()) || (i != rhythms.size())){
			Note n;
			if (chance > .5){
				if (((i + skip) >= pitches.size()) || ((i + skip) >= rhythms.size())){ // to prevent skip from going beyond scale
					n = new Note(pitches.get(i), rhythms.get(i));
				}
				else {
					n = new Note(pitches.get(i+skip), rhythms.get(i+skip));
				}
			}
			else if (chance < .5){
				if ((i - skip) <= 0){ // to prevent skip from going below scale
					n = new Note(pitches.get(i), rhythms.get(i));
				}
				else{
					n = new Note(pitches.get(i-skip), rhythms.get(i-skip));
				}
			}
			else{
				n = new Note(pitches.get(i), rhythms.get(i));
			}
			phr.addNote(n);
			i++;
		}
		input.close(); // close input to prevent errors
		p.add(phr);
		s.add(p);
		Write.midi(s, "newMidi.mid");
	}

	// doing all the setup stuff
	public static void setup() {

		// playMidiFile(filePath); //use to debug 

		midiSetup(filePath);
	}

	public static void testAndTrainProbGen(){
		ProbabilityGenerator<Integer> pitchgen = new ProbabilityGenerator<Integer>(); // pitches
		ProbabilityGenerator<Double> rhythmgen = new ProbabilityGenerator<Double>(); // rhythms

		pitchgen.train(midiNotes.getPitchArray());
		rhythmgen.train(midiNotes.getRhythmArray());

		pitchgen.printProbabilityDistribution(false);
		rhythmgen.printProbabilityDistribution(false);
	}
	public static void testAndGenProbGen(){
		ProbabilityGenerator<Integer> melpitch = new ProbabilityGenerator<Integer>(); // pitches
		ProbabilityGenerator<Integer> probpitch = new ProbabilityGenerator<>();
		ProbabilityGenerator<Double> melrhythm = new ProbabilityGenerator<>(); // rhythms
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
		MarkovChainGenerator<Integer> pitchgen = new MarkovChainGenerator<Integer>(); // pitches
		MarkovChainGenerator<Double> rhythmgen = new MarkovChainGenerator<Double>(); // rhythms

		pitchgen.train(midiNotes.getPitchArray());
		rhythmgen.train(midiNotes.getRhythmArray());

		pitchgen.printProbabilityDistribution(false);
		rhythmgen.printProbabilityDistribution(false);
	}

	public static void testAndGenMarkovGen(){
		MarkovChainGenerator<Integer> melpitch = new MarkovChainGenerator<Integer>(); // melody pitches/rhythms
		MarkovChainGenerator<Double> melrhythm = new MarkovChainGenerator<>();
		MarkovChainGenerator<Integer> ttpitch = new MarkovChainGenerator<>(); // transition table pitches/rhythms
		MarkovChainGenerator<Double> ttrhythm = new MarkovChainGenerator<Double>();

		melpitch.train(midiNotes.getPitchArray());
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
