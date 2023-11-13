
/*
 * c2017 Courtney Brown 
 * 
 * Class: MidiFileToNotes
 * Description: Uses JMusic to get notes & info from a midi file
 * 
 */

 package com.example;


import java.util.*;
import jm.music.data.*;
import jm.util.*;
import jm.music.data.Score;

public class MidiFileToNotes {
	String filename;
	ArrayList<Integer> pitches; //the midi values of the notes
	ArrayList<Double> rhythms; //the rhythmic values (in jm notation)

	int whichLine; //which instrument, basically -- but "line of music" or midi channel of music to be the most precise

	ArrayList<jm.music.data.Note> melody; //list of notes in jm form --> melody + rhythm

	//input for the constructor is the midi file that we are reading
	//f - is the filename of the midi file
	MidiFileToNotes(String f) {
		filename = f;
		processPitchesAsTokens();
		whichLine = 0;
	}

	//set which line or instrument of the midi file
	//line - which line  - 0 is the first line
	void setWhichLine(int line) {
		whichLine = line;
	}

	//extract the midi data from jm.music format into separate arrays of pitch and melody
	void processPitchesAsTokens() {
		pitches = new ArrayList<Integer>();
		melody = new ArrayList<jm.music.data.Note>();
		rhythms = new ArrayList<Double>();

		String scoreName = "score_" + filename;
		Score theScore = new Score(scoreName);

		// read the midi file into a score
		Read.midi(theScore, filename);

		// extract the melody and all its parts
		Part part = theScore.getPart(whichLine);
		Phrase[] phrases = part.getPhraseArray();

		// extract all the pitches and notes from the melody
		for (int i = 0; i < phrases.length; i++) {
			jm.music.data.Note[] notes = phrases[i].getNoteArray();

			for (int j = 0; j < notes.length; j++) {
				pitches.add(notes[j].getPitch());
				rhythms.add(notes[j].getDuration());
				melody.add(notes[j]);
			}

		}
	}

	public Integer[] getPitches() {
		return pitches.toArray(new Integer[pitches.size()]);
	}

	public ArrayList<Integer> getPitchArray() {
		return pitches;
	}

	public ArrayList<Double> getRhythmArray() {
		return rhythms;
	}

	public ArrayList<jm.music.data.Note> getMelody() {
		return melody;
	}

	public Double[] getRhythms() {
		return rhythms.toArray(new Double[rhythms.size()]);
	}

}