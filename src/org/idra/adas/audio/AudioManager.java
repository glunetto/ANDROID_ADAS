package org.idra.adas.audio;

import java.util.ArrayList;

import java.util.List;
import java.util.Locale;

import android.R;
import android.content.Context;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class AudioManager implements TextToSpeech.OnInitListener, Runnable
{
	private static final String TAG = "AudioManager";
	
	private Context context;
	private MediaPlayer mp;
	
	enum AUDIO_FILE {AUDIO_JOHN_CENA, AUDIO_PILOT_DISABLED, AUDIO_PILOT_ENGAGED, AUDIO_CONNECTING, AUDIO_WARD_DRIVE};
	
	private TextToSpeech engine;
	private List<AUDIO_FILE> requests = new ArrayList<AUDIO_FILE>();
	
	private boolean tts_ready = false;
	
	public AudioManager (Context context)
	{
		this.context = context;
		
		// TTS
		engine = new TextToSpeech(this.context, this);
		//engine.setLanguage(Locale.ITALY);
		
		// Media Player Thread
		requests.add(AUDIO_FILE.AUDIO_CONNECTING);
		requests.add(AUDIO_FILE.AUDIO_PILOT_ENGAGED);
		
	}
	
	public void speech(String text)
	{
		if (tts_ready) engine.speak(text, TextToSpeech.QUEUE_ADD, null);
	}
	
	public void play(AUDIO_FILE file)
	{
		requests.add(file);
	}

	@Override
	public void onInit(int status) 
	{
		tts_ready = true;
		//this.speech ("Ciao");
	}

	@Override
	public void run()
	{
		while (true)
		{
			if (!requests.isEmpty())
			{
				AUDIO_FILE temp = requests.get(0);
				requests.remove(0);
				switch (temp)
				{
					case AUDIO_PILOT_ENGAGED:
						mp = MediaPlayer.create(this.context, org.idra.adas.R.raw.auto_pilot_engaged);
						mp.start();
						break;
					case AUDIO_PILOT_DISABLED:
						mp = MediaPlayer.create(this.context, org.idra.adas.R.raw.auto_pilot_disabled);
						mp.start();
						break;
					case AUDIO_CONNECTING:
						mp = MediaPlayer.create(this.context, org.idra.adas.R.raw.connecting);
						mp.start();
						break;
					case AUDIO_WARD_DRIVE:
						mp = MediaPlayer.create(this.context, org.idra.adas.R.raw.ward_drive_active);
						mp.start();
						break;
					case AUDIO_JOHN_CENA:
						mp = MediaPlayer.create(this.context, org.idra.adas.R.raw.john_cena);
						mp.start();
				};
				
				while (mp.isPlaying());
			}
		}
	}
}
