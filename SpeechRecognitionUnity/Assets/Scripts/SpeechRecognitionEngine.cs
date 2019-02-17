using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
//using UnityEngine.Windows.Speech; //for PC testing
using KKSpeech; //for android integration


public class SpeechRecognitionEngine : MonoBehaviour 
{
    //public string[] keywords = new string[] { "up", "down", "left", "right", "why"};
    //public ConfidenceLevel confidence = ConfidenceLevel.Medium;
    //public float speed = 1;

    //public Text results;
    //public Image target;

    //protected PhraseRecognizer recognizer;
    //protected string word = "right";

    //private void Start()
    //{

    //    if (keywords != null)
    //    {
    //        recognizer = new KeywordRecognizer(keywords, confidence);
    //        recognizer.OnPhraseRecognized += Recognizer_OnPhraseRecognized;
    //        recognizer.Start();
    //    }
    //}

    //private void Recognizer_OnPhraseRecognized(PhraseRecognizedEventArgs args)
    //{
    //    word = args.text;
    //    results.text = "You said: <b>" + word + "</b>";
    //}

    //private void Update()
    //{
    //    var x = target.transform.position.x;
    //    var y = target.transform.position.y;

    //    switch (word)
    //    {
    //        case "up":


    //            break;
    //        case "down":


    //            break;
    //        case "left":

    //            break;
    //        case "right":

    //            break;
    //    }

    //    target.transform.position = new Vector3(x, y, 0);
    //}

    //private void OnApplicationQuit()
    //{
    //    if (recognizer != null && recognizer.IsRunning)
    //    {
    //        recognizer.OnPhraseRecognized -= Recognizer_OnPhraseRecognized;
    //        recognizer.Stop();
    //    }
    //}


    /// <summary>
    /// STOP HERE. EVERYTHING ABOVE IS CODE THAT WORKS FOR WINDOWS PLUGIN. IF TESTING FOR ANDROID 
    /// PLEASE COMMENT ABOVE AND UNCOMMENT BELOW. ALSO MAKE SURE GLOBAL LIBRARIES HAS KKSPEECH UNCOMMENTED AND WINDOWS COMMENTED
    /// </summary>

    //COMMENT IF WINDOWS TESTING. UNCOMMENT IF ANDROID TESTING.


    public Text resultText;
    public Button startRecordingButton;
    public Image target;
    protected string word = "right";
    public string[] keywords = new string[] { "up", "down", "left", "right", "why" };

    void Start()
    {
        if (SpeechRecognizer.ExistsOnDevice())
        {
            SpeechRecognizerListener listener = GameObject.FindObjectOfType<SpeechRecognizerListener>();
            listener.onAuthorizationStatusFetched.AddListener(OnAuthorizationStatusFetched);
            listener.onAvailabilityChanged.AddListener(OnAvailabilityChange);
            listener.onErrorDuringRecording.AddListener(OnError);
            listener.onErrorOnStartRecording.AddListener(OnError);
            listener.onFinalResults.AddListener(OnFinalResult);
            //listener.onPartialResults.AddListener(OnPartialResult);
            listener.onEndOfSpeech.AddListener(OnEndOfSpeech);
            startRecordingButton.enabled = false;
            SpeechRecognizer.RequestAccess();
        }
        else
        {
            resultText.text = "Sorry, but this device doesn't support speech recognition";
            startRecordingButton.enabled = false;
        }

    }

    public void OnFinalResult(string result)
    {
        resultText.text = result;
        word = result;
    }

    //public void OnPartialResult(string result)
    //{
    //    resultText.text = result;
    //}

    public void OnAvailabilityChange(bool available)
    {
        startRecordingButton.enabled = available;
        if (!available)
        {
            resultText.text = "Speech Recognition not available";
        }
        else
        {
            resultText.text = "Say something :-)";
        }
    }

    //from example -> checks for validity
    public void OnAuthorizationStatusFetched(AuthorizationStatus status)
    {
        switch (status)
        {
            case AuthorizationStatus.Authorized:
                startRecordingButton.enabled = true;
                break;
            default:
                startRecordingButton.enabled = false;
                resultText.text = "Cannot use Speech Recognition, authorization status is " + status;
                break;
        }
    }

    //user stops recording speech
    public void OnEndOfSpeech()
    {
        startRecordingButton.GetComponentInChildren<Text>().text = "Start Recording";
    }


    //if something goes wrong with button or speech to text
    public void OnError(string error)
    {
        Debug.LogError(error);
        resultText.text = "Something went wrong... Try again! \n [" + error + "]";
        startRecordingButton.GetComponentInChildren<Text>().text = "Start Recording";
    }


    //BEGIN RECORDING
    public void OnStartRecordingPressed()
    {
        if (SpeechRecognizer.IsRecording())
        {
            SpeechRecognizer.StopIfRecording();
            startRecordingButton.GetComponentInChildren<Text>().text = "Start Recording";
        }
        else
        {
            SpeechRecognizer.StartRecording(true);
            startRecordingButton.GetComponentInChildren<Text>().text = "Stop Recording";
            resultText.text = "Say something :-)";
        }
    }

    //justins code figure out how this works
    private void Update()
    {
        var x = target.transform.position.x;
        var y = target.transform.position.y;

        switch (word)
        {
            case "up":


                break;
            case "down":


                break;
            case "left":

                break;
            case "right":

                break;
        }

        target.transform.position = new Vector3(x, y, 0);
    }
}
