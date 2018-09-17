"""Google Cloud Speech API sample that demonstrates word time offsets with language change option.
Example usage:
    python transcribe_word_time_offsets.py -s <language> resources/audio.raw
    python transcribe_word_time_offsets.py -s <language> \gs://cloud-samples-tests/speech/vr.flac
"""

"""importing necessary libraries and creating credentials to get speechAPI credentials from key"""
import argparse
import io

from google.oauth2 import service_account


credentials = service_account.Credentials.from_service_account_file('api-key.json')

"""passes audio and language of audio as parameters. prints recognised words with their time offset value (timestamps)"""

def transcribe_file_with_word_time_offsets(speech_file,language):
    """Transcribe the given audio file synchronously and output the word time
    offsets."""
    print("Start")
    #import libraries necessary
    from google.cloud import speech
    from google.cloud.speech import enums
    from google.cloud.speech import types  
    
    print("checking credentials")
    #verify service key with speechclient  
    client = speech.SpeechClient(credentials=credentials)
    
    print("Checked")
	#read binary speech file - set to audio_file
    with io.open(speech_file, 'rb') as audio_file:
        content = audio_file.read()
              
    print("audio file read")    
    #takes speech.types.recognitionAudio which saves the content (bits) of the audio signal
    audio = types.RecognitionAudio(content=content)
    
    print("config start")
	#config to type of audio file (flac in this example) the language it is in, and a time stamp
    config = types.RecognitionConfig(
            encoding=enums.RecognitionConfig.AudioEncoding.FLAC,
            language_code=language,
            enable_word_time_offsets=True)
    
    print("Recognizing:")
    response = client.recognize(config, audio) #recognizes if any audio played and outputs a confidence level as well
    print("Recognized")

    for result in response.results:
        alternative = result.alternatives[0]
        print('Transcript: {}'.format(alternative.transcript))

        for word_info in alternative.words:
            word = word_info.word
            start_time = word_info.start_time
            end_time = word_info.end_time
            print('Word: {}, start_time: {}, end_time: {}'.format(
                word,
                start_time.seconds + start_time.nanos * 1e-9,
                end_time.seconds + end_time.nanos * 1e-9))


# [START def_transcribe_gcs]
def transcribe_gcs_with_word_time_offsets(gcs_uri,language):
    """Transcribe the given audio file asynchronously and output the word time
    offsets."""
    from google.cloud import speech
    from google.cloud.speech import enums
    from google.cloud.speech import types
    client = speech.SpeechClient()

    audio = types.RecognitionAudio(uri=gcs_uri)
    config = types.RecognitionConfig(
        encoding=enums.RecognitionConfig.AudioEncoding.FLAC,
        sample_rate_hertz=16000,
        language_code=language,
        enable_word_time_offsets=True)

    operation = client.long_running_recognize(config, audio)

    print('Waiting for operation to complete...')
    result = operation.result(timeout=90)

    for result in result.results:
        alternative = result.alternatives[0]
        print('Transcript: {}'.format(alternative.transcript))
        print('Confidence: {}'.format(alternative.confidence))

        for word_info in alternative.words:
            word = word_info.word
            start_time = word_info.start_time
            end_time = word_info.end_time
            print('Word: {}, start_time: {}, end_time: {}'.format(
                word,
                start_time.seconds + start_time.nanos * 1e-9,
                end_time.seconds + end_time.nanos * 1e-9))
# [END def_transcribe_gcs]



if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument(dest='path', help='File or GCS path for audio file to be recognized')
    parser.add_argument("-s","--string", type=str, required=True)
    args = parser.parse_args()
    if args.path.startswith('gs://'):
        transcribe_gcs_with_word_time_offsets(args.path,args.string)
    else:
        transcribe_file_with_word_time_offsets(args.path,args.string)