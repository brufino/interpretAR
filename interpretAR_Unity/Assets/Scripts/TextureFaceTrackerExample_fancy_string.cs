using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Threading;
using OpenCVForUnity.CoreModule;
using OpenCVForUnity.ObjdetectModule;
using OpenCVForUnity.ImgprocModule;
using OpenCVForUnity.UnityUtils;
using OpenCVForUnity.UnityUtils.Helper;
using OpenCVFaceTracker;
using KKSpeech;




/// <summary>
/// WebCamTexture Face Tracker Example
/// </summary>
[RequireComponent (typeof(WebCamTextureToMatHelper))]
public class TextureFaceTrackerExample_fancy_string : MonoBehaviour
{
    ///<summary>
    //////public variables for speech recognition
    ////// </summary>

    public Text resultText;
    private Animator anim;
    public string[] Keywords_array;
    private int facecount = 0;
    private int facerec = 0;



    /// <summary>
    /// The auto reset mode. if ture, Only if face is detected in each frame, face is tracked.
    /// </summary>
    public bool isAutoResetMode;

    /// <summary>
    /// The auto reset mode toggle.
    /// </summary>
    public Toggle isAutoResetModeToggle;
        
    /// <summary>
    /// The gray mat.
    /// </summary>
    Mat grayMat;
        
    /// <summary>
    /// The texture.
    /// </summary>
    Texture2D texture;
        
    /// <summary>
    /// The cascade.
    /// </summary>
    CascadeClassifier cascade;
        
    /// <summary>
    /// The face tracker.
    /// </summary>
    FaceTracker faceTracker;
        
    /// <summary>
    /// The face tracker parameters.
    /// </summary>
    FaceTrackerParams faceTrackerParams;

    /// <summary>
    /// The web cam texture to mat helper.
    /// </summary>
    WebCamTextureToMatHelper webCamTextureToMatHelper;

    /// <summary>
    /// The tracker_model_json_filepath.
    /// </summary>
    private string tracker_model_json_filepath;
        
    /// <summary>
    /// The haarcascade_frontalface_alt_xml_filepath.
    /// </summary>
    private string haarcascade_frontalface_alt_xml_filepath;

    #if UNITY_WEBGL && !UNITY_EDITOR
    IEnumerator getFilePath_Coroutine;
    #endif


    // left and right hand Hashtable for each word 
    Hashtable ASL_vocab_right = CreateASLvocabRight();
    Hashtable ASL_vocab_left = CreateASLvocabLeft();
    Queue<string> sentence_queue = new Queue<string>(); 
    Thread t = new Thread(()=>sleep());

    public static void sleep() 
    {
        Thread.Sleep(500); 
    }


    // Use this for initialization
    void Start ()
    {
        //anim = GetComponent<Animator>();
        //GameObject.Find("twohand)").transform.localScale = new Vector3(0, 0, 0); //initialize hands to zero scale until a face is recognized
        //speechrec
        if (SpeechRecognizer.ExistsOnDevice())
        {
            resultText.text = "I exist";
        }
        else
        {
            resultText.text = "Sorry, but this device doesn't support speech recognition";
            //anim.transform.localScale = new Vector3(0, 0, 0);
            //GameObject hand = GameObject.FindGameObjectWithTag("(twohand)");
            //GameObject.Find("(twohand)").transform.localScale = new Vector3(0, 0, 0);
            //startRecordingButton.enabled = false;
        }

        //facerec
        webCamTextureToMatHelper = gameObject.GetComponent<WebCamTextureToMatHelper> ();

        isAutoResetModeToggle.isOn = isAutoResetMode;

        #if UNITY_WEBGL && !UNITY_EDITOR
        getFilePath_Coroutine = GetFilePath ();
        StartCoroutine (getFilePath_Coroutine);
        #else
        tracker_model_json_filepath = Utils.getFilePath ("tracker_model.json");
        haarcascade_frontalface_alt_xml_filepath = Utils.getFilePath ("haarcascade_frontalface_alt.xml");
        Run ();
        #endif            
    }

    #if UNITY_WEBGL && !UNITY_EDITOR
    private IEnumerator GetFilePath ()
    {
        var getFilePathAsync_0_Coroutine = Utils.getFilePathAsync ("tracker_model.json", (result) => {
            tracker_model_json_filepath = result;
        });
        yield return getFilePathAsync_0_Coroutine;

        var getFilePathAsync_1_Coroutine = Utils.getFilePathAsync ("haarcascade_frontalface_alt.xml", (result) => {
            haarcascade_frontalface_alt_xml_filepath = result;
        });
        yield return getFilePathAsync_1_Coroutine;

        getFilePath_Coroutine = null;
            
        Run();
    }
    #endif


    public void Run ()
    {
        //initialize FaceTracker
        faceTracker = new FaceTracker (tracker_model_json_filepath);
        //initialize FaceTrackerParams
        faceTrackerParams = new FaceTrackerParams ();

        cascade = new CascadeClassifier ();
        cascade.load (haarcascade_frontalface_alt_xml_filepath);
//            if (cascade.empty())
//            {
//                Debug.LogError("cascade file is not loaded.Please copy from “FaceTrackerExample/StreamingAssets/” to “Assets/StreamingAssets/” folder. ");
//            }

        #if UNITY_ANDROID && !UNITY_EDITOR
        // Avoids the front camera low light issue that occurs in only some Android devices (e.g. Google Pixel, Pixel2).
        webCamTextureToMatHelper.avoidAndroidFrontCameraLowLightIssue = true;
        #endif
        webCamTextureToMatHelper.Initialize ();
        if (SpeechRecognizer.ExistsOnDevice())
        {
            resultText.text = "I am running run";
            SpeechRecognizerListener listener = GameObject.FindObjectOfType<SpeechRecognizerListener>();
            listener.onAuthorizationStatusFetched.AddListener(OnAuthorizationStatusFetched);
            listener.onAvailabilityChanged.AddListener(OnAvailabilityChange);
            listener.onErrorDuringRecording.AddListener(OnError);
            listener.onErrorOnStartRecording.AddListener(OnError);
            listener.onFinalResults.AddListener(OnFinalResult);
            listener.onPartialResults.AddListener(OnPartialResult);
            listener.onEndOfSpeech.AddListener(OnEndOfSpeech);
            //startRecordingButton.enabled = false;
            SpeechRecognizer.RequestAccess();
            SpeechRecognizer.StartRecording(true);
            resultText.text = "Say something :-)";
        }
        else
        {
            resultText.text = "Sorry, but this device doesn't support speech recognition";
            Debug.Log("Next Command is crossfade from run function");
            //GameObject.FindGameObjectWithTag("twohand)").GetComponent<Animator>().CrossFade("V", -1);
            //startRecordingButton.enabled = false;
        }
    }

    ///<summary>
    ///speechrec functions below
    /// </summary>
    public void OnFinalResult(string result)
    {
        sentence_queue.Enqueue(result); 

        if (!t.IsAlive)
        {
            String sentence = sentence_queue.Dequeue();
            t = new Thread(()=>ActionPerformer(sentence));
            t.Start();
        }

        //resultText.text = "I am running final Results";

        if (SpeechRecognizer.IsRecording())
        {
            SpeechRecognizer.StopIfRecording();
            //resultText.text = "I stopped recording";
        }
        else
        {
            SpeechRecognizer.StartRecording(true);
            //resultText.text = "Say something :-)";
        }
    }

    public void OnPartialResult(string result)
    {
        //resultText.text = "I am running Partial Results";
//        ActionPerformer(result);
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
        //resultText.text = "Exit app and try again";
        if (SpeechRecognizer.IsRecording())
        {
            // resultText.text="end of speech turning off recording";
            SpeechRecognizer.StopIfRecording();
            //resultText.text = "I stopped recording";
        }
        else
        {
            //SpeechRecognizer.StartRecording(true);
            //resultText.text = "Say something :-)";
        }
    }

    public void sentence_queue_handler() 
    {
        // while there are setences to be displayed
        while (sentence_queue.Count != 0)
        {
            // if thread ASL display thread isn't running
            if (!t.IsAlive)
            {
                // go into sentence queue 
                String sentence = sentence_queue.Dequeue();
                t = new Thread(()=>ActionPerformer(sentence));
                t.Start();
            }
        }
        resultText.text = "no speech input, close app and try again";

        // queue is empty, no more speech, display final message
    }

    //if something goes wrong with button or speech to text
    public void OnError(string error)
    {
        Debug.LogError(error);
        if (error.Contains("6"))
        {
            StartCoroutine(Example());
            SpeechRecognizer.StartRecording(true);
            
            Thread t2 = new Thread(()=>sentence_queue_handler());
            t2.Start();
        }
        else if (error.Contains("3"))
        {
            StartCoroutine(Example());
            resultText.text = "Audio Recording error... try moving to a quieter enviornment";
            SpeechRecognizer.StartRecording(true);
        }
        else if (error.Contains("1") || error.Contains("2") || error.Contains("4") || error.Contains("5"))
        {
            StartCoroutine(Example());
            resultText.text = "Server / Network errors... Press back and try again! \n [" + error + "]";
            SpeechRecognizer.StartRecording(true);
        }
        else if (error.Contains("8") || error.Contains("7"))
        {
            StartCoroutine(Example());
            SpeechRecognizer.StartRecording(true);
        }
        else
        {
            resultText.text = "Something went wrong... Press back and try again! \n [" + error + "]";
        }
        //startRecordingButton.GetComponentInChildren<Text>().text = "Start Recording";
    }

    IEnumerator Example()
    {
        Debug.Log("waitforsecondsrealtime");
        yield return new WaitForSecondsRealtime(5);
        Debug.Log("done delay");
    }


   //public static string WordHighlighter(string command, string current_word)
   //{
       //command = String.Replace(current_word.ToLower(), String.Concat("<span style='color:red'>", current_word.ToLower(), "</span>")); 
       //return command;
   //}

    void ActionPerformer(string command)
    {
        // splits the setence spoken by user into single words as array elements
        string[] word_array = command.Split(' ');

        // checks if each word of the sentence is in the ASL vocab list 
        // if so, runs the ASL sign in the program 
        for (int i=0; i < word_array.Length; i++) 
        {
            string word = word_array[i];

            if (ASL_vocab_right.ContainsKey(word.ToLower())||ASL_vocab_left.ContainsKey(word.ToLower()))
            {
                if (resultText.text == "no speech input, close app and try again")
                {   
                    //WordHighlighter(command, word.ToLower());
                    //resultText.text = WordHighlighter(command, word.ToLower()); 

                    GameObject.FindGameObjectWithTag("right hand").GetComponent<Animator>().Play(String.Concat(word.ToLower(),"_right"));
                    if (ASL_vocab_left.ContainsKey(word))
                    {
                        GameObject.FindGameObjectWithTag("left hand").GetComponent<Animator>().Play(String.Concat(word.ToLower(),"_left"));
                    }
                    Thread.Sleep(3000);
                    resultText.text = "no speech input, close app and try again";                
                }
                else
                {
                    resultText.text = word.ToLower(); 
                    GameObject.FindGameObjectWithTag("right hand").GetComponent<Animator>().Play(String.Concat(word.ToLower(),"_right"));
                    if (ASL_vocab_left.ContainsKey(word))
                    {
                        GameObject.FindGameObjectWithTag("left hand").GetComponent<Animator>().Play(String.Concat(word.ToLower(),"_left"));
                    }
                    Thread.Sleep(3000);
                }
            }
            else
            {
                resultText.text = String.Concat("word not in vocab: ",word);
                Thread.Sleep(500);
            }
        }

    }

    static Hashtable CreateASLvocabRight() 
    {
        Hashtable ASL_vocab_right = new Hashtable(); 

        // list of all ASL words available 
        string[] vocab_list_right = {"apple", "boy", "cat", "dog", "eat", "okay", "what", "when", "where", "who", "angry", "more", "why"};


        // populates the ASLvocab_right hash with keys and values 
        for (int i=0; i < vocab_list_right.Length; i++)
        {
            string key = vocab_list_right[i]; 
            ASL_vocab_right.Add(key, null);
        } 
    return ASL_vocab_right; 
    }

    static Hashtable CreateASLvocabLeft() 
    {
        Hashtable ASL_vocab_left = new Hashtable();

        // list of all ASL words available 
        string[] vocab_list_left = {"how", "what", "when", "who", "angry", "more"};

        // populates the ASLvocab_left hash with keys and values 
        for (int i=0; i < vocab_list_left.Length; i++)
        {
            string key = vocab_list_left[i]; 
            ASL_vocab_left.Add(key, null);
        } 
    return ASL_vocab_left; 
    }

    /// <summary>
    /// Raises the webcam texture to mat helper initialized event.
    /// </summary>
    public void OnWebCamTextureToMatHelperInitialized ()
    {
        Debug.Log ("OnWebCamTextureToMatHelperInitialized");
            
        Mat webCamTextureMat = webCamTextureToMatHelper.GetMat ();
            
        texture = new Texture2D (webCamTextureMat.cols (), webCamTextureMat.rows (), TextureFormat.RGBA32, false);


        gameObject.transform.localScale = new Vector3 (webCamTextureMat.cols (), webCamTextureMat.rows (), 1);
        Debug.Log ("Screen.width " + Screen.width + " Screen.height " + Screen.height + " Screen.orientation " + Screen.orientation);
            
        float width = 0;
        float height = 0;
            
        width = gameObject.transform.localScale.x;
        height = gameObject.transform.localScale.y;
            
        float widthScale = (float)Screen.width / width;
        float heightScale = (float)Screen.height / height;
        if (widthScale < heightScale) {
            Camera.main.orthographicSize = (width * (float)Screen.height / (float)Screen.width) / 2;
        } else {
            Camera.main.orthographicSize = height / 2;
        }
            
        gameObject.GetComponent<Renderer> ().material.mainTexture = texture;

        grayMat = new Mat (webCamTextureMat.rows (), webCamTextureMat.cols (), CvType.CV_8UC1);
    }

    /// <summary>
    /// Raises the webcam texture to mat helper disposed event.
    /// </summary>
    public void OnWebCamTextureToMatHelperDisposed ()
    {
        Debug.Log ("OnWebCamTextureToMatHelperDisposed");

        faceTracker.reset ();
        grayMat.Dispose ();
    }

    /// <summary>
    /// Raises the webcam texture to mat helper error occurred event.
    /// </summary>
    /// <param name="errorCode">Error code.</param>
    public void OnWebCamTextureToMatHelperErrorOccurred (WebCamTextureToMatHelper.ErrorCode errorCode)
    {
        Debug.Log ("OnWebCamTextureToMatHelperErrorOccurred " + errorCode);
    }
            
    // Update is called once per frame
    void Update ()
    {

        if (webCamTextureToMatHelper.IsPlaying () && webCamTextureToMatHelper.DidUpdateThisFrame ()) {
                
            Mat rgbaMat = webCamTextureToMatHelper.GetMat ();

            //convert image to greyscale
            Imgproc.cvtColor (rgbaMat, grayMat, Imgproc.COLOR_RGBA2GRAY);
                                        
                                            
            if (isAutoResetMode || faceTracker.getPoints ().Count <= 0) {
//                    Debug.Log ("detectFace");

                //convert image to greyscale
                using (Mat equalizeHistMat = new Mat ()) using (MatOfRect faces = new MatOfRect ()) {
                                                
                    Imgproc.equalizeHist (grayMat, equalizeHistMat);
                                                
                    cascade.detectMultiScale (equalizeHistMat, faces, 1.1f, 2, 0
                    //                                                                                 | Objdetect.CASCADE_FIND_BIGGEST_OBJECT
                    | Objdetect.CASCADE_SCALE_IMAGE, new Size (equalizeHistMat.cols () * 0.15, equalizeHistMat.cols () * 0.15), new Size ());
                                                
                    if (faces.rows () > 0) {
//                            Debug.Log ("faces " + faces.dump ());

                        List<OpenCVForUnity.CoreModule.Rect> rectsList = faces.toList ();
                        List<Point[]> pointsList = faceTracker.getPoints ();

                        if (isAutoResetMode) {
                            //add initial face points from MatOfRect
                            if (pointsList.Count <= 0) {
                                faceTracker.addPoints (faces);
//                                    Debug.Log ("reset faces ");
                            } else {
                                                        
                                for (int i = 0; i < rectsList.Count; i++) {
                                                        
                                    OpenCVForUnity.CoreModule.Rect trackRect = new OpenCVForUnity.CoreModule.Rect (rectsList [i].x + rectsList [i].width / 3, rectsList [i].y + rectsList [i].height / 2, rectsList [i].width / 3, rectsList [i].height / 3);
                                    //It determines whether nose point has been included in trackRect.                                      
                                    if (i < pointsList.Count && !trackRect.contains (pointsList [i] [67])) {
                                        rectsList.RemoveAt (i);
                                        pointsList.RemoveAt (i);
//                                                                                      Debug.Log ("remove " + i);
                                    }
                                    //uncomment below for rectangle around face
                                    Imgproc.rectangle (rgbaMat, new Point (trackRect.x, trackRect.y), new Point (trackRect.x + trackRect.width, trackRect.y + trackRect.height), new Scalar (0, 0, 255, 255), 2);
                                }
                            }
                        } else {
                            faceTracker.addPoints (faces);
                        }
                        //draw face rect
                        for (int i = 0; i < rectsList.Count; i++) {
                            //uncomment below for rectangle around face
                            Imgproc.rectangle (rgbaMat, new Point (rectsList [i].x, rectsList [i].y), new Point (rectsList [i].x + rectsList [i].width, rectsList [i].y + rectsList [i].height), new Scalar (255, 0, 0, 255), 2);
                        }                                                    
                                                
                    } else {
                        if (isAutoResetMode) {
                            faceTracker.reset ();
                        }
                    }
                }                                            
            }

            //track face points.if face points <= 0, always return false.
            if (faceTracker.track(grayMat, faceTrackerParams))
            {
                //GameObject.FindGameObjectWithTag("left hand").transform.localScale = new Vector3(0.05f, 0.05f, 50);
                //GameObject.FindGameObjectWithTag("right hand").transform.localScale = new Vector3(0.05f, 0.05f, 50);
                //facecount = 0;
                if(facerec > 15)
                {
                    GameObject.FindGameObjectWithTag("left hand").transform.localScale = new Vector3(0.2f, 0.2f, 50);
                    GameObject.FindGameObjectWithTag("right hand").transform.localScale = new Vector3(0.2f, 0.2f, 50);
                    facecount = 0;
                }
                else
                {
                    facerec++;
                }
                //uncomment below for rectangle around face
                //faceTracker.draw(rgbaMat, new Scalar(255, 0, 0, 255), new Scalar(0, 255, 0, 255));
            }
            else
            {
                //facecount prevents flickering of hand from poor face recognition
                if (facecount > 15)
                {
                    facerec = 0;
                    GameObject.FindGameObjectWithTag("left hand").transform.localScale = new Vector3(0f, 0f, 0);
                    GameObject.FindGameObjectWithTag("right hand").transform.localScale = new Vector3(0f, 0f, 0);                    facecount++;
                }
                else
                {
                    facecount++;
                }
            }

            //Imgproc.putText (rgbaMat, "'Tap' or 'Space Key' to Reset", new Point (5, rgbaMat.rows () - 5), Imgproc.FONT_HERSHEY_SIMPLEX, 0.8, new Scalar (255, 255, 255, 255), 2, Imgproc.LINE_AA, false);                                        
                                        
//                Imgproc.putText (rgbaMat, "W:" + rgbaMat.width () + " H:" + rgbaMat.height () + " SO:" + Screen.orientation, new Point (5, rgbaMat.rows () - 10), Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar (255, 255, 255, 255), 2, Imgproc.LINE_AA, false);

            Utils.fastMatToTexture2D (rgbaMat, texture);                                 
        }

        //facetrac resets upon screen click and space bar                        
        if (Input.GetKeyUp (KeyCode.Space) || Input.touchCount > 0) {
            faceTracker.reset ();
        }

        if (Input.GetKeyDown(KeyCode.Escape))
        {
            if (SpeechRecognizer.IsRecording())
            {
                SpeechRecognizer.StopIfRecording();
                //resultText.text = "I stopped recording";
            }
            Application.Quit();
            //Application.LoadLevel ("MainActivity.class");
        }
    }

                
    /// <summary>
    /// Raises the disable event.
    /// </summary>
    void OnDisable ()
    {
        webCamTextureToMatHelper.Dispose ();

        if (cascade != null) cascade.Dispose ();

        #if UNITY_WEBGL && !UNITY_EDITOR
        if (getFilePath_Coroutine != null) {
            StopCoroutine (getFilePath_Coroutine);
            ((IDisposable)getFilePath_Coroutine).Dispose ();
        }
        #endif
    }

    /// <summary>
    /// Raises the back button event.
    /// </summary>
    //public void OnBackButton ()
    //{
    //    SceneManager.LoadScene ("FaceTrackerExample");
    //}

    /// <summary>
    /// Raises the play button event.
    /// </summary>
    public void OnPlayButton ()
    {
        webCamTextureToMatHelper.Play ();
    }

    /// <summary>
    /// Raises the pause button event.
    /// </summary>
    public void OnPauseButton ()
    {
        webCamTextureToMatHelper.Pause ();
    }

    /// <summary>
    /// Raises the stop button event.
    /// </summary>
    public void OnStopButton ()
    {
        webCamTextureToMatHelper.Stop ();
    }

    /// <summary>
    /// Raises the change camera button event.
    /// </summary>
    //public void OnChangeCameraButton ()
    //{
    //    webCamTextureToMatHelper.requestedIsFrontFacing = !webCamTextureToMatHelper.IsFrontFacing ();
    //}

    /// <summary>
    /// Raises the change auto reset mode toggle event.
    /// </summary>
    public void OnIsAutoResetModeToggle ()
    {
        if (isAutoResetModeToggle.isOn) {
            isAutoResetMode = true;
        } else {
            isAutoResetMode = false;
        }
    }
}

