using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
//using UnityEngine.Windows.Speech;
using KKSpeech;

public class OneHand : MonoBehaviour{

    ////KeywordRecognizer KeywordRecognizerObj;

    ////public string[] Keywords_array;

    ////private Animator anim;
    ////void Start()
    ////{
    ////    KeywordRecognizerObj = new KeywordRecognizer(Keywords_array);
    ////    KeywordRecognizerObj.OnPhraseRecognized += OnKeywordsRecognized;
    ////    KeywordRecognizerObj.Start();

    ////    anim = GetComponent<Animator>();
    ////}

    // Update is called once per frame

    ////    void OnKeywordsRecognized(PhraseRecognizedEventArgs args)
    ////{
    ////    Debug.Log("keyword" + args.text + "; Confidence" + args.confidence);
    ////    ActionPerformer(args.text);
    ////}


    ////void ActionPerformer(string command)
    ////{
   
    ////    if(command.Contains("why"))
    ////    {
    ////        // anim.CrossFade("why", -1, 0f);
    ////        anim.CrossFade("why",1);

    ////    }

    ////    if (command.Contains("vee"))
    ////    {
    ////        //anim.CrossFade("V", -1, 0f);
    ////        anim.CrossFade("V",1);
    ////    }

    ////    if (command.Contains("hello"))
    ////    {
    ////        //anim.CrossFade("X", -1, 0f);
    ////        anim.CrossFade("X", 1);
    ////    }
    ////}
    
    ////void Update()
    ////{
        
    ////}

    /// <summary>
    /// STOP HERE. EVERYTHING ABOVE IS CODE THAT WORKS FOR WINDOWS PLUGIN. IF TESTING FOR ANDROID 
    /// PLEASE COMMENT ABOVE AND UNCOMMENT BELOW. ALSO MAKE SURE GLOBAL LIBRARIES HAS KKSPEECH UNCOMMENTED AND WINDOWS COMMENTED
    /// </summary>

    //COMMENT BELOW IF WINDOWS TESTING. UNCOMMENT IF ANDROID TESTING.


    public Text resultText;
    //public Button startRecordingButton;
    public Image target;
    private Animator anim;
    protected string word = "right";
    public string[] Keywords_array;

    void Start()
    {
        if (SpeechRecognizer.ExistsOnDevice())
        {
            resultText.text = "I am running start";
            SpeechRecognizerListener listener = GameObject.FindObjectOfType<SpeechRecognizerListener>();
            listener.onAuthorizationStatusFetched.AddListener(OnAuthorizationStatusFetched);
            listener.onAvailabilityChanged.AddListener(OnAvailabilityChange);
            listener.onErrorDuringRecording.AddListener(OnError);
            listener.onErrorOnStartRecording.AddListener(OnError);
            listener.onFinalResults.AddListener(OnFinalResult);
            listener.onPartialResults.AddListener(OnPartialResult);
            listener.onEndOfSpeech.AddListener(OnEndOfSpeech);
            //startRecordingButton.enabled = false;
            anim = GetComponent<Animator>();
            SpeechRecognizer.RequestAccess();
            SpeechRecognizer.StartRecording(true);
            resultText.text = "Say something :-)";
        }
        else
        {
            resultText.text = "Sorry, but this device doesn't support speech recognition";
            //startRecordingButton.enabled = false;
        }

    }

    public void OnFinalResult(string result)
    {
        //resultText.text = result;
        //word = result;
        //ActionPerformer(result);
        resultText.text = "I am running final Results";
        if (SpeechRecognizer.IsRecording())
        {
            SpeechRecognizer.StopIfRecording();
            resultText.text = "I stopped recording";
        }
        else
        {
            SpeechRecognizer.StartRecording(true);
            resultText.text = "Say something :-)";
        }
    }

    public void OnPartialResult(string result)
    {
        resultText.text = "I am running Partial Results";
        resultText.text = result;
        ActionPerformer(result);
    }

    public void OnAvailabilityChange(bool available)
    {
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
                //startRecordingButton.enabled = true;
                break;
            default:
                //startRecordingButton.enabled = false;
                resultText.text = "Cannot use Speech Recognition, authorization status is " + status;
                break;
        }
    }

    ////user stops recording speech
    public void OnEndOfSpeech()
    {
        //resultText.text = "I am running onEndofSpeech";
        if (SpeechRecognizer.IsRecording())
        {
            SpeechRecognizer.StopIfRecording();
            resultText.text = "I stopped recording";
        }
        else
        {
            SpeechRecognizer.StartRecording(true);
            resultText.text = "Say something :-)";
        }
    }


    //if something goes wrong with button or speech to text
    public void OnError(string error)
    {
        Debug.LogError(error);
        if(error.Contains("6"))
        {
            SpeechRecognizer.StartRecording(true);
            resultText.text = "Say something :-)";
        }
        else if (error.Contains("3"))
        {
            resultText.text = "Audio Recording error... try moving to a quieter enviornment";
            SpeechRecognizer.StartRecording(true);
        }
        else if (error.Contains("1")|| error.Contains("2")||error.Contains("4")||error.Contains("5"))
        {
            resultText.text = "Server / Network errors... Press back and try again! \n [" + error + "]";
        }
        else
        {
            resultText.text = "Something went wrong... Press back and try again! \n [" + error + "]";
        }
        //startRecordingButton.GetComponentInChildren<Text>().text = "Start Recording";
    }


    ////BEGIN RECORDING
    //public void OnStartRecordingPressed()
    //{
    //    if (SpeechRecognizer.IsRecording())
    //    {
    //        SpeechRecognizer.StopIfRecording();
    //        startRecordingButton.GetComponentInChildren<Text>().text = "Start Recording";
    //    }
    //    else
    //    {
    //        SpeechRecognizer.StartRecording(true);
    //        startRecordingButton.GetComponentInChildren<Text>().text = "Stop Recording";
    //        resultText.text = "Say something :-)";
    //    }
    //}
    void ActionPerformer(string command)
    {

        if (command.Contains("why"))
        {
             anim.CrossFade("why", -1);
            //anim.CrossFade("why", 1);

        }

        if (command.Contains("letter v"))
        {
            anim.CrossFade("V", -1);
            //anim.CrossFade("V", 1);
        }

        if (command.Contains("hello"))
        {
            anim.CrossFade("X", -1);
            //anim.CrossFade("X", 1);
        }
        //resultText.text = "Say something :-)";
        //SpeechRecognizer.StartRecording(true);
    }

    void Update()
    {
        if (Input.GetKeyDown(KeyCode.Escape))
        {
            Application.Quit();
            //Application.LoadLevel ("MainActivity.class");
        }

    }
    ////justins code figure out how this works
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

    //target.transform.position = new Vector3(x, y, 0);
}

