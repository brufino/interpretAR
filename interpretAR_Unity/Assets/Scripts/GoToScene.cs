using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using System;
using System.Collections;
using System.Collections.Generic;


public class GoToScene : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }
    private void ChangeScene(string sceneName)
	{
    	SceneManager.LoadScene (sceneName);
	}

	public void ReceiveJavaMessage(string message)
	{
    	if (message.Equals ("TextureFaceTrackerExample") || message.Equals ("ASLTranslation")) 
    	{
        	ChangeScene (message);
    	} 
    	else 
    	{
        	Debug.Log ("The scene name is incorrect");
    	}	
	}
}
