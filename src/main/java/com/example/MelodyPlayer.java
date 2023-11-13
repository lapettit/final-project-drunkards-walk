/*
 * c2017-c2023 Courtney Brown 
 * 
 * Class: MelodyPlayer
 * Description: Sends a melody of midi notes to an external player/midi channel
 * 
 */

 package com.example;
 
import java.util.*;

// send the MIDI elsewhere to play the notes
public class MelodyPlayer {
	MidiBusCRCP outputMidiBus; //sends midi notes to an external program (eg. Ableton, Logic, etc.)

	int note_index = 0; // where we are in the notes

	float notems; // the value of a quaver or 1/4 note in millis
	float last_time; // the last time we called draw()
	boolean play; // should we play?
	float bpm; // beats per minute of melody

	double start_time; //time at creation of the melody player in milliseconds

	double rhythm_multiplier; //determines note length and onset of the next note

	ArrayList<Integer> melody; //list of pitches in order
	ArrayList<Double> rhythm; //list of note lengths (& thus time before next note, in order)

	boolean hasRhythm; //has there been a list of rhythms assigned?
	boolean hasMelody; //has there been a list of pitches assigned?

	String outputBus; //bus to send MIDI to -- change if you have named it something else or you are using Windows
	

	//constructor -- initializes data)
	//input is the tempo - bpm (beats per minute) or how fast to play the music
	MelodyPlayer(float tempo, String bus) {
		reset();
		setBPM(tempo);
		play = true;
		hasRhythm = false;
		rhythm_multiplier = 0.5f; // default is 1/8 notes
		start_time = System.currentTimeMillis();
		outputBus = bus;
		setupMidi();
	}

	void setMelody(ArrayList<Integer> m) {
		melody = m;
		hasMelody = true;
	}

	void setRhythm(ArrayList<Double> r) {
		rhythm = r;
		hasRhythm = true;
	}

	// display all ports available to the MidiBus -- only output ports are relevant, however
	//if OS X, best to choose the IAC bus we created (for mac/OS X) so can send to an external program (eg DAW/sampler)
	//if Windows, why are you LIKE this??! -- TODO: install/document virtual port on Windows
	void listDevices()
	{
		MidiBusCRCP.listDevices(); 
	}
	
	//create the Midi port and bus to send the notes to
	void setupMidi() {
		outputMidiBus = new MidiBusCRCP(outputBus);
		//or if on windows - use a windows virtual port
	}

	void setBPM(float tempo) {
		bpm = tempo;
		notems = (float) (((1.0 / (bpm / 60.0))) * 1000); //how many ms in a 1/4 note for this tempo
	}

	//time since creation of the melody player -- mimics the processing function millis()
	int millis(){
		double millisNow = System.currentTimeMillis()-start_time;
		return (int) millisNow;
	}
	
	//send the melody out in MIDI messages, in correct timing, to the external program
	void play() {
		// just do nothing if there is no melody (pitches)
		if (!hasMelody)
		{
			System.out.println("There is no melody in the notes given.");
			return;
		}

		int vel = 100; //midi velocity -- TODO: change/assign if want to vary
		float cur_time = millis(); //what time is it now?
		play = cur_time - last_time >= notems * rhythm_multiplier; //should we send the note now? based on prev. note's duration

		if (play)
			last_time = cur_time;

		//send a note off to previous note -- TODO: control note-offs on staccato or legato, etc. values
		if (note_index <= melody.size() && note_index > 0 && play) {
			outputMidiBus.sendNoteOff(0, (int) melody.get(note_index - 1), 0);
			//System.out.println("note off:" + (note_index - 1)); //TODO: comment out when not debugging or not needed

			// don't send anything else if done
			if (note_index == melody.size())
				note_index = 0;// ++; //cycle vs. stop at end ? TODO: create as an option
		}
		
		//send out next pitch, find next rhythm / duration
		if (note_index < melody.size() && note_index > -1 && play) {

			outputMidiBus.sendNoteOn(0, (int) melody.get(note_index), vel);
			System.out.println("note on:" + note_index); //TODO: comment out when not debugging or not needed
			//get -- if its a noteOn, & what note
			if (hasRhythm)
				rhythm_multiplier = rhythm.get(note_index);

			note_index++;
		}
	}

	//reset note to 0
	void reset() {
		note_index = 0;
	}

	//have we reached the end of the melody?
	boolean atEndOfMelody()
	{
		return note_index >= melody.size();
	}
}
