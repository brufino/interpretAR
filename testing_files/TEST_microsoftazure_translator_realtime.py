import os
import io
import struct
import _thread
import time
import uuid
import wave
import sys
import pyaudio
import json

import websocket

#from auth import AzureAuthClient

def get_wave_header(frame_rate):
    """
    Generate WAV header that precedes actual audio data sent to the speech translation service.
    :param frame_rate: Sampling frequency (8000 for 8kHz or 16000 for 16kHz).
    :return: binary string
    """

    if frame_rate not in [8000, 16000]:
        raise ValueError("Sampling frequency, frame_rate, should be 8000 or 16000.")

    nchannels = 1
    bytes_per_sample = 2

    output = io.BytesIO()
    output.write(str.encode('RIFF'))
    output.write(struct.pack('<L', 0))
    output.write(str.encode('WAVE'))
    output.write(str.encode('fmt '))
    output.write(struct.pack('<L', 18))
    output.write(struct.pack('<H', 0x0001))
    output.write(struct.pack('<H', nchannels))
    output.write(struct.pack('<L', frame_rate))
    output.write(struct.pack('<L', frame_rate * nchannels * bytes_per_sample))
    output.write(struct.pack('<H', nchannels * bytes_per_sample))
    output.write(struct.pack('<H', bytes_per_sample * 8))
    output.write(struct.pack('<H', 0))
    output.write(str.encode('data'))
    output.write(struct.pack('<L', 0))

    data = output.getvalue()

    output.close()

    return data
##class MicrophoneStream(object):
##    """Opens a recording stream as a generator yielding the audio chunks."""
##    def __init__(self, rate, chunk):
##        self._rate = rate
##        self._chunk = chunk
##
##        # Create a thread-safe buffer of audio data
##        self._buff = queue.Queue()
##        self.closed = True
##
##    def __enter__(self):
##        self._audio_interface = pyaudio.PyAudio()
##        self._audio_stream = self._audio_interface.open(
##            format=pyaudio.paInt16,
##            # The API currently only supports 1-channel (mono) audio
##            # https://goo.gl/z757pE
##            channels=1, rate=self._rate,
##            input=True, frames_per_buffer=self._chunk,
##            # Run the audio stream asynchronously to fill the buffer object.
##            # This is necessary so that the input device's buffer doesn't
##            # overflow while the calling thread makes network requests, etc.
##            stream_callback=self._fill_buffer,
##        )
##
##        self.closed = False
##
##        return self
##
##    def __exit__(self, type, value, traceback):
##        self._audio_stream.stop_stream()
##        self._audio_stream.close()
##        self.closed = True
##        # Signal the generator to terminate so that the client's
##        # streaming_recognize method will not block the process termination.
##        self._buff.put(None)
##        self._audio_interface.terminate()
##
##    def _fill_buffer(self, in_data, frame_count, time_info, status_flags):
##        """Continuously collect data from the audio stream, into the buffer."""
##        self._buff.put(in_data)
##        return None, pyaudio.paContinue
##
##    def generator(self):
##        while not self.closed:
##            # Use a blocking get() to ensure there's at least one chunk of
##            # data, and stop iteration if the chunk is None, indicating the
##            # end of the audio stream.
##            chunk = self._buff.get()
##            if chunk is None:
##                return
##            data = [chunk]
##
##            # Now consume whatever other data's still buffered.
##            while True:
##                try:
##                    chunk = self._buff.get(block=False)
##                    if chunk is None:
##                        return
##                    data.append(chunk)
##                except queue.Empty:
##                    break
##
##            yield b''.join(data)
##
##def listen_print_loop(responses):
##    """Iterates through server responses and prints them.
##
##    The responses passed is a generator that will block until a response
##    is provided by the server.
##
##    Each response may contain multiple results, and each result may contain
##    multiple alternatives; for details, see https://goo.gl/tjCPAU.  Here we
##    print only the transcription for the top alternative of the top result.
##
##    In this case, responses are provided for interim results as well. If the
##    response is an interim one, print a line feed at the end of it, to allow
##    the next result to overwrite it, until the response is a final one. For the
##    final one, print a newline to preserve the finalized transcription.
##    """
##    num_chars_printed = 0
##    for response in responses:
##        if not response.results:
##            continue
##
##        # The `results` list is consecutive. For streaming, we only care about
##        # the first result being considered, since once it's `is_final`, it
##        # moves on to considering the next utterance.
##        result = response.results[0]
##        if not result.alternatives:
##            continue
##
##        # Display the transcription of the top alternative.
##        transcript = result.alternatives[0].transcript
##
##        # Display interim results, but with a carriage return at the end of the
##        # line, so subsequent lines will overwrite them.
##        #
##        # If the previous result was longer than this one, we need to print
##        # some extra spaces to overwrite the previous result
##        overwrite_chars = ' ' * (num_chars_printed - len(transcript))
##
##        if not result.is_final:
##            sys.stdout.write(transcript + overwrite_chars + '\r')
##            sys.stdout.flush()
##
##            num_chars_printed = len(transcript)
##
##        else:
##            print(transcript + overwrite_chars)
##
##            # Exit recognition if any of the transcribed phrases could be
##            # one of our keywords.
##            if re.search(r'\b(exit|quit)\b', transcript, re.I):
##                print('Exiting..')
##                break
##
##            num_chars_printed = 0
	
if __name__ == "__main__":

    client_secret = '9f9a177c87bd4f50aab6e5eb4a60f886'
    #auth_client = AzureAuthClient(client_secret)
    speaker_num = 0
    text = ''

    # Translate from this language. The language must match the source audio.
    # Supported languages are given by the 'speech' scope of the supported languages API.
    translate_from = 'en-US'
    # Translate to this language.
    # Supported languages are given by the 'text' scope of the supported languages API.
    translate_to = 'fr'
    # Features requested by the client.
    features = "Partial"


    # Setup functions for the Websocket connection

    def on_open(ws):
        """
        Callback executed once the Websocket connection is opened.
        This function handles streaming of audio to the server.
        :param ws: Websocket client.
        """

        print('Connected. Server generated request ID = ', ws.sock.headers['x-requestid'])

        def run(*args):
            """Background task which streams audio."""

            # Send input data from the mic to be translated
            msg_width = 2
            framerate = 16000 #Hz
            channels = 1
            
            data=get_wave_header(framerate)
            ws.send(data,websocket.ABNF.OPCODE_BINARY)
            p = pyaudio.PyAudio()

	    # Stream audio one chunk at a time
            def callback(in_data, frame_count, time_info, status):
                ws.send(in_data, websocket.ABNF.OPCODE_BINARY)
                return (in_data, pyaudio.paContinue)

            stream = p.open(format=p.get_format_from_width(msg_width),
                            channels=channels,
                            rate=framerate,
                            input=True,
                            output=False,
                            stream_callback=callback)

            stream.start_stream()

            while stream.is_active():
                time.sleep(0.1)

            stream.stop_stream()
            stream.close()

            p.terminate()
        _thread.start_new_thread(run, ())
    #return on_open

    def on_close(ws):
        """
        Callback executed once the Websocket connection is closed.
        :param ws: Websocket client.
        """
        print('Connection closed...')

    def on_error(ws, error):
        """
        Callback executed when an issue occurs during the connection.
        :param ws: Websocket client.
        """
        print(error)

    def on_data(ws, message, message_type, fin):
        """
        Callback executed when Websocket messages are received from the server.
        :param ws: Websocket client.
        :param message: Message data as utf-8 string.
        :param message_type: Message type: ABNF.OPCODE_TEXT or ABNF.OPCODE_BINARY.
        :param fin: Websocket FIN bit. If 0, the data continues.
        """
        global text, speaker_num
        data = json.loads(message)
        if data['type'] == 'final':
            speaker_num = sp_num
            text = data['translation']
        else:
            text = text + ' .'
        print('\n',message, '\n')
        return on_data

    client_trace_id = str(uuid.uuid4())
    request_url = "wss://dev.microsofttranslator.com/speech/translate?from={0}&to={1}&features={2}&api-version=1.0".format(translate_from, translate_to, features)	
	

    ws_client = websocket.WebSocketApp(
        request_url,
        header=[
            'Ocp-Apim-Subscription-Key: ' + client_secret,
            'X-ClientTraceId: ' + client_trace_id
        ],
        on_open=on_open,
        on_data=on_data,
        on_error=on_error,
        on_close=on_close
    )
    ws_client.run_forever()
