using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class animationV : MonoBehaviour
{
   
    // Update is called once per frame
    void Update()
    {
        if (Input.GetKeyDown(KeyCode.UpArrow))
        {
            GetComponent<Animation>().Play("Right|RightAction");
            GetComponent<Animation>().Play("Left|RightAction");
            

        }
        else if (Input.GetKeyDown(KeyCode.DownArrow))
        {
            //GetComponent<Animation>().Play("Right|RightAction");
        }
    }
}
