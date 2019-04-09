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


public class VrAslTranslation : MonoBehaviour
{
    ///<summary>
    //////public variables for speech recognition
    ////// </summary>

    public Text resultText;
    private int wordInputTimer = 0;

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

        Run ();
    }

    public void Run ()
    {
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
        wordInputTimer=0;
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
                    resultText.text = word.ToLower(); 
                    GameObject.FindGameObjectWithTag("right hand").GetComponent<Animator>().Play(String.Concat(word.ToLower(),"_right"));
                    if (ASL_vocab_left.ContainsKey(word))
                    {
                        GameObject.FindGameObjectWithTag("left hand").GetComponent<Animator>().Play(String.Concat(word.ToLower(),"_left"));
                    }
                    Thread.Sleep(1500);
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
                    Thread.Sleep(1500);
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
        string[] vocab_list_right = {"angry", "apple", "arvin", "baby", "bad", "bird", "blue", "brandon", "before", "boy", "brother", "brown", "bug", "cat", "cents", "cereal", "cheese", "church", "clean", "coat", "cold", "come", "cost", "cow", "cry", "cup", "dad", "day", "divorce", "dog", "dollars", "drink", "eat", "egg", "excuse", "finish", "fork", "full", "girl", "go", "gold", "grandma", "grandpa", "green", "hello", "help", "home", "horse", "hot", "how", "hungry", "hurt", "in", "justin", "large", "like", "little", "love", "marriage", "milk", "miles", "mom", "month", "more", "my", "name", "night", "okay", "orange", "out", "pants", "pig", "pizza", "please", "red", "sad", "school", "separate", "sheep", "shirt", "shoes", "short", "silver", "single", "sister", "sleep", "socks", "tall", "teeth", "thanks", "today", "underwear", "water", "what", "when", "where", "who", "why", "will", "with", "work", "yellow"};


        // 4populates the ASLvocab_right hash with keys and values 
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
        string[] vocab_list_left = {"angry", "baby", "brother", "cheese", "church", "clean", "coat", "cold", "come", "cost", "cry", "cup", "day", "divorce", "dollars", "egg", "excuse", "finish", "fork", "full", "go", "help", "how", "hurt", "in", "large", "little", "love", "marriage", "month", "more", "name", "night", "out", "pants", "please", "sad", "school", "seperate", "sheep", "shoes", "sister", "socks", "tall", "today", "underwear", "what", "when", "with", "work"};

        // populates the ASLvocab_left hash with keys and values 
        for (int i=0; i < vocab_list_left.Length; i++)
        {
            string key = vocab_list_left[i]; 
            ASL_vocab_left.Add(key, null);
        } 
    return ASL_vocab_left; 
    }
            
    // Update is called once per frame
    void Update ()
    {
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

        if (wordInputTimer>5)
        {
            if (!t.IsAlive && sentence_queue.Count != 0)
            {
                String sentence = sentence_queue.Dequeue();
                t = new Thread(()=>ActionPerformer(sentence));
                t.Start();
            }
        }
        wordInputTimer++;
        if (!t.IsAlive && sentence_queue.Count == 0 && wordInputTimer>1500)
        {
            resultText.text="no speech input: close app and try again :)";
            if (SpeechRecognizer.IsRecording())
            {
                SpeechRecognizer.StopIfRecording();
                //resultText.text = "I stopped recording";
            }
            Thread.Sleep(500);
            Application.Quit();
        }
    }

    /// <summary>
    /// Raises the back button event.
    /// </summary>
    //public void OnBackButton ()
    //{
    //    SceneManager.LoadScene ("FaceTrackerExample");
    //}

}
